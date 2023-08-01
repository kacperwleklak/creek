package pl.poznan.put.appcommon.db.response;

import org.h2.api.ErrorCode;
import org.h2.message.DbException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ResponseMessageStream extends DataOutputStream {

    private static final int[] POWERS10 = {1, 10, 100, 1000, 10000};
    private static final int MAX_GROUP_SCALE = 4;
    private static final int MAX_GROUP_SIZE = POWERS10[4];
    private static final short NUMERIC_POSITIVE = 0x0000;
    private static final short NUMERIC_NEGATIVE = 0x4000;
    private static final short NUMERIC_NAN = (short) 0xC000;
    private static final BigInteger NUMERIC_CHUNK_MULTIPLIER = BigInteger.valueOf(10_000L);
    private static final boolean INTEGER_DATE_TYPES = false;

    private int messageType;

    public int getMessageType() {
        return messageType;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        ((ByteArrayOutputStream) out).writeTo(outputStream);
    }

    public static ResponseMessageStream startMessage(int messageType) {
        ResponseMessageStream responseMessage = new ResponseMessageStream();
        responseMessage.messageType = messageType;
        return responseMessage;
    }

    public ResponseMessageStream() {
        super(new ByteArrayOutputStream());
    }

    public void writeString(String s, Charset charset) throws IOException {
        writeStringPart(s, charset);
        write(0);
    }

    public void writeStringPart(String s, Charset charset) throws IOException {
        write(s.getBytes(charset));
    }

    public void write(ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(this);
    }

    public void writeNumericBinary(BigDecimal value) throws IOException {
        int weight = 0;
        List<Integer> groups = new ArrayList<>();
        int scale = value.scale();
        int signum = value.signum();
        if (signum != 0) {
            BigInteger[] unscaled = {null};
            if (scale < 0) {
                unscaled[0] = value.setScale(0).unscaledValue();
                scale = 0;
            } else {
                unscaled[0] = value.unscaledValue();
            }
            if (signum < 0) {
                unscaled[0] = unscaled[0].negate();
            }
            weight = -scale / MAX_GROUP_SCALE - 1;
            int remainder = 0;
            int scaleChunk = scale % MAX_GROUP_SCALE;
            if (scaleChunk > 0) {
                remainder = divide(unscaled, POWERS10[scaleChunk]) * POWERS10[MAX_GROUP_SCALE - scaleChunk];
                if (remainder != 0) {
                    weight--;
                }
            }
            if (remainder == 0) {
                while ((remainder = divide(unscaled, MAX_GROUP_SIZE)) == 0) {
                    weight++;
                }
            }
            groups.add(remainder);
            while (unscaled[0].signum() != 0) {
                groups.add(divide(unscaled, MAX_GROUP_SIZE));
            }
        }
        int groupCount = groups.size();
        if (groupCount + weight > Short.MAX_VALUE || scale > Short.MAX_VALUE) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, value.toString());
        }
        writeInt(8 + groupCount * 2);
        writeShort(groupCount);
        writeShort(groupCount + weight);
        writeShort(signum < 0 ? NUMERIC_NEGATIVE : NUMERIC_POSITIVE);
        writeShort(scale);
        for (int i = groupCount - 1; i >= 0; i--) {
            writeShort(groups.get(i));
        }
    }

    public void writeTimeBinary(long m, int numBytes) throws IOException {
        writeInt(numBytes);
        if (INTEGER_DATE_TYPES) {
            m /= 1_000;
        } else {
            m = Double.doubleToLongBits(m * 0.000_000_001);
        }
        writeLong(m);
    }

    public void writeTimestampBinary(long m, long nanos) throws IOException {
        writeInt(8);
        if (INTEGER_DATE_TYPES) {
            m = m * 1_000_000 + nanos / 1_000;
        } else {
            m = Double.doubleToLongBits(m + nanos * 0.000_000_001);
        }
        writeLong(m);
    }

    private static int divide(BigInteger[] unscaled, int divisor) {
        BigInteger[] bi = unscaled[0].divideAndRemainder(BigInteger.valueOf(divisor));
        unscaled[0] = bi[0];
        return bi[1].intValue();
    }
}
