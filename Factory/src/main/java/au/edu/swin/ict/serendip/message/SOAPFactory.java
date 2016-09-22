package au.edu.swin.ict.serendip.message;

import au.edu.swin.ict.road.common.JaxbFactory;
import au.edu.swin.ict.road.common.StateRecord;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.*;

import javax.xml.stream.XMLStreamException;

/**
 * TODO documentation
 */
public class SOAPFactory {

    public static SOAPEnvelope createFaultEnvelope(SOAPEnvelope envelope, String reasonString) {

        boolean isSOAP11 = true;
        String soapNamespaceURI = envelope.getNamespace().getNamespaceURI();

        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI
                .equals(soapNamespaceURI)) {
            isSOAP11 = false;
        }

        org.apache.axiom.soap.SOAPFactory factory;
        if (isSOAP11) {
            factory = OMAbstractFactory.getSOAP11Factory();
        } else {
            factory = OMAbstractFactory.getSOAP12Factory();
        }
        // create the SOAP fault document and envelope
        OMDocument soapFaultDocument = factory.createOMDocument();
        SOAPEnvelope faultEnvelope = factory.getDefaultFaultEnvelope();
        soapFaultDocument.addChild(faultEnvelope);

        // create the fault element  if it is need
        SOAPFault fault = faultEnvelope.getBody().getFault();
        if (fault == null) {
            fault = factory.createSOAPFault();
        }
        SOAPFaultReason reason = factory.createSOAPFaultReason();
        SOAPFaultText text = factory.createSOAPFaultText();
        text.setText(reasonString);
        if (isSOAP11) {
            reason.setText(reasonString);
        } else {
            reason.addSOAPText(text);
        }
        fault.setReason(reason);
        return faultEnvelope;
    }

    public static SOAPEnvelope createSOAPEnvelope(String soapVersion, String namespace, StateRecord stateRecord, String opName) {
        boolean isSOAP11 = true;
        if ("SOAP12".equals(soapVersion)) {
            isSOAP11 = false;
        }
        org.apache.axiom.soap.SOAPFactory factory;
        if (isSOAP11) {
            factory = OMAbstractFactory.getSOAP11Factory();
        } else {
            factory = OMAbstractFactory.getSOAP12Factory();
        }
        OMDocument omDocumentt = factory.createOMDocument();
        SOAPEnvelope defaultEnvelope = factory.getDefaultEnvelope();
        omDocumentt.addChild(defaultEnvelope);
        Class[] classes = new Class[]{StateRecord.class, stateRecord.getStateInstance().getClass()};
        OMNamespace ns = factory.createOMNamespace(namespace, "snns");
        OMElement operationElement = factory.createOMElement(opName, ns);
        try {
            OMElement record = AXIOMUtil.stringToOM(JaxbFactory.toXml(stateRecord, classes));
            operationElement.addChild(record);
            defaultEnvelope.getBody().addChild(operationElement);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return defaultEnvelope;
    }
}
