package au.edu.swin.ict.road.common;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;

import java.util.ArrayList;

/**
 * TODO
 */
public class ClassifierFactory {

    public static Classifier createClassifier(SOAPEnvelope envelope) {
        SOAPHeader header = envelope.getHeader();
        Classifier classifier = new Classifier();
        if (header != null) {
            ArrayList headerBlocks = header.getHeaderBlocksWithNSURI("http://servicenetwork.com");
            if (headerBlocks != null) {
                for (Object headerBlock1 : headerBlocks) {
                    SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) headerBlock1;
                    String name = headerBlock.getLocalName();
                    if (name.equals("vsnId")) {
                        classifier.setVsnId(headerBlock.getText().trim());
                    } else if (name.equals("processId")) {
                        classifier.setProcessId(headerBlock.getText().trim());
                    } else if (name.equals("processInstanceId")) {
                        classifier.setProcessInsId(headerBlock.getText().trim());
                    }
                }
            }
        }
        return classifier;
    }
}
