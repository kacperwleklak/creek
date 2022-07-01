package pl.poznan.put.kacperwleklak.creek.service;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.h2.api.ErrorCode;
import org.h2.command.Command;
import org.h2.command.CommandInterface;
import org.h2.engine.*;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;
import org.h2.value.*;
import pl.poznan.put.kacperwleklak.creek.postgres.PostgresServer;
import pl.poznan.put.kacperwleklak.creek.structure.DifferentialTreeMap;
import pl.poznan.put.kacperwleklak.creek.structure.Request;
import pl.poznan.put.kacperwleklak.creek.structure.response.Response;
import pl.poznan.put.kacperwleklak.creek.structure.response.ResponseMessageStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@Data
@Slf4j
public class StateObject {

    private static final String DB_NAME = System.getenv("DBNAME");

    private SortedMap<Request, Long> undoLog;

    // H2 Logic
    private SessionLocal session;
    private CommandInterface activeRequest;
    private String clientEncoding = SysProperties.PG_DEFAULT_CLIENT_ENCODING;
    private final PostgresServer pgServer;
    private static final Pattern SHOULD_QUOTE = Pattern.compile(".*[\",\\\\{}].*");

    public StateObject(PostgresServer pgServer) {
        undoLog = new TreeMap<>();
        this.pgServer = pgServer;
        session = initSession();
        initDb();
    }

    public synchronized void rollback(Request request) {
        undoLog.remove(request);
    }

    @SneakyThrows
    public synchronized Response execute(Request request) {
        Response response = new Response();
        String operation = request.getOperation();
        ScriptReader reader = new ScriptReader(new StringReader(operation));
        long version = bumpVersion();
        undoLog.put(request, version);
        while (true) {
            String s = reader.readStatement();
            if (s == null) {
                break;
            }
            s = getSQL(s);
            try (CommandInterface command = session.prepareLocal(s)) {
                setActiveRequest(command);
                if (command.isQuery()) {
                    try (ResultInterface result = command.executeQuery(0, false)) {
                        response.addMessage(buildRowDescriptionMessage(result, null));
                        while (result.next()) {
                            response.addMessage(buildDataRowMessage(result, null));
                        }
                        response.addMessage(buildCommandCompleteMessage(command, 0));
                    }
                } else {
                    response.addMessage(buildCommandCompleteMessage(command, command.executeUpdate(null).getUpdateCount()));
                }
            } catch (Exception e) {
                response.addMessage(sendErrorOrCancelResponse(e));
                break;
            } finally {
                setActiveRequest(null);
            }
        }
        response.addMessage(sendReadyForQuery());
        return response;
    }

    private long bumpVersion() {
        Command command = session.prepareLocal("SELECT nextval('global_version');");
        ResultInterface resultInterface = command.executeQuery(0, false);
        resultInterface.next();
        return resultInterface.currentRow()[0].getLong();
    }

    /*
        SQL Logic below
     */
    private SessionLocal initSession() {
        Properties info = new Properties();
        info.put("MODE", "PostgreSQL");
        info.put("DATABASE_TO_LOWER", "TRUE");
        info.put("DEFAULT_NULL_ORDERING", "HIGH");
        String url = "jdbc:h2:./" + DB_NAME + ";AUTO_SERVER=TRUE";
        ConnectionInfo ci = new ConnectionInfo(url, info, "sa", "password");
        ci.setProperty("FORBID_CREATION", "FALSE");
        return Engine.createSession(ci);
    }

    private void initDb() {
        session.setTimeZone(DateTimeUtils.getTimeZone());
        try (CommandInterface command = session.prepareLocal("set search_path = public, pg_catalog")) {
            command.executeUpdate(null);
        }
        HashSet<Integer> typeSet = pgServer.getTypeSet();
        if (typeSet.isEmpty()) {
            try (CommandInterface command = session.prepareLocal("select oid from pg_catalog.pg_type");
                 ResultInterface result = command.executeQuery(0, false)) {
                while (result.next()) {
                    typeSet.add(result.currentRow()[0].getInt());
                }
            }
        }
    }

