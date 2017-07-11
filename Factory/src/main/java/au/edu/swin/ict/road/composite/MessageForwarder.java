package au.edu.swin.ict.road.composite;


import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.ContractEvaluationResult;
import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.log4j.Logger;

import java.util.List;

public class MessageForwarder {
    // get the logger
    private static Logger log = Logger.getLogger(MessageDeliverer.class
            .getName());

    // @Override
    public static void forward(Role role, MessageWrapper message) {
//        if (message != null) {
//            boolean isfactMethodFound = false;
//            String msgOpName = message.getOperationName();

//            for (FactSynchroniser roleFS : role.getFactSynchronisers()) {
//                if (msgOpName.equals("add" + roleFS.getId() + "Facts")
//                        || msgOpName.equals("update" + roleFS.getId()
//                        + "Facts")) {
//                    SOAPEnvelope soapEnv = (SOAPEnvelope) message
//                            .getMessage();
//                    // log.info("SOAP msg in Msg Deliverer :: " +
//                    // soapEnv.toString());
//                    Iterator soapBodyIterator = soapEnv.getBody()
//                            .getChildElements();
//                    OMElement OperationElement = (OMElement) soapBodyIterator
//                            .next();
//                    Iterator OpBodyIteraror = OperationElement
//                            .getChildElements();
//                    OMElement factsElement = (OMElement) OpBodyIteraror
//                            .next();
//                    String facts = factsElement.toString();
//                    // log.info("facts in msg deliverer : " + facts);
//
//                    String xmlFacts = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//                    xmlFacts += "<tns:facts xmlns:tns=\"http://www.swin.edu.au/ict/road/regulator/Facts\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.swin.edu.au/ict/road/regulator/Facts JFacts.xsd \">";
//                    Iterator factsIter = factsElement.getChildElements();
//                    while (factsIter.hasNext()) {
//                        xmlFacts += ((OMElement) factsIter.next())
//                                .toString();
//                    }
//                    xmlFacts += "</tns:facts>";
//                    // log.info("xml facts ::: " + xmlFacts);
//                    JAXBContext jc;
//                    try {
//                        jc = JAXBContext
//                                .newInstance("au.edu.swin.ict.road.regulator.bindings");
//                        Unmarshaller unmarshaller = jc.createUnmarshaller();
//                        Facts factsObj = (Facts) unmarshaller
//                                .unmarshal(new StringReader(xmlFacts));
//                        List<FactObject> regulatorFactList = new ArrayList<FactObject>();
//
//                        // log.info("Number of facts :: " +
//                        // factsObj.getFact().size());
//                        for (JFactType fact : factsObj.getFact()) {
//
//                            FactObject regulatorFact = new FactObject(
//                                    fact.getName(), fact.getIdentifier()
//                                    .getIdentifierKey(), fact
//                                    .getIdentifier()
//                                    .getIdentifierValue());
//
//                            // log.info(fact.getName()
//                            // + ":"
//                            // + fact.getSource()
//                            // + ":"
//                            // + fact.getIdentifier()
//                            // .getIdentifierKey()
//                            // + ":"
//                            // + fact.getIdentifier()
//                            // .getIdentifierValue());
//
//                            JFactAttributesType attributesType = fact
//                                    .getAttributes();
//                            if (attributesType != null) {
//                                List<JFactAttributeType> attrs = attributesType
//                                        .getAttribute();
//                                for (JFactAttributeType attr : attrs) {
//                                    regulatorFact.setAttribute(
//                                            attr.getAttributeKey(),
//                                            attr.getAttributeValue());
//                                    // log.info(attr.getAttributeKey() + ":"
//                                    // + attr.getAttributeValue());
//
//                                }
//                            }
//
//                            regulatorFactList.add(regulatorFact);
//
//                        }
//
//                        // log.info("Number of REGULATOR FACTS created" +
//                        // regulatorFactList.size());
//                        roleFS.manageSetOperations(msgOpName,
//                                regulatorFactList);
//
//                    } catch (JAXBException e) {
//                        e.printStackTrace();
//                        // throw new
//                        // CompositeDemarshallingException(e.getMessage());
//                    }
//
//                    // Iterator soapBodyIterator = soapEnv.getBody()
//                    // .getChildElements();
//                    // OMElement OperationElement = (OMElement)
//                    // soapBodyIterator
//                    // .next();
//                    // Iterator OpBodyIteraror = OperationElement
//                    // .getChildElements();
//                    // OMElement paramElement = (OMElement) OpBodyIteraror
//                    // .next();
//                    // String facts = paramElement.getText();
//                    //
//                    // log.info("Sent facts are: " + facts);
//
//                    isfactMethodFound = true;
//                    break;
//                } else if (msgOpName
//                        .equals("get" + roleFS.getId() + "Fact")
//                        || msgOpName.equals("getAll" + roleFS.getId()
//                        + "Facts")) {
//                    // log.info("start of else get ");
//                    String factId = FactMsgUtil.getFactId(message);
//                    roleFS.manageGetOperations(msgOpName, factId);
//                    isfactMethodFound = true;
//                    // log.info("end of else get ");
//                    break;
//
//                } else if (msgOpName.equals("remove" + roleFS.getId()
//                        + "Fact")) {
//                    // log.info("start of else get ");
//                    String factId = FactMsgUtil.getFactId(message);
//                    roleFS.manageRemoveOperations(msgOpName, factId);
//                    isfactMethodFound = true;
//                    // log.info("end of else get ");
//                    break;
//
//                }
//            }
//            if (isfactMethodFound) {
//                isfactMethodFound = false;
//                return;
//            }
//        }// end of facts impl

        // possible timeout
//        if (message != null) {
        message.setOriginRole(role);

//            this.role.getComposite().getBenchUtil()
//                    .addBenchRecord("DROOLS BEGIN", message.getMessageId());
        if (log.isInfoEnabled()) {
            log.info("MessageDeliverer for " + role.getId()
                    + " has recieved a message of type "
                    + message.getOperationName());
        }

//            compositeRules.insertMessageReceivedAtSourceEvent(message,
//                    role.getId()); // new event

        // results in message containing destination contract.
        Contract destContract = message.getDestinationContract();
//                if (compositeRules != null) {
//                    compositeRules.insertRoutingSuccessEvent(message,
//                            role.getId(), destContract.getId()); // new
//                }

        // Process the message and get the ContractEvaluationResult
        ContractEvaluationResult mpr = destContract.processMessage(message);
        if (log.isInfoEnabled()) {
            log.info("Message " + message.getMessageId()
                    + " got interpreted via contract  "
                    + destContract.getId() + " " + message.getClientID());
        }
        // try {
        // Thread.sleep(3000);
        // } catch (InterruptedException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

//                this.role
//                        .getComposite()
//                        .getBenchUtil()
//                        .addBenchRecord("DROOLS END",
//                                message.getMessageId());

        boolean isBlocked = mpr.isBlocked();
        if (isBlocked) {
            if (log.isInfoEnabled()) {
                log.info("MessageDeliverer for " + role.getId()
                        + " is routing the message"
                        + message.getMessageId() + " back to its "
                        + "source=" + message.getOriginRoleId()
                        + ", due to it being blocked by the contract "
                        + message.getDestinationContract());
            }
            message.setError(true);
            message.setErrorMessage("Message blocked by contract "
                    + message.getDestinationContract().getId()
                    + " and routed back to origin role "
                    + message.getOriginRoleId());
            role.delivererPutOutgoingSyncMessage(message);
        } else {

            // /////Serendip 1 Start////////////////////
            // In here we perform few pre-processing before the
            // message is placed in the destination role
            List<EventRecord> interpretedEvents = mpr
                    .getAllInterprettedEvents();
//            if (interpretedEvents.size() > 0) {
//                EventRecord firstEvent = interpretedEvents.get(0);
//                String pid = firstEvent.getClassifier().getProcessInsId();
//                if ((null == pid)
//                        || (pid.equals(""))
//                        || (pid.equals("null"))) {
//                    try {
//                        pid = role.getComposite()
//                                .getSerendipEngine()
//                                .startProcessForEvent(message.getClassifier(), firstEvent);
//                        if (log.isInfoEnabled()) {
//                            log.info("Started Process Id " + pid + " " + message.getClientID());
//                        }
//                    } catch (SerendipException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//                    // allocate same pid for all other events
//                    for (EventRecord e : interpretedEvents) {
//                        e.getClassifier().setProcessInsId(pid);
//                    }
//                    // allocate the pid for the message as well.
//                    // This is important as the out transform picks
//                    // up the message via <msg_id, pid>
//                    message.setCorrelationId(pid);
//                    if (log.isInfoEnabled()) {
//                        log.info("Correlation id "
//                                + message.getCorrelationId()
//                                + " is set for message ="
//                                + message.getMessageId());
//                    }
//                    // This is important to check whether the post
//                    // ep is
//                    // Triggered. Check
//                    // au.edu.swin.ict.serendip.event.EventObserver
//                    try {
//                        message.setInterpretedByRule(true);
//                    } catch (Exception e) {
//                        log.fatal(e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            }
            // //Serendip 1 End////////////////////

            // find the destination role
            Role destRole = destContract.getOppositeRole(role);
            if (log.isInfoEnabled()) {
                log.info("MessageDeliverer for " + role.getId()
                        + " is routing the message "
                        + message.getMessageId() + " to "
                        + destRole.getId() + " via the contract"
                        + destContract.getId());
            }

//                    if (compositeRules != null)
//                        compositeRules
//                                .insertMessageReceivedAtDestinationEvent(
//                                        message, destRole.getId()); // new

//            if (!message.isResponse()) {
//                if (destRole.getRoutingTable().isResponse(
//                        message.getOperationName()))
//                    destRole.getRoutingTable()
//                            .putResponseSignature(
//                                    message.getOperationName(),
//                                    destContract);
//            }

            // place the message in the pending out buffer of the
            // destination role

            destRole.putPendingOutBufMessage(message);
            if (log.isInfoEnabled()) {
                log.info("MsgDeliverer placed the message in the pendingout of destination role "
                        + destRole.getId());
            }
//                    this.role
//                            .getComposite()
//                            .getBenchUtil()
//                            .addBenchRecord("MSG OUT",
//                                    message.getMessageId());
            // Serendip 2 start
            // Now the message is safely in the destination role
            // In here we have to let the Serendip engine know about
            // what happened after processing the message.
            // We send our events to Serendip via the role.
            // this.role.fireEvents(interpretedEvents);
            // NOTE: Due to event-message correlation requirements,
            // the Event triggering need to be done after all the
            // messages are safely placed in the destRole buffer
            for (EventRecord eventRecord : interpretedEvents) {
                if (!eventRecord.isPlaceSet()) {
                    eventRecord.setPlace(destRole.getId());
                }
            }
            if (!interpretedEvents.isEmpty()) {
                try {
                    role.fireEvents(interpretedEvents);
                } catch (SerendipException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Message " + message.getMessageId()
                            + " does not interpret any events");
                }
            }
        }
    }
}
