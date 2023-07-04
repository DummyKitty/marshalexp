package xyz.eki.marshalexp.rmi.networking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * 完成一些高层次的RMI Registry交互操作 比如lookup
 *
 */
public class RMIRegistryEndpoint extends RMIEndpoint {
    private Registry rmiRegistry;
    private Map<String, Remote> remoteObjectCache;

    public RMIRegistryEndpoint(String host, int port) {
        super(host, port);
        this.remoteObjectCache = new HashMap<>();

        try {
            this.rmiRegistry = LocateRegistry.getRegistry(host, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public RMIRegistryEndpoint(RMIEndpoint rmi)
    {
        this(rmi.host, rmi.port);
    }

    /**
     * 获取绑定列表
     * @return 绑定列表
     */
    public String[] list() {
        String[] boundNames = null;
        try{
            boundNames = rmiRegistry.list();
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return boundNames;
    }


    /**
     * rmiRegistry的一个包装器，缓存到remoteObjectCache中
     *
     * @param boundName 要查询的绑定名称
     * @return Remote 远程对象
     */
    public Remote lookup(String boundName)
    {
        Remote remoteObject = remoteObjectCache.get(boundName);

        if( remoteObject == null ) {

            try {
                remoteObject = rmiRegistry.lookup(boundName);
                remoteObjectCache.put(boundName, remoteObject);

            } catch(Exception e ) {
                e.printStackTrace();
            }
        }

        return remoteObject;
    }

    /**
     * 批量查找
     *
     * @param boundNames
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public RemoteObjectWrapper[] packup(String[] boundNames) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
    {
        RemoteObjectWrapper[] remoteObjects = new RemoteObjectWrapper[boundNames.length];

        for(int ctr = 0; ctr < boundNames.length; ctr++) {

            Remote remoteObject = this.lookup(boundNames[ctr]);
            remoteObjects[ctr] = new RemoteObjectWrapper(remoteObject, boundNames[ctr]);
        }

        return remoteObjects;
    }


}
