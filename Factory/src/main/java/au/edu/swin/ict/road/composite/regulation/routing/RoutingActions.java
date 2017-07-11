package au.edu.swin.ict.road.composite.regulation.routing;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.RegulationMechanism;
import au.edu.swin.ict.road.common.VSN;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.flowcontrol.MessageTransferCommand;
import au.edu.swin.ict.road.composite.flowcontrol.SimpleAdmissionController;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.analyzer.TransformLogic;
import au.edu.swin.ict.road.composite.message.analyzer.XSLTAnalyzer;
import au.edu.swin.ict.road.composite.regulation.BasedActions;
import au.edu.swin.ict.road.composite.regulation.ProcessStateChangeListener;
import au.edu.swin.ict.road.composite.regulation.RoleServiceMessageQueue;
import au.edu.swin.ict.road.composite.regulation.VSNStateChangeListener;
import au.edu.swin.ict.road.composite.routing.WeightedRoundRobin;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.serendip.core.ProcessDefinition;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.core.VSNDefinition;
import au.edu.swin.ict.serendip.message.SOAPFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * TODO
 */
public class RoutingActions extends BasedActions {

    private static Logger log = Logger.getLogger(RoutingActions.class.getName());
    final private RoutingRegTable routingRegTable;
    private Role role;

    public RoutingActions(Role role) {
        super(role.getComposite());
        this.role = role;
        this.routingRegTable = role.getRoutingRegTable();
    }

    public void FixedThroughput(String id, String conf, RoleServiceMessage messageEvent) {
        Classifier classifier = messageEvent.getMessageWrapper().getClassifier();
        String newId = classifier.getVsnId() + "_" + id + "ft";
        SimpleAdmissionController rateController;
        if (routingRegTable.containsFlowControlFunction(newId)) {
            rateController =
                    (SimpleAdmissionController) routingRegTable.getFlowControlFunction(newId);
        } else {
            synchronized (routingRegTable) {
                if (routingRegTable.containsFlowControlFunction(newId)) {
                    rateController =
                            (SimpleAdmissionController) routingRegTable.getFlowControlFunction(newId);
                } else {
                    rateController = new SimpleAdmissionController(conf.trim());
                    routingRegTable.addFlowControlFunction(newId, rateController);
                }
            }
        }

        if (rateController.isNewConfig(conf)) {
            System.out.println("Re-instantiation of the regulation mechanism SimpleAdmissionController " +
                    newId + " with configuration " + conf);
            rateController = new SimpleAdmissionController(conf.trim());
            routingRegTable.removeFlowControlFunction(newId);
            routingRegTable.addFlowControlFunction(newId, rateController);
        }

        FlowControlResult controlResult = rateController.admit(messageEvent);
        if (controlResult.getState() == FlowControlResult.DENIED) {
            messageEvent.setState(RoleServiceMessage.STATE_DROPPABLE);
            messageEvent.setErrorMessage(controlResult.getMessage());
        } else {
            messageEvent.setState(RoleServiceMessage.STATE_ADMITTED);
            if (classifier.getProcessId() == null) {
                setDefaultProcess(classifier, messageEvent);
            }
        }

    }

    private void setDefaultProcess(Classifier classifier, RoleServiceMessage messageEvent) {
        //Single process - no loadbalance
        VSNDefinition vsn =
                role.getComposite().getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        if (vsn.getNoOfProcess() == 1) {
            for (ProcessDefinition processDefinition : vsn.getAllProcessDefinitions()) {
                classifier.setProcessId(processDefinition.getId());
                if (processDefinition.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
                    messageEvent.setState(RoleServiceMessage.STATE_INSTANTAIBLE);
//                            createProcessInstance(messageEvent.getMessageWrapper(), classifier);
                } else {
                    messageEvent.setState(RoleServiceMessage.STATE_ROUTED);
                }
                break;
            }
        }
    }

    public void AdmissionControl(String id, String conf, RoleServiceMessage messageEvent) {
        FixedThroughput(id, conf, messageEvent);
    }

