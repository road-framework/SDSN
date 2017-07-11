package au.edu.swin.ict.road.composite.regulation.passthrough;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.RegulationMechanism;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.regulation.BasedActions;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationActions;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO
 */
public class PassthroughActions extends BasedActions {
    private static Logger log = Logger.getLogger(SynchronizationActions.class.getName());
    private final PassthroughRegTable passthroughRegTable;
    private Contract contract;

    public PassthroughActions(Contract contract) {
        super(contract.getComposite());
        this.contract = contract;
        this.passthroughRegTable = contract.getPassthroughRegTable();
    }

    public Object execute(String rmId, String config, Object... arguments) {
        RegulationMechanism regulationMechanism = contract.getComposite().getRegulationMechanism(rmId);
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
            if (passthroughRegTable.containsCustomFunction(newId)) {
                regMechanism = passthroughRegTable.getCustomFunction(newId);
            } else {
                synchronized (passthroughRegTable) {
                    if (passthroughRegTable.containsCustomFunction(newId)) {
                        regMechanism = passthroughRegTable.getCustomFunction(newId);
                    } else {
                        try {
                            regMechanism = aClass.newInstance();
                            Method m = aClass.getMethod("init", String.class);
                            m.invoke(regMechanism, config);
                            passthroughRegTable.addCustomFunction(newId, regMechanism);
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
