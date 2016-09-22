package au.edu.swin.ict.road.composite.message;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;

import javax.xml.namespace.QName;

/**
 * TODO documentation
 */
public class MsgHeaderUtil {
    private static OMFactory omFactory = OMAbstractFactory.getOMFactory();
    private static OMNamespace omNamespace = omFactory.createOMNamespace("http://road.org", "road");

    public static void addClassificationHeader(MessageWrapper mw, String value) {
        SOAPEnvelope envelop = (SOAPEnvelope) mw.getMessage();
        OMElement header = omFactory.createOMElement("classifier", omNamespace);
        header.setText(value.trim());
        SOAPHeader soapHeader = envelop.getHeader();
        if (soapHeader == null) {
            SOAPFactory factory;
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                    envelop.getNamespace().getNamespaceURI())) {
                factory = OMAbstractFactory.getSOAP12Factory();

            } else {
                factory =
                        OMAbstractFactory.getSOAP11Factory();
            }
            soapHeader = factory.createSOAPHeader();
            envelop.addChild(soapHeader);
        }
        soapHeader.addChild(header);
    }

    public static String getClassificationHeader(MessageWrapper mw) {
        SOAPEnvelope env = (SOAPEnvelope) mw.getMessage();
        SOAPHeader soapHeader = env.getHeader();
        if (soapHeader != null) {
            OMElement omElement =
                    soapHeader.getFirstChildWithName(
                            new QName(omNamespace.getNamespaceURI(), "classifier", omNamespace.getPrefix()));
            if (omElement != null) {
                return omElement.getText().trim();
            }
        }
        return "";
    }
}