    public void Allow(RoleServiceMessage messageEvent) {
        Classifier classifier = messageEvent.getMessageWrapper().getClassifier();
        if (classifier.getProcessId() == null) {
            setDefaultProcess(classifier, messageEvent);
        }
    }

    public void WeightedRoundRobin(String id, String conf, RoleServiceMessage messageEvent) {
        Classifier classifier = messageEvent.getMessageWrapper().getClassifier();
        String newId = classifier.getVsnId() + "_" + id + "wrr";
        WeightedRoundRobin weightedRoundRobin;
        if (routingRegTable.containsRoutingFunction(newId)) {
            weightedRoundRobin =
                    (WeightedRoundRobin) routingRegTable.getRoutingFunction(newId);
        } else {
            synchronized (routingRegTable) {
                if (routingRegTable.containsRoutingFunction(newId)) {
                    weightedRoundRobin =
                            (WeightedRoundRobin) routingRegTable.getRoutingFunction(newId);
                } else {
                    weightedRoundRobin = new WeightedRoundRobin(conf.trim());
                    routingRegTable.addRoutingFunction(newId, weightedRoundRobin);
                }
            }
        }
        weightedRoundRobin.execute(messageEvent);
        VSNDefinition vsnDefinition =
                composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
        if (processDefinition.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
            messageEvent.setState(RoleServiceMessage.STATE_INSTANTAIBLE);
//            createProcessInstance(messageEvent.getMessageWrapper(), classifier);
        } else {
            messageEvent.setState(RoleServiceMessage.STATE_ROUTED);
        }
    }

    public void LoadBalance(String id, String conf, RoleServiceMessage messageEvent) {
        WeightedRoundRobin(id, conf, messageEvent);
    }

    public void Forward(String messageId, String xsltFile, RoleServiceMessage messageEvent) {
        role.getComposite().getMessageDelivererWorkers().execute(
                new MessageTransferCommand(role, Synthesize(messageId, xsltFile, messageEvent)));
    }

    public void AlterPath(String regIdsTobeExcluded, String regIdsTobeIncluded, RoleServiceMessage messageEvent) {

    }

    public void Forward(MessageWrapper messageWrapper) {
        role.getComposite().getMessageDelivererWorkers().execute(
                new MessageTransferCommand(role, messageWrapper));
    }

    public MessageWrapper Synthesize(String messageId, String xsltFile, RoleServiceMessage messageEvent) {
        String[] interactionInfo = messageId.trim().split("\\.");
        String contractId = interactionInfo[0].trim();
        String interactionId = interactionInfo[1].trim();
        boolean isResponse = interactionInfo[2].trim().equals("Res");
        TransformLogic tLogic = new TransformLogic();
        tLogic.setXsltFile(xsltFile);
        tLogic.setContractId(contractId);
        tLogic.setInteractionId(interactionId);
        tLogic.setResponse(isResponse);
        tLogic.setRole(role);
        return XSLTAnalyzer.getInstance().transform(messageEvent.getMessageWrapper(), tLogic);
    }

    public void Scatter(String targets, RoleServiceMessage message) {
        String[] targetsArray = targets.split(",");
        for (String st1 : targetsArray) {
            String[] st2 = st1.split(":");
            Forward(st2[0], st2[1], message);
        }
    }

    public void Drop(RoleServiceMessage roleServiceMessage) {
        MessageWrapper mw = roleServiceMessage.getMessageWrapper();
        String reasonString = roleServiceMessage.getErrorMessage();
        mw.setMessage(SOAPFactory.createFaultEnvelope((SOAPEnvelope) mw.getMessage(), reasonString));
        mw.setResponse(true);
        mw.setFault(true);
        roleServiceMessage.setState(RoleServiceMessage.STATE_DROPPED);
        role.delivererPutOutgoingSyncMessage(mw);
    }