    private String getSQL(String s) {
        String lower = StringUtils.toLowerEnglish(s);
        if (lower.startsWith("show max_identifier_length")) {
            s = "CALL 63";
        } else if (lower.startsWith("set client_encoding to")) {
            s = "set DATESTYLE ISO";
        }
        // s = StringUtils.replaceAll(s, "i.indkey[ia.attnum-1]", "0");
        log.trace(s + ";");
        return s;
    }

    private ResponseMessageStream buildRowDescriptionMessage(ResultInterface result, int[] formatCodes) throws IOException {
        if (result == null) {
            return buildNoDataMessage();
        } else {
            int columns = result.getVisibleColumnCount();
            int[] oids = new int[columns];
            int[] attnums = new int[columns];
            int[] types = new int[columns];
            int[] precision = new int[columns];
            String[] names = new String[columns];
            Database database = session.getDatabase();
            for (int i = 0; i < columns; i++) {
                String name = result.getColumnName(i);
                Schema schema = database.findSchema(result.getSchemaName(i));
                if (schema != null) {
                    Table table = schema.findTableOrView(session, result.getTableName(i));
                    if (table != null) {
                        oids[i] = table.getId();
                        Column column = table.findColumn(name);
                        if (column != null) {
                            attnums[i] = column.getColumnId() + 1;
                        }
                    }
                }
                names[i] = name;
                TypeInfo type = result.getColumnType(i);
                int pgType = PostgresServer.convertType(type);
                precision[i] = type.getDisplaySize();
//                if (type.getValueType() != Value.NULL) {
//                    server.checkType(pgType);
//                }
                types[i] = pgType;
            }
            ResponseMessageStream responseMessage = ResponseMessageStream.startMessage('T');
            responseMessage.writeShort(columns);
            for (int i = 0; i < columns; i++) {
                responseMessage.writeString(StringUtils.toLowerEnglish(names[i]), getEncoding());
                responseMessage.writeInt(oids[i]);
                responseMessage.writeShort(attnums[i]);
                responseMessage.writeInt(types[i]);
                responseMessage.writeShort(getTypeSize(types[i], precision[i]));
                responseMessage.writeInt(-1);
                responseMessage.writeShort(formatAsText(types[i], formatCodes, i) ? 0 : 1);
            }
            return responseMessage;
        }
    }

    private ResponseMessageStream sendErrorOrCancelResponse(Exception e) throws IOException {
        if (e instanceof DbException && ((DbException) e).getErrorCode() == ErrorCode.STATEMENT_WAS_CANCELED) {
            return buildCancelQueryResponse();
        } else {
            return buildErrorResponse(e);
        }
    }

    private ResponseMessageStream buildCancelQueryResponse() throws IOException {
        ResponseMessageStream responseMessageStream = ResponseMessageStream.startMessage('E');
        responseMessageStream.write('S');
        responseMessageStream.writeString("ERROR", getEncoding());
        responseMessageStream.write('C');
        responseMessageStream.writeString("57014", getEncoding());
        responseMessageStream.write('M');
        responseMessageStream.writeString("canceling statement due to user request", getEncoding());
        responseMessageStream.write(0);
        return responseMessageStream;
    }

    private ResponseMessageStream buildErrorResponse(Exception re) throws IOException {
        SQLException e = DbException.toSQLException(re);
        ResponseMessageStream responseMessageStream = ResponseMessageStream.startMessage('E');
        responseMessageStream.write('S');
        responseMessageStream.writeString("ERROR", getEncoding());
        responseMessageStream.write('C');
        responseMessageStream.writeString(e.getSQLState(), getEncoding());
        responseMessageStream.write('M');
        responseMessageStream.writeString(e.getMessage(), getEncoding());
        responseMessageStream.write('D');
        responseMessageStream.writeString(e.toString(), getEncoding());
        responseMessageStream.write(0);
        return responseMessageStream;
    }

    private ResponseMessageStream buildDataRowMessage(ResultInterface result, int[] formatCodes) throws IOException {
        int columns = result.getVisibleColumnCount();
        ResponseMessageStream responseMessage = ResponseMessageStream.startMessage('D');
        responseMessage.writeShort(columns);
        Value[] row = result.currentRow();
        for (int i = 0; i < columns; i++) {
            int pgType = PostgresServer.convertType(result.getColumnType(i));
            boolean text = formatAsText(pgType, formatCodes, i);
            writeDataColumn(responseMessage, row[i], pgType, text);
        }
        return responseMessage;
    }

