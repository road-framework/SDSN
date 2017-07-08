package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.common.Parameter;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Operation;
import au.edu.swin.ict.road.composite.contract.Term;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.composite.flowcontrol.*;
import au.edu.swin.ict.road.composite.listeners.CompositeAddRoleListener;
import au.edu.swin.ict.road.composite.listeners.CompositeRemoveRoleListener;
import au.edu.swin.ict.road.composite.listeners.CompositeUpdateRoleListener;
import au.edu.swin.ict.road.composite.listeners.RolePushMessageListener;
import au.edu.swin.ict.road.composite.player.PlayerBinding;
import au.edu.swin.ict.road.composite.regulation.passthrough.PassthroughRegTable;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRegTable;
import au.edu.swin.ict.road.composite.regulation.sglobal.GlobalKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.sglobal.GlobalRegTable;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationRegTable;
import au.edu.swin.ict.road.composite.routing.*;
import au.edu.swin.ict.road.composite.rules.ICompositeRules;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsCompositeRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsMonitoringRules;
import au.edu.swin.ict.road.composite.utills.ROADThreadPool;
import au.edu.swin.ict.road.composite.utills.ROADThreadPoolFactory;
import au.edu.swin.ict.road.regulator.BehaviorMonitor;
import au.edu.swin.ict.road.regulator.FactObject;
import au.edu.swin.ict.road.regulator.FactTupleSpace;
import au.edu.swin.ict.road.xml.bindings.*;
import au.edu.swin.ict.road.xml.bindings.FactType.Attributes;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.core.*;
import au.edu.swin.ict.serendip.core.mgmt.SerendipOrganizer;
import au.edu.swin.ict.serendip.util.BenchUtil;
import org.apache.log4j.Logger;

import java.util.*;

//import au.edu.swin.ict.serendip.core.SerendipEngine;

