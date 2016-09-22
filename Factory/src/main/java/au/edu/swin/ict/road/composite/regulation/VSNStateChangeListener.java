package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.BaseManagementStateChangeListener;
import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.ProcessManagementState;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRuleExecutionEvent;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.serendip.core.ProcessDefinition;
import au.edu.swin.ict.serendip.core.VSNDefinition;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * TODO
 */
public class VSNStateChangeListener extends BaseManagementStateChangeListener {
    private static Logger log = Logger.getLogger(VSNStateChangeListener.class.getName());
    private Role role;
    private RoleServiceMessageQueue messageQueue;
    private String vsnId;
    private Composite composite;

    public VSNStateChangeListener(Role role, RoleServiceMessageQueue messageQueue, String vsnId) {
        this.role = role;
        this.messageQueue = messageQueue;
        this.vsnId = vsnId;
        this.composite = role.getComposite();
    }

    public void notify(ManagementState managementState) {
        System.out.println("VSNStateChangeListener has been notified of state change for VSN : " + vsnId + " state : " + managementState.getState());
        List<RoleServiceMessage> messages = messageQueue.dequeueAll();
        for (RoleServiceMessage message : messages) {
            if (managementState.getState().equals(ManagementState.STATE_ACTIVE)) {
                System.out.println("VSNStateChangeListener resumes the processing of the messages for VSN : " + vsnId);
                message.setState(RoleServiceMessage.STATE_ADMITTABLE);
                role.executeRoutingRules(new RoutingRuleExecutionEvent(message), vsnId);
                Classifier classifier = message.getClassifier();
                VSNDefinition vsnDefinition = composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
                if (classifier.getProcessId() == null) {
                    role.executeRoutingRules(new RoutingRuleExecutionEvent(message, vsnDefinition.getMgtState()),
                                             classifier.getVsnId());
                }
                if (classifier.getProcessId() != null) {
                    ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
                    role.executeRoutingRules(new RoutingRuleExecutionEvent(message, vsnDefinition.getMgtState(), (ProcessManagementState) processDefinition.getMgtState()),
                                             classifier.getVsnId() + "_" + classifier.getProcessId());
                }
            }
        }
    }
}
