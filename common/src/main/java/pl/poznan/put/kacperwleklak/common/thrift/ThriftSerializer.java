package pl.poznan.put.kacperwleklak.common.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.*;
import org.apache.thrift.annotation.Nullable;
import org.apache.thrift.transport.TTransportException;

@Slf4j
public class ThriftSerializer {

    private static TSerializer serializer;
    private static TDeserializer deserializer;

    static {
        try {
            serializer = new TSerializer();
            deserializer = new TDeserializer();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public static byte[] serialize(TBase tBase) throws TException {
        return new TSerializer().serialize(tBase);
    }

    public static byte getMsgType(byte[] msg) throws TException {
        return new TDeserializer().partialDeserializeByte(msg, _Fields.MSG_TYPE);
    }

    public static void deserialize(TBase tBase, byte[] bytes) throws TException{
        new TDeserializer().deserialize(tBase, bytes);
    }

    private enum _Fields implements org.apache.thrift.TFieldIdEnum {
        MSG_TYPE((short)1, "msgType");

        private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

        static {
            for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        @Nullable
        public static _Fields findByThriftId(int fieldId) {
            if (fieldId == 1) { // MSG_TYPE
                return MSG_TYPE;
            }
            return null;
        }
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }
        @org.apache.thrift.annotation.Nullable
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        private final short _thriftId;
        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    public static void assertMessage(byte[] receivedBytes, TBase decodedMsg) {
        try {
            byte[] encodedMsg = ThriftSerializer.serialize(decodedMsg);
            int diff = receivedBytes.length - encodedMsg.length;
            if (diff > 0) {
                log.error("Multiple messages sent in one frame receivedBytes={}, encodedMsg={}, diff={}",
                        receivedBytes.length, encodedMsg.length, diff);
            }
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
