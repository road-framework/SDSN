package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.BaseManagementStateChangeListener;
import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.ProcessManagementState;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRuleExecutionEvent;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.serendip.core.ProcessDefinition;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.core.VSNDefinition;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * TODO
 */
public class ProcessStateChangeListener extends BaseManagementStateChangeListener {
    private static Logger log = Logger.getLogger(ProcessStateChangeListener.class.getName());
    private Role role;
    private RoleServiceMessageQueue messageQueue;
    private String vsnId;
    private Composite composite;

    public ProcessStateChangeListener(Role role, RoleServiceMessageQueue messageQueue, String vsnId) {
        this.role = role;
        this.messageQueue = messageQueue;
        this.vsnId = vsnId;
        this.composite = role.getComposite();
    }

    public void notify(ManagementState managementState) {
        System.out.println("ProcessStateChangeListener : resuming the processing of the messages for the process : " +
                managementState.getState());
        List<RoleServiceMessage> messages = messageQueue.dequeueAll();
        for (RoleServiceMessage message : messages) {
            if (managementState.getState().equals(ManagementState.STATE_ACTIVE)) {
                message.setState(RoleServiceMessage.STATE_FORWARDABLE);
                Classifier classifier = message.getClassifier();
                VSNDefinition vsnDefinition = composite.getSerendipEngine().getProcessDefinitionGroup(vsnId);
                ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
                if (classifier.getProcessInsId() == null) {
                    createProcessInstance(message.getMessageWrapper(), classifier);
                }
                System.out.println("ProcessStateChangeListener : resuming the processing of the messages for the process instance " + classifier.getProcessInsId());
                role.executeRoutingRules(
                        new RoutingRuleExecutionEvent(message, vsnDefinition.getMgtState(), (ProcessManagementState)
                                processDefinition.getMgtState()), classifier.getVsnId() + "_" + classifier
                                .getProcessId());
            }
        }
    }

    private void createProcessInstance(MessageWrapper message, Classifier classifier) {
        // create a process instance for the selected alternative  -
        // the selection of a process definition is a routing decision - not just CoS match
        String pid = message.getClassifier().getProcessInsId();
        if (pid == null || "".equals(pid)) {
            String route = classifier.getProcessId();
            try {
                ProcessInstance pi = role.getComposite().getSerendipEngine().startProcessInstanceV2(classifier, route);
                classifier.setProcessInsId(pi.getId());
                message.setCorrelationId(pi.getId());
                //TODO verify COS condition
            } catch (SerendipException e) {
                log.error("Error creating a process instance for the message " +
                        message.getMessageId() + "," + e.getMessage(), e);
            }
        }
    }
}
