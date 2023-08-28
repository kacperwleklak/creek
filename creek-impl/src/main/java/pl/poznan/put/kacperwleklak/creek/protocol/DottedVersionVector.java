/**
 * Autogenerated by Thrift Compiler (0.18.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pl.poznan.put.kacperwleklak.creek.protocol;

import java.util.*;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.18.1)", date = "2023-08-28")
public class DottedVersionVector implements org.apache.thrift.TBase<DottedVersionVector, DottedVersionVector._Fields>, java.io.Serializable, Cloneable, Comparable<DottedVersionVector> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DottedVersionVector");

  private static final org.apache.thrift.protocol.TField VC_FIELD_DESC = new org.apache.thrift.protocol.TField("vc", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField DOTS_FIELD_DESC = new org.apache.thrift.protocol.TField("dots", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new DottedVersionVectorStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new DottedVersionVectorTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.util.List<Long> vc; // required
  public @org.apache.thrift.annotation.Nullable java.util.List<NavigableSet<Long>> dots; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    VC((short)1, "vc"),
    DOTS((short)2, "dots");

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
        case 1: // VC
          return VC;
        case 2: // DOTS
          return DOTS;
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

    @Override
    public short getThriftFieldId() {
      return _thriftId;
    }

    @Override
    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.VC, new org.apache.thrift.meta_data.FieldMetaData("vc", org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
                    new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    tmpMap.put(_Fields.DOTS, new org.apache.thrift.meta_data.FieldMetaData("dots", org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
                    new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET,
                            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DottedVersionVector.class, metaDataMap);
  }

  public DottedVersionVector() {
  }

  public DottedVersionVector(
          java.util.List<Long> vc,
          java.util.List<java.util.NavigableSet<Long>> dots)
  {
    this();
    this.vc = vc;
    this.dots = dots;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DottedVersionVector(DottedVersionVector other) {
    if (other.isSetVc()) {
      java.util.List<Long> __this__vc = new java.util.ArrayList<Long>(other.vc);
      this.vc = __this__vc;
    }
    if (other.isSetDots()) {
      java.util.List<java.util.NavigableSet<Long>> __this__dots = new java.util.ArrayList<java.util.NavigableSet<Long>>(other.dots.size());
      for (java.util.Set<Long> other_element : other.dots) {
        java.util.NavigableSet<Long> __this__dots_copy = new TreeSet<>(other_element);
        __this__dots.add(__this__dots_copy);
      }
      this.dots = __this__dots;
    }
  }

  @Override
  public DottedVersionVector deepCopy() {
    return new DottedVersionVector(this);
  }

  @Override
  public void clear() {
    this.vc = null;
    this.dots = null;
  }

  public int getVcSize() {
    return (this.vc == null) ? 0 : this.vc.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<Long> getVcIterator() {
    return (this.vc == null) ? null : this.vc.iterator();
  }

  public void addToVc(long elem) {
    if (this.vc == null) {
      this.vc = new java.util.ArrayList<Long>();
    }
    this.vc.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<Long> getVc() {
    return this.vc;
  }

  public DottedVersionVector setVc(@org.apache.thrift.annotation.Nullable java.util.List<Long> vc) {
    this.vc = vc;
    return this;
  }

  public void unsetVc() {
    this.vc = null;
  }

  /** Returns true if field vc is set (has been assigned a value) and false otherwise */
  public boolean isSetVc() {
    return this.vc != null;
  }

  public void setVcIsSet(boolean value) {
    if (!value) {
      this.vc = null;
    }
  }

  public int getDotsSize() {
    return (this.dots == null) ? 0 : this.dots.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.util.NavigableSet<Long>> getDotsIterator() {
    return (this.dots == null) ? null : this.dots.iterator();
  }

  public void addToDots(java.util.NavigableSet<Long> elem) {
    if (this.dots == null) {
      this.dots = new java.util.ArrayList<java.util.NavigableSet<Long>>();
    }
    this.dots.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.util.NavigableSet<Long>> getDots() {
    return this.dots;
  }

  public DottedVersionVector setDots(@org.apache.thrift.annotation.Nullable java.util.List<java.util.NavigableSet<Long>> dots) {
    this.dots = dots;
    return this;
  }

  public void unsetDots() {
    this.dots = null;
  }

  /** Returns true if field dots is set (has been assigned a value) and false otherwise */
  public boolean isSetDots() {
    return this.dots != null;
  }

  public void setDotsIsSet(boolean value) {
    if (!value) {
      this.dots = null;
    }
  }

  @Override
  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
      case VC:
        if (value == null) {
          unsetVc();
        } else {
          setVc((java.util.List<Long>)value);
        }
        break;

      case DOTS:
        if (value == null) {
          unsetDots();
        } else {
          setDots((java.util.List<java.util.NavigableSet<Long>>)value);
        }
        break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public Object getFieldValue(_Fields field) {
    switch (field) {
      case VC:
        return getVc();

      case DOTS:
        return getDots();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  @Override
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
      case VC:
        return isSetVc();
      case DOTS:
        return isSetDots();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that instanceof DottedVersionVector)
      return this.equals((DottedVersionVector)that);
    return false;
  }

  public boolean equals(DottedVersionVector that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_vc = true && this.isSetVc();
    boolean that_present_vc = true && that.isSetVc();
    if (this_present_vc || that_present_vc) {
      if (!(this_present_vc && that_present_vc))
        return false;
      if (!this.vc.equals(that.vc))
        return false;
    }

    boolean this_present_dots = true && this.isSetDots();
    boolean that_present_dots = true && that.isSetDots();
    if (this_present_dots || that_present_dots) {
      if (!(this_present_dots && that_present_dots))
        return false;
      if (!this.dots.equals(that.dots))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetVc()) ? 131071 : 524287);
    if (isSetVc())
      hashCode = hashCode * 8191 + vc.hashCode();

    hashCode = hashCode * 8191 + ((isSetDots()) ? 131071 : 524287);
    if (isSetDots())
      hashCode = hashCode * 8191 + dots.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(DottedVersionVector other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.compare(isSetVc(), other.isSetVc());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVc()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.vc, other.vc);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.compare(isSetDots(), other.isSetDots());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDots()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dots, other.dots);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  @Override
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  @Override
  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  @Override
  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DottedVersionVector(");
    boolean first = true;

    sb.append("vc:");
    if (this.vc == null) {
      sb.append("null");
    } else {
      sb.append(this.vc);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("dots:");
    if (this.dots == null) {
      sb.append("null");
    } else {
      sb.append(this.dots);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DottedVersionVectorStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public DottedVersionVectorStandardScheme getScheme() {
      return new DottedVersionVectorStandardScheme();
    }
  }

  private static class DottedVersionVectorStandardScheme extends org.apache.thrift.scheme.StandardScheme<DottedVersionVector> {

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot, DottedVersionVector struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
          case 1: // VC
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.vc = new java.util.ArrayList<Long>(_list8.size);
                long _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = iprot.readI64();
                  struct.vc.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setVcIsSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DOTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list11 = iprot.readListBegin();
                struct.dots = new java.util.ArrayList<java.util.NavigableSet<Long>>(_list11.size);
                @org.apache.thrift.annotation.Nullable java.util.NavigableSet<Long> _elem12;
                for (int _i13 = 0; _i13 < _list11.size; ++_i13)
                {
                  {
                    org.apache.thrift.protocol.TSet _set14 = iprot.readSetBegin();
                    _elem12 = new java.util.TreeSet<Long>();
                    long _elem15;
                    for (int _i16 = 0; _i16 < _set14.size; ++_i16)
                    {
                      _elem15 = iprot.readI64();
                      _elem12.add(_elem15);
                    }
                    iprot.readSetEnd();
                  }
                  struct.dots.add(_elem12);
                }
                iprot.readListEnd();
              }
              struct.setDotsIsSet(true);
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

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot, DottedVersionVector struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.vc != null) {
        oprot.writeFieldBegin(VC_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, struct.vc.size()));
          for (long _iter17 : struct.vc)
          {
            oprot.writeI64(_iter17);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.dots != null) {
        oprot.writeFieldBegin(DOTS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.SET, struct.dots.size()));
          for (java.util.Set<Long> _iter18 : struct.dots)
          {
            {
              oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I64, _iter18.size()));
              for (long _iter19 : _iter18)
              {
                oprot.writeI64(_iter19);
              }
              oprot.writeSetEnd();
            }
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DottedVersionVectorTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    @Override
    public DottedVersionVectorTupleScheme getScheme() {
      return new DottedVersionVectorTupleScheme();
    }
  }

  private static class DottedVersionVectorTupleScheme extends org.apache.thrift.scheme.TupleScheme<DottedVersionVector> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DottedVersionVector struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetVc()) {
        optionals.set(0);
      }
      if (struct.isSetDots()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetVc()) {
        {
          oprot.writeI32(struct.vc.size());
          for (long _iter20 : struct.vc)
          {
            oprot.writeI64(_iter20);
          }
        }
      }
      if (struct.isSetDots()) {
        {
          oprot.writeI32(struct.dots.size());
          for (java.util.Set<Long> _iter21 : struct.dots)
          {
            {
              oprot.writeI32(_iter21.size());
              for (long _iter22 : _iter21)
              {
                oprot.writeI64(_iter22);
              }
            }
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DottedVersionVector struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list23 = iprot.readListBegin(org.apache.thrift.protocol.TType.I64);
          struct.vc = new java.util.ArrayList<Long>(_list23.size);
          long _elem24;
          for (int _i25 = 0; _i25 < _list23.size; ++_i25)
          {
            _elem24 = iprot.readI64();
            struct.vc.add(_elem24);
          }
        }
        struct.setVcIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list26 = iprot.readListBegin(org.apache.thrift.protocol.TType.SET);
          struct.dots = new java.util.ArrayList<java.util.NavigableSet<Long>>(_list26.size);
          @org.apache.thrift.annotation.Nullable java.util.NavigableSet<Long> _elem27;
          for (int _i28 = 0; _i28 < _list26.size; ++_i28)
          {
            {
              org.apache.thrift.protocol.TSet _set29 = iprot.readSetBegin(org.apache.thrift.protocol.TType.I64);
              _elem27 = new java.util.TreeSet<Long>();
              long _elem30;
              for (int _i31 = 0; _i31 < _set29.size; ++_i31)
              {
                _elem30 = iprot.readI64();
                _elem27.add(_elem30);
              }
            }
            struct.dots.add(_elem27);
          }
        }
        struct.setDotsIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }

  @SuppressWarnings("unchecked")
  public DottedVersionVector(int numOfProcesses) {
    vc = new ArrayList<Long>();
    while(vc.size() < numOfProcesses) vc.add(new Long(0));
    dots = new ArrayList<NavigableSet<Long>>();
    for (int i = 0; i < numOfProcesses; i++)
      dots.add(new TreeSet<Long>());
  }

  public void merge(DottedVersionVector other) {
    for (int i = 0; i < vc.size(); i++) {
      vc.set(i, Math.max(vc.get(i), other.vc.get(i)));
    }

    for (int i = 0; i < dots.size(); i++) {
      dots.get(i).addAll(other.dots.get(i));
      cleanUp(i);
    }
  }

  private void add(int replicaId, long eventNumber) {
    if (eventNumber <= 0)
      throw new NoSuchElementException();

    if (contains(replicaId, eventNumber))
      return;

    if (vc.get(replicaId) == eventNumber - 1) {
      vc.set(replicaId, vc.get(replicaId) + 1);
      cleanUp(replicaId);
    } else {
      dots.get(replicaId).add(eventNumber);
    }
  }

  public void add(Dot dot) {
    add(dot.replica, dot.currEventNo);
  }

  private void remove(int replicaId, long eventNumber) {
    if (eventNumber <= 0)
      throw new NoSuchElementException();

    if (dots.get(replicaId).contains(eventNumber)) {
      dots.get(replicaId).remove(eventNumber);
    } else if (vc.get(replicaId) == eventNumber) {
      vc.set(replicaId, vc.get(replicaId)-1);
    } else {
      for (long i = vc.get(replicaId); i > eventNumber; i--)
        dots.get(replicaId).add(i);
      vc.set(replicaId, eventNumber - 1);
    }
  }

  public void remove(Dot dot) {
    remove(dot.replica, dot.currEventNo);
  }

  private void cleanUp(int replicaId) {
    long vcmax = vc.get(replicaId) + 1;
    Set<Long> addedDots = new TreeSet<Long>();

    for (Long dot : dots.get(replicaId)) {
      if (dot == vcmax) {
        vc.set(replicaId, vc.get(replicaId)+1);
        vcmax++;
        addedDots.add(dot);
      } else
        break;
    }

    dots.get(replicaId).removeAll(addedDots);
  }

  // true if a \subseteq b
  private boolean isSetContainedInSet(SortedSet<Long> a, SortedSet<Long> b) {
    Iterator<Long> ai = a.iterator();
    Iterator<Long> bi = a.iterator();

    while (ai.hasNext()) {
      Long x = ai.next();

      while (true) {
        if (!bi.hasNext())
          return false;

        Long y = bi.next();

        if (x < y)
          continue;
        if (x > y)
          return false;
        break;
      }
    }
    return true;
  }

  public Dot maxDot() {
    for (int i = vc.size()-1; i >= 0; i--) {
      if (dots.get(i) != null && !dots.get(i).isEmpty())
        return new Dot((byte) i, dots.get(i).last());
      if (vc.get(i) != null && vc.get(i) > 0)
        return new Dot((byte) i, vc.get(i));
    }
    return null;
  }

  public Dot maxDot(DottedVersionVector exclude) {
    for (int i = vc.size()-1; i >= 0; i--) {
      var iter = backwardIterator(i);
      while (iter.hasNext()) {
        Dot dot = iter.next();
        if (dot.currEventNo <= exclude.vc.get(i))
          break;
        if (!exclude.dots.get(i).contains(dot.currEventNo))
          return dot;
      }
    }
    return null;
  }

  public boolean contains(int replicaId, long eventNumber) {
    if (eventNumber <= 0)
      return false;
    if (vc.get(replicaId) >= eventNumber)
      return true;
    return dots.get(replicaId).contains(eventNumber);
  }

  public boolean contains(Dot dot) {
    return contains(dot.replica, dot.currEventNo);
  }

  public boolean isSuperSetOf(DottedVersionVector other) {
    for (int i = 0; i < vc.size(); i++) {
      if (other.vc.get(i) > vc.get(i))
        return false;
    }

    for (int i = 0; i < dots.size(); i++) {
      if (!isSetContainedInSet(other.dots.get(i), dots.get(i)))
        return false;
    }

    return true;
  }

  public ListIterator<Dot> backwardIterator(int replicaId) {
    return new BackwardIterator(replicaId);
  }

  private class BackwardIterator implements ListIterator<Dot> {

    int replicaId;
    long eventNumber;
    long prevEventNumber = 0;

    BackwardIterator(int replicaId) {
      this.replicaId = replicaId;
      if (dots.get(replicaId) != null && !dots.get(replicaId).isEmpty())
        this.eventNumber = dots.get(replicaId).last();
      else
        this.eventNumber = vc.get(replicaId);
    }

    @Override
    public boolean hasNext() {
      return eventNumber > 0; //  ? contains(replicaId, eventNumber) : false;
    }

    private void progress() {
      prevEventNumber = eventNumber;
      if (eventNumber - 1 <= vc.get(replicaId)) {
        eventNumber--;
      }
      else {
        Long less = dots.get(replicaId).lower(eventNumber);
        eventNumber = less != null ? less : vc.get(replicaId);
      }
    }

    @Override
    public Dot next() {
      if (!hasNext())
        throw new NoSuchElementException();

      Dot ret = new Dot((byte) replicaId, eventNumber);

      progress();

      return ret;
    }

    @Override
    public boolean hasPrevious() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Dot previous() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int nextIndex() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int previousIndex() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
      if (prevEventNumber == 0)
        throw new IllegalStateException();

      DottedVersionVector.this.remove(replicaId, prevEventNumber);
      prevEventNumber = 0;
    }

    @Override
    public void set(Dot e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(Dot e) {
      throw new UnsupportedOperationException();
    }
  }
}



