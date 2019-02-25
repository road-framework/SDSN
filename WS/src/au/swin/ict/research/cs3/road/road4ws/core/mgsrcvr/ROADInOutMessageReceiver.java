package au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ClassifierFactory;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.IRole;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.receivers.RawXMLINOutMessageReceiver;
import org.apache.log4j.Logger;

public class ROADInOutMessageReceiver extends RawXMLINOutMessageReceiver
        implements MessageReceiver {
    private SOAPEnvelope responseEnvelope = null;
    private MessageContext oldMsgContext = null;
    private boolean wait = false;
    private String opName = null;
    private static int counter = 0;
    private static final Logger log = Logger.getLogger(ROADInOutMessageReceiver.class.getName());

    public void invokeBusinessLogic(MessageContext msgContext,
                                    MessageContext newmsgContext) throws AxisFault {
//        long startTime = System.nanoTime();
        IRole role = null;
        try {

            this.oldMsgContext = msgContext;

            this.opName = msgContext.getOperationContext().getOperationName();

            Composite composite = (Composite) msgContext
                    .getProperty(ROADConstants.ROAD4WS_CURRENT_COMPOSITE);
            role = (IRole) msgContext
                    .getProperty(ROADConstants.ROAD4WS_CURRENT_ROLE);


            if (log.isDebugEnabled()) {
                log.debug("ROADInOutMessageReceiver invokeBusinessLogic for "
                        + opName + " of " + role.getId() + " in Composite: "
                        + composite.getName());
            }

            if (null == composite) {
                if (log.isDebugEnabled()) {
                    log.debug("ROAD4WS Exception: The mesg Reciever cannot get the composite from the axis2 message contxt");
                }
                throw new AxisFault(
                        "ROAD4WS Exception: The mesg Reciever cannot get the composite from the axis2 message contxt");
            }
            if (null == role) {
                if (log.isDebugEnabled()) {
                    log.debug("ROAD4WS Exception: The mesg Reciever cannot get the Role from the axis2 message contxt");
                }
                throw new AxisFault(
                        "ROAD4WS Exception: The mesg Reciever cannot get the Role from the axis2 message contxt");
            }

            MessageWrapper mwReturn = this.syncDrop(msgContext, role, opName);
            if (null == mwReturn) {
                throw AxisFault.makeFault(new Exception("[ROAD4WS]There is no return message"));
            }

            if (mwReturn.isError()) {
                throw AxisFault.makeFault(new Exception("[ROAD4WS]Message Processing Error. Reason:" + mwReturn.getErrorMessage()));
            }
            SOAPEnvelope returnEnvelope = (SOAPEnvelope) mwReturn.getMessage();
            returnEnvelope.build();
            newmsgContext.setEnvelope(returnEnvelope);
//            long endTime = System.nanoTime();
//            StatWriter.writeResTime("End", endTime - startTime);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Drops a synchronous message
     *
     * @param msgContext
     * @param role
     * @param opName
     * @return
     * @throws AxisFault
     */
    private MessageWrapper syncDrop(
            MessageContext msgContext, final IRole role, String opName) throws AxisFault {
        MessageWrapper mwReturn = null;
        String processGpId = (String) msgContext.getProperty(ROADConstants.ROAD4WS_PROCESS_GROUP);
        SOAPEnvelope putEnvelope = null;
        String actionStr = null;
        if (null == opName) {
            log.error("Cannot get the operation name >>>");
            return null;
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
        final MessageWrapper mwRequest = new MessageWrapper(putEnvelope, opName, false);//Create new message
        Classifier classifier = ClassifierFactory.createClassifier(putEnvelope);
        if (classifier.getProcessInsId() != null) {
            mwRequest.setClassifier(classifier);
            mwRequest.setTargetVSN(classifier.getVsnId());
            mwRequest.setCorrelationId(classifier.getProcessInsId());
            mwRequest.setOriginRole(role);
            role.putMessage(mwRequest);
            return null;
        } else {
            //smw.setProperty(ROADConstants.ROAD4WS_MW_ID, counter++);
            String clientID = msgContext.getMessageID();
            if (clientID == null || "".equals(clientID)) {
                clientID = UIDGenerator.generateURNString();
            }
            mwRequest.setClientID(clientID);
            mwRequest.setProperty(ROADConstants.ROAD4WS_MW_SOAP_ACTION, actionStr);
            // log.debug("Role=" + role.getName() + " -> Operation=" + opName);
            mwRequest.setOriginRole(role);
            mwRequest.setTargetVSN(processGpId);
            mwRequest.setOrigin(true);
            if (log.isDebugEnabled()) {
                log.debug("Message successfully dropped.");
            }
//            long startTime = System.currentTimeMillis();
            MessageWrapper response = role.putSyncMessage(mwRequest);
//            if (!response.isFault()) {
//                long endTime = System.currentTimeMillis();
//                StatWriter.writeResTime(processGpId, (endTime - startTime));
//            }
            return response;
        }
    }

    /**
     * Test method
     *
     * @param msgContext
     * @return
     * @throws AxisFault
     * @deprecated
     */
    public SOAPEnvelope createDummyEnvelope(MessageContext msgContext) throws AxisFault {
        SOAPFactory soapFac = getSOAPFactory(msgContext);
        SOAPEnvelope envelope = soapFac.getDefaultEnvelope();


        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(
                "http://road4ws.com/xsd", "tns");

        OMElement method = fac.createOMElement("Empty", omNs);
        OMElement value = fac.createOMElement("EmptyMsg", omNs);
        value.addChild(fac.createOMText(value, "0"));
        method.addChild(value);

        envelope.getBody().addChild(method);


        return envelope;
    }


    // Send a message indicating that no messages are available in the queue
    private OMElement createEmptyMsg() {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(
                "http://quickstart.samples/xsd", "tns");

        OMElement method = fac.createOMElement("NoMessage", omNs);
        OMElement value = fac.createOMElement("MesseageCount", omNs);
        value.addChild(fac.createOMText(value, "0"));
        method.addChild(value);
        return method;

    }
}