/**
 * Composite is an implementation of a ROAD self-managed composite or SMC. A
 * ROAD Composite is a collection of roles bound by contracts. Roles are
 * intended to be interacted with by players. Each composite has an organiser
 * role which allows reconfiguration of the structure. Composite implements
 * Runnable and is intended to be forward in its own thread. The forward() method starts
 * a series of threads called MessageDeliverer, each of which forward in their own
 * thread and shuttle messages between roles, admit evaluation of contracts,
 * and so on. Composite requires a JAXB binding class to be instantiated, which
 * represents the XML design time representation of the composite.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class Composite implements Runnable {

    private static Logger log = Logger.getLogger(Composite.class.getName());
    private ServiceNetwork smcBinding;
    private BenchUtil benchUtil = null;
    private ROADDeploymentEnv roadDepEnv = null;
    public static final String KEY_BU_EXTENDSFROM = "extendsFrom";
    public static final String KEY_BU_ISABSTRACT = "isAbstract";
    // Symbols
    public static final String SERENDIP_SYMBOL_AND = "*";//These need to sync with ep.g and behav.g antlr grammar
    public static final String SERENDIP_SYMBOL_OR = "|";
    public static final String SERENDIP_SYMBOL_XOR = "^";

    // descriptor
    public static final String SERENDIP_PATH_SEP = "_";
    private DroolsBasedManagementPolicyEnactmentEngine policyEnactmentEngine;
    private SerendipOrganizer serendipOrganizer;
    private final Map<String, RegulationMechanism> regulationMechanisums = new HashMap<String, RegulationMechanism>();
    private final Map<String, SNStateImplementation> snStateImplementations = new HashMap<String, SNStateImplementation>();
    private final Map<String, RegulationUnitState> regulationUnitStateMap = new HashMap<String, RegulationUnitState>();
    private final ServiceNetworkState serviceNetworkState;
    private RolePushMessageListener defaultPushMessageListener;
    private GlobalRegTable globalRegTable;
    private GlobalKnowledgebase globalKnowledgebase;
//
//    public ROADThreadPool getPendingOutBufferWorkers() {
//        return pendingOutBufferWorkers;
//    }

    public ServiceNetwork getSmcBinding() {

        return smcBinding;
    }

    private String description;
    private String name;
    private Map<String, Role> roleMap; // all this composites roles
    private Map<String, Contract> contractMap; // all this composites contracts
    private Map<String, PlayerBinding> playerBindingMap; // all this contracts

    // player bindings
    private List<MessageDeliverer> workerList; // the current road workers
    // created for each role
    private OrganiserRole organiserRole;
    private IOperationalManagerRole operationalManagerRole;
    private final Object shutdownMutex;
    private String rulesDir; // general directory for rules
    private String transDir; // general directory for transformations

    public ROADThreadPool getEventCloudPool() {
        return eventCloudPool;
    }

    private String routingRulesFileName; // routing rule file
    private String compositeRulesFileName; // composite rule file

    public Timer getTimeOutTimer() {
        return timeOutTimer;
    }

    private ICompositeRules compositeRules; // composite level rule engine
    private List<CompositeAddRoleListener> addRoleListenerList;
    private List<CompositeRemoveRoleListener> removeRoleListenerList;
    private List<CompositeUpdateRoleListener> updateRoleListenerList;
    private List<RuleChangeTracker> ruleChangeTrackerList = new ArrayList<RuleChangeTracker>(); // Attribute
    private ROADThreadPool eventCloudPool =
            ROADThreadPoolFactory.createROADThreadPool("eventCloudPool");
    private Timer timeOutTimer = new Timer(true);
    private Map<String, BehaviorMonitor> monitorMap = new HashMap<String, BehaviorMonitor>();
//    private ROADThreadPool pendingOutBufferWorkers;
//    private ROADThreadPool receivingTreads;

    public ROADThreadPool getMessageDelivererWorkers() {
        return messageDelivererWorkers;
    }

    public void setMessageDelivererWorkers(ROADThreadPool messageDelivererWorkers) {
        this.messageDelivererWorkers = messageDelivererWorkers;
    }

    private ROADThreadPool messageDelivererWorkers;

    // Serendip
    private SerendipEngine serendipEngine = null;

    // Fact Tuple Space
    private FactTupleSpace FTS;

//    public ROADThreadPool getReceivingTreads() {
//        return receivingTreads;
//    }

    /**
     * Initializes a Composite using the supplied SMC, which is a JAXB binding
     * class containing properties from an XML document. Calling this
     * constructor does not *start* the Composite i.e. messages will not be
     * routed and contracts will not be evaluated. This is done using the forward()
     * method which is intended to be called using a Thread class.
     *
     * @param smcBinding the JAXB binding class whose contents will be used to
     *                   instantiate the Composite.
     * @throws ConsistencyViolationException   if composite contains invalid or inconsistent values, e.g. a
     *                                         contract without two roles attached, which is not allowed in
     *                                         the ROAD meta-model.
     * @throws CompositeInstantiationException if an error occurs attempting to instantiate the Composite
     *                                         using the supplied JAXB binding or when attempting to read
     *                                         the rules files.
     */
    public Composite(ServiceNetwork smcBinding) throws ConsistencyViolationException,
            CompositeInstantiationException {
        this.smcBinding = smcBinding;
        serviceNetworkState = new ServiceNetworkState(smcBinding.getName());
        description = null;
        roleMap = new HashMap<String, Role>();
        contractMap = new HashMap<String, Contract>();
        playerBindingMap = new HashMap<String, PlayerBinding>();
        workerList = new ArrayList<MessageDeliverer>();
        shutdownMutex = new Object();
        rulesDir = null;
        routingRulesFileName = null;
        compositeRulesFileName = null;
        compositeRules = null;
        addRoleListenerList = new ArrayList<CompositeAddRoleListener>();
        removeRoleListenerList = new ArrayList<CompositeRemoveRoleListener>();
        updateRoleListenerList = new ArrayList<CompositeUpdateRoleListener>();
//        pendingOutBufferWorkers = ROADThreadPoolFactory.createROADThreadPool("pendingOutMessageProcessor");
        messageDelivererWorkers = ROADThreadPoolFactory.createROADThreadPool("messageDeliverer");
//        receivingTreads = ROADThreadPoolFactory.createROADThreadPool("receivingTreads");

        this.benchUtil = new BenchUtil(this.smcBinding.getName());
        // Fact Tuple Space
        FTS = new FactTupleSpace();
        globalRegTable = new GlobalRegTable(smcBinding.getName());

        extractFileFolderPaths(smcBinding); // need to extract folder and file
        // paths first for composite rules
        // to be loaded
//        try {
//            compositeRules = this.loadCompositeRules(compositeRulesFileName); // load
//        }
//        catch (CompositeInstantiationException e) {
//            e.printStackTrace();
//            throw new CompositeInstantiationException("Problem loading composite rules " + compositeRulesFileName);
//        }
        // Here we create the process engine instance
        try {
            this.serendipEngine = new SerendipEngine(this);
        } catch (SerendipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // composite
        // rules
        organiserRole = new OrganiserRole(this);
        operationalManagerRole = new OperationalManagerRole(this);
        policyEnactmentEngine = new DroolsBasedManagementPolicyEnactmentEngine(operationalManagerRole, organiserRole, rulesDir);

        extractData(smcBinding); // extract all other data, may throw composite

        // Then we subscribe all the roles of the composite to the engine.
        // In this way the role gets notified when a task needs to be performed
        for (Role r : this.roleMap.values()) {
            this.serendipEngine.subscribe(r);
        }


        //   serendipOrganizer = new SerendipOrganizerImpl(serendipEngine, this);
        log.info("Deployment complete");
    }

    /**
     * Starts a Composite by starting all MessageDeliverer's (worker threads)
     * which are responsible for routing messages and executing evaluations of
     * contracts.
     */
    public void run() {
//        for (MessageDeliverer rw : workerList) {
//            rw.start();
//        }
        log.info("Composite is now running...");

        try {
            synchronized (shutdownMutex) {
                shutdownMutex.wait();
            }
        } catch (InterruptedException e) {
            log.fatal(e.getMessage());
        }

//        for (MessageDeliverer rw : workerList) {
//            try {
//                rw.join();
//            } catch (InterruptedException e) {
//                log.fatal(e.getMessage());
//            }
//        }
        this.benchUtil.finalize();
        log.info("All MessageDeliverers now terminated");
        log.info("Composite shutdown complete, Adios!");
    }

    /**
     * Shutdowns the <code>Composite</code> safely by allowing any RoadWorkers
     * to finish routing any messages they have already started. Note that any
     * messages still in queues waiting to be routed will remain in those queues
     * until the <code>Composite</code> is started again.
     */
    public void stop() {
        synchronized (shutdownMutex) {
            log.info("Beginning composite shutdown");
            log.info("Requesting all MessageDeliverers finish current jobs and terminate");

            // ask all workers to terminate
//            for (MessageDeliverer rw : workerList) {
//                rw.setTerminate(true);
//            }
            log.info("Requesting all Contracts to release resources");
            for (String cId : this.contractMap.keySet()) {
                this.contractMap.get(cId).terminate();
            }

            shutdownMutex.notify();
        }
        messageDelivererWorkers.shutdown();
        eventCloudPool.shutdown();
        timeOutTimer.cancel();
//        pendingOutBufferWorkers.shutdown();
//        receivingTreads.shutdown();
    }

    /**
     * Registers a new CompositeAddRoleListener to receive events when new roles
     * are added to the Composite by the organiser.
     * <p/>
     * This is intended to be used by deployment environments or other things
     * that need to act when something changes, e.g. updating a WSDL document in
     * the case of ROAD4WS.
     *
     * @param listener the new CompositeAddRoleListener wishing to receive events.
     */
    public void addCompositeAddRoleListener(CompositeAddRoleListener listener) {
        this.addRoleListenerList.add(listener);
    }

    /**
     * Removes a CompositeAddRoleListener from the registered event listeners.
     *
     * @param listener the CompositeAddRoleListener to be removed.
     */
    public void removeCompositeAddRoleListener(CompositeAddRoleListener listener) {
        this.addRoleListenerList.remove(listener);
    }

    /**
     * Registers a new CompositeRemoveRoleListener to receive events when roles
     * are removed from the Composite by the organiser.
     * <p/>
     * This is intended to be used by deployment environments or other things
     * that need to act when something changes, e.g. updating a WSDL document in
     * the case of ROAD4WS.
     *
     * @param listener the new CompositeRemoveRoleListener wishing to receive events.
     */
    public void addCompoisteRemovedRoleListener(
            CompositeRemoveRoleListener listener) {
        this.removeRoleListenerList.add(listener);
    }

    /**
     * Removes a CompositeRemoveRoleListener from the registered event
     * listeners.
     *
     * @param listener the CompositeRemoveRoleListener to be removed.
     */
    public void removeCompositeRoleListener(
            CompositeRemoveRoleListener listener) {
        this.removeRoleListenerList.remove(listener);
    }

    /**
     * Registers a new CompositeUpdateRoleListener to receive events when roles
     * are changed in Composite by the organiser as a result of change in one of
     * its binded contracts, e.g. a term being added or removed.
     * <p/>
     * This is intended to be used by deployment environments or other things
     * that need to act when something changes, e.g. updating a WSDL document in
     * the case of ROAD4WS.
     *
     * @param listener the new CompositeUpdateRoleListener wishing to receive events.
     */
    public void addCompositeUpdateRoleListener(
            CompositeUpdateRoleListener listener) {
        this.updateRoleListenerList.add(listener);
    }

    /**
     * Removes a CompositeUpdateRoleListener from the registered event
     * listeners.
     *
     * @param listener the CompositeUpdateRoleListener to be removed.
     */
    public void removeCompositeUpdateRoleListener(
            CompositeUpdateRoleListener listener) {
        this.updateRoleListenerList.remove(listener);
    }

    /**
     * Returns a list containing all the Composites roles (as IRole interfaces).
     *
     * @return the list of roles.
     */
    public List<IRole> getCompositeRoles() {
        ArrayList<IRole> list = new ArrayList<IRole>();
        list.addAll(this.roleMap.values());

        return list;
    }

    /**
     * Returns the organiser role for this Composite as an IOrganiser interface.
     *
     * @return the organiser role for this Composite.
     */
    public IOrganiserRole getOrganiserRole() {
        return organiserRole;
    }

    /**
     * Returns the description of the Composite.
     *
     * @return the description of the Composite.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name of the Composite.
     *
     * @return the name of the Composite.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the rulesDir of the Composite.
     *
     * @return the rulesDir of the Composite.
     */
    public String getRulesDir() {
        return rulesDir;
    }

    /**
     * Returns the directory which holds the transformations of this Composite.
     *
     * @return the transDir of the Composite.
     */
    public String getTransDir() {
        return transDir;
    }

    /**
     * Returns the routingRulesFileName of the Composite.
     *
     * @return the routingRulesFileName of the Composite.
     */
    public String getRoutingRulesFileName() {
        return routingRulesFileName;
    }

    /**
     * Returns the compositeRulesFileName of the Composite.
     *
     * @return the compositeRulesFileName of the Composite.
     */
    public String getCompositeRulesFileName() {
        return compositeRulesFileName;
    }

    /**
     * Returns a role (as IRole) that contains the specified unique id.
     *
     * @return the role or null if no role exists with the specified id.
     */
    public IRole getRoleByID(String roleId) {
        return roleMap.get(roleId);
    }

    /**
     * Returns a string containing the <code>Composites</code> <code>name</code>
     * and <code>description</code> in the format 'Composite name: blah.
     * Composite description: blah'.
     *
     * @return the description string.
     */
    public String toString() {
        return "Composite name: " + this.getName()
                + ". Composite description: " + this.getDescription();
    }

    /**
     * Sends notifications to all CompositeAddRoleListeners.
     *
     * @param newRole the new role added.
     */
    public void notifyAddRoleListeners(IRole newRole) {
        for (CompositeAddRoleListener l : addRoleListenerList) {
            l.roleAdded(newRole);
        }
    }

    /**
     * Sends notifications to all CompositeRemoveRoleListeners.
     *
     * @param removedRole the id of the role that was removed.
     */
    public void notifyRemoveRoleListeners(IRole removedRole) {
        for (CompositeRemoveRoleListener l : removeRoleListenerList) {
            l.roleRemoved(removedRole);
        }
    }

    /**
     * Sends notifications to all CompositeUpdateRoleListeners.
     *
     * @param updatedRole the role which was updated.
     */
    public void notifyUpdateRoleListeners(IRole updatedRole) {
        for (CompositeUpdateRoleListener l : updateRoleListenerList) {
            l.roleUpdated(updatedRole);
        }
    }

    /**
     * Loads up the Drools rules associated with this <code>Composite</code>
     * from the disk. Rules are assumed to be in the location specifed by the
     * properties file in the format 'composite''RULE_FILE_EXTENSION'. Generally
     * RULE_FILE_EXTENSION is the default '.drl'.
     *
     * @return the <code>DroolsCompositeRules</code> containing the loaded
     * drools rules.
     * @throws CompositeInstantiationException if the sought after rules file does not exist or the rules
     *                                         are invalid.
     */
    public DroolsCompositeRules loadCompositeRules(String ruleFile)
            throws CompositeInstantiationException {
        DroolsCompositeRules rules = null;
        try {
            rules = new DroolsCompositeRules(ruleFile, rulesDir,
                    (IInternalOrganiserView) organiserRole);
        } catch (RulesException e) {
            throw new CompositeInstantiationException(e.getMessage());
        }

        return rules;
    }

    /**
     * Procedure to add the contract to the routing table of each roles.
     *
     * @param a role A.
     * @param b role B.
     * @param c the contract to be added.
     */
    public void addToRoutingTable(Role a, Role b, Contract c) {
        // Retrieves a list of terms from the contract
        List<Term> listTerms = c.getTermList();
        for (Term t : listTerms) {
            // Putting the message signature dependent on the message
            // destination
            if (t.getDirection().equalsIgnoreCase("atob")) {
                // Put the request signature
                a.getRoutingTable().putRequestSignature(
                        t.getOperation().getName(), c);

                // If not void then put response signature
                if (!(t.getOperation().getReturnType().equalsIgnoreCase("void")))
                    b.getRoutingTable().putResponseSignature(    //TODO indika
                            t.getOperation().getName(), c);
            } else {
                // Put the request signature
                b.getRoutingTable().putRequestSignature(
                        t.getOperation().getName(), c);

                // If not void then put response signature
                if (!(t.getOperation().getReturnType().equalsIgnoreCase("void")))
                    a.getRoutingTable().putResponseSignature(
                            t.getOperation().getName(), c);          //TODO indika
            }
        }
    }

    /**
     * Extracts the folder path and file names for rule files.
     *
     * @param smcBinding
     */
    public void extractFileFolderPaths(ServiceNetwork smcBinding) {
        String pathSep = "/";

        if (smcBinding.getDataDir() != null) {
            this.rulesDir = smcBinding.getDataDir() + pathSep + "rules" + pathSep;
            this.transDir = smcBinding.getDataDir() + pathSep + "trans" + pathSep;
        }

        if (smcBinding.getRoutingRuleFile() != null) {
            routingRulesFileName = smcBinding.getRoutingRuleFile();
        }

        if (smcBinding.getCompositeRuleFile() != null) {
            compositeRulesFileName = smcBinding.getCompositeRuleFile();
        }
        if (smcBinding.getGlobalRegulation() != null) {
            globalKnowledgebase = new GlobalKnowledgebase(this, rulesDir, smcBinding.getGlobalRegulation().trim());
        } else {
            globalKnowledgebase = new GlobalKnowledgebase(this, rulesDir);
        }
    }

    /**
     * Extracts data from the <code>SMC</code> (a JAXB binding) and populates
     * the <code>Composite</code> using it.
     *
     * @param smcBinding the JAXB binding which is used to populate the
     *                   <code>Composite</code>.
     * @throws ConsistencyViolationException   if composite contains invalid or inconsistent values, e.g. a
     *                                         contract without two roles attached, which is not allowed in
     *                                         the ROAD meta-model.
     * @throws CompositeInstantiationException if an error occurs attempting to instantiate the
     *                                         <code>Composite</code> using the supplied JAXB binding or
     *                                         when attempting to read the rules files.
     */
    public void extractData(ServiceNetwork smcBinding)
            throws ConsistencyViolationException,
            CompositeInstantiationException {

        if (smcBinding.getDescription() != null) {
            description = smcBinding.getDescription();
        }

        if (smcBinding.getName() != null) {
            name = smcBinding.getName();
        }
        ServiceNetwork.Facts facts = smcBinding.getFacts();
        if (facts != null) {
            extractFacts(facts);
        }
        extractRoles(smcBinding.getRoles());
        extractContracts(smcBinding.getContracts());
        extractPlayerBindings(smcBinding.getServiceBindings());
        extractBehaviourData(smcBinding.getCollaborationUnits());
        deployInterProcessRegulationUnitData(smcBinding.getInterProcessRegulationUnits());
        deployInterCollaborationRegulationUnitData(smcBinding.getInterCollaborationRegulationUnits());
        extractProcesses(smcBinding);
        if (smcBinding.getInterVSNRegulation() != null) {
            deployNetworkLevelPolicies(smcBinding.getInterVSNRegulation(), ManagementState.STATE_ACTIVE);
        }

        // TODO: Extract Serendip descriptions. No we dont do that here.
    }

    public void deployNetworkLevelPolicies(InterVSNRegulationType ruType, String state) {
        String ruId = "network";
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                getContract(ruleIdType.getPlace()).getPassthroughRegTable().addVSNTableEntry("network",
                        new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state), ruId));
            }
            deployPassthroughRulesOfRegulationUnit(ruId, passThroughRef.getRuleRef());
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                getRole(synRuleID.getPlace()).getSynchronizationRegTable().addVSNTableEntry("network",
                        new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state), ruId));
            }
            deploySyncRulesOfRegulationUnit(ruId, synRegRuleRef.getRuleRef());
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                getRole(ruleIdType.getPlace()).getRoutingRegTable().addVSNTableEntry("network",
                        new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state), ruId));
            }
            deployRoutingRuleOfRegulationUnit(ruId, routingRuleRef.getRuleRef());
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            globalRegTable.addVSNTableEntry("network",
                    new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state), ruId));
            deployGlobalRuleOfRegulationUnit(ruId, globalRuleRef.getRuleRef());
        }
    }

    public void undeployNetworkLevelPolicies(List<String> ruIds) {
        InterVSNRegulationType ruType = smcBinding.getInterVSNRegulation();
        String ruId = "network";
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                getContract(ruleIdType.getPlace()).getPassthroughRegTable().removeVSNTableEntry("network", ruId);
            }
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                getRole(synRuleID.getPlace()).getSynchronizationRegTable().removeVSNTableEntry("network", ruId);
            }
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                getRole(ruleIdType.getPlace()).getRoutingRegTable().removeVSNTableEntry("network", ruId);
            }
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            globalRegTable.removeVSNTableEntry("network", ruId);
        }
    }

    public void extractProcesses(ServiceNetwork smc) {
        SMCDataExtractor extractor = new SMCDataExtractor(smc);
        for (ProcessDefinitionsType slice : extractor.getAllProcessDefinitions()) {
            deployVSNDefinition(slice);
        }
    }

    private void extractBehaviourData(CollaborationUnitsType collaborationUnitsType) {
        if (null == collaborationUnitsType) {
            log.warn("No collaborationUnitsType in the descriptor");
            return;
        }
        for (CollaborationUnitType collaborationUnitType : collaborationUnitsType.getCollaborationUnit()) {
            MonitorType monitorType = collaborationUnitType.getMonitor();
            if (monitorType != null) {
                addBehaviorMonitor(collaborationUnitType.getId(), monitorType);
            }
            RegulationDesignType designType = collaborationUnitType.getRegulationDesign();
            if (designType != null) {
                deployRegulationDesignOfCollaboration(collaborationUnitType.getId(), designType);
            }
        }
    }

    private void deployInterProcessRegulationUnitData(InterProcessRegulationUnitsType regulationUnitsType) {
        if (regulationUnitsType == null) {
            return;
        }
        for (InterProcessRegulationUnitType unitType : regulationUnitsType.getInterProcessRegulationUnit()) {
            addRegulationUnitState(new RegulationUnitState(unitType.getId()));
            RegulationRuleRef passThroughRef = unitType.getPassthrough();
            if (passThroughRef != null) {
                deployPassthroughRulesOfRegulationUnit(unitType.getId(), passThroughRef.getRuleRef());
            }
            RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
            if (synRegRuleRef != null) {
                deploySyncRulesOfRegulationUnit(unitType.getId(), synRegRuleRef.getRuleRef());
            }
            RegulationRuleRef routingRuleRef = unitType.getRouting();
            if (routingRuleRef != null) {
                deployRoutingRuleOfRegulationUnit(unitType.getId(), routingRuleRef.getRuleRef());
            }
            RegulationRuleRef globalRuleRef = unitType.getGlobal();
            if (globalRuleRef != null) {
                deployGlobalRuleOfRegulationUnit(unitType.getId(), globalRuleRef.getRuleRef());
            }
        }
    }

    private void deployInterCollaborationRegulationUnitData(InterCollaborationRegulationUnitsType regulationUnitsType) {
        if (regulationUnitsType == null) {
            return;
        }
        for (InterCollaborationRegulationUnitType unitType : regulationUnitsType.getInterCollaborationRegulationUnit()) {
            addRegulationUnitState(new RegulationUnitState(unitType.getId()));
            RegulationRuleRef passThroughRef = unitType.getPassthrough();
            if (passThroughRef != null) {
                deployPassthroughRulesOfRegulationUnit(unitType.getId(), passThroughRef.getRuleRef());
            }
            RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
            if (synRegRuleRef != null) {
                deploySyncRulesOfRegulationUnit(unitType.getId(), synRegRuleRef.getRuleRef());
            }
            RegulationRuleRef routingRuleRef = unitType.getRouting();
            if (routingRuleRef != null) {
                deployRoutingRuleOfRegulationUnit(unitType.getId(), routingRuleRef.getRuleRef());
            }
            RegulationRuleRef globalRuleRef = unitType.getGlobal();
            if (globalRuleRef != null) {
                deployGlobalRuleOfRegulationUnit(unitType.getId(), globalRuleRef.getRuleRef());
            }
        }
    }

    public void updeplolyInterProcessRegulationUnitType(InterProcessRegulationUnitType unitType) {
        RegulationRuleRef passThroughRef = unitType.getPassthrough();
        if (passThroughRef != null) {
            undeployPassthroughOfRegulationUnit(unitType.getId(), passThroughRef.getRuleRef());
        }
        RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
        if (synRegRuleRef != null) {
            undeploySyncRulesOfRegulationUnit(unitType.getId(), synRegRuleRef.getRuleRef());
        }
        RegulationRuleRef routingRuleRef = unitType.getRouting();
        if (routingRuleRef != null) {
            undeployRoutingRulesOfRegulationUnit(unitType.getId(), routingRuleRef.getRuleRef());
        }
        RegulationRuleRef globalRuleRef = unitType.getGlobal();
        if (globalRuleRef != null) {
            undeployGlobalRulesOfRegulationUnit(unitType.getId(), globalRuleRef.getRuleRef());
        }
    }

    public void updeplolyInterCollaborationRegulationUnitType(InterCollaborationRegulationUnitType unitType) {
        RegulationRuleRef passThroughRef = unitType.getPassthrough();
        if (passThroughRef != null) {
            undeployPassthroughOfRegulationUnit(unitType.getId(), passThroughRef.getRuleRef());
        }
        RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
        if (synRegRuleRef != null) {
            undeploySyncRulesOfRegulationUnit(unitType.getId(), synRegRuleRef.getRuleRef());
        }
        RegulationRuleRef routingRuleRef = unitType.getRouting();
        if (routingRuleRef != null) {
            undeployRoutingRulesOfRegulationUnit(unitType.getId(), routingRuleRef.getRuleRef());
        }
        RegulationRuleRef globalRuleRef = unitType.getGlobal();
        if (globalRuleRef != null) {
            undeployGlobalRulesOfRegulationUnit(unitType.getId(), globalRuleRef.getRuleRef());
        }
    }

    public void deployRegulationDesignOfCollaboration(String coId, RegulationDesignType unitType) {

        RegulationRuleRef passThroughRef = unitType.getPassthrough();
        if (passThroughRef != null) {
            deployPassthroughRulesOfRegulationUnit(coId, passThroughRef.getRuleRef());
        }
        RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
        if (synRegRuleRef != null) {
            deploySyncRulesOfRegulationUnit(coId, synRegRuleRef.getRuleRef());
        }
        RegulationRuleRef routingRuleRef = unitType.getRouting();
        if (routingRuleRef != null) {
            deployRoutingRuleOfRegulationUnit(coId, routingRuleRef.getRuleRef());
        }
        RegulationRuleRef globalRuleRef = unitType.getGlobal();
        if (globalRuleRef != null) {
            deployGlobalRuleOfRegulationUnit(coId, globalRuleRef.getRuleRef());
        }
    }

    public void undeployRegulationDesignOfCollaboration(String coId, RegulationDesignType unitType) {

        RegulationRuleRef passThroughRef = unitType.getPassthrough();
        if (passThroughRef != null) {
            undeployPassthroughOfRegulationUnit(coId, passThroughRef.getRuleRef());
        }
        RegulationRuleRef synRegRuleRef = unitType.getSynchronization();
        if (synRegRuleRef != null) {
            undeploySyncRulesOfRegulationUnit(coId, synRegRuleRef.getRuleRef());
        }
        RegulationRuleRef routingRuleRef = unitType.getRouting();
        if (routingRuleRef != null) {
            undeployRoutingRulesOfRegulationUnit(coId, routingRuleRef.getRuleRef());
        }
        RegulationRuleRef globalRuleRef = unitType.getGlobal();
        if (globalRuleRef != null) {
            undeployGlobalRulesOfRegulationUnit(coId, globalRuleRef.getRuleRef());
        }
    }

    public void deployVSNDefinition(ProcessDefinitionsType slice) {
        long interval = FlowControlConstraints.DEFAULT_INTERVAL;
        int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
        TrafficModelType trafficModelType = slice.getTraffic();
        if (trafficModelType != null) {
            String intervalStr = trafficModelType.getInterval();
            if (intervalStr != null && !intervalStr.isEmpty()) {
                interval = Long.parseLong(intervalStr.trim());
            }
            String maxRateStr = trafficModelType.getMaxCapacity();
            if (maxRateStr != null && !maxRateStr.isEmpty()) {
                threshold = Integer.parseInt(maxRateStr.trim());
            }
        }
        VSNDefinition group = new VSNDefinition(slice, serendipEngine);
        group.setInterval(interval);
        group.setThreshold(threshold);
        group.getMgtState().setState("active");
        serendipEngine.addProcessDefinitionGroup(group);
        VSNState vsnState = new VSNState(group.getId());
        serviceNetworkState.addVsnState(vsnState);
        //dummy code
        vsnState.putInCache("Throughput", new StateRecord("Throughput", new Throughput(100)));
        vsnState.putInCache("ResponseTime", new StateRecord("ResponseTime", new ResponseTime(45.49)));
        //
        deployVSNRegulationPolicy(group.getId(), slice.getInterProcessRegulationUnitRef(), ManagementState.STATE_ACTIVE);
        String vsnId = group.getId();
        for (ProcessDefinitionType pdDef : slice.getProcess()) {
            deployProcessOfVSN(vsnId, pdDef);
        }
    }

    public void deployVSNRegulationPolicy(String vsnId, List<String> regUnitIds, String state) {
        log.info("Deploying the regulation policy for the VSN : " + vsnId + " as " + regUnitIds);
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        for (String ruId : regUnitIds) {
            InterProcessRegulationUnitType ruType = extractor.getInterProcessRegulationUnitTypeById(ruId);
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().addVSNTableEntry(vsnId, new
                            RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId), ruId));
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().addVSNTableEntry(vsnId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId), ruId));
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().addVSNTableEntry(vsnId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId), ruId));
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.addVSNTableEntry(vsnId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId), ruId));
            }
        }
    }

    public void undeployVSNRegulationPolicy(String vsnId, List<String> regUnitIds) {
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        for (String ruId : regUnitIds) {
            InterProcessRegulationUnitType ruType = extractor.getInterProcessRegulationUnitTypeById(ruId);
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().removeVSNTableEntry(vsnId, ruId);
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().removeVSNTableEntry(vsnId, ruId);
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().removeVSNTableEntry(vsnId, ruId);
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.removeVSNTableEntry(vsnId, ruId);
            }
        }
    }

    public void deployProcessOfVSN(String vsnId, ProcessDefinitionType pdDef) {
        log.info("Deploying the process " + pdDef.getId() + " of the VSN : " + vsnId);
        VSNState vsnState = serviceNetworkState.getVsnState(vsnId);
        ProcessState processState = new ProcessState(vsnId, pdDef.getId());
        vsnState.addProcessState(processState);
        //dummy
        processState.putInCache("Throughput", new StateRecord("Throughput", new Throughput(50)));
        processState.putInCache("ResponseTime", new StateRecord("ResponseTime", new ResponseTime(46.50)));
        //
        VSNDefinition group = serendipEngine.getProcessDefinitionGroup(vsnId);
        ProcessDefinition processDefinition = new ProcessDefinition(pdDef, serendipEngine, this);
        processDefinition.getMgtState().setState("active");
        group.addProcessDefinition(processDefinition);
        String processId = pdDef.getId();
        deployProcessCollaborationUnitMappings(vsnId, processId, pdDef.getCollaborationUnitRef(), ManagementState.STATE_ACTIVE);
        deployProcessRegulationPolicy(vsnId, processId, pdDef.getInterCollaborationRegulationUnitRef(), ManagementState.STATE_ACTIVE);
    }

    public void deployProcessRegulationPolicy(String vsnId, String processId, List<String> ruIds, String state) {
        log.info("Deploying regulation policy of the process " + processId + " of the VSN : " + vsnId + " as " + ruIds);
        for (String ruId : ruIds) {
            InterCollaborationRegulationUnitType ruType = new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().addVSNTableEntry(vsnId + "_" +
                            processId, new
                            RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
            }
        }
    }

    public void deployProcessCollaborationUnitMappings(String vsnId, String processId, List<String> ruIds, String state) {
        log.info("Deploying regulation policy of the process " + processId + " of the VSN : " + vsnId + " as " + ruIds);
        for (String ruId : ruIds) {
            RegulationDesignType ruType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId).getRegulationDesign();
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().addVSNTableEntry(vsnId + "_" +
                            processId, new
                            RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.addVSNTableEntry(vsnId + "_" + processId, new RegulationUnitKey(new RegulationUnitKeyManagementState(ruId, state, vsnId, processId), ruId));
            }
        }
    }

    public void undeployProcessCollaborationUnitMappings(String vsnId, String processId, List<String> ruIds) {
        for (String ruId : ruIds) {
            RegulationDesignType ruType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId).getRegulationDesign();
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.removeVSNTableEntry(vsnId + "_" + processId, ruId);
            }
        }
    }

    public void undeployProcessRegulationPolicy(String vsnId, String processId, List<String> ruIds) {
        for (String ruId : ruIds) {
            InterCollaborationRegulationUnitType ruType = new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
            RegulationRuleRef passThroughRef = ruType.getPassthrough();
            if (passThroughRef != null) {
                for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                    getContract(ruleIdType.getPlace()).getPassthroughRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
            if (synRegRuleRef != null) {
                for (RegulationRuleIdType synRuleID : synRegRuleRef.getRuleRef()) {
                    getRole(synRuleID.getPlace()).getSynchronizationRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef routingRuleRef = ruType.getRouting();
            if (routingRuleRef != null) {
                for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                    getRole(ruleIdType.getPlace()).getRoutingRegTable().removeVSNTableEntry(vsnId + "_" + processId, ruId);
                }
            }
            RegulationRuleRef globalRuleRef = ruType.getGlobal();
            if (globalRuleRef != null) {
                globalRegTable.removeVSNTableEntry(vsnId + "_" + processId, ruId);
            }
        }
    }

    public void passivateRegulationUnitForProcess(String vsnId, String processId, String ruId) {
        ProcessDefinition processDefinition = serendipEngine.getProcessDefinitionGroup(vsnId).getProcessDefinition(processId);
        Map<String, ProcessInstance> pIns = processDefinition.getAllLiveInstances();
        Collection<String> pids = pIns.keySet();
        Collection<ProcessInstance> instances = pIns.values();
        processDefinition.reIncludeAllVSInstances(pids);
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        InterCollaborationRegulationUnitType ruType = extractor.getInterCollaborationRegulationUnitTypeById(ruId);
        RegulationRuleRef passThroughRef;
        RegulationRuleRef synRegRuleRef;
        RegulationRuleRef routingRuleRef;
        RegulationRuleRef globalRuleRef;
        if (ruType == null) {
            RegulationDesignType regulationDesign = extractor.getCollaborationUnitTypeById(ruId).getRegulationDesign();
            passThroughRef = regulationDesign.getPassthrough();
            synRegRuleRef = regulationDesign.getSynchronization();
            routingRuleRef = regulationDesign.getRouting();
            globalRuleRef = regulationDesign.getGlobal();
        } else {
            passThroughRef = ruType.getPassthrough();
            synRegRuleRef = ruType.getSynchronization();
            routingRuleRef = ruType.getRouting();
            globalRuleRef = ruType.getGlobal();
        }
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    if (!pids.isEmpty()) {
                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
                        unitKey.includeAllVSInstances(pids);
                        checked.add(ruleIdType.getPlace());
                        subscribeToVSNInstances(instances, unitKey);
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    } else {
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                        unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                    }
                }
            }
        }
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    if (!pids.isEmpty()) {
                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
                        unitKey.includeAllVSInstances(pids);
                        checked.add(ruleIdType.getPlace());
                        subscribeToVSNInstances(instances, unitKey);
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    } else {
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                        unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                    }
                }
            }
        }
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    if (!pids.isEmpty()) {
                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
                        unitKey.includeAllVSInstances(pids);
                        checked.add(ruleIdType.getPlace());
                        subscribeToVSNInstances(instances, unitKey);
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    } else {
                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
                        unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                    }
                }
            }
        }

        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    (vsnId + "_" + processId, ruId);
            if (!pids.isEmpty()) {
                unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
                unitKey.includeAllVSInstances(pids);
                subscribeToVSNInstances(instances, unitKey);
                unitKey.getMgtState().subscribe(policyEnactmentEngine);
            } else {
                unitKey.getMgtState().subscribe(policyEnactmentEngine);
                unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
            }
        }
    }

    public void passivateRegulationUnitForVSN(String vsnId, String ruId) {
//        ProcessDefinition processDefinition = serendipEngine.getProcessDefinitionGroup(vsnId).getProcessDefinition(processId);
//        Map<String, ProcessInstance> pIns = processDefinition.getAllLiveInstances();
//        Collection<String> pids = pIns.keySet();
//        Collection<ProcessInstance> instances = pIns.values();
//        processDefinition.reIncludeAllVSInstances(pids);
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        InterProcessRegulationUnitType ruType = extractor.getInterProcessRegulationUnitTypeById(ruId);
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    (vsnId, ruId);
//                    if (!pids.isEmpty()) {
//                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                        unitKey.includeAllVSInstances(pids);
//                        checked.add(ruleIdType.getPlace());
//                        subscribeToVSNInstances(instances, unitKey);
//                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
//                    } else {
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
//                    }
                }
            }
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    (vsnId, ruId);
//                    if (!pids.isEmpty()) {
//                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                        unitKey.includeAllVSInstances(pids);
//                        checked.add(ruleIdType.getPlace());
//                        subscribeToVSNInstances(instances, unitKey);
//                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
//                    } else {
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
//                    }
                }
            }
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    (vsnId, ruId);
//                    if (!pids.isEmpty()) {
//                        unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                        unitKey.includeAllVSInstances(pids);
//                        checked.add(ruleIdType.getPlace());
//                        subscribeToVSNInstances(instances, unitKey);
//                        unitKey.getMgtState().subscribe(policyEnactmentEngine);
//                    } else {
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
//                    }
                }
            }
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    (vsnId, ruId);
//            if (!pids.isEmpty()) {
//                unitKey.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                unitKey.includeAllVSInstances(pids);
//                subscribeToVSNInstances(instances, unitKey);
//                unitKey.getMgtState().subscribe(policyEnactmentEngine);
//            } else {
            unitKey.getMgtState().subscribe(policyEnactmentEngine);
            unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
