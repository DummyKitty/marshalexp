package xyz.eki.marshalexp.rmi.networking;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;

import java.lang.reflect.Field;
import java.rmi.Remote;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 存一些远程对象的信息
 */
public class RemoteObjectWrapper {
    public ObjID objID;
    public String className;
    public String boundName;
    public Remote remoteObject;
    public UnicastRef remoteRef;
    public TCPEndpoint endpoint;
    public RMIClientSocketFactory csf;
    public RMIServerSocketFactory ssf;

    public List<RemoteObjectWrapper> duplicates;

    public RemoteObjectWrapper(String boundName)
    {
        this.boundName = boundName;
    }

    public RemoteObjectWrapper(Remote remoteObject, String boundName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
    {
        this.boundName = boundName;
        this.remoteObject = remoteObject;
        this.remoteRef = (UnicastRef) RemoteUtils.extractRef(remoteObject);

        LiveRef lRef = remoteRef.getLiveRef();

        Field endpointField = LiveRef.class.getDeclaredField("ep");
        endpointField.setAccessible(true);

        this.objID = lRef.getObjID();
        this.endpoint = (TCPEndpoint)endpointField.get(lRef);

        this.csf = lRef.getClientSocketFactory();
        this.ssf = lRef.getServerSocketFactory();

        this.className = RemoteUtils.getClassName(remoteObject);

        this.duplicates = new ArrayList<RemoteObjectWrapper>();
    }


    public String getHost() {return endpoint.getHost();}

    public int getPort() {return endpoint.getPort();}
}
