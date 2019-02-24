package au.edu.swin.ict.road.common;

import org.apache.axiom.om.OMElement;

/**
 * <code>IOrganiser</code>. is an interface to be used by players of the
 * <code>Composite</code> organiser role. IOrganiser contains (or will contain
 * as development continues) all the methods required to configure a composites
 * contracts and structure.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public interface IOrganiserRole {
    /**
     * Adds a new <code>Role</code> to the <code>Composite</code>.
     *
     * @param id the new role to add.
     */
    public OrganiserMgtOpResult addRole(String id, String name);

    /**
     * Function to remove a <code>Role</code> from the composite.
     *
     * @param roleId the id of the <code>Role</code> to be removed as
     *               <code>String</code>.
     * @return <code>true</code> if removed, <code>false</code> otherwise.
     */
    public OrganiserMgtOpResult removeRole(String roleId);

    public OrganiserMgtOpResult updateRole(String roleId, String property, String value);

    // Player binding changes
    public OrganiserMgtOpResult setServiceBinding(String rId, String epr);

    public OrganiserMgtOpResult updateServiceBinding(String rId, String property, String value);

    public OrganiserMgtOpResult addTask(String roleId, String taskId, String inputs, String outputs);

    public OrganiserMgtOpResult removeTask(String roleId, String taskId);

    public OrganiserMgtOpResult updateTask(String roleID, String taskId, String action, String property, String value);

    public OrganiserMgtOpResult setTaskOutMessage(String roleId, String taskId, String opName, String parameters, String returnType);

    public OrganiserMgtOpResult setTaskMessage(String roleId, String taskId, String opName, String parameters, String returnType);

    public OrganiserMgtOpResult setTaskInMessage(String roleId, String taskId, String opName, String parameters, String returnType);

    public OrganiserMgtOpResult setTaskQoS(String roleId, String taskId, String QoSparameters);

    /**
     * Adds a new <code>Contract</code> to the <code>Composite</code>. The two
     * roles to bind to the new <code>Contract</code> must already exist inside
     * the <code>Composite</code>.
     *
     * @param id      the identifier of the ew contract to be added.
     * @param roleAId the unique id of role A.
     * @param roleBId the unique id of role B.
     */
    public OrganiserMgtOpResult addContract(String id, String roleAId, String roleBId);

    /**
     * Function to remove a <code>Contract</code> from the composite.
     *
     * @param contractId contract id for the <code>Contract</code> to be removed.
     * @return <code>true</code> if removed, <code>false</code> otherwise.
     */
    public OrganiserMgtOpResult removeContract(String contractId);

    public OrganiserMgtOpResult updateContract(String contractId, String property, String value);

    /**
     * Procedure to add a new <code>Term</code> in a <code>Contract</code>.
     * Hierarchically, a <code>Term</code> should be contained in a
     * <code>Contract</code>.
     *
     * @param termId     the new <code>Term</code> to be added.
     * @param contractId the contract id as <code>String</code> in which the
     *                   <code>Term</code> is added to.
     */
    public OrganiserMgtOpResult addTerm(String contractId, String termId, String direction);

    /**
     * Function to remove a <code>Term</code> from the composite.
     *
     * @param termId the id of the <code>Term</code> to be removed as
     *               <code>String</code>.
     * @return <code>true</code> if removed, <code>false</code> otherwise.
     */
    public OrganiserMgtOpResult removeTerm(String contractId, String termId);

    public OrganiserMgtOpResult updateTerm(String contractId, String termId, String property, String value);

    /**
     * Procedure to add a new <code>Operation</code> in a <code>Term</code> with
     * the term id. An <code>Operation</code> should be contained in a
     * <code>Term</code>.
     *
     * @param operationName the new <code>Operation</code> to be added.
     * @param termId        the term id as <code>String</code> in which the
     *                      <code>Operation</code> is to be added
     */
    public OrganiserMgtOpResult setTermOperation(String contractId, String termId, String operationName, String parameters, String returnType);

    public OrganiserMgtOpResult addBehaviorUnit(String buId);

    public OrganiserMgtOpResult removeBehaviorUnit(String buId);

    public OrganiserMgtOpResult updateBehaviorUnit(String buId, String property, String value);

    public OrganiserMgtOpResult addTaskToBehaviorUnit(String buId, String tId, String preep, String postep);

    public OrganiserMgtOpResult removeTaskFromBehaviorUnit(String buId, String tId);

    public OrganiserMgtOpResult updateTaskOfBehaviorUnit(String buId, String tId, String property, String value);

    public OrganiserMgtOpResult addVSN(String vsnId);

    public boolean containsVSN(String vsnId);

    public OrganiserMgtOpResult removeVSN(String vsnId);

    public OrganiserMgtOpResult updateVSN(String vsnId, String property, String value);

    public OrganiserMgtOpResult addProcessToVSN(String vsnId, String processId, String CoS, String CoT);

    public OrganiserMgtOpResult removeProcessFromVSN(String vsnId, String processId);

    public OrganiserMgtOpResult updateProcessOfVSN(String vsnId, String processId, String property, String value);

    public OrganiserMgtOpResult addBehaviorUnitsToProcess(String vsnId, String processId, String buIds);

    public OrganiserMgtOpResult addBehaviorUnitToProcess(String vsnId, String processId, String buId);

    public OrganiserMgtOpResult removeBehaviorUnitFromProcess(String vsnId, String processId, String buId);

    public OrganiserMgtOpResult replaceBehaviorUnitOfProcess(String vsnId, String processId, String buIdOld, String buIdNew);

    public String getName();

    /**
     * Returns the next MessageWrapper in the organisers message queue. The
     * MessageWrapper should contain a management related message sent from one
     * of the roles inside the <code>Composite</code>.
     *
     * @return the next <code>MessageWrapper</code> in the organisers queue.
     */
    public OMElement getNextManagementMessageBlocking();

    public OMElement getNextManagementMessage(long timeout);

    public OrganiserMgtOpResult subscribeToManagementMessages(String epPattern, String notificationOperation);

    public OrganiserMgtOpResult enactOrganizationalManagementPolicy(String policyId, String policyFile);

    public OrganiserMgtOpResult enactOrganizationalManagementPolicyRemote(String policyId, OMElement policyFile);

    public OrganiserMgtOpResult setOrganizerBinding(String epr);

    public OrganiserMgtOpResult deployVSNAsXML(OMElement vsnConf);

    public OrganiserMgtOpResult deployProcessAsXML(String vsnId, OMElement vsnConf);

    public OrganiserMgtOpResult deployProcessConfigDesignAsXML(String vsnId, String processId, OMElement vsnConf);