//            }
        }
    }

    public void passivateRegulationUnitForServiceNetwork() {
        String ruId = "network";
        InterVSNRegulationType ruType = smcBinding.getInterVSNRegulation();
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                }
            }
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                }
            }
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().subscribe(policyEnactmentEngine);
                    unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                }
            }
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    ("network", ruId);
            unitKey.getMgtState().subscribe(policyEnactmentEngine);
            unitKey.getMgtState().setState(ManagementState.STATE_QUIESCENCE);
        }
    }

    private void subscribeToVSNInstances(Collection<ProcessInstance> instances, BaseManagedElement listner) {
        for (ProcessInstance instance : instances) {
            instance.getMgtState().subscribe(listner);
        }
    }

    public void activateRegulationUnitForProcess(String vsnId, String processId, String ruId) {
        Map<String, ProcessInstance> pIns = serendipEngine.getLiveProcessInstances(vsnId, processId);
        Collection<String> pids = pIns.keySet();
        Collection<ProcessInstance> instances = pIns.values();
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        InterCollaborationRegulationUnitType ruType = extractor.getInterCollaborationRegulationUnitTypeById(ruId);
        RegulationRuleRef passThroughRef;
        RegulationRuleRef synRegRuleRef;
        RegulationRuleRef routingRuleRef;
        RegulationRuleRef globalRuleRef;
        if (ruType == null) {
            RegulationDesignType regulationDesign = extractor.getCollaborationUnitTypeById(ruId).getRegulationDesign();
            passThroughRef = regulationDesign.getPassthrough();
            synRegRuleRef = regulationDesign.getSynchronization();
            routingRuleRef = regulationDesign.getRouting();
            globalRuleRef = regulationDesign.getGlobal();
        } else {
            passThroughRef = ruType.getPassthrough();
            synRegRuleRef = ruType.getSynchronization();
            routingRuleRef = ruType.getRouting();
            globalRuleRef = ruType.getGlobal();
        }
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                    if (!pids.isEmpty()) {
                        unitKey.excludeAllVSInstances(pids);
                        subscribeToVSNInstances(instances, unitKey);
                    }
                }
            }
        }
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                    if (!pids.isEmpty()) {
                        unitKey.excludeAllVSInstances(pids);
                        subscribeToVSNInstances(instances, unitKey);
                    }
                }
            }
        }
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    (vsnId + "_" + processId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                    if (!pids.isEmpty()) {
                        unitKey.excludeAllVSInstances(pids);
                        subscribeToVSNInstances(instances, unitKey);
                    }
                }
            }
        }
        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    (vsnId + "_" + processId, ruId);
            unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
            if (!pids.isEmpty()) {
                unitKey.excludeAllVSInstances(pids);
                subscribeToVSNInstances(instances, unitKey);
            }
        }
    }

    public void activateRegulationUnitForVSN(String vsnId, String ruId) {
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        InterProcessRegulationUnitType ruType = extractor.getInterProcessRegulationUnitTypeById(ruId);
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    (vsnId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
//                    if (!pids.isEmpty()) {
//                        unitKey.excludeAllVSInstances(pids);
//                        subscribeToVSNInstances(instances, unitKey);
//                    }
                }
            }
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    (vsnId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
//                    if (!pids.isEmpty()) {
//                        unitKey.excludeAllVSInstances(pids);
//                        subscribeToVSNInstances(instances, unitKey);
//                    }
                }
            }
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    (vsnId, ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
//                    if (!pids.isEmpty()) {
//                        unitKey.excludeAllVSInstances(pids);
//                        subscribeToVSNInstances(instances, unitKey);
//                    }
                }
            }
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    (vsnId, ruId);
            unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
