package pl.poznan.put.kacperwleklak.sql.ycsb;

import org.h2.jdbc.JdbcBlob;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public class YcsbStoredProcedure {

    private static final String GET_ROW_STATEMENT = "SELECT YCSB_VALUE FROM usertable WHERE YCSB_KEY = ?";
    private static final String OUTPUT_STATEMENT = "UPDATE usertable SET YCSB_VALUE = '%s' WHERE YCSB_KEY = '%s'";
    private static final String ERROR = "ERROR";

    public static String reverseCase(Connection connection, String key, double probability) throws SQLException {
        if (ThreadLocalRandom.current().nextDouble() >= probability) {
            return new GeneratorOpResult(null, ERROR).serialize();
        } else {
            PreparedStatement selectStmt = connection.prepareStatement(GET_ROW_STATEMENT);
            selectStmt.setString(1, key);
            ResultSet selectResultSet = selectStmt.executeQuery();
            if (!selectResultSet.next()) return new GeneratorOpResult(null, ERROR).serialize();
            JdbcBlob ycsbValue = (JdbcBlob) selectResultSet.getBlob("YCSB_VALUE");
            long length = ycsbValue.length();
            byte[] bytes = ycsbValue.getBytes(0L, (int) length);
            String s = new String(bytes, StandardCharsets.UTF_8);
            String reverseCase = reverseCase(s);
            String shadowOp = String.format(OUTPUT_STATEMENT, reverseCase, key);
            String response = reverseCase;
            return new GeneratorOpResult(shadowOp, response).serialize();
        }
    }

    private static String reverseCase(String string) {
        return string.chars()
                .map(c -> Character.isLetter(c) ? c ^ ' ' : c)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}