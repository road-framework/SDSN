package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.classbuilder.ROADClassBuilder;
import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Operation;
import au.edu.swin.ict.road.composite.contract.Term;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.RoleDescriptionGenerationException;
import au.edu.swin.ict.road.composite.flowcontrol.FlowControlPolicy;
import au.edu.swin.ict.road.composite.flowcontrol.FlowControlTable;
import au.edu.swin.ict.road.composite.listeners.RolePushMessageListener;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.analyzer.TransformLogic;
import au.edu.swin.ict.road.composite.message.analyzer.XSLTAnalyzer;
import au.edu.swin.ict.road.composite.message.containers.MessageBuffer;
import au.edu.swin.ict.road.composite.message.containers.MessageBuffer.BufferType;
import au.edu.swin.ict.road.composite.message.containers.QueueListener;
import au.edu.swin.ict.road.composite.message.exceptions.MessageException;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRegTable;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRuleExecutionEvent;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationRegTable;
import au.edu.swin.ict.road.composite.routing.OutRouterPort;
import au.edu.swin.ict.road.composite.routing.RoutingPolicy;
import au.edu.swin.ict.road.composite.routing.RoutingTable;
import au.edu.swin.ict.road.composite.routing.exceptions.MessageRoutingException;
import au.edu.swin.ict.road.composite.routing.exceptions.MessageRoutingUndeterminedException;
import au.edu.swin.ict.road.composite.routing.exceptions.NoRequestException;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsMonitoringRules;
import au.edu.swin.ict.road.composite.rules.events.composite.MessageReceivedAtOutPortEvent;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.composite.utills.ROADProperties;
import au.edu.swin.ict.road.composite.utills.ResponseCallback;
import au.edu.swin.ict.road.composite.utills.ResponseFuture;
import au.edu.swin.ict.road.regulator.FactSynchroniser;
import au.edu.swin.ict.road.regulator.FactTupleSpaceRow;
import au.edu.swin.ict.road.regulator.RoleMonitor;
import au.edu.swin.ict.road.xml.bindings.*;
import au.edu.swin.ict.road.xml.bindings.ParamsType.Parameter;
import au.edu.swin.ict.road.xml.bindings.RoleType.LinkedFacts;
import au.edu.swin.ict.serendip.core.ProcessDefinition;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.core.VSNDefinition;
import au.edu.swin.ict.serendip.event.EventEngineSubscriber;
import au.edu.swin.ict.serendip.event.TaskPerformAction;
import au.edu.swin.ict.serendip.message.SOAPFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <code>Role</code> is a functional role implementation which allows a player
 * to interact with a composite. A <code>Role</code> is conceptually an
 * aggregation of linked contracts. A role may be in different states relating
 * to player bindings.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class Role implements IRole, EventEngineSubscriber {

    public static final int DEFAULT_MEX_TIMEOUT = 20 * 60 * 1000;
    public static final String MEX_TIMEOUT = "maxTimeout";
    // get the logger
    private static Logger log = Logger.getLogger(Role.class.getName());
    private final RoleManagementState mgtState;
    private String rId;
    private String id;
    private String name;
    private String description;
    private boolean bound; // is a player bound or not
    private List<Contract> contractList; // all the contracts this role is bound
    // to
    private Map<String, RolePushMessageListener> pushListeners;
    //    private QueueListener inQueueListener;
//    private QueueListener pendingOutBufListener;
    private LinkedBlockingQueue<MessageWrapper> outQueue;
    private LinkedBlockingQueue<MessageWrapper> inQueue;
    private LinkedBlockingQueue<MessageWrapper> outManagementQueue;
//    private LinkedBlockingQueue<MessageWrapper> outPushQueue;
//    private LinkedBlockingQueue<MessageWrapper> outSyncQueue;

    private MessageBuffer pendingOutBuf;
//    private LinkedBlockingQueue<MessageWrapper> routerQueue;      //TODO this needs to be added

    private OrganiserRole organiserRole;
    private Composite composite;
    private String playerBinding;
    private RoleType roleType;
    // new routing table
    private RoutingTable routingTable;
    // Fact Synchroniser
    private List<FactSynchroniser> factSynchronisers;
    //    private Map<String, AsyncResponse<MessageWrapper>> callbacks =
//            new ConcurrentHashMap<String, AsyncResponse<MessageWrapper>>();
    private Map<String, ResponseCallback> callbacks =
            new ConcurrentHashMap<String, ResponseCallback>();
    private ROADProperties roadProperties = ROADProperties.getInstance();
    private int maxTimeout = DEFAULT_MEX_TIMEOUT;
    private QueueListener scheduler;
    private RoleMonitor roleMonitor;
    private FlowControlTable ingressFlowControlTable = new FlowControlTable();
    private FlowControlTable egressFlowControlTable = new FlowControlTable();
    private SynchronizationRegTable synchronizationRegTable;
    private RoutingRegTable routingRegTable;
    private String rulesDir;
    private SynchronizationKnowledgebase synRules;
    private RoutingKnowledgebase routingRules;

    /**
     * Default constructor which initialises all values to null.
     */
    public Role(String rulesDir) {
        id = null;
        name = null;
        description = null;
        bound = false;
        contractList = new ArrayList<Contract>();
        pushListeners = new HashMap<String, RolePushMessageListener>();
        inQueue = new LinkedBlockingQueue<MessageWrapper>();
        outQueue = new LinkedBlockingQueue<MessageWrapper>();
        outManagementQueue = new LinkedBlockingQueue<MessageWrapper>();
//        outPushQueue = new LinkedBlockingQueue<MessageWrapper>();
//        outSyncQueue = new LinkedBlockingQueue<MessageWrapper>();

        pendingOutBuf = new MessageBuffer(this, BufferType.PENDINGOUT);
//        routerQueue = new LinkedBlockingQueue<MessageWrapper>();

        playerBinding = null;
        this.rulesDir = rulesDir;

//        inQueueListener = new InTransform(this);
//        pendingOutBufListener = new OutTransform(this);
        routingTable = new RoutingTable(this);
        maxTimeout = Integer.parseInt(roadProperties.getProperty(MEX_TIMEOUT,
                String.valueOf(DEFAULT_MEX_TIMEOUT)));
        synchronizationRegTable = new SynchronizationRegTable(id);
        routingRegTable = new RoutingRegTable(id);
        synRules = new SynchronizationKnowledgebase(this, rulesDir);
        routingRules = new RoutingKnowledgebase(rulesDir, this);
        mgtState = new RoleManagementState(id);
//        MBeanRegistrar registrar = MBeanRegistrar.getInstance();
//        registrar.registerMBean(new RoleView(this), name + "View", name + "View");
    }

    /**
     * Initialises a <code>Role</code> using the supplied <code>RoleType</code>,
     * which is a JAXB binding class containing properties from an XML file.
     *
     * @param roleBinding the JAXB binding.
     */
    public Role(RoleType roleBinding, String rulesDir) {
        this.roleType = roleBinding;
        this.rulesDir = rulesDir;
        id = null;
        name = null;
        description = null;
        bound = false;
        contractList = new ArrayList<Contract>();
        pushListeners = new HashMap<String, RolePushMessageListener>();
        inQueue = new LinkedBlockingQueue<MessageWrapper>();
        outQueue = new LinkedBlockingQueue<MessageWrapper>();
        outManagementQueue = new LinkedBlockingQueue<MessageWrapper>();
//        outPushQueue = new LinkedBlockingQueue<MessageWrapper>();
//        outSyncQueue = new LinkedBlockingQueue<MessageWrapper>();

//        routerQueue = new LinkedBlockingQueue<MessageWrapper>();

//        inQueueListener = new InTransform(this);
//        pendingOutBufListener = new OutTransform(this);
        routingTable = new RoutingTable(this);
        factSynchronisers = new ArrayList<FactSynchroniser>();
        synRules = new SynchronizationKnowledgebase(this, rulesDir);
        synchronizationRegTable = new SynchronizationRegTable(id);
        routingRegTable = new RoutingRegTable(id);
        routingRules = new RoutingKnowledgebase(rulesDir, this);
        // extract the data out of the JAXB object
        extractData(roleBinding);
        pendingOutBuf = new MessageBuffer(this, BufferType.PENDINGOUT);
        maxTimeout = Integer.parseInt(roadProperties.getProperty(MEX_TIMEOUT,
                String.valueOf(DEFAULT_MEX_TIMEOUT)));
        mgtState = new RoleManagementState(id);
//        MBeanRegistrar registrar = MBeanRegistrar.getInstance();
//        registrar.registerMBean(new RoleView(this), name + "View", name + "View");
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public MessageBuffer getPendingOutBuf() {
        return pendingOutBuf;
    }

    public RoleMonitor getRoleMonitor() {
        return roleMonitor;
    }

    @Override
    public void setRoleMonitor(MonitorType monitorType) {
        if (monitorType != null) {
            String monitorId = monitorType.getId();
            if (monitorId == null || monitorId.isEmpty()) {
                monitorId = id + "." + "monitor";
                monitorType.setId(monitorId);
            }
            AnalysisType analysisType = monitorType.getAnalysis();
            String monitorFileName = analysisType.getScript();
            IMonitoringRules iMonitoringRules = new DroolsMonitoringRules(monitorFileName.toLowerCase(), composite.getRulesDir(), composite.getFTS());
            roleMonitor = new RoleMonitor(monitorId, iMonitoringRules);
        }
    }

    public QueueListener getScheduler() {
        return scheduler;
    }

    public void setScheduler(QueueListener scheduler) {
        this.scheduler = scheduler;
    }

    public FlowControlTable getIngressFlowControlTable() {
        return ingressFlowControlTable;
    }

    @Override
    public void removeRoleMonitor() {
        roleType.setMonitor(null);
        roleMonitor = null;
    }

    @Override
    public void setMonitorRules(String monitorFileName) {
        roleType.getMonitor().getAnalysis().setScript(monitorFileName);
        roleMonitor.setMonitoringRules(new DroolsMonitoringRules(monitorFileName.toLowerCase(),
                composite.getRulesDir(), composite.getFTS()));
    }

    public void printOutQ() {
        if (log.isDebugEnabled()) {
            log.debug("Start");
        }
        for (MessageWrapper m : this.outQueue) {
            if (log.isDebugEnabled()) {
                log.debug(">" + m.getOperationName());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("End");
        }
    }

    public void registerNewPushListener(RolePushMessageListener listener) {
        if (!pushListeners.containsKey(listener.getId())) {
            pushListeners.put(listener.getId(), listener);
        }
    }

    /**
     * Gets the <code>Composite</code> that this role is a part of.
     *
     * @return the roles <code>Composite</code>.
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Sets the <code>Composite</code> that this role is a part of.
     *
     * @param composite the roles <code>Composite</code>.
     */
    public void setComposite(Composite composite) {
        this.composite = composite;
//        this.pendingOutBuf.setExecutor(composite.getPendingOutBufferWorkers());
        this.organiserRole = (OrganiserRole) composite.getOrganiserRole();
        MonitorType monitorType = roleType.getMonitor();
        setRoleMonitor(monitorType);
    }

    /**
     * Allows a player to send a message (in a <code>MessageWrapper</code>) to
     * the <code>Composite</code> via this functional <code>Role</code> for
     * contract processing and eventual delivery to its destination role.
     *
     * @param message the <code>MessageWrapper</code> to pass to the Composite.
     */
    public void putMessage(MessageWrapper message) {
        // TODO make routing decisions after InQueue
        //BENCH MSP OK.
//        this.composite.getBenchUtil().addBenchRecord("MSG-IN ", message.getMessageId());
        //   inQueue.add(message);
        Classifier classifier = message.getClassifier();
        VSNDefinition vsnDefinition = composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
        RoleServiceMessage roleServiceMessage = new RoleServiceMessage(message, this.id);
        String vsnInstanceId = classifier.getProcessInsId();
        if (processDefinition.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
            roleServiceMessage.setState(RoleServiceMessage.STATE_FORWARDABLE);
        } else if (processDefinition.getMgtState().getState().equals(ManagementState.STATE_PASSIVE)) {
            ProcessInstance processInstance = processDefinition.getProcessInstance(vsnInstanceId);
            if (processInstance.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                roleServiceMessage.setState(RoleServiceMessage.STATE_FORWARDABLE);
                processDefinition.includeVSInstance(vsnInstanceId);
            } else {
                System.out.println(" " + message.getMessageId() + " " + vsnInstanceId);
                roleServiceMessage.setState(RoleServiceMessage.STATE_ROUTED);
            }
        }
        executeRoutingRules(new RoutingRuleExecutionEvent(roleServiceMessage,
                        vsnDefinition.getMgtState(),
                        (ProcessManagementState) processDefinition.getMgtState()),
                classifier.getVsnId() + "_" + classifier.getProcessId());
//        inQueueListener.messageReceived(message);

    }

    private void newHandlePlayerMessage(MessageWrapper message) {
        RoleServiceMessage roleServiceMessage = new RoleServiceMessage(message, this.id);
        Classifier classifier = message.getClassifier();
        if (classifier == null || classifier.getVsnId() == null) {
            executeRoutingRules(new RoutingRuleExecutionEvent(roleServiceMessage), "network");
        }
        classifier = message.getClassifier();
        VSNDefinition vsnDefinition = composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        if (classifier.getProcessId() == null) {
            executeRoutingRules(new RoutingRuleExecutionEvent(roleServiceMessage, vsnDefinition.getMgtState()), classifier.getVsnId());
        }
        if (classifier.getProcessId() != null) {
            ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
            executeRoutingRules(new RoutingRuleExecutionEvent(roleServiceMessage, vsnDefinition.getMgtState(), (ProcessManagementState) processDefinition.getMgtState()),
                    classifier.getVsnId() + "_" + classifier.getProcessId());
            classifier = message.getClassifier();
            if (classifier.getProcessInsId() == null && processDefinition.getMgtState().getState().equals(ManagementState.STATE_PASSIVE) && roleServiceMessage.getState().equals(RoleServiceMessage.STATE_ROUTED)) {
                executeRoutingRules(new RoutingRuleExecutionEvent(roleServiceMessage, vsnDefinition.getMgtState(), (ProcessManagementState) processDefinition.getMgtState()),
                        classifier.getVsnId() + "_" + classifier.getProcessId());
            }
        }
    }

    public void executeRoutingRules(RoutingRuleExecutionEvent event, String key) {
        Classifier classifier = event.getRoleServiceMessage().getClassifier();
        String vsnInstanceId = null;
        if (classifier != null) {
            vsnInstanceId = event.getRoleServiceMessage().getClassifier().getProcessInsId();
        }
        List<RegulationUnitKey> vsnRegTableEntry = routingRegTable.getVSNTableEntry(key);
        if (vsnRegTableEntry != null) {
            List<RegulationRuleSet> ruleSets = new ArrayList<RegulationRuleSet>();

            for (RegulationUnitKey regUnitId : vsnRegTableEntry) {
                RegulationRuleSet regulationRuleSet = routingRegTable.getRegulationRuleSet(regUnitId.getUnitId());
                if (regulationRuleSet == null) {
                    log.error("No regulation rule set for : " + regUnitId.getUnitId() + " at : " + getId());
                    continue;
                }
                if (vsnInstanceId != null) {
                    if (regUnitId.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)
                            && !regUnitId.isExcluded(vsnInstanceId)) {
                        ruleSets.add(regulationRuleSet);
                    } else if (regUnitId.getMgtState().getState().equals(ManagementState.STATE_PASSIVE)
                            && regUnitId.isIncluded(vsnInstanceId)) {
                        ruleSets.add(regulationRuleSet);
                    }
                } else {
                    ruleSets.add(regulationRuleSet);
                }
            }
            if (!ruleSets.isEmpty()) {
                try {
                    event.setAgendaFilter(new NameBasedAgendaFilter(ruleSets));
                    routingRules.insertEvent(event);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error(ex.getMessage());
                }
            } else {
                log.error("No regulation rule set at role : " + getId() + " for key" + key);
            }
        }
    }

    private void handlePlayerMessage2(MessageWrapper message) {
        // 1) Classify
        // 2) Global admission controller
        // 3) Select Route
        // 3) Schedule
        try {
            FlowControlResult flowControlResult = applyIngressFlowControl(message);
            if (flowControlResult.getState() == FlowControlResult.DENIED) {
                sendASOAPFault(message, flowControlResult.getMessage());
            } else if (flowControlResult.getState() == FlowControlResult.SENDTOCONTROLLER) {
                // TODO queue message and call controller
            } else {
                List<OutRouterPort> outRouterPorts = applyRouting(message);
                if (outRouterPorts.isEmpty()) {
                    if (roleType.isEntryRole()) {
                        sendASOAPFault(message, "No alternative routes");
                    }
                    return;
                }
                applyEgressFlowControl(message, outRouterPorts);
            }
        } catch (RulesException e) {
            log.error("Error executing rules on message : " + message.getMessageId() + "," + e.getMessage(), e);
        }
    }

    private FlowControlResult applyIngressFlowControl(MessageWrapper message) throws RulesException {
        Classifier classifier = message.getClassifier();
        String gpId = classifier.getVsnId();
        composite.getServiceNetworkState().getVsnState(gpId);
        String flowControlKey = classifier.getVsnId() + "." + id + "." + message.getOperationName();
        FlowControlPolicy flowControlPolicy = ingressFlowControlTable.getFlowControlPolicy(flowControlKey);
        if (flowControlPolicy == null) {
            flowControlKey = flowControlKey + (message.isResponse() ? "Response" : "Request");
        }
        flowControlPolicy = ingressFlowControlTable.getFlowControlPolicy(flowControlKey);
        if (flowControlPolicy == null) {
            return new FlowControlResult(FlowControlResult.ALLOWED);
        }
        return flowControlPolicy.apply(new RoleServiceMessage(message, id));
    }

    // Per interaction - outport independently from the slices
    private void applyEgressFlowControl(MessageWrapper message, List<OutRouterPort> outRouterPorts) throws RulesException {

        for (OutRouterPort port : outRouterPorts) {
            String flowControlKey = port.getDesContract() + "." + port.getInteraction();
            FlowControlPolicy flowControlPolicy =
                    egressFlowControlTable.getFlowControlPolicy(flowControlKey);
            ResultMsgType resultType = port.getOutInterface();
            MessageWrapper toBeScheduled;
            if (resultType != null) {
                TransformLogic tLogic = new TransformLogic();
                tLogic.setRole(this);
                tLogic.setResultMsgType(resultType);
                toBeScheduled = XSLTAnalyzer.getInstance().transform(message, tLogic);
            } else {
                //TODO merge two clone functions
                toBeScheduled = message.cloneMessageWrapper(this, port.getDesContract(), port.getInteraction());
            }
            flowControlPolicy.apply(new MessageReceivedAtOutPortEvent(this, port, toBeScheduled));
        }
    }

    private List<OutRouterPort> applyRouting(MessageWrapper message) throws RulesException {
        Classifier classifier = message.getClassifier();
        String routingKey = classifier.getVsnId() + "." + id + "." + message.getOperationName();
        RoutingPolicy routingPolicy = routingTable.getRoutingPolicy(routingKey);
        if (routingPolicy == null) {
            routingKey = routingKey + (message.isResponse() ? "Response" : "Request");
        }
        routingPolicy = routingTable.getRoutingPolicy(routingKey);
        if (routingPolicy == null) {
            log.warn("Cannot find a routing policy for the key : " + routingKey);
            return new ArrayList<OutRouterPort>();
        }
        List<OutRouterPort> outRouterPorts = routingPolicy.apply(message).getOutRouterPorts();
        if (outRouterPorts.isEmpty()) {
            return outRouterPorts;
        }
        // create a process instance for the selected alternative  -
        // the selection of a process definition is a routing decision - not just CoS match
        String pid = message.getClassifier().getProcessInsId();
        if (pid == null || "".equals(pid)) {
            String route = classifier.getProcessId();
            try {
                ProcessInstance pi = composite.getSerendipEngine().startProcessInstanceV2(classifier, route);
                classifier.setProcessInsId(pi.getId());
                message.setCorrelationId(pi.getId());
                //TODO verify COS condition
            } catch (SerendipException e) {
                log.error("Error creating a process instance for the message " +
                        message.getMessageId() + "," + e.getMessage(), e);
            }
        }
        return outRouterPorts;
    }

//    private void forward(MessageWrapper message, List<OutRouterPort> outRouterPorts) {
//
//        for (OutRouterPort port : outRouterPorts) {
//            ResultMsgType resultType = port.getOutInterface();
//            MessageWrapper toBeScheduled;
//            if (resultType != null) {
//                TransformLogic tLogic = new TransformLogic();
//                tLogic.setRole(this);
//                tLogic.setResultMsgType(resultType);
//                toBeScheduled = XSLTAnalyzer.getInstance().transform(message, tLogic);
//            } else {
//                //TODO merge two clone functions
//                toBeScheduled = message.cloneMessageWrapper(this, port.getDesContract(), port.getInteraction());
//            }
//            scheduler.messageReceived(toBeScheduled);
//        }
//    }

    public MessageWrapper consumeVSN(MessageWrapper message) {

        String clientID = message.getClientID();
        ResponseCallback responseCallback = new ResponseCallback();
        callbacks.put(clientID, responseCallback);
        newHandlePlayerMessage(message);
        try {
            return (MessageWrapper) new ResponseFuture(responseCallback).get(maxTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn(name + " " + e.getMessage());
        } catch (ExecutionException e) {
            log.warn(name + " " + e.getMessage());
        } catch (TimeoutException e) {
            log.warn(name + " " + e.getMessage());
        }
        return null;
    }

    public MessageWrapper putSyncMessage(MessageWrapper message) {
        MessageWrapper result = consumeVSN(message);
        //TODO Indika - terminate when all tasks are completed or from an explict condition
//        if (message.getClassifier().getProcessInsId() != null) {
//            ProcessInstance processInstance =
//                    composite.getSerendipEngine().getProcessInstance(message.getClassifier());
//            processInstance.markForTermination();
//        }
        callbacks.remove(message.getClientID());
        return result;
    }

    public MessageWrapper getInQueueMessage() {

        return inQueue.poll();
    }

    /**
     * Allows a player to get incoming messages (in message wrappers) from this
     * functional roles message queue. Blocks if there are are currently no
     * messages.
     *
     * @return the new MessageWrapper to receive.
     */
    public MessageWrapper getNextMessage() {
        try {
            return outQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * @param timeout
     * @param unit
     * @return the Message Wrapper
     */
    public MessageWrapper getNextMessage(long timeout, TimeUnit unit) {
        try {
            return outQueue.poll(timeout, unit);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public MessageWrapper peekNextMessage() {
        return outQueue.peek();
    }

    /**
     * @return MessageWrapper
     */
    public MessageWrapper getNextPushMessage() {
//        try {
//            return outPushQueue.take();
        throw new RuntimeException("Not supported");
//        } catch (InterruptedException e) {
//            return null;
//        }
    }

    public MessageWrapper peekNextPushMessage() {
        throw new RuntimeException("Not supported");
//        return outPushQueue.peek();
    }

    /**
     * @param timeout
     * @param unit
     * @return MessageWrapper which contains the push message
     */
    public MessageWrapper getNextPushMessage(long timeout, TimeUnit unit) {
        throw new RuntimeException("Not supported");
//        try {
//            return outPushQueue.poll(timeout, unit);
//        } catch (InterruptedException e) {
//            return null;
//        }
    }

    /**
     * Allows the InQueue listener to placed the messages directly from the
     * inQueue or after transformation into the router queue which will then be
     * processed by the contract and delivered to the destination player.
     *
     * @param message
     */
    public void putSchedulerQueueMessage(MessageWrapper message) {
        scheduler.messageReceived(message);
//        routerQueue.add(message);
    }

    /**
     * Allows the InQueue listener to placed the messages directly from the
     * inQueue or after transformation into the router queue which will then be
     * processed by the contract and delivered to the destination player.
     *
     * @param messages
     */
    public void putAllSchedulerQueueMessages(List<MessageWrapper> messages) {
//        routerQueue.addAll(messages);
        for (MessageWrapper message : messages) {
            scheduler.messageReceived(message);
        }
        messages.clear();
    }

    /**
     * Allows the message deliverer to place a message in the pending out buffer
     *
     * @param message
     */
    public void putPendingOutBufMessage(MessageWrapper message) {
        try {
            pendingOutBuf.dropMessage(message);
//            pendingOutBufListener.messageReceived(message);
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to return a message from the pending out buffer and
     * remove it
     *
     * @param msgId
     * @param correlationId
     * @return
     */
    public MessageWrapper pollPendingOutBufMessage(String msgId,
                                                   String correlationId) {
        try {
            MessageWrapper resultMsg = pendingOutBuf.getAndRemoveMessage(msgId, correlationId);
            if (resultMsg == null) {
                if (log.isInfoEnabled()) {
                    log.warn("No message found for : correlation ID " + correlationId + " message ID  :" + msgId);
                }
            }
            return resultMsg;

        } catch (Exception e) {
            throw null;
        }
    }

//	public MessageWrapper pollPendingOutBufMessage(String msgId,
//			String correlationId) {
//		MessageWrapper resultMsg = null;
//		try {
//			resultMsg = pendingOutBuf.getMessage(msgId, correlationId);
//			if (resultMsg != null)
//				pendingOutBuf.removeMessage(msgId, correlationId);
//		} catch (MessageException e) {
//			e.printStackTrace();
//		}
//		return resultMsg;
//	}

    /**
     * This method is used to return a message from the pending out buffer. It
     * does not remove the message from the buffer.
     *
     * @param msgId
     * @param correlationId
     * @return
     */
    public MessageWrapper peekPendingOutBufMessage(String msgId,
                                                   String correlationId) {
        MessageWrapper resultMsg = null;
        try {
            resultMsg = pendingOutBuf.getMessage(msgId, correlationId);
        } catch (MessageException e) {
            e.printStackTrace();
        }
        return resultMsg;
    }

    /**
     * Used to remove a message from the pending out buffer.
     *
     * @param msgId
     * @param correlationId
     */
    public void removePendingOutBufMessage(String msgId, String correlationId) {
        try {
            pendingOutBuf.removeMessage(msgId, correlationId);
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }

    public void removePendingOutBufMessage(MessageWrapper messageWrapper) {
        try {
            pendingOutBuf.removeMessage(messageWrapper);
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns al pending out buffer messages
     *
     * @return
     */
    public Iterator<MessageWrapper> getAllPendingOutBufMessages() {
        return pendingOutBuf.getAllMessages();
    }

    /**
     * Allows a player to send a management related message (in a
     * <code>MessageWrapper</code>) to the <code>Composite</code> organiser via
     * this functional <code>Role</code>. This functional <code>Role</code> will
     * be marked as the source of the management message.
     *
     * @param message the <code>MessageWrapper</code> to pass to the organiser.
     */
    public void putManagementMessage(MessageWrapper message) {
        if (this.organiserRole != null) {
            message.setOriginRole(this);
//            this.organiserRole.sendToOrganiser(message);
        }
    }

    /**
     * Allows a player to get incoming management related messages sent from the
     * organiser (in message wrappers) from this functional roles message queue.
     * Blocks if there are are currently no messages.
     *
     * @return the new MessageWrapper to receive.
     */
    public MessageWrapper getNextManagementMessage() {
        try {
            return outManagementQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public MessageWrapper getNextManagementMessage(long timeout, TimeUnit unit) {
        try {
            return outManagementQueue.poll(timeout, unit);
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Method for a MessageDeliverer to place a <code>MessageWrapper</code> on
     * the outgoing queue of this functional <code>Role</code> for a player to
     * take.
     *
     * @param message the <code>MessageWrapper</code> for a player to take.
     */
    public void delivererPutOutgoingMessage(MessageWrapper message) {
        outQueue.add(message);
    }

    public void delivererPutOutgoingSyncMessage(MessageWrapper message) {
        if (callbacks.isEmpty()) {
            return;
        }
        String clientID = message.getClientID();
        if (log.isInfoEnabled()) {
            log.info(name + " Delivering Message : " + clientID);
        }
//        AsyncResponse<MessageWrapper> callBack = callbacks.remove(clientID);
//        if (!message.isFault()) {
//            VSNState tenantState =
//                    ServiceNetworkState.getInstance().getVsnState(
//                            message.getClassifier().getVsnId());
//            ProcessState routeState = tenantState.getProcessState(message.getClassifier().getProcessId());
//            tenantState.getLock().lock();
//            routeState.getLock().lock();
//            try {
//                tenantState.decreaseUsedGlobalCapacity();
//                routeState.decreaseUsedCapacity();
//               // log.error("Decreased : " + tenantState.getVSNId() + " " + routeState.getProcessId());
//            } finally {
//                routeState.getLock().unlock();
//                tenantState.getLock().unlock();
//            }
//        }
        ResponseCallback callBack = callbacks.remove(clientID);
        if (callBack != null) {
            try {
                callBack.responseReceived(message);
            } catch (Exception ignored) {
                log.error(name + " " + "Failed to Deliver Response message: " +
                        clientID + " Error : " + ignored.getMessage(), ignored);
            }
        } else {
            log.warn(name + " " + "Failed to Deliver Response message: " + clientID);
            log.warn(name + " " + callbacks.keySet());
        }
    }

    /**
     * @param message
     */
    public void workerPutOutgoingPushMessage(MessageWrapper message) {
        message.setDestinationPlayerBinding(playerBinding);
//        outPushQueue.add(message);
        if (bound)
            notifyPushListeners(message);

    }

    /**
     * Method for the organiser to place a management related
     * <code>MessageWrapper</code> on the outgoing queue of this functional
     * <code>Role</code> for a player to take.
     *
     * @param message the <code>MessageWrapper</code> for a player to take.
     */
    public void organiserPutOutgoingManagement(MessageWrapper message) {
        outManagementQueue.add(message);
    }

    public RoleType getRoleType() {
        return this.roleType;
    }

    @Override
    public void cleanUp() {
        synRules.cleanUp();
        routingRules.cleanUp();
    }

    /**
     * Gets a list of Operation objects that contain the provided operations for
     * this role (based on the linked Contracts).
     *
     * @return the list of provided operations.
     */
    public List<Operation> getProvidedOperationsList() {
        List<Operation> operations = new ArrayList<Operation>();
        // this set is used to hold "contractId+TermId" values of all the result
        // msgs in the task. So that the corresponding operations in contracts
        // can be eliminated.
        Set<String> resultMsgSet = new HashSet<String>();
        // get the tasks of this role
        TasksType tasksType = roleType.getTasks();
        List<TaskType> taskList;
        if (tasksType != null) {
            taskList = tasksType.getTask();
        } else {
            taskList = new ArrayList<TaskType>();
        }
        // iterate on the list of tasks to retrieve the operations in the tasks
        // and add them to the provided interface
        for (TaskType task : taskList) {
            // log.info(this.id + " ::: " + task.getId());
            InMsgType inMsgType = task.getIn();

            if (inMsgType == null) {
                continue;
            }
            // log.info(this.id + " ::: " + task.getId() + " ::: 1");
            OperationType inOperation = inMsgType.getOperation();
            if (inOperation == null) {
                continue;
            }
            // log.info(this.id + " ::: " + task.getId() + " ::: 2");
            ResultMsgsType resultMsgsType = task.getResultMsgs();
            if (resultMsgsType == null) {
                continue;
            }
            // log.info(this.id + " ::: " + task.getId() + " ::: 3");
            List<ResultMsgType> resultMsgList = resultMsgsType.getResultMsg();
            if (resultMsgList.size() == 0) {
                continue;
            }
            // log.info(this.id + " ::: " + task.getId() + " ::: 4");
            if (!inMsgType.isIsResponse()) { // isResponse attribute is false by
                // default
                // create an object of type
                // au.edu.swin.ict.road.composite.contract.Operation. All the
                // operation details are retrived from the task and set in this
                // object.
                Operation anOperation = new Operation();
                List<au.edu.swin.ict.road.common.Parameter> parameterList = new ArrayList<au.edu.swin.ict.road.common.Parameter>();

                anOperation.setName(inOperation.getName());
                String returnType = inOperation.getReturn();
                if (returnType != null) {
                    anOperation.setReturnType(returnType);
                }
                ParamsType paramsType = inOperation.getParameters();
                List<Parameter> paramList = null;
                if (paramsType != null) {
                    paramList = paramsType.getParameter();
                } else {
                    paramList = new ArrayList<ParamsType.Parameter>();
                }
                for (Parameter parameter : paramList) {
                    parameterList
                            .add(new au.edu.swin.ict.road.common.Parameter(
                                    parameter.getType(), parameter.getName()));
                }
                anOperation.setParameters(parameterList);

                // check for duplicate operations
                boolean duplicate = duplicateOperationCheck(anOperation,
                        operations);
                if (!duplicate) {
                    operations.add(anOperation);
                }
            }

            // add the "contractId+TermId" values of each result msg to the
            // resultMshSet
            for (ResultMsgType resultMsg : resultMsgList) {
                resultMsgSet.add(resultMsg.getContractId()
                        + resultMsg.getTermId());
                // log.info(">>>>>>>>>>>>>>>>>> for Role " + this.id +
                // " and task " + task.getId() + " adding " +
                // resultMsg.getContractId() + resultMsg.getTermId() +
                // " to the resultMsg ignore set");
            }
        }

        // iterate over the list of contracts this role shares with other roles.
        for (Contract c : contractList) {
            List<Term> terms = c.getTermList();
            // iterate over the list of terms in this contract
            for (Term t : terms) {
                if (t.getOperation() == null) {
                    continue;
                }
                // if this contract and term pair is not part of any task, then
                // we add it else we ignore it.
                if (!resultMsgSet.contains(c.getId() + t.getId())) {
                    if (c.getRoleA() == this) {
                        if (t.getDirection().equals("AtoB")) {
                            // check for duplicate operations
                            boolean duplicate = duplicateOperationCheck(
                                    t.getOperation(), operations);
                            if (!duplicate) {
                                operations.add(t.getOperation());
                            }
                        }
                    } else if (c.getRoleB() == this) {
                        if (t.getDirection().equals("BtoA")) {
                            // check for duplicate operations
                            boolean duplicate = duplicateOperationCheck(
                                    t.getOperation(), operations);
                            if (!duplicate) {
                                operations.add(t.getOperation());
                            }
                        }
                    }
                }
            }
        }

        // add the set and get methods of each fact to the provided interface
        addFactOperationsToProvidedOblationsList(operations);

        return operations;
    }

    /**
     * Adds the required set and get methods for each fact to the list of
     * provided operations
     *
     * @param operations
     */
    public void addFactOperationsToProvidedOblationsList(
            List<Operation> operations) {
        // iterate over the list of facts this role is linked to
        for (FactSynchroniser factSynchroniser : factSynchronisers) {
            // if this is a context provider, add a setXFacts methods to the
            // provided interface
            if (factSynchroniser.getFactRegime().isContextProvider()) {
                au.edu.swin.ict.road.common.Parameter factMethodParameter =
                        new au.edu.swin.ict.road.common.Parameter(
                                "au.edu.swin.ict.road.regulator.types.Facts", "facts");
                List<au.edu.swin.ict.road.common.Parameter> factMethodParameterList = new ArrayList<au.edu.swin.ict.road.common.Parameter>();
                factMethodParameterList.add(factMethodParameter);
                Operation factMethodOperation1 = new Operation("add"
                        + factSynchroniser.getFactType() + "Facts",
                        factMethodParameterList, "void");

                Operation factMethodOperation2 = new Operation("update"
                        + factSynchroniser.getFactType() + "Facts",
                        factMethodParameterList, "void");
                // check for duplicate operations
                boolean duplicate = duplicateOperationCheck(
                        factMethodOperation1, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation1);
                }
                duplicate = duplicateOperationCheck(factMethodOperation2, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation2);
                }
                au.edu.swin.ict.road.common.Parameter factMethodParameter1 =
                        new au.edu.swin.ict.road.common.Parameter(
                                "String", "factId");
                List<au.edu.swin.ict.road.common.Parameter> factMethodParameterList1 =
                        new ArrayList<au.edu.swin.ict.road.common.Parameter>();
                factMethodParameterList1.add(factMethodParameter1);
                Operation factMethodOperation3 = new Operation("remove"
                        + factSynchroniser.getFactType() + "Fact",
                        factMethodParameterList1,
                        "Boolean");
                // check for duplicate operations
                duplicate = duplicateOperationCheck(factMethodOperation3, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation3);
                }
            }

            // if this is a monitor, add getXFacts and getAllXFacts methods to
            // the provided interface
            if (factSynchroniser.getFactRegime().isMonitor()) {
                au.edu.swin.ict.road.common.Parameter factMethodParameter =
                        new au.edu.swin.ict.road.common.Parameter("String", "factId");
                List<au.edu.swin.ict.road.common.Parameter> factMethodParameterList = new ArrayList<au.edu.swin.ict.road.common.Parameter>();
                factMethodParameterList.add(factMethodParameter);
                Operation factMethodOperation = new Operation("get"
                        + factSynchroniser.getFactType() + "Fact",
                        factMethodParameterList,
                        "au.edu.swin.ict.road.regulator.types.Fact");
                // check for duplicate operations
                boolean duplicate = duplicateOperationCheck(factMethodOperation, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation);
                }
                Operation factMethodOperation1 = new Operation(
                        "getAll" + factSynchroniser.getFactType() + "Facts",
                        new ArrayList<au.edu.swin.ict.road.common.Parameter>(),
                        "au.edu.swin.ict.road.regulator.types.Facts");
                // check for duplicate operations
                duplicate = duplicateOperationCheck(factMethodOperation1, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation1);
                }
            }
        }
    }

    /**
     * Gets a list of strings the contains the required operations this role
     * contains, which depends on linked contracts (operations a player can have
     * invoked on them, or message types a player may receive).
     *
     * @return the list of required operations.
     */
    public List<Operation> getRequiredOperationsList() {
        List<Operation> operations = new ArrayList<Operation>();
        // this set is used to hold "contractId+TermId" values of all the source
        // msgs in the task. So that the corresponding operations in contracts
        // can be eliminated.
        Set<String> srcMsgSet = new HashSet<String>();
        // get the tasks of this role
        TasksType tasksType = roleType.getTasks();
        List<TaskType> taskList = null;
        if (tasksType != null) {
            taskList = tasksType.getTask();
        } else {
            taskList = new ArrayList<TaskType>();
        }

        // iterate on the list of tasks to retrieve the operations in the tasks
        // and add them to the provided interface
        for (TaskType task : taskList) {
            OutMsgType outMsgType = task.getOut();
            if (outMsgType == null) {
                continue;
            }
            OperationType outOperation = outMsgType.getOperation();
            if (outOperation == null) {
                continue;
            }

            SrcMsgsType srcMsgsType = task.getSrcMsgs();
            if (srcMsgsType == null) {
                continue;
            }

            List<SrcMsgType> srcMsgList = srcMsgsType.getSrcMsg();
            if (srcMsgList.size() == 0) {
                continue;
            }
            String transformation = srcMsgsType.getTransformation();
            if (transformation == null) {
                continue;
            }

            if (!outMsgType.isIsResponse()) {
                List<au.edu.swin.ict.road.common.Parameter> parameterList = new ArrayList<au.edu.swin.ict.road.common.Parameter>();

                // create an object of type
                // au.edu.swin.ict.road.composite.contract.Operation. All the
                // operation details are retrived from the task and set in this
                // object.
                Operation anOperation = new Operation();
                anOperation.setName(outOperation.getName());
                String returnType = outOperation.getReturn();
                if (returnType != null) {
                    anOperation.setReturnType(returnType);
                }
                ParamsType paramsType = outOperation.getParameters();
                List<Parameter> paramList = null;
                if (paramsType != null) {
                    paramList = paramsType.getParameter();
                } else {
                    paramList = new ArrayList<ParamsType.Parameter>();
                }

                for (Parameter parameter : paramList) {
                    parameterList.add(new au.edu.swin.ict.road.common.Parameter(
                            parameter.getType(), parameter.getName()));
                }

                anOperation.setParameters(parameterList);
                // check for duplicate operations
                boolean duplicate = duplicateOperationCheck(anOperation,
                        operations);
                if (!duplicate) {
                    operations.add(anOperation);
                }
            }

            // add the "contractId+TermId" values of each src msg to the
            // srcMsgSet
            for (SrcMsgType srcMsg : srcMsgList) {
                srcMsgSet.add(srcMsg.getContractId() + srcMsg.getTermId());
                // log.info(">>>>>>>>>>>>>>>>>> for Role " + this.id +
                // " and task " + task.getId() + " adding " +
                // srcMsg.getContractId() + srcMsg.getTermId() +
                // " to the srcMsg ignore set");
            }
        }

        // iterate over the lisr of contracts this role shares with other roles.
        for (Contract c : contractList) {
            List<Term> terms = c.getTermList();
            // iterate over the list of terms in this contract
            for (Term t : terms) {
                // if this contract and term pair is not part of any task, then
                // we add it else we ignore it.
                if (!srcMsgSet.contains(c.getId() + t.getId())) {
                    if (!(t.getMessageType().equalsIgnoreCase("pull"))) {
                        if (c.getRoleA() == this) {
                            if (t.getDirection().equals("BtoA")) {
                                // check for duplicate operations
                                boolean duplicate = duplicateOperationCheck(
                                        t.getOperation(), operations);
                                if (!duplicate)
                                    operations.add(t.getOperation());
                            }
                        } else if (c.getRoleB() == this) {
                            if (t.getDirection().equals("AtoB")) {
                                // check for duplicate operations
                                boolean duplicate = duplicateOperationCheck(
                                        t.getOperation(), operations);
                                if (!duplicate)
                                    operations.add(t.getOperation());
                            }
                        }
                    }
                }
            }
        }

        // add the set and get methods of each fact to the required interface
        addFactOperationsToRequiredOperationsList(operations);

        return operations;
    }

    /**
     * Adds the required set and get methods for each fact to the list of
     * required operations
     *
     * @param operations
     */
    public void addFactOperationsToRequiredOperationsList(
            List<Operation> operations) {
        // iterate over the list of facts this role is linked to
        for (FactSynchroniser factSynchroniser : factSynchronisers) {
            // if this is a context provider or a monitor, add setXFacts,
            // getXFacts methods to the required interface
            if (factSynchroniser.getFactRegime().isContextProvider()
                    || factSynchroniser.getFactRegime().isMonitor()) {
                au.edu.swin.ict.road.common.Parameter factMethodParameter =
                        new au.edu.swin.ict.road.common.Parameter(
                                "au.edu.swin.ict.road.regulator.types.Facts", "facts");
                List<au.edu.swin.ict.road.common.Parameter> factMethodParameterList = new ArrayList<au.edu.swin.ict.road.common.Parameter>();
                factMethodParameterList.add(factMethodParameter);
                Operation factMethodOperation = new Operation("set"
                        + factSynchroniser.getFactType() + "Facts",
                        factMethodParameterList, "void");
                // check for duplicate operation
                boolean duplicate = duplicateOperationCheck(factMethodOperation, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation);
                }
                // requiredFactOperations.add(factMethodOperation.getName());

                List<au.edu.swin.ict.road.common.Parameter> factMethodParameterList2 = new ArrayList<au.edu.swin.ict.road.common.Parameter>();
                Operation factMethodOperation2 = new Operation("getAll"
                        + factSynchroniser.getFactType() + "Facts",
                        factMethodParameterList2,
                        "au.edu.swin.ict.road.regulator.types.Facts");
                // check for duplicate operation
                duplicate = duplicateOperationCheck(factMethodOperation2, operations);
                if (!duplicate) {
                    operations.add(factMethodOperation2);
                }
                // requiredFactOperations.add(factMethodOperation2.getName());
            }
        }
    }

    public Object getProvidedOperationObject()
            throws RoleDescriptionGenerationException {
        ROADClassBuilder rcb = new ROADClassBuilder();

        rcb.buildClass("au.edu.swin.ict.road.composite", name + "Provided",
                this.getProvidedOperationsList());
        Class<?> clz = rcb.loadClass("au.edu.swin.ict.road.composite." + name
                + "Provided");
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            log.fatal(e.getMessage());
            throw new RoleDescriptionGenerationException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.fatal(e.getMessage());
            throw new RoleDescriptionGenerationException(e.getMessage());
        }
    }

    public Object getRequiredOperationObject()
            throws RoleDescriptionGenerationException {
        ROADClassBuilder rcb = new ROADClassBuilder();

        rcb.buildClass("au.edu.swin.ict.road.composite", name + "Required",
                this.getRequiredOperationsList());
        Class<?> clz = rcb.loadClass("au.edu.swin.ict.road.composite." + name
                + "Required");
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            log.fatal(e.getMessage());
            throw new RoleDescriptionGenerationException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.fatal(e.getMessage());
            throw new RoleDescriptionGenerationException(e.getMessage());
        }
    }

    /**
     * Get the roles unique id.
     *
     * @return the unique id.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the roles unique id.
     *
     * @param id the new unique id.
     */
    public void setId(String id) {
        this.id = id;
        synchronizationRegTable.setName(id);
        routingRegTable.setName(id);
    }

    /**
     * Get the roles name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the roles name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the roles description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the roles description.
     *
     * @param description the new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the routing table for this role
     *
     * @return the routing table
     */
    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    /**
     * Is the role currently bound to a player?
     *
     * @return true if bound, else false.
     */
    public boolean isBound() {
        return bound;
    }

    /**
     * Binds this role to the new contract.
     *
     * @param newContract the new contract.
     */
    public void addContract(Contract newContract) {
        this.contractList.add(newContract);
    }

    /**
     * Unbind's this contract from the role.
     *
     * @param rContract the contract to remove.
     */
    public void removeContract(Contract rContract) {
        this.contractList.remove(rContract);
        this.routingTable.removeContract(rContract);
    }

    /**
     * Destination contract will be retrieved as follows. 1, If the destination
     * contract is directly available in the table, it will be returned. 2, If
     * the there is no destination contract a set of rules will be executed.
     * (@messageWrapper should not be null)
     *
     * @param msgWrapper the message which requires routing
     * @return the destination contract
     * @throws MessageRoutingException
     */
    public Contract getDestinationContract(MessageWrapper msgWrapper,
                                           boolean executeRules) throws MessageRoutingUndeterminedException,
            MessageRoutingException, NoRequestException {
        return routingTable.getDestinationContract(msgWrapper, executeRules);
    }

    /**
     * Gets the organiser role currently used by this functional role for
     * sending management messages to the organiser player.
     *
     * @return the organiser role
     */
    public OrganiserRole getOrganiserRole() {
        return this.organiserRole;
    }

    /**
     * Sets the organiser role to be used by this functional role for sending
     * management messages to the organiser.
     *
     * @param organiserRole the organiser role of the Composite of which this functional
     *                      <code>Role</code> is a part.
     */
    public void setOrganiserRole(OrganiserRole organiserRole) {
        this.organiserRole = organiserRole;
    }

    /**
     * @return The String of the player binding
     */
    public String getPlayerBinding() {
        return playerBinding;
    }

    /**
     * @param playerBinding
     */
    public void bind(String playerBinding) {
        this.playerBinding = playerBinding;
        bound = true;
//        if (!this.outPushQueue.isEmpty()) {
//            this.notifyPushListeners();
//        }
    }

    /**
     *
     */
    public void unBind() {
        this.playerBinding = null;
        bound = false;
    }

    private void notifyPushListeners(MessageWrapper messageWrapper) {
        if (pushListeners.isEmpty()) {
            composite.getDefaultPushMessageListener().pushMessageRecieved(this, messageWrapper);
        } else {
            for (RolePushMessageListener l : pushListeners.values()) {
                l.pushMessageRecieved(this, messageWrapper);
            }
        }
    }

    /**
     * Gets an array containing all the contracts this role is bound to.
     *
     * @return the contract array.
     */
    public Contract[] getAllContracts() {
        if (this.contractList.size() > 0) {
            Contract[] cArray = new Contract[this.contractList.size()];
            return this.contractList.toArray(cArray);
        }

        return null;
    }

    /**
     * Loads up data from a JAXB binding class.
     *
     * @param roleBinding the JAXB class.
     */
    private void extractData(RoleType roleBinding) {
        if (roleBinding.getId() != null) {
            id = roleBinding.getId();
        }
        if (roleBinding.getName() != null) {
            name = roleBinding.getName();
        }
        if (roleBinding.getDescription() != null) {
            description = roleBinding.getDescription();
        }
        try {
            if (roleBinding.getSynchronization() != null) {
                loadSyncRegRules(roleBinding.getSynchronization());
            }
            if (roleBinding.getRouting() != null) {
                loadRoutingRegRules(roleBinding.getRouting());
            }
        } catch (CompositeInstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracts the facts linked to this role, creates a fact synchroniser is
     * this fact exists in the FTS of the composite
     *
     * @param roleBinding
     */
    public void extractFacts(RoleType roleBinding) {
        // if this role has any linked facts
        if (roleBinding.getLinkedFacts() != null) {
            // get all linked facts for this role
            LinkedFacts linkedFactsType = roleBinding.getLinkedFacts();

            // iterate over the list of linked facts
            for (RoleLinkedFactType factType : linkedFactsType.getFact()) {
                // iterate over the list of facts in the composite FTS
                for (FactTupleSpaceRow factRow : this.composite.getFTS()
                        .getFTSMemory()) {
                    // if this fact type exists in the FTS
                    if (factRow.getFactType().equalsIgnoreCase(
                            factType.getName())) {
                        // create a fact synchroniser and add it to the fact
                        // synchronisers list in this role
                        FactSynchroniser synchroniser = new FactSynchroniser(
                                factType.getName(),
                                this,
                                factType.isProvide(),
                                factType.isMonitor(),
                                (factType.getAcquisitionRegime() != null ? factType.getAcquisitionRegime()
                                        .getSyncInterval() : 1000),
                                (factType.getProvisionRegime() != null ? factType.getProvisionRegime().
                                        getSyncInterval() : 1000),
                                factType.isOnChange());
                        synchroniser.setAcceptableFact(factRow.getMasterFact());
                        factSynchronisers.add(synchroniser);

                        if (factType.isProvide()) {
                            this.composite.getFTS().registerSource(this,
                                    factType.getName());
                        }

                        if (factType.isMonitor()) {
                            this.composite.getFTS().registerSubscribers(this,
                                    factType.getName());
                        }
                    }
                }
            }
        }
    }

    public RoutingKnowledgebase getRoutingRules() {
        return routingRules;
    }

    /**
     * Creates and returns the role binding object from the composite's Role
     * object to use in the JAXB transformation of the XML representation of the
     * composite.
     *
     * @return the role binding object used in JAXB transformation
     */
    public RoleType createRoleBinding() {

        /*
         * Creating a new role type - JAXB binding object and setting the
         * instance variables using the role object's instance values
         */
        RoleType rt = new RoleType();

        rt.setId(id);
        rt.setName(name);
        rt.setDescription(description);

        return rt;
    }

    /**
     * Returns all the updated rules in a map data structure associated with
     * this role. The rules are grouped according to its appropriate contract.
     * The method scans the associated drl files and the RuleChangeTracker list
     * to populate an array list of rules grouped according to contract Ids.
     *
     * @return a map which has contract Ids as keys and array lists containing
     * the rules as values
     */
    public Map<String, List<String>> getRules() {

        // Declaring variables to hold the rules, drl filenames and the map to
        // be returned
        List<String> associatedContractId = new ArrayList<String>();
        List<String> associatedRuleFileList = new ArrayList<String>();
        List<String> associatedRules = new ArrayList<String>();
        String associatedRuleFile = "";
        String tempLine = "";
        Map<String, List<String>> associatedRuleMap = new HashMap<String, List<String>>();

        // Iterating through all the contracts associated with this role. For
        // every contract, the corresponding drl file is read and the rules are
        // extracted.
        for (Contract contractObject : contractList) {

            // Getting the rule file and the contract ID
            associatedContractId.add(contractObject.getId());
            associatedRuleFile = contractObject.getRules().getRuleFile();
            associatedRuleFileList.add(associatedRuleFile);
            boolean recordRule = false;
            String actualRule = "";
            // Reading the appropriated rule file
            try {
                FileReader droolsFileReader = new FileReader(new File(".")
                        .getCanonicalPath().toString()
                        + "\\"
                        + associatedRuleFile);
                // FileReader droolsFileReader = new FileReader(new File(
                // associatedRuleFile));
                BufferedReader droolsFile = new BufferedReader(droolsFileReader);
                while ((tempLine = droolsFile.readLine()) != null) {

                    recordRule = false;
                    // When a rule is encountered, it is checked with the
                    // RuleChangeTracker list to ensure that it is not deleted
                    if (tempLine.startsWith("rule")) {

                        recordRule = true;

                        List<RuleChangeTracker> changedRulesList = composite
                                .getRuleChangeTrackerList();
                        for (RuleChangeTracker ruleChangeTrackerObject : changedRulesList) {

                            if (ruleChangeTrackerObject.getDrlFile()
                                    .equalsIgnoreCase(associatedRuleFile)
                                    && ruleChangeTrackerObject.getAction()
                                    .equalsIgnoreCase("remove")
                                    && tempLine
                                    .contains(ruleChangeTrackerObject
                                            .getRuleName())) {
                                recordRule = false;

                            }
                        }

                        // If the rule was not deleted according to the
                        // RuleChangeTracker list then the rule is added to an
                        // array list
                        if (recordRule) {
                            do {
                                actualRule = actualRule + tempLine;
                                tempLine = droolsFile.readLine();
                            } while (!tempLine.endsWith("end"));

                            associatedRules.add(actualRule);
                            actualRule = "";
                        }

                    }

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Since the associated rules have to cleared for every contract,
            // storing the contents of the associated rules list in a temporary
            // list and adding that temporary list to the map
            List<String> tempAssociatedRules = new ArrayList<String>();
            for (String associatedRuleObject : associatedRules) {
                tempAssociatedRules.add(associatedRuleObject);
            }
            associatedRuleMap.put(contractObject.getId(), tempAssociatedRules);
            associatedRules.clear();
        }

        // Adding Rules from the rule change tracker list
        List<RuleChangeTracker> ruleChangesList = composite
                .getRuleChangeTrackerList();

        boolean addRule = false;
        for (RuleChangeTracker ruleChangeObject : ruleChangesList) {

            if (ruleChangeObject.getAction().equalsIgnoreCase("insert")
                    && associatedRuleFileList.contains(ruleChangeObject
                    .getDrlFile())) {

                String action = ruleChangeObject.getAction();
                String drlFilename = ruleChangeObject.getDrlFile();
                String ruleInformation = ruleChangeObject.getRuleInformation();
                String ruleName = ruleChangeObject.getRuleName();

                // Checking which all rules are newly added
                for (int i = 0; i < ruleChangesList.size(); i++) {

                    if (ruleName.equalsIgnoreCase(ruleChangesList.get(i)
                            .getRuleName())
                            && drlFilename.equalsIgnoreCase(ruleChangesList
                            .get(i).getDrlFile())
                            && ruleChangesList.get(i).getAction()
                            .equalsIgnoreCase("remove")) {

                        addRule = false;
                    } else {
                        addRule = true;
                    }
                }

                if (addRule
                        && associatedRuleMap.containsKey(ruleChangeObject
                        .getContractId())) {
                    // associatedRules.add(ruleChangeObject.getRuleInformation());
                    if (!associatedRuleMap
                            .get(ruleChangeObject.getContractId()).contains(
                                    ruleInformation)) {
                        associatedRuleMap.get(ruleChangeObject.getContractId())
                                .add(ruleInformation);
                    }

                } else if (addRule) {
                    associatedRules.add(ruleInformation);
                    List<String> tempAssociatedRules = new ArrayList<String>();
                    for (String associatedRuleObject : associatedRules) {
                        tempAssociatedRules.add(associatedRuleObject);
                    }
                    associatedRuleMap.put(ruleChangeObject.getContractId(),
                            tempAssociatedRules);
                    associatedRules.clear();
                }

            }
        }

        return associatedRuleMap;
    }

    private void loadSyncRegRules(String ruleFile) throws CompositeInstantiationException {
        synRules.addRule(ruleFile);
    }

    private void loadRoutingRegRules(String ruleFile) throws CompositeInstantiationException {
        routingRules.addRule(ruleFile);
    }

    /**
     * Method to fire multiple event records
     *
     * @param events
     * @throws SerendipException
     */
    public String fireEvents(List<EventRecord> events) throws SerendipException {
        String pid = null;
        for (EventRecord e : events) {
            pid = this.composite.getSerendipEngine().addEvent(e);
        }
        return pid;
    }

    /**
     * This is where the Serendip actuate the tasks specified in a Role.
     *
     * @see au.edu.swin.ict.serendip.composition.Task eventPatternMatched()
     */
    @Override
    public void performTask(TaskPerformAction tpa) {
//        // MessageAnalyzer will be used in here.
//        try {
//            OutTransform.transform(this, tpa.getTask().getId(), tpa.getTask()
//                    .getClassifier().getProcessInsId());
//        } catch (Exception e) {
//            log.error("Error executing task : " + tpa.getTask().getId() + e.getMessage(), e);
//        }
    }

    /**
     * Return the list of fact synchronisers associarted with this role
     *
     * @return
     */
    public List<FactSynchroniser> getFactSynchronisers() {
        return factSynchronisers;
    }

    /**
     * Set the list of fact synchronisers associarted with this role
     *
     * @param factSynchronisers
     */
    public void setFactSynchronisers(List<FactSynchroniser> factSynchronisers) {
        this.factSynchronisers = factSynchronisers;
    }

    /**
     * returns the fact synchroniser with the given name which is associated
     * with this role
     *
     * @param name
     * @return
     */
    public FactSynchroniser getFactSynchroniser(String name) {
        FactSynchroniser synchroniser = null;

        for (FactSynchroniser factSynchroniser : factSynchronisers) {
            if (factSynchroniser.getId().equalsIgnoreCase(name)) {
                synchroniser = factSynchroniser;
                break;
            }
        }

        return synchroniser;
    }

    /**
     * Checks if the given operation already exists in the given list of
     * operations
     *
     * @param anOperation
     * @param operations
     * @return
     */
    private boolean duplicateOperationCheck(Operation anOperation,
                                            List<Operation> operations) {
        boolean duplicate = false;
        // iterate over the list of operations
        for (Operation operation : operations) {
            // if the operation name is same
            if (anOperation.getName().equals(operation.getName())) {
                // if operation return type is same
                if (anOperation.getReturnType().equals(operation.getReturnType())) {
                    // if number of parameters is same
                    if (anOperation.getParameters().size() == operation.getParameters().size()) {
                        int i = 0;
                        // loop over the list of parameters
                        for (i = 0; i < anOperation.getParameters().size(); i++) {
                            // if the parameter type is not same
                            if (!(anOperation.getParameters().get(i).getType().
                                    equals(operation.getParameters().get(i).getType()))) {
                                break;
                            }
                        }
                        // if i equals the number of parameters, then it is a duplicate
                        if (i == anOperation.getParameters().size()) {
                            duplicate = true;
                        }
                    }
                }
            }
        }
        return duplicate;
    }

    public void clean(String pid) {
        pendingOutBuf.cleanProcessMessages(pid);
    }

    public TaskType getTaskType(String taskId) {
        for (TaskType taskType : roleType.getTasks().getTask()) {
            if (taskType.getId().equals(taskId)) {
                return taskType;
            }
        }
        return null;
    }

    private void sendASOAPFault(MessageWrapper mw, String reasonString) {
        mw.setMessage(SOAPFactory.createFaultEnvelope((SOAPEnvelope) mw.getMessage(), reasonString));
        mw.setResponse(true);
        mw.setFault(true);
        delivererPutOutgoingSyncMessage(mw);
    }

    public FlowControlTable getEgressFlowControlTable() {
        return egressFlowControlTable;
    }

    public SynchronizationRegTable getSynchronizationRegTable() {
        return synchronizationRegTable;
    }

    public RoutingRegTable getRoutingRegTable() {
        return routingRegTable;
    }

    public SynchronizationKnowledgebase getSynRules() {
        return synRules;
    }

    public void setSynchronization(String ruleFile) {
        if (synRules != null) {
            synRules.cleanUp();
        }
        try {
            loadSyncRegRules(ruleFile);
        } catch (CompositeInstantiationException e) {
            e.printStackTrace();
        }
    }

    public void setRouting(String ruleFile) {
        if (routingRules != null) {
            routingRules.cleanUp();
        }
        try {
            loadRoutingRegRules(ruleFile);
        } catch (CompositeInstantiationException e) {
            e.printStackTrace();
        }
    }

    public RoleManagementState getMgtState() {
        return mgtState;
    }
}
