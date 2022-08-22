/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pl.poznan.put.kacperwleklak.cab.protocol;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-08-19")
public class CabBroadcastMessage implements org.apache.thrift.TBase<CabBroadcastMessage, CabBroadcastMessage._Fields>, java.io.Serializable, Cloneable, Comparable<CabBroadcastMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CabBroadcastMessage");

  private static final org.apache.thrift.protocol.TField MSG_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("msgType", org.apache.thrift.protocol.TType.BYTE, (short)1);
  private static final org.apache.thrift.protocol.TField CAB_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("cabMessage", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CabBroadcastMessageStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CabBroadcastMessageTupleSchemeFactory();

  public byte msgType; // required
  public @org.apache.thrift.annotation.Nullable CabMessage cabMessage; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MSG_TYPE((short)1, "msgType"),
    CAB_MESSAGE((short)2, "cabMessage");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // MSG_TYPE
          return MSG_TYPE;
        case 2: // CAB_MESSAGE
          return CAB_MESSAGE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
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

  // isset id assignments
  private static final int __MSGTYPE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MSG_TYPE, new org.apache.thrift.meta_data.FieldMetaData("msgType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BYTE)));
    tmpMap.put(_Fields.CAB_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("cabMessage", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, CabMessage.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CabBroadcastMessage.class, metaDataMap);
  }

  public CabBroadcastMessage() {
    this.msgType = (byte)2;

  }

  public CabBroadcastMessage(
    CabMessage cabMessage)
  {
    this();
    setMsgTypeIsSet(true);
    this.cabMessage = cabMessage;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CabBroadcastMessage(CabBroadcastMessage other) {
    __isset_bitfield = other.__isset_bitfield;
    this.msgType = other.msgType;
    if (other.isSetCabMessage()) {
      this.cabMessage = new CabMessage(other.cabMessage);
    }
  }

  public CabBroadcastMessage deepCopy() {
    return new CabBroadcastMessage(this);
  }

  @Override
  public void clear() {
    this.msgType = (byte)2;

    this.cabMessage = null;
  }

  public byte getMsgType() {
    return this.msgType;
  }

  public CabBroadcastMessage setMsgType(byte msgType) {
    this.msgType = msgType;
    setMsgTypeIsSet(true);
    return this;
  }

  public void unsetMsgType() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __MSGTYPE_ISSET_ID);
  }

  /** Returns true if field msgType is set (has been assigned a value) and false otherwise */
  public boolean isSetMsgType() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __MSGTYPE_ISSET_ID);
  }

  public void setMsgTypeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __MSGTYPE_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public CabMessage getCabMessage() {
    return this.cabMessage;
  }

  public CabBroadcastMessage setCabMessage(@org.apache.thrift.annotation.Nullable CabMessage cabMessage) {
    this.cabMessage = cabMessage;
    return this;
  }

  public void unsetCabMessage() {
    this.cabMessage = null;
  }

  /** Returns true if field cabMessage is set (has been assigned a value) and false otherwise */
  public boolean isSetCabMessage() {
    return this.cabMessage != null;
  }

  public void setCabMessageIsSet(boolean value) {
    if (!value) {
      this.cabMessage = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
    case MSG_TYPE:
      if (value == null) {
        unsetMsgType();
      } else {
        setMsgType((Byte)value);
      }
      break;

    case CAB_MESSAGE:
      if (value == null) {
        unsetCabMessage();
      } else {
        setCabMessage((CabMessage)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MSG_TYPE:
      return getMsgType();

    case CAB_MESSAGE:
      return getCabMessage();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MSG_TYPE:
      return isSetMsgType();
    case CAB_MESSAGE:
      return isSetCabMessage();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that instanceof CabBroadcastMessage)
      return this.equals((CabBroadcastMessage)that);
    return false;
  }

  public boolean equals(CabBroadcastMessage that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_msgType = true;
    boolean that_present_msgType = true;
    if (this_present_msgType || that_present_msgType) {
      if (!(this_present_msgType && that_present_msgType))
        return false;
      if (this.msgType != that.msgType)
        return false;
    }

    boolean this_present_cabMessage = true && this.isSetCabMessage();
    boolean that_present_cabMessage = true && that.isSetCabMessage();
    if (this_present_cabMessage || that_present_cabMessage) {
      if (!(this_present_cabMessage && that_present_cabMessage))
        return false;
      if (!this.cabMessage.equals(that.cabMessage))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + (int) (msgType);

    hashCode = hashCode * 8191 + ((isSetCabMessage()) ? 131071 : 524287);
    if (isSetCabMessage())
      hashCode = hashCode * 8191 + cabMessage.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CabBroadcastMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.compare(isSetMsgType(), other.isSetMsgType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMsgType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.msgType, other.msgType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.compare(isSetCabMessage(), other.isSetCabMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCabMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.cabMessage, other.cabMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("CabBroadcastMessage(");
    boolean first = true;

    sb.append("msgType:");
    sb.append(this.msgType);
    first = false;
    if (!first) sb.append(", ");
    sb.append("cabMessage:");
    if (this.cabMessage == null) {
      sb.append("null");
    } else {
      sb.append(this.cabMessage);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (cabMessage != null) {
      cabMessage.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CabBroadcastMessageStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CabBroadcastMessageStandardScheme getScheme() {
      return new CabBroadcastMessageStandardScheme();
    }
  }

  private static class CabBroadcastMessageStandardScheme extends org.apache.thrift.scheme.StandardScheme<CabBroadcastMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CabBroadcastMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MSG_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.BYTE) {
              struct.msgType = iprot.readByte();
              struct.setMsgTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CAB_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.cabMessage = new CabMessage();
              struct.cabMessage.read(iprot);
              struct.setCabMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, CabBroadcastMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(MSG_TYPE_FIELD_DESC);
      oprot.writeByte(struct.msgType);
      oprot.writeFieldEnd();
      if (struct.cabMessage != null) {
        oprot.writeFieldBegin(CAB_MESSAGE_FIELD_DESC);
        struct.cabMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CabBroadcastMessageTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CabBroadcastMessageTupleScheme getScheme() {
      return new CabBroadcastMessageTupleScheme();
    }
  }

  private static class CabBroadcastMessageTupleScheme extends org.apache.thrift.scheme.TupleScheme<CabBroadcastMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CabBroadcastMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetMsgType()) {
        optionals.set(0);
      }
      if (struct.isSetCabMessage()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetMsgType()) {
        oprot.writeByte(struct.msgType);
      }
      if (struct.isSetCabMessage()) {
        struct.cabMessage.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CabBroadcastMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.msgType = iprot.readByte();
        struct.setMsgTypeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.cabMessage = new CabMessage();
        struct.cabMessage.read(iprot);
        struct.setCabMessageIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

