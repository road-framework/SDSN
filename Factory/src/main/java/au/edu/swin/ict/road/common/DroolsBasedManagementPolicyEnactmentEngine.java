package au.edu.swin.ict.road.common;

import au.edu.swin.ict.road.composite.utills.ROADThreadPool;
import au.edu.swin.ict.road.composite.utills.ROADThreadPoolFactory;

import javax.activation.DataHandler;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 */
public class DroolsBasedManagementPolicyEnactmentEngine extends BaseManagementStateChangeListener {

    private IOperationalManagerRole iOperationalManagerRole;
    private IOrganiserRole iOrganiserRole;
    private String rulesDir;
    private final ConcurrentHashMap<String, ManagementPolicy> managementPolicyMap = new ConcurrentHashMap<String,
            ManagementPolicy>();
    private Map<String, DisabledRuleSet> disabledRuleSetMap = new HashMap<String, DisabledRuleSet>();
    private ROADThreadPool roadThreadPool = ROADThreadPoolFactory.createROADThreadPool("ManamentPolicyExecutor");

    public DroolsBasedManagementPolicyEnactmentEngine(IOperationalManagerRole iOperationalManagerRole, IOrganiserRole iOrganiserRole, String rulesDir) {
        this.iOperationalManagerRole = iOperationalManagerRole;
        this.iOrganiserRole = iOrganiserRole;
        this.rulesDir = rulesDir;
    }

    public void enactManagementPolicy(String policyId, String policyFile) {
        if (managementPolicyMap.containsKey(policyId)) {
            throw new RuntimeException("There is already a policy with the given id : " + policyId);
        }
        DroolsManagementRules mgtPolicy = loadDroolsManagementRules(policyFile);
        ManagementPolicyState policyState = new ManagementPolicyState();
        policyState.setiOperationalManagerRole(iOperationalManagerRole);
        policyState.setiOrganiserRole(iOrganiserRole);
        policyState.setId(policyId);
        ManagementPolicy managementPolicy = new ManagementPolicy(policyId, mgtPolicy, policyState);
        managementPolicyMap.put(policyId, managementPolicy);
        managementPolicy.getManagementPolicyState().subscribe(this);
        enactPolicy(policyId, policyState);
    }

    public void enactManagementPolicy(String policyId, DataHandler dataHandler) {
        if (managementPolicyMap.containsKey(policyId)) {
            throw new RuntimeException("There is already a policy with the given id : " + policyId);
        }
        DroolsManagementRules mgtPolicy = loadDroolsManagementRules(dataHandler);
        ManagementPolicyState policyState = new ManagementPolicyState();
        policyState.setiOperationalManagerRole(iOperationalManagerRole);
        policyState.setiOrganiserRole(iOrganiserRole);
        policyState.setId(policyId);
        ManagementPolicy managementPolicy = new ManagementPolicy(policyId, mgtPolicy, policyState);
        managementPolicyMap.put(policyId, managementPolicy);
        managementPolicy.getManagementPolicyState().subscribe(this);
        enactPolicy(policyId, policyState);
    }

    private DroolsManagementRules loadDroolsManagementRules(String ruleFile) {
        if (new File(ruleFile).exists()) {
            return new DroolsManagementRules(ruleFile, rulesDir);
        } else {
            String mgtPolicyDir = "mgtpolicies" +"/";
            return new DroolsManagementRules(ruleFile, rulesDir + mgtPolicyDir);
        }
    }

    private DroolsManagementRules loadDroolsManagementRules(DataHandler dataHandle) {
        return new DroolsManagementRules(dataHandle);
    }

    private void enactPolicy(String policyId, IEvent iEvent) {
        EventCollection eventCollection = new EventCollection();
        eventCollection.setiOperationalManagerRole(iOperationalManagerRole);
        eventCollection.setiOrganiserRole(iOrganiserRole);
        eventCollection.addIEvent(iEvent);
        eventCollection.setDisabledRuleSet(getDisabledRuleSet(policyId));
        roadThreadPool.execute(new PolicyExecutionCommand(managementPolicyMap.get(policyId).getManagementRules(), eventCollection));

    }

    public void notify(ManagementState managementState) {
        super.notify(managementState);
        if (managementState instanceof ManagementPolicyState) {
            ManagementPolicyState mps = (ManagementPolicyState) managementState;
            if (mps.getState().equals(ManagementState.STATE_QUIESCENCE)) {
                managementPolicyMap.remove(mps.getId());
            }
        }
        Collection<IEvent> managementStates = getManagementStates();
        for (String policyId : managementPolicyMap.keySet()) {
            ManagementPolicy managementPolicy = managementPolicyMap.get(policyId);
            if (managementPolicy == null) {
                return;
            }
            if (managementPolicy.getManagementPolicyState().getState().equals(ManagementState.STATE_QUIESCENCE)) {
                managementPolicyMap.remove(policyId);
            } else {
                EventCollection eventCollection = new EventCollection();
                eventCollection.addAllIEvents(managementStates);
                eventCollection.addIEvent(managementPolicy.getManagementPolicyState());
                eventCollection.setDisabledRuleSet(getDisabledRuleSet(policyId));
                eventCollection.setiOperationalManagerRole(iOperationalManagerRole);
                eventCollection.setiOrganiserRole(iOrganiserRole);
                roadThreadPool.execute(new PolicyExecutionCommand(managementPolicy.getManagementRules(), eventCollection));
            }
        }
    }

    public synchronized DisabledRuleSet getDisabledRuleSet(String policyId) {

        DisabledRuleSet disabledRuleSet = disabledRuleSetMap.get(policyId);
        if (disabledRuleSet == null) {
            disabledRuleSet = new DisabledRuleSet(policyId);
            disabledRuleSetMap.put(policyId, disabledRuleSet);
        }
        return disabledRuleSet;
    }
}