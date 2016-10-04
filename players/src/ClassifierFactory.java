import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.context.MessageContext;

import java.util.ArrayList;

/**
 * TODO
 */
public class ClassifierFactory {

    public static Classifier createClassifier(SOAPEnvelope envelope){
        MessageContext cMessageContext =
                MessageContext.getCurrentMessageContext();
        SOAPEnvelope soapEnvelope = cMessageContext.getEnvelope();
        SOAPHeader header = soapEnvelope.getHeader();
        ArrayList headerBlocks = header.getHeaderBlocksWithNSURI("http://servicenetwork.com");
        Classifier classifier = new Classifier();

        for (Object headerBlock1 : headerBlocks) {

            SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) headerBlock1;
            String name = headerBlock.getLocalName();
            if (name.equals("vsnId")) {
                classifier.setVsnId(headerBlock.getText().trim());
            } else if (name.equals("processId")) {
                classifier.setProcessId(headerBlock.getText().trim());
            } else if (name.equals("processInstanceId")) {
                classifier.setProcessInstanceId(headerBlock.getText().trim());
            }

        }
        return classifier;
    }
}
