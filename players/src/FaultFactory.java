import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.*;
import org.apache.axis2.context.MessageContext;

import javax.xml.namespace.QName;

/**
 * TODO documentation
 */
public class FaultFactory {

    public static void createFault(MessageContext inMessageContext) {
        SOAPFactory soapFactory;
        if (inMessageContext.isSOAP11()) {
            soapFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            soapFactory = OMAbstractFactory.getSOAP12Factory();
        }

        SOAPFaultCode soapFaultCode = soapFactory.createSOAPFaultCode();
        SOAPFaultValue soapFaultValue = soapFactory.createSOAPFaultValue(soapFaultCode);
        soapFaultValue.setText(new QName("http://ws.apache.org/axis2", "TestFault", "test"));

        SOAPFaultReason soapFaultReason = soapFactory.createSOAPFaultReason();
        SOAPFaultText soapFaultText = soapFactory.createSOAPFaultText(soapFaultReason);
        soapFaultText.setText("This is some FaultReason");

        SOAPFaultDetail soapFaultDetail = soapFactory.createSOAPFaultDetail();
        QName qName = new QName("http://ws.apache.org/axis2", "FaultException");
        OMElement detail = soapFactory.createOMElement(qName, soapFaultDetail);
        qName = new QName("http://ws.apache.org/axis2", "ExceptionMessage");
        Throwable e = new Exception("This is a test Exception");
        OMElement exception = soapFactory.createOMElement(qName, null);
        exception.setText(e.getMessage());
        detail.addChild(exception);

        inMessageContext.setProperty(SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME, soapFaultCode);
        inMessageContext.setProperty(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME, soapFaultReason);
        inMessageContext.setProperty(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME, soapFaultDetail);
    }
}
