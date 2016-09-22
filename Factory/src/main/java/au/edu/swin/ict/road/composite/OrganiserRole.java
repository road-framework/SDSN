package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.common.Parameter;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Term;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.composite.exceptions.ConsistencyViolationException;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.player.PlayerBinding;
import au.edu.swin.ict.road.xml.bindings.*;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.core.ProcessDefinition;
import au.edu.swin.ict.serendip.core.VSNDefinition;
import au.edu.swin.ict.road.common.StatWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <code>OrganiserRole<code> is an implementation of the <code>IOrganiserRole</code>
 * interface. Because its an inner class to <code>Composite</code> it can
 * access all of its private instance variables and methods to make
 * reconfiguring the contracts and structure easier.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class OrganiserRole implements IOrganiserRole {
    private static Logger log = Logger.getLogger(OrganiserRole.class.getName());
    private Composite composite;
    private LinkedBlockingQueue<MessageWrapper> outQueue;

    /**
     * Creates a new instance of <code>OrganiserRole</code>.
     */
    public OrganiserRole(Composite composite) {
        this.composite = composite;
        outQueue = new LinkedBlockingQueue<MessageWrapper>();
    }

    /**
     * Puts a <code>MessageWrapper</code> containing a management related
     * message into the organisers message queue for the organiser to
     * retrieve. This method is for internal <code>Composite</code> use and
     * is not intended to be exposed publicly to any role.
     *
     * @param message the MessageWrapper intended for the organiser.
     */
//    @Override
//    public void sendToOrganiser(MessageWrapper message) {
//        outQueue.add(message);
//        if (log.isDebugEnabled()) {
//            log.debug("Added a management message the organiser roles outQueue");
//        }
//    }

    /**
     * Returns the next MessageWrapper in the organisers message queue. The
     * MessageWrapper should contain a management related message sent from
     * one of the roles inside the <code>Composite</code>.
     *
     * @return the next <code>MessageWrapper</code> in the organisers queue.
     */
    @Override
    public OMElement getNextManagementMessageBlocking() {
        try {
            log.info("retrieving next message for organiser");
            return (OMElement) outQueue.take().getMessage();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public OMElement getNextManagementMessage(long timeout) {
        try {
            if (log.isInfoEnabled()) {
                log.info("retrieving next message for organiser with a timeout");
            }
            return (OMElement) outQueue.poll(timeout, TimeUnit.MILLISECONDS).getMessage();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public OrganiserMgtOpResult subscribeToManagementMessages(String epPattern, String notificationOperation) {
        //TODO
        return null;
    }

    @Override
    public OrganiserMgtOpResult enactOrganizationalManagementPolicy(String policyId, String policyFile) {
        log.info("Got a management policy to admit \n" + policyFile);

        try {
            long start = System.nanoTime();
            composite.getPolicyEnactmentEngine().enactManagementPolicy(policyId, policyFile);
            long stop = System.nanoTime();
            return new OrganiserMgtOpResult(true, "Management policy applied in ." + (stop - start) + "nanoseconds");
        } catch (Exception e) {
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Management policy enactment has failed." + e.getMessage());
        }
    }

    @Override
    public OrganiserMgtOpResult enactOrganizationalManagementPolicyRemote(String policyId, OMElement policyFile) {
        log.info("Got a management policy to admit \n" + policyId);
        OMText binaryNode = (OMText) (policyFile).getFirstOMChild();
        DataHandler actualDH = (DataHandler) binaryNode.getDataHandler();
        try {
            long start = System.nanoTime();
            composite.getPolicyEnactmentEngine().enactManagementPolicy(policyId, actualDH);
            long stop = System.nanoTime();
            return new OrganiserMgtOpResult(true, "Management policy applied in ." + (stop - start) + "nanoseconds");
        } catch (Exception e) {
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Management policy enactment has failed." + e.getMessage());
        }
    }

    @Override
    public OrganiserMgtOpResult setOrganizerBinding(String epr) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: Set OrganiserBinding with epr : " + epr);
        }
        String pbId = composite.getSmcBinding().getOrganiserBinding();
        if (pbId == null) {
            pbId = "Organiser" + "_SB";
        }
        addServiceBinding(pbId, null, epr);
        composite.getSmcBinding().setOrganiserBinding(pbId);
        return new OrganiserMgtOpResult(true, "Set OrganiserBinding with epr : " + epr);
    }

    @Override
    public OrganiserMgtOpResult deployVSNAsXML(OMElement vsnConf) {
        log.info("Organiser: deploying a new VSN configuration");
        long startTime = System.currentTimeMillis();
        ServiceNetwork definitionsType =
                (ServiceNetwork) JaxbFactory.toObjects(
                        vsnConf.getFirstElement(), new Class[]{ServiceNetwork.class, ProcessDefinitionsType.class, ProcessDefinitionType.class,
                                                               InterProcessRegulationUnitsType.class, InterProcessRegulationUnitsType.class, QoSType.class, TrafficModelType.class, ConstraintsType.class, MonitorType.class});
        composite.addProcessGroup(definitionsType.getVirtualServiceNetwork().get(0));
        long endTime = System.currentTimeMillis();
        StatWriter.writeResTime("Service", endTime - startTime);
        return new OrganiserMgtOpResult(true, "Deployed VSN configuaration  successfully : " + definitionsType.getVirtualServiceNetwork().get(0).getId());
    }

    @Override
    public OrganiserMgtOpResult deployProcessAsXML(String vsnId, OMElement vsnConf) {
        return null;
    }

    @Override
    public OrganiserMgtOpResult deployProcessConfigDesignAsXML(String vsnId, String processId, OMElement vsnConf) {
        return null;
    }


    /**
     * Allows the organiser to send management related messages to specific
     * roles inside a standard <code>MessageWrapper</code>. If the specified
     * recipient role does not exist the method returns <code>false</code>,
     * or <code>true</code> otherwise.
     *
     * @param msg               the <code>MessageWrapper</code> to send.
     * @param destinationRoleId the recipient roles unique id.
     * @return <code>true</code> if recipient role exists,
     * <code>false</code> if not.
     */
    private OrganiserMgtOpResult sendManagementMessage(
            MessageWrapper msg, String destinationRoleId) {

        log.info("Sending a management message to Role:"
                 + destinationRoleId + " from the" + "organiser role");

        if (composite.getRoleMap().containsKey(destinationRoleId)) {
            Role destRole = composite.getRoleMap().get(destinationRoleId);
            destRole.organiserPutOutgoingManagement(msg);
            return new OrganiserMgtOpResult(true,
                                            "Management message sent");
        } else
            return new OrganiserMgtOpResult(false,
                                            "Management message not sent. Destination role not found");
    }

    @Override
    public OrganiserMgtOpResult addRole(String id, String name) {
        log.info("Organiser: addRole, roleId: " + id);

        RoleType roleType = new RoleType();
        roleType.setId(id.trim());
        roleType.setName(name.trim());
//        roleType.setRouting(routingRules);
//        roleType.setSynchronization(syncRules);
        Role newRole = new Role(roleType, composite.getRulesDir());
        newRole.getMgtState().subscribe(composite.getPolicyEnactmentEngine());
        composite.getSmcBinding().getRoles().getRole().add(roleType);
        composite.getSerendipEngine().subscribe(newRole);
        try {
            composite.postRoleDeployment(newRole);
            composite.notifyAddRoleListeners(newRole);
            return new OrganiserMgtOpResult(true, "New role "
                                                  + newRole.getId() + " added successfully");
        } catch (CompositeInstantiationException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        }
    }

    @Override
    public OrganiserMgtOpResult removeRole(String roleId) {
        log.info("Organiser: remove, roleId: " + roleId);
        Role removedRole = (Role) composite.deleteRole(roleId.trim());
        if (removedRole != null) {
            removedRole.getMgtState().notifyRemoval();
            composite.notifyRemoveRoleListeners(removedRole);
            return new OrganiserMgtOpResult(true, "Role" + roleId
                                                  + " has been sucessfully removed");
        } else
            return new OrganiserMgtOpResult(false, "Role" + roleId
                                                   + " was not removed. Role not found");
    }

    @Override
    public OrganiserMgtOpResult updateRole(String roleId, String property, String value) {
        log.info("Organiser: updateRole, roleId: " + roleId);
        value = value.trim();
        property = property.trim();
        Role role = composite.getRole(roleId.trim());
        if ("name".equals(property)) {
            role.setName(value);
        } else if ("state".equals(property)) {
            role.getMgtState().setState(value);
        } else if ("synchronization".equals(property)) {
            role.setSynchronization(value);
        } else if ("routing".equals(property)) {
            role.setRouting(value);
        }
        return new OrganiserMgtOpResult(true, "Property " + property + "of Role" + roleId
                                              + " was successfully updated with value " + value);
    }

    @Override
    public OrganiserMgtOpResult setServiceBinding(String rId, String epr) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: UpdateServiceBinding, roleId: " + rId);
        }
        String pbId = rId + "_SB";
        addServiceBinding(pbId, rId, epr);
        return new OrganiserMgtOpResult(true, "SetServiceBinding " + pbId + " successful for the role " + rId);
    }

    @Override
    public OrganiserMgtOpResult updateServiceBinding(String rId, String property, String value) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: UpdateServiceBinding, roleId: " + rId);
        }
        String pbId = rId + "_SB";
        if ("epr".equals(property)) {
            updateServiceBinding2(pbId, "endpoint", value);
        }
        return new OrganiserMgtOpResult(true, "UpdateServiceBinding " + pbId + " successful for the role " + rId);

    }

    private OrganiserMgtOpResult addNewContract(String id, String name,
                                                String description, String state, String type, String ruleFile,
                                                boolean isAbstract, String roleAId, String roleBId) {
        log.info("Organiser: addNewContract, contractId: " + id);

        Contract newContract = null;
        try {
            newContract = new Contract(id.trim(), name.trim(), description, state.trim(), type,
                                       ruleFile, isAbstract, composite.getRulesDir());
            newContract.getMgtState().subscribe(composite.getPolicyEnactmentEngine());
            composite.addContractInternal(newContract, roleAId, roleBId);
            return new OrganiserMgtOpResult(true, "Contract "
                                                  + newContract.getId() + " was successfully added");
        } catch (CompositeInstantiationException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        } catch (ConsistencyViolationException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        }
    }

    @Override
    public OrganiserMgtOpResult removeContract(String contractId) {
        log.info("Organiser: removeContract, contractId: " + contractId);
        Contract result = composite.deleteContract(contractId);
        if (result != null) {
            result.getMgtState().notifyRemoval();
            return new OrganiserMgtOpResult(true,
                                            "Contract with the id '" + contractId
                                            + "' has been removed from thcomposite");
        } else {
            return new OrganiserMgtOpResult(false,
                                            "Contract with the id '" + contractId
                                            + "' was not removed as it could not be found");
        }
    }

    @Override
    public OrganiserMgtOpResult updateContract(String contractId, String property, String value) {
        log.info("Organiser: update contract , contractId: " + contractId);
        property = property.trim();
        value = value.trim();
        contractId = contractId.trim();
        Contract contract = composite.getContractMap().get(contractId);
        if (contract != null) {
            if ("state".equals(property)) {
                contract.getMgtState().setState(value);
            } else if ("passthrough".equals(property)) {
                try {
                    contract.setPassthroughRules(value);
                } catch (CompositeInstantiationException e) {
                    return new OrganiserMgtOpResult(false,
                                                    "Error setting passthrough of the contract " + contractId + ", error : " + e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    public OrganiserMgtOpResult addTerm(String contractId, String termId, String direction) {
        log.info("Organiser: addwTerm, contractId: " + contractId);
        Contract contract = composite.getContractMap().get(contractId);
        if (contract == null) {
            log.fatal("New term with id '" + contractId
                      + "' has NOT been added. Contract with the id '"
                      + contractId + "' can not be found!");

        } else {
            boolean result = composite.addTermInternal(termId, new Term(termId, direction, contract.getContractRules()), contract);
            if (result) {
                return new OrganiserMgtOpResult(result, "Term " + termId + " was successfully added");
            }
        }
        return new OrganiserMgtOpResult(
                false,
                "Term "
                + contractId
                + " could not be added as the specified contract does not exist");
    }

    private OrganiserMgtOpResult addNewTerm(String id, String name,
                                            String messageType, String deonticType, String description,
                                            String direction, String contractId) {
        log.info("Organiser: addNewTerm, contractId: " + contractId);
        Contract contract = composite.getContractMap().get(contractId);

        boolean result = composite.addTermInternal(id, new Term(id, name, messageType, deonticType,
                                                                description, contract.getContractRules(), direction), contract);
        if (result)
            return new OrganiserMgtOpResult(result, "Term " + id
                                                    + " was successfully added");
        else
            return new OrganiserMgtOpResult(
                    result,
                    "Term "
                    + id
                    + " could not be added as the specified contract does not exist");
    }

    public OrganiserMgtOpResult updateTerm(String contractId, String termId, String property, String value) {
        log.info("Organiser: updateTerm, contractId: " + contractId);
        boolean result = composite.updateTermPrivate(termId, contractId, property, value);
        if (result) {
            return new OrganiserMgtOpResult(result, "Term " + termId
                                                    + " was successfully added");
        } else {
            return new OrganiserMgtOpResult(
                    result,
                    "Term "
                    + termId
                    + " could not be added as the specified contract does not exist");
        }
    }

    @Override
    public OrganiserMgtOpResult setTermOperation(String contractId, String termId, String operationName, String parameters, String returnType) {
        return addNewOperation(operationName, returnType, createParameters(parameters), termId, contractId);
    }

    @Override
    public OrganiserMgtOpResult removeTerm(String ctId, String termId) {
        log.info("Organiser: removeTerm, termId: " + termId);
        boolean result = composite.deleteTerm(ctId.trim(), termId.trim());
        if (result)
            return new OrganiserMgtOpResult(result, "Term " + termId
                                                    + " was successfully removed");
        else
            return new OrganiserMgtOpResult(result, "Term " + termId
                                                    + " was not found");
    }


    private OrganiserMgtOpResult addNewOperation(String operationName,
                                                 String operationReturnType, Parameter[] parameters,
                                                 String termId, String contractName) {
        log.info("Organiser: addNewOperation");
        boolean result = composite.addOperation(operationName.trim(), operationReturnType.trim(),
                                                parameters, termId.trim(), contractName.trim());
        if (result)
            return new OrganiserMgtOpResult(result,
                                            "Operation successfully added");
        else
            return new OrganiserMgtOpResult(result,
                                            "Could not change operation. " + "Term with id: '"
                                            + termId + "' not found.");
    }

    private OrganiserMgtOpResult setOutMessageType(String deliveryType,
                                                   String operationName,
                                                   String operationReturnType,
                                                   Parameter[] parameters, String tId, String rId) {
        log.info("Organiser: setOutMessageType");
        Role role = composite.getRoleMap().get(rId.trim());
        if (role == null) {
            return new OrganiserMgtOpResult(false,
                                            "Could not set the out message.A role cannot be found : " + rId);
        }

        TasksType tasksType = role.getRoleType().getTasks();
        TaskType taskType = null;
        for (TaskType t : tasksType.getTask()) {
            if (tId.trim().equals(t.getId())) {
                taskType = t;
                break;
            }
        }
        if (taskType == null) {
            return new OrganiserMgtOpResult(false,
                                            "Could not set the out message. A task cannot be found : " + tId);
        }
        String mep = taskType.getMep();
        if (mep == null) {
            mep = Task.OPTYPE_SOLI_RES;
            taskType.setMep(mep);
        }
        OutMsgType outMsgType = new OutMsgType();
        outMsgType.setDeliveryType(deliveryType.trim());
        composite.setOutMessageDirection(mep, outMsgType);
        OperationType operationType = new OperationType();
        operationType.setName(operationName.trim());
        operationType.setReturn(operationReturnType.trim());
        ParamsType paramsType = new ParamsType();
        for (Parameter parameter : parameters) {
            ParamsType.Parameter r = new ParamsType.Parameter();
            r.setName(parameter.getName());
            r.setType(parameter.getType());
            paramsType.getParameter().add(r);
        }
        operationType.setParameters(paramsType);
        outMsgType.setOperation(operationType);
        taskType.setOut(outMsgType);
        return new OrganiserMgtOpResult(true, "Updated the out message : " + tId);
    }

    private OrganiserMgtOpResult setInMessageType(String operationName, String operationReturnType, Parameter[] parameters, String tId, String rId) {
        log.info("Organiser: setInMessageType");
        Role role = composite.getRoleMap().get(rId.trim());
        if (role == null) {
            return new OrganiserMgtOpResult(false,
                                            "Could not set the in message.A role cannot be found : " + rId);
        }

        TasksType tasksType = role.getRoleType().getTasks();
        TaskType taskType = null;
        for (TaskType t : tasksType.getTask()) {
            if (tId.trim().equals(t.getId())) {
                taskType = t;
                break;
            }
        }
        if (taskType == null) {
            return new OrganiserMgtOpResult(false,
                                            "Could not set the in message. A task cannot be found : " + tId);
        }
        String mep = taskType.getMep();
        if (mep == null) {
            mep = Task.OPTYPE_SOLI_RES;
            taskType.setMep(mep);
        }
        InMsgType inMsgType = new InMsgType();
        composite.setInMessageDirection(mep, inMsgType);
        OperationType operationType = new OperationType();
        operationType.setName(operationName.trim());
        operationType.setReturn(operationReturnType.trim());
        ParamsType paramsType = new ParamsType();
        for (Parameter parameter : parameters) {
            ParamsType.Parameter r = new ParamsType.Parameter();
            r.setName(parameter.getName());
            r.setType(parameter.getType());
            paramsType.getParameter().add(r);
        }
        operationType.setParameters(paramsType);
        inMsgType.setOperation(operationType);
        taskType.setIn(inMsgType);
        return new OrganiserMgtOpResult(true, "Updated the in message : " + tId);
    }

    private OrganiserMgtOpResult removeOperation(String operationName,
                                                 String termId) {
        log.info("Organiser: removeOperation");
        boolean result = composite.deleteOperation(operationName, termId);
        if (result)
            return new OrganiserMgtOpResult(result,
                                            "Operation successfully removed");
        else
            return new OrganiserMgtOpResult(result,
                                            "Could not remove operation. " + "Term with id: '"
                                            + termId + "' not found.");
    }


//    public OrganiserMgtOpResult addNewContractRule(String newRule,
//                                                   String contractId) {
//        log.info("Organiser: addNewContractRule");
//        boolean result = composite.insertNewRule(newRule, contractId);
//        if (result)
//            return new OrganiserMgtOpResult(result,
//                                            "New rule successfully inserted");
//        else
//            return new OrganiserMgtOpResult(result,
//                                            "New rule couldn't be "
//                                            + "inserted in the contract with the id  '"
//                                            + contractId + "'. Contract does not exist");
//    }
//
//    public OrganiserMgtOpResult removeContractRule(String contractId,
//                                                   String ruleName) {
//        log.info("Organiser: removeContractRule");
//        boolean result = composite.deleteRule(contractId, ruleName);
//        if (result)
//            return new OrganiserMgtOpResult(result, "Rule " + ruleName
//                                                    + " removed from contract " + contractId
//                                                    + " successfully");
//        else
//            return new OrganiserMgtOpResult(result, "Rule " + ruleName
//                                                    + " could not be removed from contract " + contractId);
//
//    }

//    /**
//     * Returns a reference to a <code>Contract</code> based on its unique
//     * id.
//     *
//     * @param id the id of the desired contract.
//     * @return the contract or false if a Contract with the specified unique
//     * id does not exist.
//     */
//    public Contract getContractById(String id) {
//        return composite.getContractMap().get(id);
//    }
//
//    public OrganiserMgtOpResult addNewCompositeRule(String ruleName, String newRule) {
//        log.info("Organiser: addNewCompositeRule");
//        boolean result = composite.getCompositeRules().addRule(ruleName, newRule);
//        if (result)
//            return new OrganiserMgtOpResult(result,
//                                            "New rule successfully inserted");
//        else
//            return new OrganiserMgtOpResult(result,
//                                            "New rule couldn't be " + "inserted");
//    }
//
//    public OrganiserMgtOpResult removeCompositeRule(String ruleName) {
//        log.info("Organiser: removeCompositeRule");
//        boolean result = composite.getCompositeRules().removeRule(ruleName);
//        if (result)
//            return new OrganiserMgtOpResult(result, "Rule " + ruleName
//                                                    + " removed successfully");
//        else
//            return new OrganiserMgtOpResult(result, "Rule " + ruleName
//                                                    + " was not removed sucessfully");
//    }

    @Override
    public OrganiserMgtOpResult addBehaviorUnit(String bId) {
        return addNewBehavior(bId, null);
    }

    @Override
    public OrganiserMgtOpResult removeBehaviorUnit(String bId) {
        return removeBehavior(bId);
    }

    @Override
    public OrganiserMgtOpResult updateBehaviorUnit(String bId, String property, String value) {
        return updateBehavior(bId, property, value);
    }

    @Override
    public OrganiserMgtOpResult addTaskToBehaviorUnit(String buId, String tId, String preep, String postep) {
        return addTaskToBehavior(buId, tId, preep, postep, null);
    }

    @Override
    public OrganiserMgtOpResult removeTaskFromBehaviorUnit(String buId, String tId) {
        return removeTaskFromBehavior(tId, buId);
    }

    @Override
    public OrganiserMgtOpResult updateTaskOfBehaviorUnit(String buId, String tId, String property, String value) {
        return updateTaskFromBehavior(tId, buId, property, value);
    }

    @Override
    public OrganiserMgtOpResult addVSN(String vsnId) {
        log.info("Organiser: Add a VSN : " + vsnId);
        ProcessDefinitionsType vsnDef = new ProcessDefinitionsType();
        vsnDef.setId(vsnId);
        composite.addProcessGroup(vsnDef);
        return new OrganiserMgtOpResult(true, "Successfully added a VSN : " + vsnId);
    }

    @Override
    public OrganiserMgtOpResult removeVSN(String vsnId) {
        log.info("Organiser: Remove a VSN : " + vsnId);
        composite.removeProcessGroup(vsnId);
        return new OrganiserMgtOpResult(true, "Successfully removed a VSN : " + vsnId);
    }

    @Override
    public OrganiserMgtOpResult updateVSN(String vsnId, String property, String value) {
        log.info("Organiser: Update a VSN : " + vsnId);
        if ("state".equals(property)) {
            ProcessDefinitionsType slice =
                    new SMCDataExtractor(composite.getSmcBinding()).getProcessDefinitionGroup(vsnId);
            slice.setState(value);
            VSNDefinition vsnDefinition = composite.getSerendipEngine().getProcessDefinitionGroup(vsnId);
            vsnDefinition.getMgtState().setState(value);
        }
        return new OrganiserMgtOpResult(true, "Successfully updated a VSN : " + vsnId + " property : " + property + " value : " + value);
    }

    @Override
    public OrganiserMgtOpResult addProcessToVSN(String vsnId, String processId, String CoS, String CoT) {
        log.info("Organiser: Add a process : " + processId);
        SMCDataExtractor dataExtractor = new SMCDataExtractor(composite.getSmcBinding());
        ProcessDefinitionType pdType = dataExtractor.getProcessDefinition(vsnId, processId);
        if (pdType == null) {
            pdType = new ProcessDefinitionType();
            pdType.setId(processId);
            pdType.setCoS(CoS);
            pdType.setCoT(CoT);
            composite.addProcessToGroup(vsnId, pdType);
            return new OrganiserMgtOpResult(true, "ProcessDefinition " + processId + " has been added");
        }
        return new OrganiserMgtOpResult(false, "The addition of the processDefinition " + processId + " is unsuccessful");
    }

    @Override
    public OrganiserMgtOpResult removeProcessFromVSN(String vsnId, String processId) {
        log.info("Organiser: Remove a process : " + processId);
        composite.removeProcessFromGroup(vsnId, processId);
        return new OrganiserMgtOpResult(true, "ProcessDefinition " + processId + " has been removed");
    }

    @Override
    public OrganiserMgtOpResult updateProcessOfVSN(String vsnId, String processId, String property, String value) {
        log.info("Organiser: Update a process : " + processId);
        ServiceNetwork smcCur = composite.getSmcBinding();
        ProcessDefinitionType pdType = new SMCDataExtractor(smcCur).getProcessDefinition(vsnId, processId);
        if ("CoS".equalsIgnoreCase(property)) {
            pdType.setCoS(value);
        } else if ("CoT".equalsIgnoreCase(property)) {
            pdType.setCoT(value);
        } else if ("state".equals(property)) {
            pdType.setState(value);
            ProcessDefinition processDefinition = composite.getProcessDefinition(vsnId, processId);
            processDefinition.getMgtState().setState(value);
            if (ManagementState.STATE_PASSIVE.equals(value)) {
                List<String> pids = processDefinition.getAllLiveInstanceIds();
                processDefinition.includeAllVSInstances(pids);
            }
        } else {
            return new OrganiserMgtOpResult(false, "Unknown property " + property + " for ProcessDefinition");
        }
        return new OrganiserMgtOpResult(true, "ProcessDefinition " + processId +
                                              " hss been updated. property : " + property + " value : " + value);
    }

    @Override
    public OrganiserMgtOpResult addBehaviorUnitsToProcess(String vsnId, String processId, String buIds) {
        log.info("Organiser: Set configuration design of the  process : " + processId);
        ServiceNetwork smcCur = composite.getSmcBinding();
        ProcessDefinitionType pdType = new SMCDataExtractor(smcCur).getProcessDefinition(vsnId, processId);
        String[] ids = buIds.split(",");
        for (String id : ids) {
            pdType.getCollaborationUnitRef().add(id.trim());
        }
        return new OrganiserMgtOpResult(true, "Configuration design for the process  " + processId +
                                              " hss been set with behavior units : " + buIds);
    }

    @Override
    public OrganiserMgtOpResult addBehaviorUnitToProcess(String vsnId, String processId, String buId) {
        log.info("Organiser: add a behavior unit " + buId + " to the configuration design of the process : " + processId);
        ServiceNetwork smcCur = composite.getSmcBinding();
        ProcessDefinitionType pdType = new SMCDataExtractor(smcCur).getProcessDefinition(vsnId, processId);
        pdType.getCollaborationUnitRef().add(buId);
        return new OrganiserMgtOpResult(true, "Configuration design for the process  " + processId +
                                              " hss been updated with behavior unit : " + buId);
    }

    @Override
    public OrganiserMgtOpResult removeBehaviorUnitFromProcess(String vsnId, String processId, String buId) {
        log.info("Organiser: remove a behavior unit " + buId + " from the configuration design of the process : " + processId);
        ServiceNetwork smcCur = composite.getSmcBinding();
        ProcessDefinitionType pdType = new SMCDataExtractor(smcCur).getProcessDefinition(vsnId, processId);
        List<String> buIds = pdType.getCollaborationUnitRef();
        String tobeRemoved = null;
        for (String id : buIds) {
            if (id.equals(buId)) {
                tobeRemoved = id;
                break;
            }
        }
        if (tobeRemoved != null) {
            pdType.getCollaborationUnitRef().remove(tobeRemoved);
        }
        return new OrganiserMgtOpResult(true, "Configuration design for the process  " + processId +
                                              " hss been updated by removing the behavior unit : " + buId);
    }

    @Override
    public OrganiserMgtOpResult replaceBehaviorUnitOfProcess(String vsnId, String processId, String buIdOld, String buIdNew) {
        log.info("Organiser: replace a behavior unit " + buIdOld + " with " + buIdNew + " in  the configuration design of the process : " + processId);
        ServiceNetwork smcCur = composite.getSmcBinding();
        ProcessDefinitionType pdType = new SMCDataExtractor(smcCur).getProcessDefinition(vsnId, processId);
        List<String> buIds = pdType.getCollaborationUnitRef();
        String tobeRemoved = null;
        for (String id : buIds) {
            if (id.equals(buIdOld)) {
                tobeRemoved = id;
                break;
            }
        }
        if (tobeRemoved == null) {
            return new OrganiserMgtOpResult(true, "There is no the behavior unit with id : " + buIdOld);
        }
        pdType.getCollaborationUnitRef().remove(tobeRemoved);
        pdType.getCollaborationUnitRef().add(buIdNew);
        return new OrganiserMgtOpResult(true, "Configuration design for the process  " + processId +
                                              " hss been updated by replacing the behavior unit : " + buIdOld + "  with " + buIdNew);
    }

    /*
       * (non-Javadoc)
       *
       * @see au.edu.swin.ict.road.common.IOrganiserRole#takeSnapshot()
       */
//    public OrganiserMgtOpResult takeSnapshot() {
//        log.info("Organiser: takeSnapshot");
//
//        Composite currentComposite = composite;
//        CompositeMarshalling cm = new CompositeMarshalling();
//
//        boolean result = cm.marshalSMC(currentComposite);
//        if (result) {
//            return new OrganiserMgtOpResult(result,
//                                            "Snapshot generated on: " + cm.getFoldername());
//        } else {
//            return new OrganiserMgtOpResult(result,
//                                            "Snapshot generation failed. Contact System Administrator.");
//        }
//
//    }


    /*
       * (non-Javadoc)
       *
       * @see
       * au.edu.swin.ict.road.common.IOrganiserRole#takeSnapshot(java.lang
       * .String)
       */
//    public OrganiserMgtOpResult takeSnapshot(String folder) {
//        log.info("Organiser: takeSnapshot");
//
//        Composite currentComposite = composite;
//        CompositeMarshalling cm = new CompositeMarshalling();
//
//        boolean result = cm.marshalSMC(currentComposite, folder);
//        if (result) {
//            return new OrganiserMgtOpResult(result,
//                                            "Snapshot generated on: " + cm.getFoldername());
//        } else {
//            return new OrganiserMgtOpResult(result,
//                                            "Snapshot generation failed. Contact System Administrator.");
//        }
//    }
//
//    public OrganiserMgtOpResult changePlayerBinding(String roleId, String endpoint) {
//
//        PlayerBinding pb = composite.getPlayerBindingMap().get(roleId);
//        if (null == pb) {
//            return new OrganiserMgtOpResult(false, "No such player binding");
//        } else {
//            pb.setEndpoint(endpoint);
//            return new OrganiserMgtOpResult(true, "Successfully changed the player binding of " + roleId + " to " + endpoint);
//        }
//    }
    ///////////////////////////////////////////////////////////////

    /**
     * Fact related organizer methods
     */

//    public FactObject getFact(String factType, String factIdentifierValue) {
//        FactTupleSpaceRow factTupleSpaceRow = composite.getFTS().getFactTupleSpaceRow(factType);
//        if (null != factTupleSpaceRow) {
//            if (null == factIdentifierValue) {
//                return factTupleSpaceRow.getMasterFact();
//            } else {
//                return factTupleSpaceRow.getFactObjectByIdValue(factIdentifierValue);
//            }
//        }
//        return null;
//    }
//
//    public OrganiserMgtOpResult updateFact(FactObject factObject) {
//        composite.getFTS().updateFact(factObject);
//        return new OrganiserMgtOpResult(true, "update fact successfully");
//    }
//
//    public OrganiserMgtOpResult addFact(String factType, FactObject factObject) {
//        FactTupleSpaceRow factTupleSpaceRow = composite.getFTS().getFactTupleSpaceRow(factType);
//        if (null != factTupleSpaceRow) {
//            factTupleSpaceRow.addFact(factObject);
//            return new OrganiserMgtOpResult(true, "fact added successfully");
//        } else {
//            return new OrganiserMgtOpResult(false, "No such fact type " + factType);
//        }
//    }
//
//    public OrganiserMgtOpResult removeFact(String factType, String factIdentifierValue) {
//        FactTupleSpaceRow factTupleSpaceRow = composite.getFTS().getFactTupleSpaceRow(factType);
//        if (null != factTupleSpaceRow) {
//            FactObject factObject = factTupleSpaceRow.getFactObjectByIdValue(factIdentifierValue);
//            boolean deleteFact = factTupleSpaceRow.deleteFact(factObject);
//            if (deleteFact) {
//                return new OrganiserMgtOpResult(true, "fact deleted successfully");
//            } else {
//                return new OrganiserMgtOpResult(true, "fact deletion unsuccessful");
//            }
//
//        }
//        return new OrganiserMgtOpResult(false, "No such fact type " + factType);
//    }
    @Override
    public String getName() {

        return composite.getName();
    }
    ///////////////////////////////////////////////////////////////

    /**
     * To adapt the serendip processes
     */
    private OrganiserMgtOpResult addTaskToBehavior(String btid, String tid,
                                                   String preep, String postep, String pp) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(btid)) {
                //Found
                TaskRefType tt = new TaskRefType();
                tt.setId(tid);
                tt.setPreEP(preep);
                tt.setPostEP(postep);
                tt.setPerformanceVal(pp);
                btt.getConfigurationDesign().getTaskRef().add(tt);
                return new OrganiserMgtOpResult(true, "Task ref  " + tid + " added to " + btid);
            }
        }
        return new OrganiserMgtOpResult(false, "Behaviour  " + btid + " cannot be found");
    }

    private OrganiserMgtOpResult removeTaskFromBehavior(String id, String behaviorId) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        if (null == smcCur.getCollaborationUnits()) {
            return new OrganiserMgtOpResult(false, "Cannot find Behavior " + behaviorId);
        }

        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(behaviorId)) {
                ConfigurationDesignType trefs = btt.getConfigurationDesign();
                if (null == trefs) {
                    return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in behavior " + behaviorId);
                }
                for (TaskRefType tt : trefs.getTaskRef()) {
                    if (tt.getId().equals(id)) {
                        trefs.getTaskRef().remove(tt);
                        return new OrganiserMgtOpResult(true, " Task removed from " + id + " behavior " + behaviorId);
                    }
                }
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in behavior " + behaviorId);
    }

    private OrganiserMgtOpResult updateTaskFromBehavior(String id, String behaviorId, String property, String value) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        if (null == smcCur.getCollaborationUnits()) {
            return new OrganiserMgtOpResult(false, "Cannot find Behavior " + behaviorId);
        }

        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(behaviorId)) {
                ConfigurationDesignType trefs = btt.getConfigurationDesign();
                if (null == trefs) {
                    return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in behavior " + behaviorId);
                }
                for (TaskRefType tt : trefs.getTaskRef()) {
                    if (tt.getId().equals(id)) {
                        if ("preEP".equals(property)) {
                            tt.setPreEP(value);
                        } else if ("postEP".equals(property)) {
                            tt.setPostEP(value);
                        }
                        return new OrganiserMgtOpResult(true, " Task updated from " + id + " behavior " + behaviorId);
                    }
                }
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in behavior " + behaviorId);
    }

    public OrganiserMgtOpResult addTask(String roleId,
                                        String taskId, String srcMsgsStr,
                                        String resultMsgsStr) {
        String roleId1 = roleId.trim();
        String taskId1 = taskId.trim();
        RoleType rt = composite.getRoleByID(roleId1).getRoleType();
        TasksType tasks = rt.getTasks();
        if (null == tasks) {//if no  TasksType, we will create a new one
            tasks = new TasksType();
            rt.setTasks(tasks);
        }
        TaskType tt = new TaskType();
        tt.setId(taskId1);
        tt.setIsMsgDriven(false);

        if (null != srcMsgsStr) {
            String srcMsgsStr1 = srcMsgsStr.trim();
            SrcMsgsType srcMsgsType = new SrcMsgsType();
            String[] srcMsgArr = srcMsgsStr1.split(",");
            for (String srcMsgStr : srcMsgArr) {
                //e.g., CO_TT.sendTowRequest.Req
                SrcMsgType smt = new SrcMsgType();
                String[] split = srcMsgStr.split("\\.");
                smt.setContractId(split[0]);
                smt.setTermId(split[1]);
                smt.setIsResponse(!(split[2].equals("Req")));
                srcMsgsType.getSrcMsg().add(smt);
            }
//                        srcMsgsType.setTransformation("");
            tt.setSrcMsgs(srcMsgsType);
        }
        if (null != resultMsgsStr) {
            String resultMsgsStr1 = resultMsgsStr.trim();
            ResultMsgsType resultMsgsType = new ResultMsgsType();
            String[] resultMsgArr = resultMsgsStr1.split(",");
            for (String resultMsgStr : resultMsgArr) {
                //e.g., CO_TT.sendTowRequest.Res
                ResultMsgType rmt = new ResultMsgType();
                String[] split = resultMsgStr.split("\\.");
                rmt.setContractId(split[0]);
                rmt.setTermId(split[1]);
                rmt.setIsResponse(!(split[2].equals("Req")));
                if (split.length > 3) {
                    rmt.setTransformation(split[3] + ".xsl");
                }
                resultMsgsType.getResultMsg().add(rmt);
            }
            tt.setResultMsgs(resultMsgsType);
        }
        tasks.getTask().add(tt);

        return new OrganiserMgtOpResult(true, "Added a Task : " + taskId1);

    }

    public OrganiserMgtOpResult updateTask(String roleId, String taskId, String action, String property, String value) {

        RoleType rt = composite.getRoleByID(roleId).getRoleType();

        TasksType tasks = rt.getTasks();
        if (null == tasks) {//if no  TasksType, we will create a new one
            return new OrganiserMgtOpResult(false, "Cannot find role " + roleId);
        }
        for (TaskType taskType : tasks.getTask()) {
            if (taskId.equals(taskType.getId())) {
                if ("inputs".equals(property)) {
                    if ("add".equals(action)) {
                        addSrcMsgsOfTask(taskType, value);
                    } else if ("remove".equals(action)) {
                        removeSrcMsgsOfTask(taskType, value);
                    }
                } else if ("outputs".equals(property)) {
                    if ("add".equals(action)) {
                        addRsultsMsgsOfTask(taskType, value);
                    } else if ("remove".equals(action)) {
                        removeRsultsMsgsOfTask(taskType, value);
                    }
                } else if ("mep".equals(property)) {
                    taskType.setMep(value);
                }
            }
        }

        return new OrganiserMgtOpResult(true, "Updated the task : " + taskId + " of the role :" + roleId);
    }

    @Override
    public OrganiserMgtOpResult setTaskOutMessage(String roleId, String taskId, String opName, String parameters, String returnType) {
        return setOutMessageType("push", opName, returnType, createParameters(parameters), taskId, roleId);
    }

    public OrganiserMgtOpResult setTaskMessage(String roleId, String taskId, String opName, String parameters, String returnType) {
        setOutMessageType("push", opName, returnType, createParameters(parameters), taskId, roleId);
        return setInMessageType(opName, returnType, createParameters(parameters), taskId, roleId);
    }

    @Override
    public OrganiserMgtOpResult setTaskInMessage(String roleId, String taskId, String opName, String parameters, String returnType) {
        return setInMessageType(opName, returnType, createParameters(parameters), taskId, roleId);
    }

    @Override
    public OrganiserMgtOpResult setTaskQoS(String roleId, String taskId, String QoSparameters) {
        Parameter[] parameters = createParameters(QoSparameters);
        RoleType rt = composite.getRoleByID(roleId).getRoleType();
        TasksType tasks = rt.getTasks();
        if (null == tasks) {
            return new OrganiserMgtOpResult(false, "Cannot find Task " + taskId + " in role " + roleId);
        }
        for (TaskType tt : tasks.getTask()) {
            if (tt.getId().equals(taskId)) {
                QoSType qoSType = new QoSType();
                for (Parameter para : parameters) {
                    if ("ResponseTime".equals(para.getType())) {
                        qoSType.setResponseTime(para.getName());
                    } else if ("Throughput".equals(para.getType())) {
                        qoSType.setThroughput(para.getName());
                    }
                }
                tt.setQoS(qoSType);
                return new OrganiserMgtOpResult(true, " QoS values were set on the task " + taskId + "in the role " + roleId);
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find Task " + taskId + " in role " + roleId);
    }

    @Override
    public OrganiserMgtOpResult addContract(String id, String roleAId, String roleBId) {
        log.info("Organiser: addNewContract, contractId: " + id);

        Contract newContract = null;
        try {
            newContract = new Contract(id.trim(), composite.getRulesDir());
            composite.addContractInternal(newContract, roleAId.trim(), roleBId.trim());
            return new OrganiserMgtOpResult(true, "Contract "
                                                  + newContract.getId() + " was successfully added");
        } catch (CompositeInstantiationException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        } catch (ConsistencyViolationException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        }
    }

    public OrganiserMgtOpResult removeTask(String roleId, String id) {
        ServiceNetwork smcCur = composite.getSmcBinding();//This is the SMC maintained in MPF
        if (null == smcCur.getRoles()) {
            return new OrganiserMgtOpResult(false, "Cannot find role " + roleId);
        }

        RoleType rt = composite.getRoleByID(roleId.trim()).getRoleType();

        TasksType tasks = rt.getTasks();
        if (null == tasks) {
            return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in role " + roleId);
        }
        for (TaskType tt : tasks.getTask()) {
            if (tt.getId().equals(id.trim())) {
                tasks.getTask().remove(tt);
                return new OrganiserMgtOpResult(true, " Task removed from " + id + " role " + roleId);
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find Task " + id + " in role " + roleId);
    }

    private OrganiserMgtOpResult addServiceBinding(String pbId, String rid, String endpoint) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: addServiceBinding, pbID: " + pbId);
        }

        PlayerBindingType playerBindingType = new PlayerBindingType();
        playerBindingType.setId(pbId.trim());
        if (rid != null) {
            PlayerBindingType.Roles roles = new PlayerBindingType.Roles();
            roles.getRoleID().add(rid.trim());
            playerBindingType.setRoles(roles);
        }
        playerBindingType.setEndpoint(endpoint.trim());

        PlayerBinding playerBinding = new PlayerBinding(playerBindingType);
        composite.getPlayerBindingMap().put(pbId.trim(), playerBinding);
        composite.updateRoleBindings(playerBinding, true);
        return new OrganiserMgtOpResult(true, "New PlayerBinding "
                                              + playerBinding.getId() + " added successfully");
    }

    private OrganiserMgtOpResult removeServiceBinding(String pbId) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: removeServiceBinding, pbID: " + pbId);
        }
        PlayerBinding playerBinding = composite.getPlayerBindingMap().remove(pbId);
        composite.updateRoleBindings(playerBinding, false);
        return new OrganiserMgtOpResult(true, "New PlayerBinding "
                                              + playerBinding.getId() + " removed successfully");
    }

    private OrganiserMgtOpResult updateServiceBinding2(String pbId, String property, String value) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: updateServiceBinding, pbID: " + pbId);
        }
        PlayerBinding playerBinding = composite.getPlayerBindingMap().remove(pbId);
        if ("endpoint".equals(property)) {
            playerBinding.setEndpoint(value);
        }
        composite.getPlayerBindingMap().put(pbId, playerBinding);
        composite.updateRoleBindings(playerBinding, true);
        return new OrganiserMgtOpResult(true, "New PlayerBinding "
                                              + playerBinding.getId() + " updated successfully");
    }

    private OrganiserMgtOpResult addNewBehavior(String bid, String extendfrom) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        CollaborationUnitType btt = new CollaborationUnitType();
        btt.setId(bid);
        btt.setExtends(extendfrom);
        if (btt.getConstraints() == null) {
            btt.setConstraints(new ConstraintsType());
        }
        if (btt.getConfigurationDesign() == null) {
            btt.setConfigurationDesign(new ConfigurationDesignType());
        }
        smcCur.getCollaborationUnits().getCollaborationUnit().add(btt);
        return new OrganiserMgtOpResult(true, "BehaviorUnit " + bid + " has been added");
    }

    private OrganiserMgtOpResult updateBehavior(String bid, String prop,
                                                String val) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(bid)) {
                //Found
                if (prop.equals(Composite.KEY_BU_EXTENDSFROM)) {
                    btt.setExtends(val);
                } else if (prop.equals(Composite.KEY_BU_ISABSTRACT)) {
                    btt.setIsAbstract(Boolean.valueOf(val));
                } else if ("state".equals(prop)) {
                    btt.setState(val);
                } else {
                    return new OrganiserMgtOpResult(false, "Unknown property " + prop + " for BehaviorUnit");
                }

                return new OrganiserMgtOpResult(true, "Update property " + prop + " of BehaviorUnit" + bid + " is done");
            }
        }
        return new OrganiserMgtOpResult(false, "BehaviorUnit " + bid + " cannot be found");
    }

    private OrganiserMgtOpResult removeBehavior(String id) {
        ServiceNetwork smcCur = composite.getSmcBinding();
        if (null == smcCur.getCollaborationUnits()) {
            return new OrganiserMgtOpResult(false, "Cannot find Behavior ");
        }

        for (CollaborationUnitType bt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (bt.getId().equals(id)) {
                //remove
                smcCur.getCollaborationUnits().getCollaborationUnit().remove(bt);

                return new OrganiserMgtOpResult(true, " Behavior removed " + id);
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find Behavior ");
    }

    private void addSrcMsgsOfTask(TaskType tt, String srcMsgsStr) {
        SrcMsgsType srcMsgsType = tt.getSrcMsgs();
        if (srcMsgsType == null) {
            srcMsgsType = new SrcMsgsType();
            tt.setSrcMsgs(srcMsgsType);
        }
        String[] srcMsgArr = srcMsgsStr.split(",");
        for (String srcMsgStr : srcMsgArr) {
            //e.g., CO_TT.sendTowRequest.Req
            SrcMsgType smt = new SrcMsgType();
            String[] split = srcMsgStr.split("\\.");
            smt.setContractId(split[0]);
            smt.setTermId(split[1]);
            smt.setIsResponse(!(split[2].equals("Req")));
            srcMsgsType.getSrcMsg().add(smt);
        }
    }

    private void addRsultsMsgsOfTask(TaskType tt, String resultMsgsStr) {
        ResultMsgsType resultMsgsType = tt.getResultMsgs();
        if (resultMsgsType == null) {
            resultMsgsType = new ResultMsgsType();
            tt.setResultMsgs(resultMsgsType);
        }
        String[] resultMsgArr = resultMsgsStr.split(",");
        for (String resultMsgStr : resultMsgArr) {
            //e.g., CO_TT.sendTowRequest.Res.exsltFile
            ResultMsgType rmt = new ResultMsgType();
            String[] split = resultMsgStr.split("\\.");
            rmt.setContractId(split[0]);
            rmt.setTermId(split[1]);
            rmt.setIsResponse(!(split[2].equals("Req")));
            if (split.length > 3 && split[3] != null) {
                rmt.setTransformation(split[3] + ".xsl");
            }
            resultMsgsType.getResultMsg().add(rmt);
        }
    }

    private void removeSrcMsgsOfTask(TaskType tt, String srcMsgsStr) {
        String[] srcMsgArr = srcMsgsStr.split(",");
        List<String> srcMsgList = new ArrayList<String>();
        for (String s : srcMsgArr) {
            srcMsgList.add(s.trim());
        }
        List<SrcMsgType> tobeRemoved = new ArrayList<SrcMsgType>();
        for (SrcMsgType srcMsgType : tt.getSrcMsgs().getSrcMsg()) {
            String msgId = srcMsgType.getContractId() + "."
                           + srcMsgType.getTermId() + ".";
            msgId += srcMsgType.isIsResponse() ? "Res" : "Req";
            if (srcMsgList.contains(msgId)) {
                tobeRemoved.add(srcMsgType);
            }
        }
        tt.getSrcMsgs().getSrcMsg().removeAll(tobeRemoved);
    }

    private void removeRsultsMsgsOfTask(TaskType tt, String resultMsgsStr) {
        String[] resutlSrt = resultMsgsStr.split(",");
        List<String> resultList = new ArrayList<String>();
        for (String s : resutlSrt) {
            resultList.add(s.trim());
        }
        List<ResultMsgType> tobeRemoved = new ArrayList<ResultMsgType>();
        for (ResultMsgType resultMsgType : tt.getResultMsgs().getResultMsg()) {
            String msgId = resultMsgType.getContractId() + "."
                           + resultMsgType.getTermId() + ".";
            msgId += resultMsgType.isIsResponse() ? "Res" : "Req";
            if (resultList.contains(msgId)) {
                tobeRemoved.add(resultMsgType);
            }
        }
        tt.getResultMsgs().getResultMsg().remove(tobeRemoved);
    }

    private Parameter[] createParameters(String paramStr) {
        List<Parameter> parameters = new ArrayList<Parameter>();
        String[] paraStrs = paramStr.split(",");
        for (String resultMsgStr : paraStrs) {
            //e.g., String:Name,....
            String[] split = resultMsgStr.split("\\:");
            parameters.add(new Parameter(split[0], split[1]));
        }
        return parameters.toArray(new Parameter[parameters.size()]);
    }
}// END OF ORGANIZER IMPL