    private ResponseMessageStream buildCommandCompleteMessage(CommandInterface command, long updateCount) throws IOException {
        ResponseMessageStream responseMessage = ResponseMessageStream.startMessage('C');
        switch (command.getCommandType()) {
            case CommandInterface.INSERT:
                responseMessage.writeStringPart("INSERT 0 ", getEncoding());
                responseMessage.writeString(Long.toString(updateCount), getEncoding());
                break;
            case CommandInterface.UPDATE:
                responseMessage.writeStringPart("UPDATE ", getEncoding());
                responseMessage.writeString(Long.toString(updateCount), getEncoding());
                break;
            case CommandInterface.DELETE:
                responseMessage.writeStringPart("DELETE ", getEncoding());
                responseMessage.writeString(Long.toString(updateCount), getEncoding());
                break;
            case CommandInterface.SELECT:
            case CommandInterface.CALL:
                responseMessage.writeString("SELECT", getEncoding());
                break;
            case CommandInterface.BEGIN:
                responseMessage.writeString("BEGIN", getEncoding());
                break;
            default:
                responseMessage.writeStringPart("UPDATE ", getEncoding());
                responseMessage.writeString(Long.toString(updateCount), getEncoding());
        }
        return responseMessage;
    }

    private ResponseMessageStream buildNoDataMessage() throws IOException {
        return ResponseMessageStream.startMessage('n');
    }

    private static int getTypeSize(int pgType, int precision) {
        switch (pgType) {
            case PostgresServer.PG_TYPE_BOOL:
                return 1;
            case PostgresServer.PG_TYPE_VARCHAR:
                return Math.max(255, precision + 10);
            default:
                return precision + 4;
        }
    }

    private static boolean formatAsText(int pgType, int[] formatCodes, int column) {
        boolean text = true;
        if (formatCodes != null && formatCodes.length > 0) {
            if (formatCodes.length == 1) {
                text = formatCodes[0] == 0;
            } else if (column < formatCodes.length) {
                text = formatCodes[column] == 0;
            }
        }
        return text;
    }

