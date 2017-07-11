package au.edu.swin.ict.road.composite.regulation.synchronization;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.RegulationMechanism;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.analyzer.MessageAnalyzer;
import au.edu.swin.ict.road.composite.message.analyzer.TransformLogic;
import au.edu.swin.ict.road.composite.message.analyzer.XSLTAnalyzer;
import au.edu.swin.ict.road.composite.regulation.BasedActions;
import au.edu.swin.ict.road.xml.bindings.InMsgType;
import au.edu.swin.ict.road.xml.bindings.OutMsgType;
import au.edu.swin.ict.road.xml.bindings.TaskType;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 */
public class SynchronizationActions extends BasedActions {

    private static Logger log = Logger.getLogger(SynchronizationActions.class.getName());
    private Classifier classifier;
    private Role role;
    private SynchronizationRegTable synchronizationRegTable;

    public SynchronizationActions(Role role, Classifier classifier) {
        super(role.getComposite());
        this.role = role;
        this.classifier = classifier;
        this.synchronizationRegTable = role.getSynchronizationRegTable();
    }

    public void Synchronize(String[] messageIds, String xsltFile, String taskId) {
        MessageWrapper[] msgs = Pull(messageIds);
        MessageWrapper result = Synthesis(msgs, xsltFile, taskId);
        ExecuteTask(result);
    }

    public MessageWrapper[] Pull(String[] messageIds) {
        List<MessageWrapper> messages = new ArrayList<MessageWrapper>();
        for (String msgId : messageIds) {
            MessageWrapper message = role.pollPendingOutBufMessage(msgId, classifier.getProcessInsId());

            if (null != message) {
                messages.add(message);
            } else {
                if (log.isInfoEnabled()) {
                    log.info(
                            "Message retrieval failed (" + msgId + "," + classifier.getProcessInsId() +
                                    ") from PendingOutBuf of " + role.getId()
                    );
                }
            }
        }
        return messages.toArray(new MessageWrapper[messages.size()]);
    }

    public MessageWrapper Synthesis(MessageWrapper[] messages, String xsltFile, String taskId) {
        if (messages == null || (messages.length == 0)) {
            log.warn("The messages are empty for task " + taskId);
            return null;
        }
        TaskType task = role.getTaskType(taskId);
        TransformLogic tLogic = new TransformLogic();
        OutMsgType outMsgType = task.getOut();
        boolean isResponse = outMsgType.isIsResponse();
        if (!isResponse) {
            InMsgType inMsgType = task.getIn();
            if (inMsgType != null && inMsgType.isIsResponse()) {
                tLogic.setSyncType(MessageWrapper.SyncType.OUTIN);
            } else {
                tLogic.setSyncType(MessageWrapper.SyncType.OUT);
            }
        }

        String deliveryType = outMsgType.getDeliveryType();
        if (deliveryType == null) {
            deliveryType = "push";
            outMsgType.setDeliveryType(deliveryType);
        }
        tLogic.setTask(task);
        tLogic.setOperationName(outMsgType.getOperation().getName());
        tLogic.setDeliveryType(deliveryType);
        tLogic.setResponse(isResponse);
        tLogic.setRole(role);
        tLogic.setXsltFile(xsltFile);
        try {
            MessageAnalyzer analyzer = XSLTAnalyzer.getInstance();
            MessageWrapper conjunctMessage = analyzer.conjunct(Arrays.asList(messages), tLogic);
//                        role.getComposite().getBenchUtil().addBenchRecord("OUTTRANS END", task.getId());
            conjunctMessage.setCorrelationId(messages[0].getClassifier().getProcessInsId());
            conjunctMessage.setClassifier(messages[0].getClassifier());       //TODO INDIKA
            conjunctMessage.setClientID(messages[0].getClientID());       //TODO INDIKA
            return conjunctMessage;
        } catch (Exception e) {
            log.error("Error on transforming : " + e.getMessage() + xsltFile + "  process instance : " + classifier.getProcessInsId(), e);
        }
        return null;
    }

    public void ExecuteTask(MessageWrapper conjunctMessage) {
        if (conjunctMessage == null) {
            return;
        }
        Classifier classifier = conjunctMessage.getClassifier();
        boolean isGateway = false;
        if (classifier != null) {
            isGateway = role.getPlayerBinding() == null &&
                    !role.getId().equals(classifier.getProcessRole());
        }
        // based on the delivery type of the conjunct message,
        // place it in the appropriate out queue
        if ("pull".equalsIgnoreCase(conjunctMessage.getMessageType())) {
            role.delivererPutOutgoingMessage(conjunctMessage);
        } else if (isGateway) {
            role.putSchedulerQueueMessage(conjunctMessage);
        } else if (conjunctMessage.isUserResponse()) {
            role.delivererPutOutgoingSyncMessage(conjunctMessage);
        } else {
            role.workerPutOutgoingPushMessage(conjunctMessage);//Common push delivery
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
            if (synchronizationRegTable.containsCustomFunction(newId)) {
                regMechanism = synchronizationRegTable.getCustomFunction(newId);
            } else {
                synchronized (synchronizationRegTable) {
                    if (synchronizationRegTable.containsCustomFunction(newId)) {
                        regMechanism = synchronizationRegTable.getCustomFunction(newId);
                    } else {
                        try {
                            regMechanism = aClass.newInstance();
                            Method m = aClass.getMethod("init", String.class);
                            m.invoke(regMechanism, config);
                            synchronizationRegTable.addCustomFunction(newId, regMechanism);
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
        return null;
    }
}
