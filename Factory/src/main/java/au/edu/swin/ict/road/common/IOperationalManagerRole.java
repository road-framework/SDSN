package au.edu.swin.ict.road.common;

import org.apache.axiom.om.OMElement;

/**
 * TODO documentation
 */
public interface IOperationalManagerRole {

    public OMElement getNextManagementMessageBlocking();

    public OMElement getNextManagementMessage(long timeout);

    public OrganiserMgtOpResult subscribeToManagementMessages(String epPattern, String notificationOperation);

    public OrganiserMgtOpResult enactOperationalManagementPolicy(String policyId, String policyFile);

    public OrganiserMgtOpResult enactOperationalManagementPolicyRemote(String policyId, OMElement policyFile);

    public OperationalMgtOpResult addRegulationMechanism(String rmId, String className, String jarFileLocation);

    public OperationalMgtOpResult removeRegulationMechanism(String rmId);

    public OperationalMgtOpResult updateRegulationMechanism(String rmId, String property, String value);

    public OperationalMgtOpResult addSynchronizationRule(String place, String ruleContent);

    public OperationalMgtOpResult removeSynchronizationRule(String place, String ruleId);

    public OperationalMgtOpResult updateSynchronizationRule(String place, String ruleId, String property, String value);

    public OperationalMgtOpResult addRoutingRule(String place, String ruleContent);

    public OperationalMgtOpResult removeRoutingRule(String place, String ruleId);

    public OperationalMgtOpResult updateRoutingRule(String place, String ruleId, String property, String value);

    public OperationalMgtOpResult addPassthroughRule(String place, String ruleContent);

    public OperationalMgtOpResult removePassthroughRule(String place, String ruleId);

    public OperationalMgtOpResult updatePassthroughRule(String place, String ruleId, String property, String value);

    public OperationalMgtOpResult addGlobalRule(String ruleContent);

    public OperationalMgtOpResult removeGlobalRule(String ruleId);

    public OperationalMgtOpResult updateGlobalRule(String ruleId, String property, String value);

    public OperationalMgtOpResult addRegulationUnit(String ruId);

    public OperationalMgtOpResult removeRegulationUnit(String ruId);

    public OperationalMgtOpResult updateRegulationUnit(String ruId, String property, String value);

    public OperationalMgtOpResult addSynchronizationRulesToRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult removeSynchronizationRulesFromRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult addRoutingRulesToRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult removeRoutingRulesFromRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult addPassthroughRulesToRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult removePassthroughRulesFromRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult addGlobalRulesToRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult removeGlobalRulesFromRegulationUnit(String ruId, String ruleIds);

    public OperationalMgtOpResult addRegulationUnitsToProcessRegulationPolicy(String vsnId, String processId, String ruIds);

    public OperationalMgtOpResult removeRegulationUnitsFromProcessRegulationPolicy(String vsnId, String processId, String ruIds);

    public OperationalMgtOpResult updateRegulationUnitOfProcessRegulationPolicy(String vsnId, String processId, String ruId, String property, String value);

    public OperationalMgtOpResult updateRegulationUnitsOfProcessRegulationPolicy(String vsnId, String processId, String ruId, String property, String value);

    public OperationalMgtOpResult addRegulationUnitsToVSNRegulationPolicy(String vsnId, String ruIds);

    public OperationalMgtOpResult addParameterizedRegulationUnitToVSNRegulationPolicy(String vsnId, String ruId, String parameters);

    public OperationalMgtOpResult removeRegulationUnitsFromVSNRegulationPolicy(String vsnId, String ruIds);

    public OperationalMgtOpResult updateRegulationUnitOfVSNRegulationPolicy(String vsnId, String ruId, String property, String value);

    public OperationalMgtOpResult updateRegulationUnitsOfVSNRegulationPolicy(String vsnId, String ruId, String property, String value);

    public OperationalMgtOpResult addRegulationUnitsToServiceNetworkRegulationPolicy(String ruIds);

    public OperationalMgtOpResult removeRegulationUnitsFromServiceNetworkRegulationPolicy(String ruIds);

    public OperationalMgtOpResult updateRegulationUnitsOfServiceNetworkRegulationPolicy(String ruIds, String property, String value);

    public OperationalMgtOpResult updateRegulationUnitOfServiceNetworkRegulationPolicy(String ruIds, String property, String value);

    public OperationalMgtOpResult addServiceNetworkEvent(String eventId, String places, Classifier classifier);

    public OperationalMgtOpResult removeServiceNetworkEvent(String eventId, Classifier classifier);

    public OperationalMgtOpResult updateServiceNetworkEvent(String eventId, Classifier classifier, String property, String value);

    public OperationalMgtOpResult addServiceNetworkStateImplementation(String stateType, String className, String jarFileLocation);

    public OperationalMgtOpResult removeServiceNetworkStateImplementation(String stateType);

    public OperationalMgtOpResult updateServiceNetworkStateImplementation(String stateType, String property, String value);

    public OperationalMgtOpResult addServiceNetworkState(String stateId, String stateType, String scope, Classifier classifier);

    public OperationalMgtOpResult removeServiceNetworkState(String stateId, String scope, Classifier classifier);

    public OperationalMgtOpResult updateServiceNetworkState(String stateId, String scope, Classifier classifier, String property, String value);

    public OperationalMgtOpResult setOperationalManagerBinding(String epr);
}