    public void OnPassiveVSN(String id, String conf, RoleServiceMessage roleServiceMessage) {
        roleServiceMessage.setState(RoleServiceMessage.STATE_DELAYED);
        Classifier classifier = roleServiceMessage.getMessageWrapper().getClassifier();
        String newId = classifier.getVsnId() + "_" + id + "queue";
        RoleServiceMessageQueue roleServiceMessageQueue;
        if (routingRegTable.containsRoutingFunction(newId)) {
            roleServiceMessageQueue =
                    (RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId);
        } else {
            synchronized (routingRegTable) {
                if (routingRegTable.containsRoutingFunction(newId)) {
                    roleServiceMessageQueue =
                            (RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId);
                } else {
                    roleServiceMessageQueue = new RoleServiceMessageQueue(conf);
                    routingRegTable.addRoutingFunction(newId, roleServiceMessageQueue);
                }
            }
        }
        roleServiceMessageQueue.enqueue(roleServiceMessage);
        System.out.println("Queuing Message : " + roleServiceMessage.getMessageWrapper().getMessageId() + " as VSN : " + classifier.getVsnId() + " is passive.");
        VSNDefinition vsnDefinition =
                composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        vsnDefinition.getMgtState().subscribe(new VSNStateChangeListener(role, roleServiceMessageQueue, classifier.getVsnId()));
    }

    public void OnPassiveProcess(String id, String conf, RoleServiceMessage roleServiceMessage) {
        roleServiceMessage.setState(RoleServiceMessage.STATE_DELAYED);
        Classifier classifier = roleServiceMessage.getMessageWrapper().getClassifier();
        String newId = classifier.getVsnId() + "_" + classifier.getProcessId() + "_" + id + "queue";
        RoleServiceMessageQueue roleServiceMessageQueue;
        if (routingRegTable.containsRoutingFunction(newId)) {
            roleServiceMessageQueue =
                    (RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId);
        } else {
            synchronized (routingRegTable) {
                if (routingRegTable.containsRoutingFunction(newId)) {
                    roleServiceMessageQueue =
                            (RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId);
                } else {
                    roleServiceMessageQueue = new RoleServiceMessageQueue(conf);
                    routingRegTable.addRoutingFunction(newId, roleServiceMessageQueue);
                }
            }
        }
        roleServiceMessageQueue.enqueue(roleServiceMessage);
        System.out.println("Queuing Message : " + roleServiceMessage.getMessageWrapper().getMessageId() + " as Process : " + classifier.getProcessId() + " is passive.");
        VSNDefinition vsnDefinition =
                composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        ProcessDefinition processDefinition = vsnDefinition.getProcessDefinition(classifier.getProcessId());
        processDefinition.getMgtState().subscribe(new ProcessStateChangeListener(role, roleServiceMessageQueue, classifier.getVsnId()));
    }

    public RoleServiceMessage DeQueue(String id, VSN vsn) {
        String newId = vsn.getId() + "_" + id + "queue";
        RoleServiceMessage roleServiceMessage =
                ((RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId)).dequeue();
        roleServiceMessage.setState(RoleServiceMessage.STATE_ADMITTABLE);
        return roleServiceMessage;
    }

    public List<RoleServiceMessage> DeQueueAll(String id, VSN vsn) {
        String newId = vsn.getId() + "_" + id + "queue";
        List<RoleServiceMessage> messages =
                ((RoleServiceMessageQueue) routingRegTable.getRoutingFunction(newId)).dequeueAll();
        for (RoleServiceMessage message : messages) {
            message.setState(RoleServiceMessage.STATE_ADMITTABLE);
        }
        return messages;
    }

    public RoleServiceMessage Pull(String messageId) {
        return null;
    }

    public void Schedule(String id, String conf, RoleServiceMessage roleServiceMessage) {

    }

    public void Schedule(MessageWrapper messageWrapper) {

    }