//
//    /**
//     * Allows the organiser to send management related messages to specific
//     * roles inside a standard <code>MessageWrapper</code>. If the specified
//     * recipient role does not exist the method return <code>false</code>, or
//     * <code>true</code> otherwise.
//     *
//     * @param msg               the <code>MessageWrapper</code> to send.
//     * @param destinationRoleId the recipient roles unique id.
//     * @return <code>true</code> if recipient role exists, <code>false</code> if
//     * not.
//     */

//    /**
//     * Returns a reference to a <code>Contract</code> based on its unique id.
//     *
//     * @param id the id of the desired contract.
//     * @return the contract or false if a Contract with the specified unique id
//     * does not exist.
//     */
//    public Contract getContractById(String id);
//
//    /**
//     * Generates a snapshot of the current runtime composite. The snapshot
//     * includes the SMC file and all the drool files related to this composite.
//     * The latest rule modifications done are also included in the snapshot. The
//     * snapshot is stored at a default location in the 'data' folder.
//     *
//     * @return true if the snapshot generation was successful
//     */
//    public OrganiserMgtOpResult takeSnapshot();
//
//    public OrganiserMgtOpResult takeSnapshot(String folder);
//
//    //Newly added methods
//    public OrganiserMgtOpResult changePlayerBinding(String roleId, String endpoint);
//
//    public FactObject getFact(String factType, String factIdentifierValue);
//
//    public OrganiserMgtOpResult updateFact(FactObject factObject);
//
//    public OrganiserMgtOpResult addFact(String factType, FactObject factObject);
//
//    public OrganiserMgtOpResult removeFact(String factType, String factIdentifierValue);

//    public OrganiserMgtOpResult sendManagementMessage(MessageWrapper msg,
//                                                          String destinationRoleId);

//    // Player binding changes
//    public OrganiserMgtOpResult addServiceBinding(String pbId, String rId, String endpoint);
//
//    public OrganiserMgtOpResult removeServiceBinding(String pbId);

//    public OrganiserMgtOpResult removeOperation(String operationName,
//                                                    String termId);
//    public OrganiserMgtOpResult addNewOperation(String operationName,
//                                                    String operationReturnType, Parameter[] parameters, String termId, String contractId);

//    public OrganiserMgtOpResult addNewTerm(String id, String name,
//                                               String messageType, String deonticType, String description,
//                                               String direction, String contractId);
//    OrganiserMgtOpResult addNewContract(String id, String name,
//                                            String description, String state, String type, String ruleFile,
//                                            boolean isAbstract, String roleAId, String roleBId);
//    public OrganiserMgtOpResult updateServiceBinding2(String pbId, String property, String value);

//    public OrganiserMgtOpResult setOutMessageType2(String deliveryType, boolean isResponse, String operationName,
//                                                      String operationReturnType, Parameter[] parameters, String tId, String rId);
//
//    public OrganiserMgtOpResult setInMessageType2(boolean isResponse, String operationName,
//                                                     String operationReturnType, Parameter[] parameters, String tId, String rId);
}
