package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.IOrganiserRole;
import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.exceptions.CompositeInstantiationException;
import au.edu.swin.ict.road.xml.bindings.*;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork.Contracts;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork.Facts;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork.Roles;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork.ServiceBindings;
import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.composition.Composition;
import au.edu.swin.ict.serendip.composition.PerformanceProperty;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.core.Constants;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.core.mgmt.action.*;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of the Serendip Organizer.
 * This class will handle all the process related adaptations.
 *
 * @author Malinda
 */
public class SerendipOrganizerImpl implements SerendipOrganizer, Constants {
    private static Logger log = Logger.getLogger(SerendipOrganizerImpl.class.getName());
    private SerendipEngine engine = null;
    //    private AdaptationScriptEngine scriptEngine = null;
//    private DroolsBasedManagementPolicyEnactmentEngine policyEnactmentEngine;
    private Composite composite;

    public SerendipOrganizerImpl(SerendipEngine engine, Composite composite) {
        this.engine = engine;
        this.composite = composite;
//        this.policyEnactmentEngine = new DroolsBasedManagementPolicyEnactmentEngine(composite);
    }

    @Override
    public OrganiserMgtOpResult adaptProcessInstance(
            String processInstanceId, List<InstanceAdaptAction> adaptationActionsList) {
        log.info("Got a list of commands for instance" + processInstanceId);
        for (InstanceAdaptAction aa : adaptationActionsList) {
            log.info("Command found " + aa.toString());
        }
        //Create a new instance adaptation engine
        ProcessInstanceAdaptationEngine piae = new ProcessInstanceAdaptationEngine(this.engine);

        try {
            piae.executeAdaptation(processInstanceId, adaptationActionsList);
        } catch (AdaptationException e) {

            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Cannot perform adaptation. " + e.getMessage());
        }
        return new OrganiserMgtOpResult(true, "Process Instance Level Adaptation Complete");
    }

    @Override
    public OrganiserMgtOpResult adaptDefinition(
            List<DefAdaptAction> adaptationActions) {
        // TODO Auto-generated method stub
        log.info("Got a list of commands for a definition level adaptation ");
        for (DefAdaptAction aa : adaptationActions) {
            log.info("Command found " + aa.toString());
        }

        //Create a new instance of adaptaiton engine
        DefAdaptationEngine defe = new DefAdaptationEngine(this.engine);
        try {
            defe.executeAdaptation(adaptationActions);
        } catch (AdaptationException e) {
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Cannot perform adaptation. " + e.getMessage());
        }
        return new OrganiserMgtOpResult(true, "Composite Definition Level Adaptation Complete.");
    }

