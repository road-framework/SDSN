package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ClassifierFactory;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.IRole;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.exceptions.MessageParsingException;
import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import au.swin.ict.research.cs3.road.road4ws.core.util.Axis2Util;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver;
import org.apache.log4j.Logger;

/**
 * Drops an asynchronous message to the roadfactory and then forget about it
 *
 * @author Malinda Kapuruge
 */
public class ROADInOnlyMessageReceiver extends RawXMLINOnlyMessageReceiver {

    private static final Logger log = Logger.getLogger(ROADInOnlyMessageReceiver.class);

    public void invokeBusinessLogic(MessageContext msgContext) throws AxisFault {
        Composite composite = null;
        IRole role = null;
        boolean msgDelivered;
        composite = (Composite) msgContext
                .getProperty(ROADConstants.ROAD4WS_CURRENT_COMPOSITE);
        role = (IRole) msgContext
                .getProperty(ROADConstants.ROAD4WS_CURRENT_ROLE);
        String opName = Axis2Util.getMappingOperation(msgContext);

        if (log.isDebugEnabled()) {
            log.debug("ROADInOnlyMessageReceiver invokeBusinessLogic for " + opName
                    + " of " + role.getId() + " in Composite: "
                    + composite.getName());
        }

        if (log.isDebugEnabled()) {
            log.debug("SOAP Action is " + msgContext.getSoapAction());
        }

        if (null == composite) {
            if (log.isDebugEnabled()) {
                log.debug("ROAD Exception: The mesg Reciever cannot get the composite from the axis2 message contxt");
            }
            throw new AxisFault(
                    "ROAD Exception: The mesg Reciever cannot get the composite from the axis2 message contxt");
        }
        if (null == role) {
            if (log.isDebugEnabled()) {
                log.debug("ROAD Exception: The mesg Reciever cannot get the Role from the axis2 message contxt");
            }
            throw new AxisFault(
                    "ROAD Exception: The mesg Reciever cannot get the Role from the axis2 message contxt");
        }
        if (null == opName) {
            if (log.isDebugEnabled()) {
                log.debug("ROAD Exception: The mesg Reciever cannot get the operation name from the axis2 message contxt");
            }
            throw new AxisFault(
                    "ROAD Exception: The mesg Reciever cannot get the operation name from the axis2 message contxt");
        }

        try {
            msgDelivered = this.dropMessageToComposite(composite, msgContext,
                    role, opName);
        } catch (MessageParsingException e) {
            log.error("Message failed to enter the composite. Exception");
            e.printStackTrace();
            throw new AxisFault(e.getMessage());
        }

        if (msgDelivered) {
            if (log.isDebugEnabled()) {
                log.debug("Message suucessfully dropped to the composite");
            }
        } else {
            log.error("Message failed to enter the composite");
        }
    }


    public boolean dropMessageToComposite(Composite compo,
                                          MessageContext msgContext, IRole role, String opName)
            throws MessageParsingException {
        SOAPEnvelope putEnvelope = null;
        String actionStr = null;
        if (null == opName) {
            log.error("Cannot get the operation name >>>");
            return false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("The operation name is " + opName);
            }
        }

        // Create a SOAP Message Wrapper
        putEnvelope = msgContext.getEnvelope();
        putEnvelope.build();

        actionStr = msgContext.getSoapAction();
        if (log.isDebugEnabled()) {
            log.debug("Dropping msg " + putEnvelope.toString()
                    + " \n to comp; Role =" + role.getName() + " -> Operation="
                    + opName);
        }
        MessageWrapper smw = new MessageWrapper(putEnvelope, opName, false);
        Classifier classifier = ClassifierFactory.createClassifier(putEnvelope);
        System.out.println(classifier);
        smw.setClassifier(classifier);
        smw.setProperty(ROADConstants.ROAD4WS_MW_SOAP_ACTION, actionStr);
        // log.debug("Role=" + role.getName() + " -> Operation=" + opName);
        smw.setOriginRole(role);
        if (log.isDebugEnabled()) {
            log.debug("Message successfully dropped.");
        }
        // Drop message to the composite
        role.putMessage(smw);

        return true;
    }
}