//            if (!pids.isEmpty()) {
//                unitKey.excludeAllVSInstances(pids);
//                subscribeToVSNInstances(instances, unitKey);
//            }
        }
    }

    public void activateRegulationUnitForServiceNetwork() {
        String ruId = "network";
        InterVSNRegulationType ruType = smcBinding.getInterVSNRegulation();
        RegulationRuleRef passThroughRef = ruType.getPassthrough();
        if (passThroughRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                }
            }
        }
        RegulationRuleRef synRegRuleRef = ruType.getSynchronization();
        if (synRegRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                }
            }
        }
        RegulationRuleRef routingRuleRef = ruType.getRouting();
        if (routingRuleRef != null) {
            List<String> checked = new ArrayList<String>();
            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
                if (!checked.contains(ruleIdType.getPlace())) {
                    RegulationUnitKey unitKey =
                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getVSNTableEntry
                                    ("network", ruId);
                    unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
                    checked.add(ruleIdType.getPlace());
                }
            }
        }
        RegulationRuleRef globalRuleRef = ruType.getGlobal();
        if (globalRuleRef != null) {
            RegulationUnitKey unitKey = globalRegTable.getVSNTableEntry
                    ("network", ruId);
            unitKey.getMgtState().setState(ManagementState.STATE_ACTIVE);
        }
    }