    public void Classify(RoleServiceMessage roleServiceMessage) {
        MessageWrapper mwRequest = roleServiceMessage.getMessageWrapper();
        String roleID = mwRequest.getOriginRoleId();
        Classifier classifier = new Classifier();
        classifier.setProcessRole(roleID);
        classifier.setVsnId(mwRequest.getTargetVSN());  //Tenant Identifier
        mwRequest.setClassifier(classifier);
        VSNDefinition vsnDefinition =
                composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId());
        if (vsnDefinition.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
            roleServiceMessage.setState(RoleServiceMessage.STATE_ADMITTABLE);
        } else {
            roleServiceMessage.setState(RoleServiceMessage.STATE_CLASSIFIED);
        }
    }

    public Object execute(String rmId, String config, Object... arguments) {
        RegulationMechanism regulationMechanism = role.getComposite().getRegulationMechanism(rmId);
        if (regulationMechanism == null) {
            log.error("There is no regulation mechanism with the id :  " + rmId);
            return null;
        }
        if (!regulationMechanism.getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
            log.error("The regulation mechanism with the id :  " + rmId + " currently cannot be used");
            return null;
        }
        String className = regulationMechanism.getClassName();
        if (className == null) {
            log.error("The regulation mechanism with the id :  " + rmId + " does not have a class name");
            return null;
        }
        Classifier classifier = (Classifier) arguments[0];
        String newId = classifier.getVsnId() + "_" + rmId + "ft";
        Object regMechanism = null;
        try {
            Class aClass = Class.forName(className);
            if (routingRegTable.containsCustomFunction(newId)) {
                regMechanism = routingRegTable.getCustomFunction(newId);
            } else {
                synchronized (routingRegTable) {
                    if (routingRegTable.containsCustomFunction(newId)) {
                        regMechanism = routingRegTable.getCustomFunction(newId);
                    } else {
                        try {
                            regMechanism = aClass.newInstance();
                            Method m = aClass.getMethod("init", String.class);
                            m.invoke(regMechanism, config);
                            routingRegTable.addCustomFunction(newId, regMechanism);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            Class[] argTypes = new Class[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                argTypes[i] = arguments[i].getClass();
            }
            try {
                Method m2 = aClass.getMethod("execute", argTypes);
                m2.invoke(regMechanism, arguments);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (classifier.getProcessInsId() == null) {
            if (arguments[1] instanceof RoleServiceMessage) {
                ((RoleServiceMessage) arguments[1]).setState(RoleServiceMessage.STATE_INSTANTAIBLE);
//                createProcessInstance(((RoleServiceMessage) arguments[1]));
            }
        }
        return null;
    }

    public void CreateVSNInstance(RoleServiceMessage roleServiceMessage) {
        MessageWrapper message = roleServiceMessage.getMessageWrapper();
        Classifier classifier = roleServiceMessage.getClassifier();

        // create a process instance for the selected alternative  -
        // the selection of a process definition is a routing decision - not just CoS match
        String pid = message.getClassifier().getProcessInsId();
        if (pid == null || "".equals(pid)) {
            String route = classifier.getProcessId();
            try {
                ProcessInstance pi = role.getComposite().getSerendipEngine().startProcessInstanceV2(classifier, route);
                if (pi == null) {
                    ProcessDefinition definition = composite.getSerendipEngine().getProcessDefinitionGroup(classifier.getVsnId()).getProcessDefinition(classifier.getProcessId());
                    if (definition.getMgtState().getState().equals(ManagementState.STATE_PASSIVE)) {
                        roleServiceMessage.setState(RoleServiceMessage.STATE_ROUTED);
                    }
                } else {
                    classifier.setProcessInsId(pi.getId());
                    message.setCorrelationId(pi.getId());
                    roleServiceMessage.setState(RoleServiceMessage.STATE_FORWARDABLE);
                }
                //TODO verify COS condition
            } catch (SerendipException e) {
                log.error("Error creating a process instance for the message " +
                        message.getMessageId() + "," + e.getMessage(), e);
            }
        }
    }
}