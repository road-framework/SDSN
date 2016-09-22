package au.edu.swin.ict.road.composite.message;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.composite.IRole;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Term;
import au.edu.swin.ict.road.composite.message.analyzer.TransformLogic;
import au.edu.swin.ict.road.xml.bindings.ResultMsgType;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.EventObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to wrap for all messages being routed through the <code>Composite</code>. Content could
 * be (but not limited to) SOAP and REST messages or management specific message types.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class MessageWrapper {
    public final static String MSG_ID_SEPERATOR = ".";
    public final static String MSG_ID_REQUEST = "Req";
    public final static String MSG_ID_RESPONSE = "Res";

    private Object message;
    private IRole originRole;
    private Contract destinationContract;
    private String destinationPlayerBinding;
    private String taskId;
    private String operationName;
    private String messageType;
    private String correlationId;//this is same as the process instance id. It is required to differentiate messages of two different process instances
    private Map<String, Object> propertiesMap;
    private boolean isResponse;
    private String msgId = null;

    public boolean isUserResponse() {
        return userResponse;
    }

    public void setUserResponse(boolean userResponse) {
        this.userResponse = userResponse;
    }

    private SyncType syncType = null;
    private boolean interpretedByRule = false;
    private boolean isFault;
    private String targetVSN;
    private boolean userResponse;

    public boolean isOrigin() {
        return origin;
    }

    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    private boolean origin = false;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    private boolean isError = false;
    private String clientID;

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    private String errorMessage = null;
    private EventObserver eventObserver = null;
    private Classifier classifier;

    public boolean isFault() {
        return isFault;
    }

    public void setFault(boolean fault) {
        isFault = fault;
    }

    public String getTargetVSN() {
        return targetVSN;
    }

    public void setTargetVSN(String targetVSN) {
        this.targetVSN = targetVSN;
    }

    public enum SyncType {
        OUT, OUTIN
    }

    public MessageWrapper() {
        message = null;
        taskId = null;
        operationName = null;
        destinationContract = null;
        originRole = null;
        isResponse = false;
        propertiesMap = new HashMap<String, Object>();
    }

    public MessageWrapper(Object message) {
        this();
        this.message = message;
    }

    /**
     * Creates a new <code>MessageWrapper</code> which will wrap
     * the specified message object and tag it with an operation name
     * identifying the messages type.
     *
     * @param message the message object to wrap.
     * @param opName  the operation name identifying the messages type.
     */
    public MessageWrapper(Object message, String opName, boolean isResponse) {
        this(message);
        operationName = opName;
        this.isResponse = isResponse;
    }

    public void setProperty(String key, Object value) {
        propertiesMap.put(key, value);
    }

    public Object getProperty(String key) {
        return propertiesMap.get(key);
    }

    public Map<String, Object> getPropertiesMap() {
        return propertiesMap;
    }


    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean isResponse) {
        this.isResponse = isResponse;
    }

    public String getCorrelationId() {
        return correlationId;

    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isInterpretedByRule() {
        return interpretedByRule;
    }

    /**
     * This MUST be called when the message is interpreted.
     *
     * @param interpretedByRule
     * @throws Exception
     * @throws Exception
     */
    public void setInterpretedByRule(boolean interpretedByRule) throws Exception {
        this.interpretedByRule = interpretedByRule;
        //we'll let our observer know
        if (null != this.eventObserver) {
            try {
                this.eventObserver.msgInterpreted();
            } catch (SerendipException e) {
                // TODO Auto-generated catch block
                throw new Exception(e.getMessage());
            }
        }
    }

    public EventObserver getEventObserver() {
        return eventObserver;
    }

    public void setEventObserver(EventObserver eventObserver) {
        this.eventObserver = eventObserver;
    }

    /**
     * Returns the <code>IRole</code> that this <code>MessageWrapper</code>
     * has originated from (the sending players <code>Role</code>).
     *
     * @return the originating <code>IRole</code>.
     */
    public IRole getOriginRole() {
        return originRole;
    }

    /**
     * Returns the unique id of the <code>IRole</code> that this <code>MessageWrapper</code>
     * has originated from (the sending players <code>Role</code>).
     *
     * @return the unique id of the originating <code>IRole</code>.
     */
    public String getOriginRoleId() {
        return originRole.getId();
    }

    /**
     * Sets the origin IRole this MessageWrapper is being sent from which will
     * allow the destination to determine the sender.
     *
     * @param originRole the <code>IRole</code> sending this <code>MessageWrapper</code>.
     */
    public void setOriginRole(IRole originRole) {
        this.originRole = originRole;
    }

    /**
     * @return
     */
    public Contract getDestinationContract() {
        return destinationContract;
    }

    /**
     * @param destinationContract
     */
    public void setDestinationContract(Contract destinationContract) {
        this.destinationContract = destinationContract;
    }


    /**
     * Returns the operation name associated with the instance of a MessageWrapper.
     * An operation name is separated from a messages content and can be
     * things such as BuyBook, or SendInvoice.
     */
    public String getOperationName() {
        return this.operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Returns the content of the message being wrapped by MessageWrapper.
     *
     * @return the message content
     */
    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDestinationPlayerBinding() {
        return destinationPlayerBinding;
    }

    public void setDestinationPlayerBinding(String destinationPlayerBinding) {
        this.destinationPlayerBinding = destinationPlayerBinding;
    }

    public String getMessageId() {
        this.msgId = this.constructMessageId();
        return this.msgId;
    }

    public void setMessageId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * message id = DestContractId.TermId.{Req/Res}
     *
     * @return
     */
    public String constructMessageId() {
        String reqOrRes = null;
        if (this.isResponse) {
            reqOrRes = MSG_ID_RESPONSE;
        } else {
            reqOrRes = MSG_ID_REQUEST;
        }
        // Added condition to handle fact messages
        if (this.destinationContract != null) {
            return this.destinationContract.getId() + MSG_ID_SEPERATOR + this.operationName + MSG_ID_SEPERATOR + reqOrRes;
        } else {
            return this.operationName + MSG_ID_SEPERATOR + reqOrRes;
        }
    }

    /**
     * Sets the syncType of the MessageWrapper
     *
     * @param syncType
     */
    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }

    /**
     * Returns the syncType of the MessageWrapper
     *
     * @return
     */
    public SyncType getSyncType() {
        return syncType;
    }

    //TODO INDIKA
    public MessageWrapper cloneMessageWrapper(Role role, TransformLogic tLogic) {

        MessageWrapper aMessageWrapper = new MessageWrapper();

        aMessageWrapper.setMessage(message);// TODO Indika
        aMessageWrapper.setOriginRole(role);

        String contractId = tLogic.getContractId();
        String interactiveTermId = tLogic.getInteractionId();
        boolean isResponse = tLogic.isResponse();

        // get the destination contract details using the contract id
        // and set it to the result message wrapper
        Contract contract = role.getComposite().getContractMap().get(contractId);
        aMessageWrapper.setDestinationContract(contract);
        // get the operation name using the termId and set it to the
        // result message wrapper
        Term term = contract.getTermById(interactiveTermId);
        if (null == term) {
            throw new RuntimeException("Cannot find term " + interactiveTermId + " in cotract " + contract.getId());
        }
        aMessageWrapper.setOperationName(term.getOperation().getName());
        // set the response flag
        aMessageWrapper.setResponse(isResponse);
        // create the message id and set it to the result message
        // wrapper
//        String msgId = contractId + MSG_ID_SEPERATOR
//                + interactiveTermId + MSG_ID_SEPERATOR;
//        msgId += isResponse ? MSG_ID_RESPONSE : MSG_ID_REQUEST;
        aMessageWrapper.setMessageId(tLogic.getTargetMsgId());

        //Set the correlation id that of the message subjected to disjunct

        aMessageWrapper.setCorrelationId(getCorrelationId());
        aMessageWrapper.setClassifier(getClassifier());
        aMessageWrapper.setClientID(getClientID());
        return aMessageWrapper;
    }

    public MessageWrapper cloneMessageWrapper(Role role, ResultMsgType resultMsg) {

        MessageWrapper aMessageWrapper = new MessageWrapper();

        aMessageWrapper.setMessage(message);// TODO Indika
        aMessageWrapper.setOriginRole(role);

        String contractId = resultMsg.getContractId();
        String interactiveTermId = resultMsg.getTermId();
        boolean isResponse = resultMsg.isIsResponse();

        // get the destination contract details using the contract id
        // and set it to the result message wrapper
        Contract contract = role.getComposite().getContractMap().get(contractId);
        aMessageWrapper.setDestinationContract(contract);
        // get the operation name using the termId and set it to the
        // result message wrapper
        Term term = contract.getTermById(interactiveTermId);
        if (null == term) {
            throw new RuntimeException("Cannot find term " + interactiveTermId + " in cotract " + contract.getId());
        }
        aMessageWrapper.setOperationName(term.getOperation().getName());
        // set the response flag
        aMessageWrapper.setResponse(isResponse);
        // create the message id and set it to the result message
        // wrapper
        String msgId = contractId + MSG_ID_SEPERATOR
                       + interactiveTermId + MSG_ID_SEPERATOR;
        msgId += isResponse ? MSG_ID_RESPONSE : MSG_ID_REQUEST;
        aMessageWrapper.setMessageId(msgId);

        //Set the correlation id that of the message subjected to disjunct

        aMessageWrapper.setCorrelationId(getCorrelationId());
        aMessageWrapper.setClassifier(getClassifier());
        aMessageWrapper.setClientID(getClientID());
        return aMessageWrapper;
    }

    public MessageWrapper cloneMessageWrapper(Role role, String contractId, String interactiveTermId) {

        MessageWrapper aMessageWrapper = new MessageWrapper();

        aMessageWrapper.setMessage(message);// TODO Indika Clone
        aMessageWrapper.setOriginRole(role);
        // get the destination contract details using the contract id
        // and set it to the result message wrapper
        Contract contract = role.getComposite().getContractMap().get(contractId);
        aMessageWrapper.setDestinationContract(contract);
        // get the operation name using the termId and set it to the
        // result message wrapper
        Term term = contract.getTermById(interactiveTermId);
        if (null == term) {
            throw new RuntimeException("Cannot find term " + interactiveTermId + " in cotract " + contract.getId());
        }
        aMessageWrapper.setOperationName(term.getOperation().getName());
        // set the response flag
        aMessageWrapper.setResponse(false);
        // create the message id and set it to the result message
        // wrapper
        String msgId = contractId + MSG_ID_SEPERATOR
                       + interactiveTermId + MSG_ID_SEPERATOR;
        msgId += isResponse ? MSG_ID_RESPONSE : MSG_ID_REQUEST;
        aMessageWrapper.setMessageId(msgId);

        //Set the correlation id that of the message subjected to disjunct

        aMessageWrapper.setCorrelationId(getCorrelationId());
        aMessageWrapper.setClassifier(getClassifier());
        aMessageWrapper.setClientID(getClientID());
        return aMessageWrapper;
    }
}