//
//    private void passivateRegulationUnit(CollaborationUnitType ruType) {
//        RegulationRuleRef passThroughRef = ruType.getRegulationDesign().getPassthrough();
//        List<String> pids = serendipEngine.getLiveProcessInstancesIds();
//        String ruId = ruType.getId();
//        if (passThroughRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                    regulationRuleSet.includeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef synRegRuleRef = ruType.getRegulationDesign().getSynchronization();
//        if (synRegRuleRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                    regulationRuleSet.includeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef routingRuleRef = ruType.getRegulationDesign().getRouting();
//        if (routingRuleRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_PASSIVE);
//                    regulationRuleSet.includeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef globalRuleRef = ruType.getRegulationDesign().getGlobal();
//        if (globalRuleRef != null) {
//            RegulationRuleSet regulationRuleSet = globalRegTable.getRegulationRuleSet(ruId);
//            regulationRuleSet.getMgtState().setState(ManagementState.STATE_PASSIVE);
//            regulationRuleSet.includeAllVSInstances(pids);
//        }
//    }
//
//    private void activateRegulationUnit(CollaborationUnitType ruType) {
//        RegulationRuleRef passThroughRef = ruType.getRegulationDesign().getPassthrough();
//        List<String> pids = serendipEngine.getLiveProcessInstancesIds();
//        String ruId = ruType.getId();
//        if (passThroughRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : passThroughRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getContract(ruleIdType.getPlace()).getPassthroughRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_ACTIVE);
//                    regulationRuleSet.excludeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef synRegRuleRef = ruType.getRegulationDesign().getSynchronization();
//        if (synRegRuleRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : synRegRuleRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getRole(ruleIdType.getPlace()).getSynchronizationRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_ACTIVE);
//                    regulationRuleSet.excludeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef routingRuleRef = ruType.getRegulationDesign().getRouting();
//        if (routingRuleRef != null) {
//            List<String> checked = new ArrayList<String>();
//            for (RegulationRuleIdType ruleIdType : routingRuleRef.getRuleRef()) {
//                if (!checked.contains(ruleIdType.getPlace())) {
//                    RegulationRuleSet regulationRuleSet =
//                            getRole(ruleIdType.getPlace()).getRoutingRegTable().getRegulationRuleSet(ruId);
//                    regulationRuleSet.getMgtState().setState(ManagementState.STATE_ACTIVE);
//                    regulationRuleSet.excludeAllVSInstances(pids);
//                    checked.add(ruleIdType.getPlace());
//                }
//            }
//        }
//        RegulationRuleRef globalRuleRef = ruType.getRegulationDesign().getGlobal();
//        if (globalRuleRef != null) {
//            RegulationRuleSet regulationRuleSet = globalRegTable.getRegulationRuleSet(ruId);
//            regulationRuleSet.getMgtState().setState(ManagementState.STATE_ACTIVE);
//            regulationRuleSet.excludeAllVSInstances(pids);
//        }
//    }

    private ProcessPath createProcessPath(ProcessDefinitionType pdDef, VSNDefinition group) {
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        ProcessDefinition processDefinition = new ProcessDefinition(pdDef, serendipEngine, this);
        processDefinition.setPerformanceModel(createPerformanceModel(pdDef, group.getThreshold()));
        group.addProcessDefinition(processDefinition);
        MonitorType monitorType = pdDef.getMonitor();
        processDefinition.setProcessMonitor(monitorType);
        ProcessPath processPath = new ProcessPath(pdDef.getId());

        for (String btID : pdDef.getCollaborationUnitRef()) {
            CollaborationUnitType bt = extractor.getCollaborationUnitTypeById(btID);
            for (TaskRefType taskRefType : bt.getConfigurationDesign().getTaskRef()) {
                String refId = taskRefType.getId();
                String roleID = refId.substring(0, refId.indexOf("."));
                String taskId = refId.substring(refId.indexOf(".") + 1);
                Role role = (Role) getRoleByID(roleID);
                processPath.addNode(new Node(refId, role, role.getTaskType(taskId), taskRefType));
            }
        }
        processPath.buildPath();
        return processPath;
    }

    public void deployPassthroughRulesOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {

        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String ruleId = ruleIdType.getId();
            String contractId = ruleIdType.getPlace();
            Contract contract = getContract(contractId);
            PassthroughRegTable passthroughRegTable = contract.getPassthroughRegTable();
            RegulationRuleSet ruleSet = passthroughRegTable.getRegulationRuleSet(ruId);
            if (ruleSet == null) {
                ruleSet = new RegulationRuleSet(new RegulationRuleSetManagementState(ManagementState.STATE_ACTIVE));
                passthroughRegTable.addRuleSet(ruId, ruleSet);
            }
            ruleSet.addRuleName(ruleId);
        }
    }

    public void undeployPassthroughOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String contractId = ruleIdType.getPlace();
            Contract contract = getContract(contractId);
            PassthroughRegTable passthroughRegTable = contract.getPassthroughRegTable();
            passthroughRegTable.removeRegulationRuleSet(ruId);
        }
    }


    public void deploySyncRulesOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String ruleId = ruleIdType.getId();
            String roleID = ruleIdType.getPlace();
            Role role = getRole(roleID);
            SynchronizationRegTable regTable = role.getSynchronizationRegTable();
            RegulationRuleSet ruleSet = regTable.getRegulationRuleSet(ruId);
            if (ruleSet == null) {
                ruleSet = new RegulationRuleSet(new RegulationRuleSetManagementState(ManagementState.STATE_ACTIVE));
                regTable.addRuleSet(ruId, ruleSet);
            }
            ruleSet.addRuleName(ruleId);
        }
    }

    public void undeploySyncRulesOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String roleID = ruleIdType.getPlace();
            Role role = getRole(roleID);
            SynchronizationRegTable regTable = role.getSynchronizationRegTable();
            regTable.removeRegulationRuleSet(ruId);
        }
    }

    public void deployRoutingRuleOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String ruleId = ruleIdType.getId();
            String roleID = ruleIdType.getPlace();
            Role role = getRole(roleID);
            RoutingRegTable regTable = role.getRoutingRegTable();
            RegulationRuleSet ruleSet = regTable.getRegulationRuleSet(ruId);
            if (ruleSet == null) {
                ruleSet = new RegulationRuleSet(new RegulationRuleSetManagementState(ManagementState.STATE_ACTIVE));
                regTable.addRuleSet(ruId, ruleSet);
            }
            ruleSet.addRuleName(ruleId);
        }
    }

    public void undeployRoutingRulesOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String roleID = ruleIdType.getPlace();
            Role role = getRole(roleID);
            RoutingRegTable regTable = role.getRoutingRegTable();
            regTable.removeRegulationRuleSet(ruId);
        }
    }

    public void deployGlobalRuleOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        RegulationRuleSet ruleSet = globalRegTable.getRegulationRuleSet(ruId);
        if (ruleSet == null) {
            ruleSet = new RegulationRuleSet(new RegulationRuleSetManagementState(ManagementState.STATE_ACTIVE));
            ruleSet.getMgtState().setState(ManagementState.STATE_ACTIVE);
            globalRegTable.addRuleSet(ruId, ruleSet);
        }
        for (RegulationRuleIdType ruleIdType : ruleIdTypes) {
            String ruleId = ruleIdType.getId();
            ruleSet.addRuleName(ruleId);
        }
    }

    public void undeployGlobalRulesOfRegulationUnit(String ruId, List<RegulationRuleIdType> ruleIdTypes) {
        globalRegTable.removeRegulationRuleSet(ruId);
    }

    private void deployProcessDefinition(ProcessPath route, String sliceID) {
        VSNDefinition group = serendipEngine.getProcessDefinitionGroup(sliceID);
        ProcessDefinition routeDef = group.getProcessDefinition(route.getId());
        ProcessState processState = new ProcessState(sliceID, route.getId());
        String weightKey = route.getId() + "_wgt";
        processState.putInCache(weightKey, new StateRecord(weightKey, new Weight(routeDef.getWeight())));
        serviceNetworkState.getVsnState(sliceID).addProcessState(processState);
        for (Node node : route.getNodes()) {
            IRole thisRole = node.getRole();
            //TODO - we do not handle here when the response message is just ignored - but may need to monitoring
            for (Edge edge : node.getInEdges()) {
                Node source = edge.getSource();
                Contract inCon = getContract(edge.getSrcMsgType().getContractId());
                if (inCon == null) {
                    throw new RuntimeException("No contract by the id : " +
                            edge.getSrcMsgType().getContractId());
                }
                Role sourceRole;
                String operationName;
                if (source == null) {
                    sourceRole = inCon.getOppositeRole((Role) thisRole);
                    operationName =
                            inCon.getTermById(edge.getSrcMsgType().getTermId()).getOperation().getName();
                } else {
                    sourceRole = source.getRole();
                    InMsgType inMsgType =
                            source.getTaskType().getIn();
                    operationName = inMsgType.getOperation().getName() + (inMsgType.isIsResponse() ? "Response" : "Request");
                }
                //Routing table key
                String routingKey = sliceID + "." + sourceRole.getId() + "." + operationName;
                //Routing policy
                RoutingPolicy routingPolicy =
                        sourceRole.getRoutingTable().getRoutingPolicy(routingKey);
                if (routingPolicy == null) {
                    //TODO create a routing policy from the policy configuration
                    //Add routing rule file
                    routingPolicy = new RoutingPolicy(null, sliceID, sourceRole.getId());
                    if (sourceRole.getRoleType().isEntryRole()) {
                        routingPolicy.add(new WeightedRoundRobin());
                    }
                    routingPolicy.add(new Forward());
                    sourceRole.getRoutingTable().addRoutingPolicy(routingKey, routingPolicy);
                }
                // Setup routing function
                if (sourceRole.getRoleType().isEntryRole()) {
                    WeightedRoundRobin roundRobin =
                            (WeightedRoundRobin) routingPolicy.getRoutingFunction("WeightedRoundRobin");
                    Route route1 = new Route(route.getId());
                    route1.setThreshold(routeDef.getThreshold());
                    route1.setWeight(routeDef.getWeight());
                    roundRobin.addTarget(route1);
                }
                // Output interface at the source role for the process
                ResultMsgType result = edge.getResultMsgType();
                OutRouterPort outPort;
                if (result != null) {
                    outPort = new OutRouterPort(result);
                } else {
                    outPort = new OutRouterPort(inCon.getId());
                    outPort.setInteraction(edge.getSrcMsgType().getTermId());
                }
                Forward forward = (Forward) routingPolicy.getRoutingFunction("Forward");
                forward.addOutPort(route.getId(), outPort);

                // Ingress Flow Control Table Setup
                String flowControlKey = sliceID + "." + sourceRole.getId() + "." + operationName;
                FlowControlPolicy ingressFlowCtPolicy =
                        sourceRole.getIngressFlowControlTable().getFlowControlPolicy(flowControlKey);
                if (ingressFlowCtPolicy == null) {
                    ingressFlowCtPolicy = new FlowControlPolicy(null, sliceID, sourceRole.getId());
                    sourceRole.getIngressFlowControlTable().addFlowControlPolicy(flowControlKey, ingressFlowCtPolicy);
                }

                if (sourceRole.getRoleType().isEntryRole()) {
                    //per slice - not per path
                    if (!ingressFlowCtPolicy.contains("StaticFairRateController")) {
                        ingressFlowCtPolicy.add(new StaticFairRateController(group.getInterval(),
                                group.getThreshold(), this));
                    }
                } else {
                    //TODO local rate controller
                    if (!ingressFlowCtPolicy.contains("OnOffAdmissionController")) {
                        ingressFlowCtPolicy.add(new OnOffAdmissionController(true));
                    }
//                    TaskType taskType = node.getTaskType();
//                        QoSType qoSType = taskType.getQoS();
//                        int localRate = -1; // Indicate infinite
//                        if (qoSType != null) {
//                            String capUnitStr = qoSType.getCapacityUnit();
//                            if (qoSType != null) {
//                                if (capUnitStr != null && !capUnitStr.isEmpty()) {
//                                    localRate = threshold * Integer.parseInt(capUnitStr.trim());
//                                }
//                            }
//                        }
//                        //per slice - not per path
//                        ingressFlowCtPolicy.add(new LocalRateController(interval, localRate, getFTS()));
//                    }
                }
                //per slice - not per path
                String taskRef = node.getTaskRefType().getId();
                if (!group.contains(taskRef, flowControlKey)) {
                    group.addLocalFlowPolicyKey(node.getTaskRefType().getId(),
                            new FlowPolicyInfo(flowControlKey, sourceRole.getId()));
                }

                //Setup egress flow policy table

                String egressPolicyKey = outPort.getDesContract() + "." + outPort.getInteraction();
                FlowControlPolicy egressPolicy =
                        sourceRole.getEgressFlowControlTable().getFlowControlPolicy(egressPolicyKey);
                if (egressPolicy == null) {
                    egressPolicy = new FlowControlPolicy(null, sliceID, sourceRole.getId());
                    sourceRole.getEgressFlowControlTable().addFlowControlPolicy(egressPolicyKey, egressPolicy);
                }

                if (sourceRole.getRoleType().isEntryRole()) {
                    //per slice - not per path
                    SimpleForwarder simpleForwarder;
                    if (!egressPolicy.contains("SimpleForwarder")) {
                        simpleForwarder = new SimpleForwarder(this);
                        egressPolicy.add(simpleForwarder);
                    } else {
                        simpleForwarder =
                                (SimpleForwarder) egressPolicy.getFlowControlFunction("SimpleForwarder");
                    }
//                    String queueId = sliceID + "." + route.getId();
//                    if (!simpleForwarder.containQueue(queueId)) {
//                        simpleForwarder.addQueue(queueId,
//                                new LinkedBlockingQueue<MessageReceivedAtOutPortEvent>());
//                    }
                } else {
                    if (!egressPolicy.contains("SimpleForwarder")) {
                        //per slice - not per path
                        egressPolicy.add(new SimpleForwarder(this));
                    }
                }
            }
        }
    }

    private PerformanceModel createPerformanceModel(ProcessDefinitionType type, int threshold) {
        PerformanceModel performanceModel = new PerformanceModel();
        for (String behaviorTermRef : type.getCollaborationUnitRef()) {
            CollaborationUnitType termType = getCollaborationUnitTypeById(behaviorTermRef);
            for (TaskRefType taskRefType : termType.getConfigurationDesign().getTaskRef()) {
                String refId = taskRefType.getId();
                String roleID = refId.substring(0, refId.indexOf("."));
                String taskId = refId.substring(refId.indexOf(".") + 1);
                performanceModel.addTaskNode(new TaskNode(getTaskTypeById(roleID, taskId),
                        taskRefType, roleID, threshold));
            }
        }
        return performanceModel;
    }

    public void addProcessGroup(ProcessDefinitionsType slice) {
        smcBinding.getVirtualServiceNetwork().add(slice);
        deployVSNDefinition(slice);
    }

    public void addProcessToGroup(String pgId, ProcessDefinitionType processAlt) {
        ProcessDefinitionsType pdsType = new SMCDataExtractor(smcBinding).getProcessDefinitionGroup(pgId);
        pdsType.getProcess().add(processAlt);
        deployProcessOfVSN(pgId, processAlt);
    }

    public void removeProcessGroup(String sliceId) {
        ProcessDefinitionsType slice =
                new SMCDataExtractor(smcBinding).getProcessDefinitionGroup(sliceId);
        smcBinding.getVirtualServiceNetwork().remove(slice);
        serendipEngine.removeProcessDefinitionGroup(sliceId);
    }

    public void removeProcessFromGroup(String sliceId, String processId) {
        SMCDataExtractor dataExtractor = new SMCDataExtractor(smcBinding);
        ProcessDefinitionsType slice = dataExtractor.getProcessDefinitionGroup(sliceId);
        ProcessDefinitionType processDefinitionType =
                dataExtractor.getProcessDefinition(sliceId, processId);
        slice.getProcess().remove(processDefinitionType);
        serendipEngine.getProcessDefinitionGroup(sliceId).removeProcessDefinition(processId);
    }

    public ProcessDefinition getProcessDefinition(String vsnId, String processId) {
        return serendipEngine.getProcessDefinitionGroup(vsnId).getProcessDefinition(processId);
    }


    /**
     * Extracts data from the <code>PlayerBindings</code> (a JAXB binding) and
     * populates the <code>Composite</code> player bindings using it.
     *
     * @param pBindings the JAXB binding which is used to populate the
     *                  <code>Composite</code> player bindings.
     */
    public void extractPlayerBindings(ServiceNetwork.ServiceBindings pBindings) {
        if (null == pBindings) {
            log.warn("No player bindings in the descriptor");
            return;
        }
        List<PlayerBindingType> pBindingsTypeList = pBindings
                .getServiceBinding();
        for (PlayerBindingType pbt : pBindingsTypeList) {
            PlayerBinding pb = new PlayerBinding(pbt);
            this.playerBindingMap.put(pb.getId(), pb);
        }

        populateRolesWithPlayerBindings();
    }

    /**
     * Populates the <code>Role</code> objects in this <code>Composite</code>
     * with the binding information present in the <code>PlayerBinding</code>
     * objects located inside the playerBindingMap.
     */
    public void populateRolesWithPlayerBindings() {
        Collection<PlayerBinding> pbCollection = playerBindingMap.values();
        for (PlayerBinding pb : pbCollection) {
            updateRoleBindings(pb, true);
        }
    }

    public void updateRoleBindings(PlayerBinding pb, boolean bind) {
        Collection<Role> roleCollection = roleMap.values();
        for (Role role : roleCollection) {
            if (pb.isBoundToRole(role.getId())) {
                if (!bind) {
                    role.unBind();
                    continue;
                }
                if (pb.isEndpointBinding()) {
                    role.bind(pb.getEndpoint());
                } else {
                    role.bind(pb.getImplementation());
                }
            }
        }
    }

    /**
     * Extracts data from the <code>Roles</code> (a JAXB binding) and populates
     * the <code>Composite</code> roles using it.
     *
     * @param roles the JAXB binding which is used to populate the
     *              <code>Composite</code> roles.
     * @throws CompositeInstantiationException
     */
    public void extractRoles(ServiceNetwork.Roles roles)
            throws CompositeInstantiationException {
        if (null == roles) {
            log.warn("No roles in the descriptor");
            return;
        }
        List<RoleType> roleTypeList = roles.getRole();
        for (RoleType r : roleTypeList) {
            Role aRole = new Role(r, rulesDir);
            this.postRoleDeployment(aRole);
            aRole.extractFacts(r);
            aRole.getMgtState().setState((ManagementState.STATE_ACTIVE));
            aRole.getMgtState().subscribe(policyEnactmentEngine);
            for (TaskType tt : r.getTasks().getTask()) {
                String mep = tt.getMep();
                if (mep == null) {
                    mep = Task.OPTYPE_SOLI_RES;
                    tt.setMep(mep);
                }
                if (tt.getOut() != null) {
                    setOutMessageDirection(mep, tt.getOut());
                }
                if (tt.getIn() != null) {
                    setInMessageDirection(mep, tt.getIn());
                }
            }
        }
    }

    public void setOutMessageDirection(String mep, OutMsgType outMsgType) {
        boolean isResponse = false;
        if (Task.OPTYPE_NOTIFICATION.equals(mep)) {
            isResponse = true;
        }
        outMsgType.setIsResponse(isResponse);
    }

    public void setInMessageDirection(String mep, InMsgType inMsgType) {
        boolean isResponse = true;
        if (Task.OPTYPE_ONEWAY.equals(mep)) {
            isResponse = false;
        }
        inMsgType.setIsResponse(isResponse);
    }

    /**
     * Extracts data from the regulator JAXB binding and then populates the map
     * containing all the regulators for the <code>Composite</code>.
     *
     * @param facts the JAXB binding which is used to populate the
     *              <code>Composite</code> regulators.
     * @throws ConsistencyViolationException
     * @throws CompositeInstantiationException
     */

    public void extractFacts(ServiceNetwork.Facts facts) {
        if (null == facts) {
            return;
        }
        List<FactType> factList = facts.getFact();
        for (FactType factType : factList) {
            FactObject factObj = new FactObject(factType.getName(),
                    factType.getIdentifier(), "unidentified");

            if (factType.getSource().equalsIgnoreCase("external")) {
                factObj.setFactSource(FactObject.EXTERNAL_SOURCE);
            } else {
                factObj.setFactSource(FactObject.INTERNAL_SOURCE);
            }

            Attributes attributes = factType.getAttributes();
            if (attributes != null) {
                for (String attr : attributes.getAttribute())
                    factObj.setAttribute(attr, null);
            }
            FTS.createFactRow(factObj);
        }
        // log.info("FTS: \t" + FTS.toString());
    }

    /**
     * Extracts data from the <code>Contracts</code> (a JAXB binding) and
     * populates the <code>Composite</code> contracts using it.
     *
     * @param contracts the JAXB binding which is used to populate the
     *                  <code>Composite</code> contracts.
     */
    public void extractContracts(ServiceNetwork.Contracts contracts)
            throws ConsistencyViolationException,
            CompositeInstantiationException {
        if (null == contracts) {
            log.warn("No contracts in the descriptor");
            return;
        }
        List<ContractType> contractTypeList = contracts.getContract();
        for (ContractType c : contractTypeList) {
            Contract aCon = new Contract(c, rulesDir);
            this.addContractInternal(aCon, c.getRoleAID(), c.getRoleBID());
            aCon.extractFacts(c);
            aCon.getMgtState().setState(ManagementState.STATE_ACTIVE);
            aCon.getMgtState().subscribe(policyEnactmentEngine);
        }
    }


    /**
     * Returns a boolean indicating whether a role exists in the
     * <code>Composite</code> with the specified unique id.
     *
     * @param roleId the unique id of a role.
     * @return <code>true</code> if the role is found or <code>false</code> if
     * not.
     */
    public boolean containsRole(String roleId) {
        return this.roleMap.containsKey(roleId);
    }

    /**
     * Adds a new role to the <code>Composite</code>. This will also create an
     * new <code>RoadWorker</code> to route messages for this role. Furthermore
     * it will load the rules for routing rules. The file is the file which was
     * retrieved from the SMC on createion of this composite.
     *
     * @param newRole the new role to add to the <code>Composite</code>.     *
     * @throws CompositeInstantiationException
     */
    public void postRoleDeployment(Role newRole)
            throws CompositeInstantiationException {
        newRole.setComposite(this);
        roleMap.put(newRole.getId(), newRole);
        log.info("Adding role with id '" + newRole.getId() + "' to composite "
                + name);

        newRole.setScheduler(new MessageDeliverer(newRole, compositeRules));
//        workerList.add(newWorker);
//        if (alreadyStarted) {
//            newWorker.start();
//        }
    }

    /**
     * Function to delete <code>Role</code> from the composite. It removes the
     * <code>Role</code> from the role map. Additionally it will remove all
     * contracts which were connected to this <code>Role</code>.
     *
     * @param roleId the <code>Role</code> to be removed.
     * @return <code>true</code> if removed, <code>false</code> otherwise.
     */
    public IRole deleteRole(String roleId) {
        Role role = roleMap.get(roleId);
        if (role != null) {
            Contract[] cArr = role.getAllContracts();
            if (cArr != null) {
                for (Contract c : cArr) {
                    this.deleteContract(c.getId());
                }
            }
            roleMap.remove(role.getId());
            log.info("Role with id '" + roleId
                    + "' has been removed from this composite.");

            //TODO: Remove worker thread too? use workerList and role id - Kau (100% CPU problem)


            return role;
        }
        log.fatal("Role with id '" + roleId
                + "' can not be found in this composite.");
        return null;
    }

    /**
     * Adds a new contract to the <code>Composite</code>.
     *
     * @param newContract the new contract to add to the <code>Composite</code>.
     * @param roleAId     the unique id of role A which must already exist in the
     *                    <code>Composite</code>.
     * @param roleBId     the unique id of role B which must already exist in the
     *                    <code>Composite</code>.
     * @throws ConsistencyViolationException if the role unique id's supplied do not belong to any role in
     *                                       the <code>Composite</code>.
     */
    public void addContractInternal(Contract newContract, String roleAId,
                                    String roleBId) throws ConsistencyViolationException {

        if (!(containsRole(roleAId))) {
            throw new ConsistencyViolationException(roleAId + " in contract "
                    + newContract.getId() + " does not exist");
        }
        if (!(containsRole(roleBId))) {
            throw new ConsistencyViolationException(roleBId + " in contract "
                    + newContract.getId() + " does not exist");
        }

        // get roles and add them as party a and b
        Role roleA = this.roleMap.get(roleAId);
        Role roleB = this.roleMap.get(roleBId);

        newContract.setRoleA(roleA);
        newContract.setRoleB(roleB);

        roleA.addContract(newContract);
        roleB.addContract(newContract);

        // adds the newContract to each role to whom it belongs
//        this.addToRoutingTable(roleA, roleB, newContract);

        contractMap.put(newContract.getId(), newContract);
        newContract.setCompositeRules(this.compositeRules);

        newContract.setComposite(this);
        this.notifyUpdateRoleListeners(roleA);
        this.notifyUpdateRoleListeners(roleB);

        log.info("Adding contract " + newContract.getId() + " to composite "
                + name);
    }

    /**
     * Function that removes a <code>Contract</code> from the composite. This
     * function by default will remove all instances of the
     * <code>Contract</code> from both <code>Role</code>s in the composite, as
     * well from the contract map.
     *
     * @param contractId the id of the <code>Contract</code> to be removed as
     *                   <code>String</code>.
     * @return <code>true</code> if removal is successful, <code>false</code>
     * otherwise.
     */
    public Contract deleteContract(String contractId) {
        Contract contract = contractMap.get(contractId);
        if (contract != null) {
            contract.getRoleA().removeContract(contract);
            contract.getRoleB().removeContract(contract);
            this.contractMap.remove(contractId);

            this.notifyUpdateRoleListeners(contract.getRoleA());
            this.notifyUpdateRoleListeners(contract.getRoleB());

            log.info("Contract with the id '" + contractId
                    + "' has been removed from the composite");
        }
        log.info("Contract with the id '" + contractId
                + "' was not removed as it could not be found");
        return contract;
    }

    /**
     * Add a term to the contract with the given contract id.
     *
     * @param id       the new <code>Term</code> which will be added to the contract
     * @param newTerm
     * @param contract the contract id where the new term as to be added as
     *                 <code>String</code>.
     */
    public boolean addTermInternal(String id, Term newTerm, Contract contract) {

        contract.addTerm(newTerm);
        this.notifyUpdateRoleListeners(contract.getRoleA());
        this.notifyUpdateRoleListeners(contract.getRoleB());

        log.info("New term with id '" + id
                + "' has been added to the contract with the id '"
                + contract.getId() + "'");
        return true;
    }

    public boolean updateTermPrivate(String id, String contractId, String property, String value) {
        Contract contract = contractMap.get(contractId);
        if (contract != null) {
            Term term = contract.getTermById(id);
            if ("messageType".equals(property)) {
                term.setMessageType(value);
            } else if ("direction".equals(property)) {
                term.setDirection(value);
            }
            this.notifyUpdateRoleListeners(contract.getRoleA());
            this.notifyUpdateRoleListeners(contract.getRoleB());

            log.info("New term with id '" + id
                    + "' has been added to the contract with the id '"
                    + contractId + "'");
            return true;
        } else
            log.fatal("New term with id '" + id
                    + "' has NOT been added. Contract with the id '"
                    + contractId + "' can not be found!");

        return false;
    }

    /**
     * Function to delete <code>Term</code> from the composite. It removes the
     * <code>Term</code> from the <code>Contract</code> containing the
     * <code>Term</code>. It iterates through the <code>Contract</code>s in the
     * composite, and matching the <code>Term</code>s inside the term list to be
     * removed.
     *
     * @param termId the id of the <code>Term</code> to be removed as
     *               <code>String</code>.
     * @return <code>true</code> if removed, <code>false</code> otherwise.
     */
    public boolean deleteTerm(String ctId, String termId) {
        Contract c = contractMap.get(ctId);
        for (Term t : c.getTermList()) {
            if (t.getId().equals(termId)) {
                c.removeTerm(t);

                this.notifyUpdateRoleListeners(c.getRoleA());
                this.notifyUpdateRoleListeners(c.getRoleB());

                log.info("The term with the id '"
                        + termId
                        + "' has been removed from the contract with the id '"
                        + c.getId() + "'.");
                return true;
            }
        }
        log.info("The term with the id '" + termId
                + "' has NOT been removed. The term id can not be found!");
        return false;
    }

    /**
     * Change the operation in a term where the id and the new operation to set
     * is provided
     *
     * @param operationName New <code>operation</code> to set in the term
     * @param termId        The id of the <code>Term</code> to which the
     *                      <code>Operation</code> shoule be set
     */
    public boolean addOperation(String operationName,
                                String operationReturnType, Parameter[] parameters, String termId, String contractID) {

        // convert param array to list
        List<Parameter> paramList = Arrays.asList(parameters);

        // create the new operation
        Operation newOperation = new Operation(operationName, paramList,
                operationReturnType);

        boolean found = false;
        Term term = null;
        Contract contract = this.contractMap.get(contractID);

        // search for the contract where the term is located and save them
//        for (Contract c : this.contractMap.values()) {
        for (Term t : contract.getTermList()) {
            if (t.getId().equals(termId)) {
                term = t;
                found = true;
            }
        }
//        }

        // if no term with that id found get out
        if (!found) {
            log.fatal("Could not change operation. Term with id: '" + termId
                    + "' not found.");
            return false;
        }

        contract.updateRoutingTable(newOperation, term.getDirection());
        term.setOperation(newOperation);

        this.notifyUpdateRoleListeners(contract.getRoleA());
        this.notifyUpdateRoleListeners(contract.getRoleB());

        return true;
    }

    public boolean deleteOperation(String operationName, String termId) {
        boolean found = false;
        Term term = null;
        Contract contract = null;

        // search for the contract where the term is located and save them
        for (Contract c : this.contractMap.values()) {
            for (Term t : c.getTermList()) {
                if (t.getId().equals(termId)) {
                    term = t;
                    contract = c;
                    found = true;
                }
            }
        }

        // if no term with that id found get out
        if (!found) {
            log.fatal("Could not remove operation. Term with id: '" + termId
                    + "' not found.");
            return false;
        }

        contract.getRoleA().getRoutingTable()
                .removeOperationName(operationName, term, contract);
        contract.getRoleB().getRoutingTable()
                .removeOperationName(operationName, term, contract);

        term.setOperation(null);

        this.notifyUpdateRoleListeners(contract.getRoleA());
        this.notifyUpdateRoleListeners(contract.getRoleB());

        return true;
    }

    /**
     * Inserts a new rule which is written in the drools syntax to a given
     * contract
     *
     * @param newRule    the new rule which is written in drools syntax as
     *                   <code>String</code>.
     * @param contractId the id for the contract where the new rule should be inserted.
     * @return <code>true</code> if inserting the new rules was successful,
     * otherwise <code>false</code>.
     */
    public boolean insertNewRule(String ruleName, String newRule, String contractId) {
        Contract contract = contractMap.get(contractId);
        if (contract != null) {
            if (contract.addRule(ruleName, newRule)) {

                // Creating the RuleChangeTracker object and adding it to the
                // ruleChangeTrackerList for snapshot generation
                ruleChangeTrackerList
                        .add(new RuleChangeTracker("insert", contract
                                .getRules().getRuleFile(), newRule, contractId));
                log.info("A new rule has been inserted in the conract with the id '"
                        + contractId + "'.");

                this.notifyUpdateRoleListeners(contract.getRoleA());
                this.notifyUpdateRoleListeners(contract.getRoleB());
                return true;
            }
        }
        log.fatal("New rule couldn't be inserted in the contract with the id  '"
                + contractId + "'. Contract does not exist");
        return false;
    }

    /**
     * Remove a rule which is existent in a contracts drool rules
     *
     * @param contractId the id for the contract where the new rule should be inserted.
     * @param ruleName   The name of the rule which needs to be removed.
     */
    public boolean deleteRule(String contractId, String ruleName) {
        boolean result = false;
        if (contractMap.containsKey(contractId)) {
            Contract contract = contractMap.get(contractId);

            // Creating the RuleChangeTracker object and adding it to the
            // ruleChangeTrackerList for snapshot generation
            ruleChangeTrackerList.add(new RuleChangeTracker("remove", contract
                    .getRules().getRuleFile(), ruleName, contractId));

            result = contract.removeRule(ruleName);
            this.notifyUpdateRoleListeners(contract.getRoleA());
            this.notifyUpdateRoleListeners(contract.getRoleB());
        }

        return result;
    }

    public IOperationalManagerRole getOperationalManagerRole() {
        return operationalManagerRole;
    }

    /**
     * Returns the role map which contains all the roles for this Composite
     *
     * @return the role map which contains all the roles for this Composite
     */
    public Map<String, Role> getRoleMap() {
        return roleMap;
    }

    /**
     * Sets the role map to the desired role map
     *
     * @param roleMap which contains all the roles for this Composite
     */
    public void setRoleMap(Map<String, Role> roleMap) {
        this.roleMap = roleMap;
    }

    /**
     * Returns the contract map which contains all the contracts for this
     * Composite
     *
     * @return contract map which contains all the contracts for this Composite
     */
    public Map<String, Contract> getContractMap() {
        return contractMap;
    }

    public Contract getContract(String id) {
        return this.contractMap.get(id);
    }

    public Role getRole(String id) {
        return this.roleMap.get(id);
    }

    public boolean containsContract(String id) {
        return this.contractMap.containsKey(id);
    }

    /**
     * Sets the contract map to the desired contract map
     *
     * @param contractMap which contains all the contracts for this Composite
     */
    public void setContractMap(Map<String, Contract> contractMap) {
        this.contractMap = contractMap;
    }

    /**
     * Returns the player binding map which contains all the player bindings for
     * this Composite
     *
     * @return playerBindingMap which contains all the player bindings for this
     * Composite
     */
    public Map<String, PlayerBinding> getPlayerBindingMap() {
        return playerBindingMap;
    }

    /**
     * Sets the player binding map to the desired role map
     *
     * @param playerBindingMap map which contains all the player bindings for this Composite
     */
    public void setPlayerBindingMap(Map<String, PlayerBinding> playerBindingMap) {
        this.playerBindingMap = playerBindingMap;
    }

    /**
     * Returns the rule change tracker list which contains all the rule change
     * tracker objects for keeping track of all the rule changes for this
     * Composite
     *
     * @return rule change tracker list which contains all the rule change
     * tracker objects for keeping track of all the rule changes for
     * this Composite
     */
    public List<RuleChangeTracker> getRuleChangeTrackerList() {
        return ruleChangeTrackerList;
    }

    /**
     * Sets the rule change tracker list to the desired rule change tracker list
     *
     * @param ruleChangeTrackerList list which contains all the rule change tracker objects for
     *                              keeping track of all the rule changes for this Composite
     */
    public void setRuleChangeTrackerList(
            List<RuleChangeTracker> ruleChangeTrackerList) {
        this.ruleChangeTrackerList = ruleChangeTrackerList;
    }

    // Serendip

    /**
     * Returns the instance of the process engine in the composite.
     */
    public SerendipEngine getSerendipEngine() {
        return this.serendipEngine;
    }

    public FactTupleSpace getFTS() {
        return FTS;
    }

    public void setFTS(FactTupleSpace fts) {
        FTS = fts;
    }

    public BenchUtil getBenchUtil() {
        return this.benchUtil;
    }

    public ROADDeploymentEnv getRoadDepEnv() {
        return roadDepEnv;
    }

    public void setRoadDepEnv(ROADDeploymentEnv roadDepEnv) {
        this.roadDepEnv = roadDepEnv;
    }

    public BehaviorMonitor getBehaviorMonitor(String monitorId) {
        return monitorMap.get(monitorId);
    }

    public CollaborationUnitType getCollaborationUnitTypeById(String btId) {
        for (CollaborationUnitType btt : smcBinding.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(btId)) {
                return btt;
            }
        }
        return null;
    }

    private TaskType getTaskTypeById(String roleId, String tId) {
        return getRoleByID(roleId).getTaskType(tId);
    }

    public void addBehaviorMonitor(String btId, MonitorType monitorType) {
        String monitorId = monitorType.getId();
        if (monitorId == null || monitorId.isEmpty()) {
            monitorId = btId + "." + "monitor";
        }
        AnalysisType analysisType = monitorType.getAnalysis();
        String monitorFileName = analysisType.getScript();

        IMonitoringRules iMonitoringRules = new DroolsMonitoringRules(monitorFileName.toLowerCase(), getRulesDir(), getFTS());
        BehaviorMonitor behaviorMonitor = new BehaviorMonitor(monitorId, iMonitoringRules);
        for (MonitorEventType eventType : monitorType.getEvent()) {
            behaviorMonitor.addEventPattern(eventType.getPattern());
        }
        monitorMap.put(behaviorMonitor.getId(), behaviorMonitor);
    }

    public void removeBehaviorMonitor(String btId, String monitorId) {
        monitorMap.remove(monitorId);
        CollaborationUnitType termType = getCollaborationUnitTypeById(btId);
        termType.setMonitor(null);
    }

    public void setBehaviorMonitorRules(String btId, String monitorId, String monitorFileName) {
        CollaborationUnitType type = getCollaborationUnitTypeById(btId);
        type.getMonitor().getAnalysis().setScript(monitorFileName);
        monitorMap.get(monitorId).setMonitoringRules(new DroolsMonitoringRules(monitorFileName.toLowerCase(), getRulesDir(), getFTS()));
    }

    public ICompositeRules getCompositeRules() {
        return compositeRules;
    }

    public DroolsBasedManagementPolicyEnactmentEngine getPolicyEnactmentEngine() {
        return policyEnactmentEngine;
    }

    public SerendipOrganizer getSerendipOrganizer() {
        return serendipOrganizer;
    }

    public void setSerendipOrganizer(SerendipOrganizer serendipOrganizer) {
        this.serendipOrganizer = serendipOrganizer;
    }

    public void addRegulationMechanism(RegulationMechanism regulationMechanism) {
        this.regulationMechanisums.put(regulationMechanism.getId(), regulationMechanism);
    }

    public RegulationMechanism getRegulationMechanism(String id) {
        return regulationMechanisums.get(id);
    }

    public boolean containsRegulationMechanism(String id) {
        return regulationMechanisums.containsKey(id);
    }

    public RegulationMechanism removeRegulationMechanism(String id) {
        return regulationMechanisums.remove(id);
    }

    public void addRegulationUnitState(RegulationUnitState regulationUnitState) {
        this.regulationUnitStateMap.put(regulationUnitState.getId(), regulationUnitState);
    }

    public RegulationUnitState getRegulationUnitState(String id) {
        return regulationUnitStateMap.get(id);
    }

    public boolean containsRegulationUnitState(String id) {
        return regulationUnitStateMap.containsKey(id);
    }

    public RegulationUnitState removeRegulationUnitState(String id) {
        return regulationUnitStateMap.remove(id);
    }

    public ServiceNetworkState getServiceNetworkState() {
        return serviceNetworkState;
    }

    public void addSNStateImplementation(SNStateImplementation stateImplementation) {
        this.snStateImplementations.put(stateImplementation.getId(), stateImplementation);
    }

    public SNStateImplementation getSNStateImplementation(String id) {
        return snStateImplementations.get(id);
    }

    public boolean containsSNStateImplementation(String id) {
        return snStateImplementations.containsKey(id);
    }

    public SNStateImplementation removeSNStateImplementation(String id) {
        return snStateImplementations.remove(id);
    }

    public RolePushMessageListener getDefaultPushMessageListener() {
        return defaultPushMessageListener;
    }

    public void setDefaultPushMessageListener(RolePushMessageListener defaultPushMessageListener) {
        this.defaultPushMessageListener = defaultPushMessageListener;
    }

    public GlobalRegTable getGlobalRegTable() {
        return globalRegTable;
    }

    public GlobalKnowledgebase getGlobalKnowledgebase() {
        return globalKnowledgebase;
    }
}