    private void writeDataColumn(ResponseMessageStream responseMessage, Value v, int pgType, boolean text) throws IOException {
        if (v == ValueNull.INSTANCE) {
            responseMessage.writeInt(-1);
            return;
        }
        if (text) {
            switch (pgType) {
                case PostgresServer.PG_TYPE_BOOL:
                    responseMessage.writeInt(1);
                    responseMessage.writeByte(v.getBoolean() ? 't' : 'f');
                    break;
                case PostgresServer.PG_TYPE_BYTEA: {
                    byte[] bytes = v.getBytesNoCopy();
                    int length = bytes.length;
                    int cnt = length;
                    for (int i = 0; i < length; i++) {
                        byte b = bytes[i];
                        if (b < 32 || b > 126) {
                            cnt += 3;
                        } else if (b == 92) {
                            cnt++;
                        }
                    }
                    byte[] data = new byte[cnt];
                    for (int i = 0, j = 0; i < length; i++) {
                        byte b = bytes[i];
                        if (b < 32 || b > 126) {
                            data[j++] = '\\';
                            data[j++] = (byte) (((b >>> 6) & 3) + '0');
                            data[j++] = (byte) (((b >>> 3) & 7) + '0');
                            data[j++] = (byte) ((b & 7) + '0');
                        } else if (b == 92) {
                            data[j++] = '\\';
                            data[j++] = '\\';
                        } else {
                            data[j++] = b;
                        }
                    }
                    responseMessage.writeInt(data.length);
                    responseMessage.write(data);
                    break;
                }
                case PostgresServer.PG_TYPE_INT2_ARRAY:
                case PostgresServer.PG_TYPE_INT4_ARRAY:
                case PostgresServer.PG_TYPE_VARCHAR_ARRAY:
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write('{');
                    Value[] values = ((ValueArray) v).getList();
                    Charset encoding = getEncoding();
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) {
                            baos.write(',');
                        }
                        String s = values[i].getString();
                        if (SHOULD_QUOTE.matcher(s).matches()) {
                            List<String> ss = new ArrayList<>();
                            for (String s0 : s.split("\\\\")) {
                                ss.add(s0.replace("\"", "\\\""));
                            }
                            s = "\"" + String.join("\\\\", ss) + "\"";
                        }
                        baos.write(s.getBytes(encoding));
                    }
                    baos.write('}');
                    responseMessage.writeInt(baos.size());
                    responseMessage.write(baos);
                    break;
                default:
                    byte[] data = v.getString().getBytes(getEncoding());
                    responseMessage.writeInt(data.length);
                    responseMessage.write(data);
            }
        } else {
            // binary
            switch (pgType) {
                case PostgresServer.PG_TYPE_BOOL:
                    responseMessage.writeInt(1);
                    responseMessage.writeByte(v.getBoolean() ? 1 : 0);
                    break;
                case PostgresServer.PG_TYPE_INT2:
                    responseMessage.writeInt(2);
                    responseMessage.writeShort(v.getShort());
                    break;
                case PostgresServer.PG_TYPE_INT4:
                    responseMessage.writeInt(4);
                    responseMessage.writeInt(v.getInt());
                    break;
                case PostgresServer.PG_TYPE_INT8:
                    responseMessage.writeInt(8);
                    responseMessage.writeLong(v.getLong());
                    break;
                case PostgresServer.PG_TYPE_FLOAT4:
                    responseMessage.writeInt(4);
                    responseMessage.writeFloat(v.getFloat());
                    break;
                case PostgresServer.PG_TYPE_FLOAT8:
                    responseMessage.writeInt(8);
                    responseMessage.writeDouble(v.getDouble());
                    break;
                case PostgresServer.PG_TYPE_NUMERIC:
                    responseMessage.writeNumericBinary(v.getBigDecimal());
                    break;
                case PostgresServer.PG_TYPE_BYTEA: {
                    byte[] data = v.getBytesNoCopy();
                    responseMessage.writeInt(data.length);
                    responseMessage.write(data);
                    break;
                }
                case PostgresServer.PG_TYPE_DATE:
                    responseMessage.writeInt(4);
                    responseMessage.writeInt((int) toPostgresDays(((ValueDate) v).getDateValue()));
                    break;
                case PostgresServer.PG_TYPE_TIME:
                    responseMessage.writeTimeBinary(((ValueTime) v).getNanos(), 8);
                    break;
                case PostgresServer.PG_TYPE_TIMETZ: {
                    ValueTimeTimeZone t = (ValueTimeTimeZone) v;
                    long m = t.getNanos();
                    responseMessage.writeTimeBinary(m, 12);
                    responseMessage.writeInt(-t.getTimeZoneOffsetSeconds());
                    break;
                }
                case PostgresServer.PG_TYPE_TIMESTAMP: {
                    ValueTimestamp t = (ValueTimestamp) v;
                    long m = toPostgresDays(t.getDateValue()) * 86_400;
                    long nanos = t.getTimeNanos();
                    responseMessage.writeTimestampBinary(m, nanos);
                    break;
                }
                case PostgresServer.PG_TYPE_TIMESTAMPTZ: {
                    ValueTimestampTimeZone t = (ValueTimestampTimeZone) v;
                    long m = toPostgresDays(t.getDateValue()) * 86_400;
                    long nanos = t.getTimeNanos() - t.getTimeZoneOffsetSeconds() * 1_000_000_000L;
                    if (nanos < 0L) {
                        m--;
                        nanos += DateTimeUtils.NANOS_PER_DAY;
                    }
                    responseMessage.writeTimestampBinary(m, nanos);
                    break;
                }
                default:
                    throw new IllegalStateException("output binary format is undefined");
            }
        }
    }

    private ResponseMessageStream sendReadyForQuery() throws IOException {
        ResponseMessageStream responsesMessageStream = ResponseMessageStream.startMessage('Z');
        responsesMessageStream.write((byte) (session.getAutoCommit() ? /* idle */ 'I' : /* in a transaction block */ 'T'));
        return responsesMessageStream;
    }

    private Charset getEncoding() {
        if ("UNICODE".equals(clientEncoding)) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(clientEncoding);
    }

    private static long toPostgresDays(long dateValue) {
        return DateTimeUtils.absoluteDayFromDateValue(dateValue) - 10_957;
    }
}
