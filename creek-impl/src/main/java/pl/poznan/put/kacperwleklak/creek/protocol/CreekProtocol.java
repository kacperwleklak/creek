/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pl.poznan.put.kacperwleklak.creek.protocol;

import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;
import pl.poznan.put.kacperwleklak.reliablechannel.thrift.TAsyncClientFactoryBuilder;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-08-01")
public class CreekProtocol {

  public interface Iface {

    public void operationRequestHandler(Request request) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void operationRequestHandler(Request request, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public void operationRequestHandler(Request request) throws org.apache.thrift.TException
    {
      send_operationRequestHandler(request);
    }

    public synchronized void send_operationRequestHandler(Request request) throws org.apache.thrift.TException
    {
      operationRequestHandler_args args = new operationRequestHandler_args();
      args.setRequest(request);
      sendBaseOneway("operationRequestHandler", args);
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }

      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public static class FactoryBuilder implements TAsyncClientFactoryBuilder {

      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;

      public FactoryBuilder() {}

      @Override
      public TAsyncClientFactoryBuilder builder() {
        return new FactoryBuilder();
      }

      @Override
      public void setClientManager(TAsyncClientManager clientManager) {
        this.clientManager = clientManager;
      }

      @Override
      public void setProtocolFactory(TProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
      }

      @Override
      public TAsyncClientFactory build() {
        if (clientManager == null) {
          throw new IllegalStateException("Missing client manager");
        }
        if (protocolFactory == null) {
          throw new IllegalStateException("Missing protocol factory");
        }
        return new AsyncClient.Factory(clientManager, protocolFactory);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public synchronized void operationRequestHandler(Request request, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException {
      operationRequestHandler_call method_call = new operationRequestHandler_call(request, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class operationRequestHandler_call extends org.apache.thrift.async.TAsyncMethodCall<Void> {
      private Request request;
      public operationRequestHandler_call(Request request, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, true);
        this.request = request;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("operationRequestHandler", org.apache.thrift.protocol.TMessageType.ONEWAY, 0));
        operationRequestHandler_args args = new operationRequestHandler_args();
        args.setRequest(request);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public Void getResult() throws org.apache.thrift.TException {
        if (getState() != State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return null;
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
    private static final org.slf4j.Logger _LOGGER = org.slf4j.LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new java.util.HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, java.util.Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> java.util.Map<String,  org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(java.util.Map<String, org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("operationRequestHandler", new operationRequestHandler());
      return processMap;
    }

    public static class operationRequestHandler<I extends Iface> extends org.apache.thrift.ProcessFunction<I, operationRequestHandler_args> {
      public operationRequestHandler() {
        super("operationRequestHandler");
      }

      public operationRequestHandler_args getEmptyArgsInstance() {
        return new operationRequestHandler_args();
      }

      protected boolean isOneway() {
        return true;
      }

      @Override
      protected boolean rethrowUnhandledExceptions() {
        return false;
      }

      public org.apache.thrift.TBase getResult(I iface, operationRequestHandler_args args) throws org.apache.thrift.TException {
        iface.operationRequestHandler(args.request);
        return null;
      }
    }

  }

  public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
    private static final org.slf4j.Logger _LOGGER = org.slf4j.LoggerFactory.getLogger(AsyncProcessor.class.getName());
    public AsyncProcessor(I iface) {
      super(iface, getProcessMap(new java.util.HashMap<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
    }

    protected AsyncProcessor(I iface, java.util.Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends AsyncIface> java.util.Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase,?>> getProcessMap(java.util.Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      processMap.put("operationRequestHandler", new operationRequestHandler());
      return processMap;
    }

    public static class operationRequestHandler<I extends AsyncIface> extends org.apache.thrift.AsyncProcessFunction<I, operationRequestHandler_args, Void> {
      public operationRequestHandler() {
        super("operationRequestHandler");
      }

      public operationRequestHandler_args getEmptyArgsInstance() {
        return new operationRequestHandler_args();
      }

      public org.apache.thrift.async.AsyncMethodCallback<Void> getResultHandler(final org.apache.thrift.server.AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
        final org.apache.thrift.AsyncProcessFunction fcall = this;
        return new org.apache.thrift.async.AsyncMethodCallback<Void>() { 
          public void onComplete(Void o) {
          }
          public void onError(Exception e) {
            if (e instanceof org.apache.thrift.transport.TTransportException) {
              _LOGGER.error("TTransportException inside handler", e);
              fb.close();
            } else {
              _LOGGER.error("Exception inside oneway handler", e);
            }
          }
        };
      }

      protected boolean isOneway() {
        return true;
      }

      public void start(I iface, operationRequestHandler_args args, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException {
        iface.operationRequestHandler(args.request,resultHandler);
      }
    }

  }

  public static class operationRequestHandler_args implements org.apache.thrift.TBase<operationRequestHandler_args, operationRequestHandler_args._Fields>, java.io.Serializable, Cloneable, Comparable<operationRequestHandler_args>   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("operationRequestHandler_args");

    private static final org.apache.thrift.protocol.TField REQUEST_FIELD_DESC = new org.apache.thrift.protocol.TField("request", org.apache.thrift.protocol.TType.STRUCT, (short)1);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new operationRequestHandler_argsStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new operationRequestHandler_argsTupleSchemeFactory();

    public @org.apache.thrift.annotation.Nullable Request request; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      REQUEST((short)1, "request");

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
          case 1: // REQUEST
            return REQUEST;
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
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.REQUEST, new org.apache.thrift.meta_data.FieldMetaData("request", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Request.class)));
      metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(operationRequestHandler_args.class, metaDataMap);
    }

    public operationRequestHandler_args() {
    }

    public operationRequestHandler_args(
      Request request)
    {
      this();
      this.request = request;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public operationRequestHandler_args(operationRequestHandler_args other) {
      if (other.isSetRequest()) {
        this.request = new Request(other.request);
      }
    }

    public operationRequestHandler_args deepCopy() {
      return new operationRequestHandler_args(this);
    }

    @Override
    public void clear() {
      this.request = null;
    }

    @org.apache.thrift.annotation.Nullable
    public Request getRequest() {
      return this.request;
    }

    public operationRequestHandler_args setRequest(@org.apache.thrift.annotation.Nullable Request request) {
      this.request = request;
      return this;
    }

    public void unsetRequest() {
      this.request = null;
    }

    /** Returns true if field request is set (has been assigned a value) and false otherwise */
    public boolean isSetRequest() {
      return this.request != null;
    }

    public void setRequestIsSet(boolean value) {
      if (!value) {
        this.request = null;
      }
    }

    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
      switch (field) {
      case REQUEST:
        if (value == null) {
          unsetRequest();
        } else {
          setRequest((Request)value);
        }
        break;

      }
    }

    @org.apache.thrift.annotation.Nullable
    public Object getFieldValue(_Fields field) {
      switch (field) {
      case REQUEST:
        return getRequest();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case REQUEST:
        return isSetRequest();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that instanceof operationRequestHandler_args)
        return this.equals((operationRequestHandler_args)that);
      return false;
    }

    public boolean equals(operationRequestHandler_args that) {
      if (that == null)
        return false;
      if (this == that)
        return true;

      boolean this_present_request = true && this.isSetRequest();
      boolean that_present_request = true && that.isSetRequest();
      if (this_present_request || that_present_request) {
        if (!(this_present_request && that_present_request))
          return false;
        if (!this.request.equals(that.request))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;

      hashCode = hashCode * 8191 + ((isSetRequest()) ? 131071 : 524287);
      if (isSetRequest())
        hashCode = hashCode * 8191 + request.hashCode();

      return hashCode;
    }

    @Override
    public int compareTo(operationRequestHandler_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;

      lastComparison = Boolean.compare(isSetRequest(), other.isSetRequest());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetRequest()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.request, other.request);
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
      StringBuilder sb = new StringBuilder("operationRequestHandler_args(");
      boolean first = true;

      sb.append("request:");
      if (this.request == null) {
        sb.append("null");
      } else {
        sb.append(this.request);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
      if (request != null) {
        request.validate();
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
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class operationRequestHandler_argsStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
      public operationRequestHandler_argsStandardScheme getScheme() {
        return new operationRequestHandler_argsStandardScheme();
      }
    }

    private static class operationRequestHandler_argsStandardScheme extends org.apache.thrift.scheme.StandardScheme<operationRequestHandler_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, operationRequestHandler_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // REQUEST
              if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                struct.request = new Request();
                struct.request.read(iprot);
                struct.setRequestIsSet(true);
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

      public void write(org.apache.thrift.protocol.TProtocol oprot, operationRequestHandler_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.request != null) {
          oprot.writeFieldBegin(REQUEST_FIELD_DESC);
          struct.request.write(oprot);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class operationRequestHandler_argsTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
      public operationRequestHandler_argsTupleScheme getScheme() {
        return new operationRequestHandler_argsTupleScheme();
      }
    }

    private static class operationRequestHandler_argsTupleScheme extends org.apache.thrift.scheme.TupleScheme<operationRequestHandler_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, operationRequestHandler_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
        java.util.BitSet optionals = new java.util.BitSet();
        if (struct.isSetRequest()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetRequest()) {
          struct.request.write(oprot);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, operationRequestHandler_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
        java.util.BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.request = new Request();
          struct.request.read(iprot);
          struct.setRequestIsSet(true);
        }
      }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
      return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
  }

}