    @Override
    public OrganiserMgtOpResult addNewBehaviorConstraint(String btid,
                                                         String cid, String expression, boolean enabled) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(btid)) {
                //Found BT. Then add constrain
                ConstraintType ct = new ConstraintType();
                ct.setId(cid);
                ct.setExpression(expression);
                ct.setEnabled(enabled);
                btt.getConstraints().getConstraint().add(ct);
                return new OrganiserMgtOpResult(true, "New constraint  " + expression + " has been added to Behaviour  " + btid);
            }
        }
        return new OrganiserMgtOpResult(false, "Behaviour  " + btid + " cannot be found");
    }


    @Override
    public OrganiserMgtOpResult addBehaviorRefToPD(String pdid,
                                                   String btid) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pdType : pdsType.getProcess()) {
                if (pdType.getId().equals(pdid)) {
                    //Found PD.
                    //Check if reference already exist
                    for (String refId : pdType.getCollaborationUnitRef()) {
                        if (refId.equals(btid)) {
                            return new OrganiserMgtOpResult(false, "Behaviour  " + btid + " already referred by Process Def " + pdid);
                        }
                    }
                    //Now look for BT
                    for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
                        if (btt.getId().equals(btid)) {
                            //Found BT. Then connect
                            pdType.getCollaborationUnitRef().add(btid);
                            return new OrganiserMgtOpResult(true, "Behaviour  " + btid + " has been added to Process Def " + pdid);
                        }
                    }
                    return new OrganiserMgtOpResult(false, "Behaviour  " + btid + " cannot be found");
                }
            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult addConstraintToProcessInstance(String pid,
                                                               String cid, String expression, boolean enabled) {
        //Get Instance
        ProcessInstance pi = this.engine.getProcessInstanceByInsId(pid);
        if (null == pi) {
            return new OrganiserMgtOpResult(false, "Process Instances cannot be found.");
        }
        //adapt
        InstanceProcessConstraintAddAction action = new InstanceProcessConstraintAddAction(cid, expression, enabled);
        try {
            action.adapt(pi);
        } catch (AdaptationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Process Instnace " + pid + " cannot adapted" + e.getStackTrace());
        }
        return new OrganiserMgtOpResult(true, "Process Instnace " + pid + " has been adapted");
    }

    @Override
    public OrganiserMgtOpResult addTaskToInstance(String pid,
                                                  String btid, String tid, String preEP, String postEP, String obligRole, String pp) {
        //Get Instance
        ProcessInstance pi = this.engine.getProcessInstanceByInsId(pid);
        if (null == pi) {
            return new OrganiserMgtOpResult(false, "Process Instances cannot be found.");
        }

        InstanceTaskAddAction action = new InstanceTaskAddAction(btid, tid, preEP, postEP, obligRole, pp);
        try {
            action.adapt(pi);
        } catch (AdaptationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Process Instances cannot be adapted " + e.getMessage());
        }
        return new OrganiserMgtOpResult(false, "Process Instances adapted " + pid);

    }

    @Override
    public OrganiserMgtOpResult addNewProcessConstraint(String pdid,
                                                        String cid, String expression, boolean enabled) {
        // TODO Auto-generated method stub
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pdType : pdsType.getProcess()) {
                if (pdType.getId().equals(pdid)) {
                    ConstraintType ct = new ConstraintType();
                    ct.setId(cid);
                    ct.setExpression(expression);
                    ct.setEnabled(enabled);
                    pdType.getConstraints().getConstraint().add(ct);
                    return new OrganiserMgtOpResult(true, "New constraint  " + expression + " has been added to Process Def " + pdid);
                }
            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult updateProcessInstanceState(String pid,
                                                           String status) {

        for (ProcessInstance pi : this.engine.getLiveProcessInstances().values()) {
            if (pi.getClassifier().getProcessInsId().equals(pid)) {
                pi.currentStatus = ProcessInstance.status.valueOf(status);
                return new OrganiserMgtOpResult(true, "Change status of instance " + pid + " to " + pi.currentStatus.toString());
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find instance " + pid);
    }

    @Override
    public OrganiserMgtOpResult updateStateofAllProcessInstances(String state) {
        // TODO Auto-generated method stub
        for (ProcessInstance pi : this.engine.getLiveProcessInstances().values()) {
            pi.currentStatus = ProcessInstance.status.valueOf(state);
        }
        return new OrganiserMgtOpResult(true, "Change status of all process instances");
    }

    @Override
    public OrganiserMgtOpResult executeScript(String script) {
        log.info("Got a script to admit \n" + script);

        try {
            long start = System.nanoTime();
//            this.scriptEngine.admit(script);
//            this.policyEnactmentEngine.enactManagementPolicy(script, script);
            long stop = System.nanoTime();
            return new OrganiserMgtOpResult(true, "Script applied in ." + (stop - start) + "nanoseconds");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Process Instances Adaptation failed." + e.getMessage());
        }

    }

    @Override
    public OrganiserMgtOpResult executeScheduledScript(String script,
                                                       String onEventPattern, String pid) {
        return executeScript(script);
//        if ((null == onEventPattern) || (onEventPattern.equals(""))) {
//            return new OrganiserMgtOpResult(false, "Must specify an event or event pattern");
//        }
//        if ((null == pid) || (pid.equals(""))) {
//            return new OrganiserMgtOpResult(false, "Must specify a process instance id");
//        }
//        //First we need to validate the script
//        try {
//            if (!this.scriptEngine.validateScriptSyntax(script)) {
//                return new OrganiserMgtOpResult(false, "Script Error.");
//            }
//        }
//        catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return new OrganiserMgtOpResult(false, "Script Error. " + e.getMessage());
//        }
//
//        //Create new adaptation script schedular.
//        Classifier classifier = new Classifier();
//        classifier.setProcessInsId(pid);
//        //TODO
//        AdaptationScriptSchedular scriptSchedular = new AdaptationScriptSchedular(script, onEventPattern, classifier, this);
//        this.engine.getEventCloud().subscribe(scriptSchedular);
//        return new OrganiserMgtOpResult(true, "A schedular added for event pattern " +
//                                                  scriptSchedular.getEventPatterns().get(0) + " with id " + scriptSchedular.getId());
    }

    @Override
    public OrganiserMgtOpResult updatePropertyOfProcessInstance(String pid,
                                                                String property, String value) {
        ProcessInstance pi = this.engine.getProcessInstanceByInsId(pid);
        if (null == pi) {
            return new OrganiserMgtOpResult(false, "Process Instances cannot be found.");
        }

        if (ProcessInstance.propertyAttribute.cos.toString().equals(property)) {
            return new OrganiserMgtOpResult(false, "Process already enacted");
        } else if (ProcessInstance.propertyAttribute.cot.toString().equals(property)) {
            pi.removeAllEventPatterns();
            pi.addEventPattern(value);
        } else {
            return new OrganiserMgtOpResult(false, "Unknown property " + property);
        }
        return new OrganiserMgtOpResult(true, "ProcessInstance property modified.");
    }

    @Override
    /**
     * Property = PreEP/PostEP/PP/Role
     e.g., Set pre event pattern of a task

     */
    public OrganiserMgtOpResult updatePropertyofTaskOfInstance(String pid,
                                                               String taskid, String property, String value) {
        //Get Instance
        ProcessInstance pi = this.engine.getProcessInstanceByInsId(pid);
        if (null == pi) {
            return new OrganiserMgtOpResult(false, "Process Instances cannot be found.");
        }
        //Pause
        pi.setCurrentStatus(ProcessInstance.status.paused);

        BehaviorTerm bt = pi.getContainedBehavior(taskid);
        if (null == bt) {
            return new OrganiserMgtOpResult(false, "Task cannot be found.");
        }

        Task t = bt.getTask(taskid);
        if (null == t) {
            return new OrganiserMgtOpResult(false, "Task cannot be found.");
        }
        //Modify Task
        if (Task.propertyAttribute.preep.toString().equals(property)) {
            t.removeAllEventPatterns();
            t.addEventPattern(value);
        } else if (Task.propertyAttribute.postep.toString().equals(property)) {
            t.setPostEventPattern(value);
        } else if (Task.propertyAttribute.pp.toString().equals(property)) {
            t.setProperty(new PerformanceProperty(value));
        } else {
            return new OrganiserMgtOpResult(false, "Unknown property " + property);
        }
        //Resume
        pi.setCurrentStatus(ProcessInstance.status.active);
        return new OrganiserMgtOpResult(true, "Task property modified.");

    }

    @Override
    public OrganiserMgtOpResult addNewEvent(String eventId, String pid,
                                            int expiration) {
        // TODO Auto-generated method stub
        try {
            Classifier classifier = new Classifier();
            classifier.setProcessInsId(pid);
            this.engine.addEvent(new EventRecord(eventId, classifier));
        } catch (SerendipException e) {
            // TODO Auto-generated catch block
            return new OrganiserMgtOpResult(true, "Cannot add event." + e.getMessage());
        }
        return new OrganiserMgtOpResult(true, "Event Added.");
    }

    @Override
    public OrganiserMgtOpResult removeEvent(String eventId, String pid) {
        // TODO Auto-generated method stub
        this.engine.expireEvent(eventId, pid);
        return new OrganiserMgtOpResult(true, "Event Removed.");
    }

    @Override
    public OrganiserMgtOpResult setEventExpiration(String eventId,
                                                   String pid, int expiration) {
        EventRecord event = this.engine.getEventCloud().getEventReocrd(eventId, pid);
        if (null == event) {
            return new OrganiserMgtOpResult(true, "Cannot find event.");
        }
//        event.setExpiration(new Date(expiration));
        return new OrganiserMgtOpResult(true, "Set Event Expiration.");
    }

    @Override
    public OrganiserMgtOpResult subscribeToEventPattern(
            String eventPattern, String pid) {
        IOrganiserRole organiserRole = this.getOrganiserRole();
        Classifier classifier = new Classifier();
        classifier.setProcessInsId(pid);
        //TODO
        this.engine.getEventCloud().subscribe(new OrganizerEventListener(eventPattern, classifier, organiserRole));
        return null;
    }


    @Override
    /**
     * patchFileName  must be xml and need to be placed in AXIS2_HOME/patches/
     */
    public OrganiserMgtOpResult applyPatch(String patchFileName) {
        // TODO Auto-generated method stub
        String patchFile = System.getenv("AXIS2_HOME") + System.getProperty("file.separator") + Constants.PATCH_FILE_DIR + System.getProperty("file.separator") + patchFileName;
        File file = new File(patchFile);
        if (!file.canRead()) {
            return new OrganiserMgtOpResult(false, "Invalid file path");
        }
        JAXBContext jc;
        ServiceNetwork smcNew = null;
        try {

            jc = JAXBContext.newInstance("au.edu.swin.ict.road.xml.bindings");
            Unmarshaller unmarshaller = jc.createUnmarshaller();


            smcNew = (ServiceNetwork) unmarshaller.unmarshal(new File(patchFile));
        } catch (Exception e) {
            return new OrganiserMgtOpResult(false, "Cannot load XML descriptor " + e.getMessage());
        }

        //TODO : Now go through all the properties and add them accordingly to existing SMC in MPF
        Composition serComposition = this.engine.getComposition();
        Composite composite = serComposition.getComposite();//This is the ROAD composite maintained in MPF
        ServiceNetwork smcCur = this.getSmcBinding();//This is the SMC maintained in MPF

        //Process Defintiions
        ProcessDefinitionsType processDefinitions = smcNew.getVirtualServiceNetwork().get(0);
        if (null != processDefinitions) {
            smcCur.getVirtualServiceNetwork().get(0).getProcess().addAll(
                    processDefinitions.getProcess());
        }
        //Behavior units
        CollaborationUnitsType behaviorTerms = smcNew.getCollaborationUnits();
        if (null != behaviorTerms) {
            smcCur.getCollaborationUnits().getCollaborationUnit().addAll(behaviorTerms.getCollaborationUnit());
        }

        //Roles
        Roles roles = smcNew.getRoles();
        if (null != roles) {
            smcCur.getRoles().getRole().addAll(roles.getRole());
            try {
                composite.extractRoles(roles);
            } catch (CompositeInstantiationException e) {
                return new OrganiserMgtOpResult(false, "Cannot xtract roles " + e.getMessage());
            }
        }

        // Contracts
        Contracts contracts = smcNew.getContracts();
        if (null != contracts) {
            smcCur.getContracts().getContract().addAll(contracts.getContract());
            try {
                composite.extractContracts(contracts);
            } catch (Exception e) {
                return new OrganiserMgtOpResult(false, "Cannot xtract contracts " + e.getMessage());
            }

        }

        //Player bindings
        ServiceBindings playerBindings = smcNew.getServiceBindings();
        if (null != playerBindings) {
            smcCur.getServiceBindings().getServiceBinding().addAll(playerBindings.getServiceBinding());
            composite.extractPlayerBindings(playerBindings);
        }


        //Facts
        Facts facts = smcNew.getFacts();
        if (null != facts) {
            smcCur.getFacts().getFact().addAll(facts.getFact());
            composite.extractFacts(facts);
        }

        return new OrganiserMgtOpResult(true, "Successfully patched");
    }

    @Override
    public OrganiserMgtOpResult removePD(String id) {
        ServiceNetwork smcCur = this.getSmcBinding();//This is the SMC maintained in MPF
        if (null == smcCur.getVirtualServiceNetwork()) {
            return new OrganiserMgtOpResult(false, "Cannot find process definition ");
        }

        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pd : pdsType.getProcess()) {
                if (pd.getId().equals(id)) {
                    //remove
                    smcCur.getVirtualServiceNetwork().get(0).getProcess().remove(pd);
                    return new OrganiserMgtOpResult(true, "removed " + id);
                }
            }
        }
        return new OrganiserMgtOpResult(false, "Cannot find process definition ");
    }

    @Override
    public OrganiserMgtOpResult addNewPD(String pdid) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pdType : pdsType.getProcess()) {
                if (pdType.getId().equals(pdid)) {
                    //Found. ERROR
                    return new OrganiserMgtOpResult(true, "ProcessDefinition " + pdid + " already exist. Choose another id");
                }
            }
        }
        ProcessDefinitionType pdType = new ProcessDefinitionType();
        pdType.setId(pdid);
        smcCur.getVirtualServiceNetwork().get(0).getProcess().add(pdType);
        return new OrganiserMgtOpResult(true, "ProcessDefinition " + pdid + " has been added");
    }

    @Override
    public OrganiserMgtOpResult updatePD(String pdid, String prop,
                                         String val) {

        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionType pdType : smcCur.getVirtualServiceNetwork().get(0).getProcess()) {
            if (pdType.getId().equals(pdid)) {
                //Found
                if (KEY_PD_COS.equalsIgnoreCase(prop)) {
                    pdType.setCoS(val);
                } else if (KEY_PD_COT.equalsIgnoreCase(prop)) {
                    pdType.setCoT(val);
                } else {
                    return new OrganiserMgtOpResult(false, "Unknown property " + prop + " for ProcessDefinition");
                }

                return new OrganiserMgtOpResult(true, "Update property " + prop + " of ProcessDefinition" + pdid + " is done");
            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult removeBehaviorConstraint(String bid,
                                                         String cid) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(bid)) {
                for (ConstraintType ct : btt.getConstraints().getConstraint()) {
                    if (ct.getId().equals(cid)) {
                        //Found
                        btt.getConstraints().getConstraint().remove(ct);
                        return new OrganiserMgtOpResult(true, "Constraint " + cid + " of BehaviorUnit " + bid + " has been removed");
                    }
                }
                return new OrganiserMgtOpResult(false, "Constraint " + cid + " of BehaviorUnit " + bid + " cannot be found");
            }
        }
        return new OrganiserMgtOpResult(false, "BehaviorUnit " + bid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult updateBehaviorConstraint(String bid,
                                                         String cid, String prop, String val) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (CollaborationUnitType btt : smcCur.getCollaborationUnits().getCollaborationUnit()) {
            if (btt.getId().equals(bid)) {
                for (ConstraintType ct : btt.getConstraints().getConstraint()) {
                    if (ct.getId().equals(cid)) {
                        //Found
                        if (prop.equals(KEY_CT_ISENABLED)) {

                            ct.setEnabled(Boolean.valueOf(val));

                        } else if (prop.equals(KEY_CT_EXPRESSION)) {
                            ct.setExpression(val);
                        } else if (prop.equals(KEY_CT_LANG)) {
                            ct.setLanguage(val);
                        } else {
                            return new OrganiserMgtOpResult(false, "Property  " + prop + " cannot be found");
                        }
                        return new OrganiserMgtOpResult(true, "Property  " + prop + " of Constraint " + cid + "of BehaviorUnit " + bid + " has been updated ");
                    }
                }
                return new OrganiserMgtOpResult(false, "Constraint " + cid + " of BehaviorUnit " + bid + " cannot be found");
            }
        }
        return new OrganiserMgtOpResult(false, "BehaviorUnit " + bid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult removeProcessConstraint(String pdid,
                                                        String cid) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pdType : pdsType.getProcess()) {
                if (pdType.getId().equals(pdid)) {
                    //Found
                    for (ConstraintType ct : pdType.getConstraints().getConstraint()) {
                        if (ct.getId().equals(cid)) {

                            pdType.getConstraints().getConstraint().remove(ct);
                            return new OrganiserMgtOpResult(true, "Constraint " + cid + "of ProcessDefinition " + pdid + " has been removed ");
                        }
                    }
                    return new OrganiserMgtOpResult(false, "Constraint " + cid + " of ProcessDefinition " + pdid + " cannot be found");
                }

            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult updateProcessConstraint(String pdid,
                                                        String cid, String prop, String val) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionType pdType : smcCur.getVirtualServiceNetwork().get(0).getProcess()) {
            if (pdType.getId().equals(pdid)) {
                //Found
                for (ConstraintType ct : pdType.getConstraints().getConstraint()) {
                    if (ct.getId().equals(cid)) {

                        if (prop.equals(KEY_CT_EXPRESSION)) {
                            ct.setExpression(val);
                        } else if (prop.equals(KEY_CT_ISENABLED)) {
                            ct.setEnabled(Boolean.valueOf(val));
                        } else if (prop.equals(KEY_CT_LANG)) {
                            ct.setLanguage(val);
                        } else {
                            return new OrganiserMgtOpResult(false, "Property  " + prop + " cannot be found");
                        }
                        return new OrganiserMgtOpResult(true, "Constraint " + cid + "of ProcessDefinition " + pdid + " has been updated ");
                    }
                }
                return new OrganiserMgtOpResult(false, "Constraint " + cid + " of ProcessDefinition " + pdid + " cannot be found");
            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult removeBehaviorRefFromPD(String pdid,
                                                        String bid) {
        ServiceNetwork smcCur = this.getSmcBinding();
        for (ProcessDefinitionsType pdsType : smcCur.getVirtualServiceNetwork()) {
            for (ProcessDefinitionType pdType : pdsType.getProcess()) {
                if (pdType.getId().equals(pdid)) {
                    //found pd
                    for (String btRef : pdType.getCollaborationUnitRef()) {
                        if (btRef.equals(bid)) {
                            pdType.getCollaborationUnitRef().remove(btRef);
                            return new OrganiserMgtOpResult(true, "Behaviour Ref " + bid + "of ProcessDefinition " + pdid + " has been removed ");
                        }
                    }
                    return new OrganiserMgtOpResult(false, "Behaviour Ref " + bid + "of ProcessDefinition " + pdid + " cannot be found ");
                }
            }
        }
        return new OrganiserMgtOpResult(false, "ProcessDefinition " + pdid + " cannot be found");
    }

    @Override
    public OrganiserMgtOpResult removeTaskFromInstance(String pid,
                                                       String tid) {
        ProcessInstance processInstance = this.engine.getProcessInstanceByInsId(pid);
        if (null == processInstance) {
            return new OrganiserMgtOpResult(false, "Process Instance " + pid + " cannot be found");
        }

        InstanceTaskDeleteAction action = new InstanceTaskDeleteAction(tid);
        try {
            action.adapt(processInstance);
        } catch (AdaptationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Process Instance " + pid + " cannot be adapted " + e.getMessage());
        }

        return new OrganiserMgtOpResult(true, "Process Instance " + pid + " adapted");

    }

    @Override
    public OrganiserMgtOpResult removeContraintFromProcessInstance(
            String pid, String cid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrganiserMgtOpResult updateContraintOfProcessInstance(
            String pid, String cid, String expression) {
        // TODO Auto-generated method stub
        return null;
    }

    private ServiceNetwork getSmcBinding() {
        Composition serComposition = this.engine.getComposition();
        Composite composite = serComposition.getComposite();//This is the ROAD composite maintained in MPF
        return composite.getSmcBinding();//This is the SMC maintained in MPF
    }

    //////////////////////////////ROAD //////////////////////////////////////////////
    /*
       @Override
       public MessageWrapper getNextManagementMessage() {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().getNextManagementMessage();
       }
       @Override
       public MessageWrapper getNextManagementMessage(long timeout, TimeUnit unit) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().getNextManagementMessage(timeout, unit);
       }
       @Override
       public OrganiserMgtOpResult sendManagementMessage(MessageWrapper msg,
               String destinationRoleId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().sendManagementMessage(msg, destinationRoleId);
       }
       @Override
       public OrganiserMgtOpResult addRole(String id, String name,
               String description) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().addRole(id, name, description);
       }
       @Override
       public OrganiserMgtOpResult removeRole(String roleId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().removeRole(roleId);
       }
       @Override
       public OrganiserMgtOpResult addNewContract(String id, String name,
               String description, String state, String type, String ruleFile,
               boolean isAbstract, String roleAId, String roleBId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().addNewContract(id, name, description, state, type, ruleFile, isAbstract, roleAId, roleBId);
       }
       @Override
       public OrganiserMgtOpResult removeContract(String contractId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().removeContract(contractId);
       }
       @Override
       public OrganiserMgtOpResult addNewTerm(String id, String name,
               String messageType, String deonticType, String description,
               String direction, String contractId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().addNewTerm(id, name, messageType, deonticType, description, direction, contractId);
       }
       @Override
       public OrganiserMgtOpResult removeTerm(String termId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().removeTerm(termId);
       }
       @Override
       public OrganiserMgtOpResult addNewOperation(String operationName,
               String operationReturnType, Parameter[] parameters, String termId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().addNewOperation(operationName, operationReturnType, parameters, termId);
       }
       @Override
       public OrganiserMgtOpResult removeOperation(String operationName,
               String termId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().removeOperation(operationName, termId);
       }
       @Override
       public OrganiserMgtOpResult addNewContractRule(String newRule,
               String contractId) {
           // TODO Auto-generated method stub
           return this .getOrganiserRole().addNewContractRule(newRule, contractId);
       }
       @Override
       public OrganiserMgtOpResult removeContractRule(String contractId,
               String ruleName) {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().removeContractRule(contractId, ruleName);
       }
       @Override
       public OrganiserMgtOpResult addNewCompositeRule(String newRule) {
           // TODO Auto-generated method stub
           return this.addNewCompositeRule(newRule);
       }
       @Override
       public OrganiserMgtOpResult removeCompositeRule(String ruleName) {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().removeCompositeRule(ruleName);
       }
       @Override
       public Contract getContractById(String id) {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().getContractById(id);
       }
       @Override
       public OrganiserMgtOpResult takeSnapshot() {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().takeSnapshot();
       }
       @Override
       public OrganiserMgtOpResult takeSnapshot(String folder) {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().takeSnapshot(folder);
       }
       @Override
       public OrganiserMgtOpResult changePlayerBinding(String roleId,
               String endpoint) {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().changePlayerBinding(roleId, endpoint);
       }
       @Override
       public FactObject getFact(String factType, String factIdentifierValue) {
           // TODO Auto-generated method stub
           return this.getFact(factType, factIdentifierValue);
       }
       @Override
       public OrganiserMgtOpResult updateFact(FactObject factObject) {
           // TODO Auto-generated method stub
           return this.updateFact(factObject);
       }
       @Override
       public OrganiserMgtOpResult addFact(String factType,
               FactObject factObject) {
           // TODO Auto-generated method stub
           return this.addFact(factType, factObject);
       }
       @Override
       public OrganiserMgtOpResult removeFact(String factType,
               String factIdentifierValue) {
           // TODO Auto-generated method stub
           return this.removeFact(factType, factIdentifierValue);
       }
       @Override
       public String getName() {
           // TODO Auto-generated method stub
           return this.getOrganiserRole().getName();
       }
    */
    private IOrganiserRole getOrganiserRole() {
        return this.getOrganiserRole();
    }

    private ProcessDefinitionType createProcessDefinitionType(OMElement omElement) {
        String nsURI = "http://www.ict.swin.edu.au/serendip/types";
        ProcessDefinitionType processDefinitionType = new ProcessDefinitionType();
        processDefinitionType.setCoS(omElement.getFirstChildWithName(new QName(nsURI, "CoS")).getText().trim());
        processDefinitionType.setCoT(omElement.getFirstChildWithName(new QName(nsURI, "CoT")).getText().trim());
        processDefinitionType.setId(omElement.getAttributeValue(new QName("id")));
        OMElement constraintEle =
                omElement.getFirstChildWithName(new QName(nsURI, "Constraints"));
        if (constraintEle != null) {
            ConstraintsType constraintsType = new ConstraintsType();

            Iterator iterator =
                    constraintEle.getChildrenWithLocalName("Constraint");

            while (iterator.hasNext()) {
                OMElement childConstraint = (OMElement) iterator.next();
                ConstraintType constraintType = new ConstraintType();
                constraintType.setExpression(childConstraint.getText().trim());
            }
            processDefinitionType.setConstraints(constraintsType);
        }
        Iterator iterator =
                omElement.getChildrenWithName(new QName(nsURI, "CollaborationUnitRef"));
        while (iterator.hasNext()) {
            OMElement childBT = (OMElement) iterator.next();
            processDefinitionType.getCollaborationUnitRef().add(childBT.getText().trim());
        }
        return processDefinitionType;
    }

    public OrganiserMgtOpResult addProcessDefinition(OMElement element) {
        if (element == null) {
            return new OrganiserMgtOpResult(false, "Must give a non-empty process definition");
        }
        ProcessDefinitionType processDefinitionType = createProcessDefinitionType(element);
        if (processDefinitionType == null) {
            return new OrganiserMgtOpResult(false, "Must give a non-empty process definition");
        }
        this.engine.getComposition().getComposite().getSmcBinding().getVirtualServiceNetwork().get(0)
                .getProcess().add(processDefinitionType);
        return new OrganiserMgtOpResult(true, "A process definition was added  with id : " + processDefinitionType.getId());
    }

    @Override
    public OrganiserMgtOpResult addProcessDefinitionToGroup(String pgId, OMElement element) {
        JAXBContext jc;
        try {
            jc = JAXBContext
                    .newInstance("au.edu.swin.ict.road.xml.bindings");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<ProcessDefinitionType> pDfsType = unmarshaller.unmarshal(
                    element.getSAXSource(true), ProcessDefinitionType.class);
            this.engine.getComposition().getComposite().addProcessToGroup(pgId, pDfsType.getValue());
        } catch (JAXBException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        }
        return new OrganiserMgtOpResult(true, "A process definition has been added successfully for " + pgId);
    }

    @Override
    public OrganiserMgtOpResult removeProcessDefinitionFromGroup(String pgId, String processId) {
        this.engine.getComposition().getComposite().removeProcessFromGroup(pgId, processId);
        return new OrganiserMgtOpResult(true, "A process group has been removed successfully. Id : " + processId);
    }

    @Override
    public OrganiserMgtOpResult addProcessGroupDefinition(OMElement element) {
        JAXBContext jc;
        try {
            jc = JAXBContext
                    .newInstance("au.edu.swin.ict.road.xml.bindings");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<ProcessDefinitionsType> pDfsType = unmarshaller.unmarshal(
                    element.getSAXSource(true), ProcessDefinitionsType.class);
            this.engine.getComposition().getComposite().addProcessGroup(pDfsType.getValue());
        } catch (JAXBException e) {
            return new OrganiserMgtOpResult(false, e.getMessage());
        }
        return new OrganiserMgtOpResult(true, "A process group has been added successfully.");
    }

    @Override
    public OrganiserMgtOpResult removeProcessGroupDefinition(String pgId) {
        this.engine.getComposition().getComposite().removeProcessGroup(pgId);
        return new OrganiserMgtOpResult(true, "A process group has been removed successfully. Id : " + pgId);
    }

    private String readAFile() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("C:\\Testing\\file.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //ToDO
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();  //ToDO
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();  //ToDo
            }
        }
        return null;
    }


}
