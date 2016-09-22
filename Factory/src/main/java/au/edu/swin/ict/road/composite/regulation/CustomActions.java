package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.RegulationMechanism;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingRegTable;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO
 */
public class CustomActions {
    private static Logger log = Logger.getLogger(CustomActions.class.getName());
    protected Role role;
    protected final RoutingRegTable routingRegTable;

    public CustomActions(Role role) {
        this.role = role;
        this.routingRegTable = role.getRoutingRegTable();
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
                createProcessInstance(((RoleServiceMessage) arguments[1]).getMessageWrapper(), classifier);
            }
        }
        return null;
    }

    public void createProcessInstance(MessageWrapper message, Classifier classifier) {
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
