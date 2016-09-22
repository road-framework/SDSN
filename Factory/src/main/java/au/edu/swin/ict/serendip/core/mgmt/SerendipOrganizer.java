package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.serendip.core.mgmt.action.DefAdaptAction;
import au.edu.swin.ict.serendip.core.mgmt.action.InstanceAdaptAction;
import org.apache.axiom.om.OMElement;

import java.util.List;

public interface SerendipOrganizer {


    //Advanced Adaptation methods. Can be used to do batch mode executions
    public OrganiserMgtOpResult executeScript(String script);

    public OrganiserMgtOpResult executeScheduledScript(String script, String onEventPattern, String pid);

    public OrganiserMgtOpResult applyPatch(String patchFile);//used to patch a partial SMC file. Can be also called via executeScript

    //////////////////Evolutionary phase 2 changes/////////////
    public OrganiserMgtOpResult addNewPD(String pdid);

    public OrganiserMgtOpResult removePD(String pdid);

    public OrganiserMgtOpResult updatePD(String pdid, String prop, String val);

    public OrganiserMgtOpResult addNewBehaviorConstraint(String btid, String cid, String expression, boolean enabled);

    public OrganiserMgtOpResult removeBehaviorConstraint(String btid, String cid);

    public OrganiserMgtOpResult updateBehaviorConstraint(String btid, String cid, String prop, String val);

    public OrganiserMgtOpResult addNewProcessConstraint(String pdid, String cid, String expression, boolean enabled);

    public OrganiserMgtOpResult removeProcessConstraint(String pdid, String cid);

    public OrganiserMgtOpResult updateProcessConstraint(String pdif, String cid, String prop, String val);

//    public OrganiserMgtOpResult addTaskToBehavior(String btid, String tid, String preep, String postep, String pp);
//
//    public OrganiserMgtOpResult removeTaskFromBehavior(String id, String behaviorId);
//
//    public OrganiserMgtOpResult updateTaskFromBehavior(String id, String behaviorId, String property, String value);

    public OrganiserMgtOpResult addBehaviorRefToPD(String pdid, String btid);

    public OrganiserMgtOpResult removeBehaviorRefFromPD(String pdid, String bid);

    //////////////////Ad-hoc runtime process instance level (phase 3) operations /////////////


    public OrganiserMgtOpResult addTaskToInstance(String pid, String btid, String tid, String preEP, String postEP, String obligRole, String pp);

    public OrganiserMgtOpResult removeTaskFromInstance(String pid, String tid);

    public OrganiserMgtOpResult updatePropertyofTaskOfInstance(String pid, String taskid, String property, String value);

    public OrganiserMgtOpResult addConstraintToProcessInstance(String pid, String cid, String expression, boolean enabled);

    public OrganiserMgtOpResult removeContraintFromProcessInstance(String pid, String cid);

    public OrganiserMgtOpResult updateContraintOfProcessInstance(String pid, String cid, String expression);

    public OrganiserMgtOpResult updatePropertyOfProcessInstance(String pid, String property, String value);

    public OrganiserMgtOpResult updateProcessInstanceState(String pid, String status);

    public OrganiserMgtOpResult updateStateofAllProcessInstances(String state);


    //Events
    public OrganiserMgtOpResult addNewEvent(String eventId, String pid, int expiration);

    public OrganiserMgtOpResult removeEvent(String eventId, String pid);

    public OrganiserMgtOpResult setEventExpiration(String eventId, String pid, int expiration);

    public OrganiserMgtOpResult subscribeToEventPattern(String eventPattern, String pid);

    //Methods to be used by local organizers
    public OrganiserMgtOpResult adaptProcessInstance(String processInstanceId, List<InstanceAdaptAction> adaptationActions);

    public OrganiserMgtOpResult adaptDefinition(List<DefAdaptAction> adaptationActions);

    public OrganiserMgtOpResult addProcessDefinition(OMElement element);

    public OrganiserMgtOpResult addProcessDefinitionToGroup(String pgId, OMElement element);

    public OrganiserMgtOpResult removeProcessDefinitionFromGroup(String pgId, String processI);

    public OrganiserMgtOpResult addProcessGroupDefinition(OMElement element);

    public OrganiserMgtOpResult removeProcessGroupDefinition(String pgId);

}
