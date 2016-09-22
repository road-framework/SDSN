package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.*;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;

import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

/**
 * TODO documentation
 */
public class TestMessageContextBuilder {

    public static SOAPEnvelope build(String contentString) throws Exception {
        XMLStreamReader parser = StAXUtils.createXMLStreamReader(new StringReader(contentString));
        SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultEnvelope();
        OMDocument omDoc = OMAbstractFactory.getSOAP11Factory().createOMDocument();
        omDoc.addChild(envelope);
        SOAPBody body = envelope.getBody();
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(parser);
        OMElement bodyElement = builder.getDocumentElement();
        body.addChild(bodyElement);
        return envelope;
    }
}
