package au.edu.swin.ict.road.common;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * TODO
 */
@XmlRootElement(name = "StateRecord")
public class StateRecord implements IEvent, Serializable {
    private static final long serialVersionUID = -3257004811771809561L;
    private SNStateManagementState mgtState;
    private Object stateInstance;
    private String stateId;
    private Classifier classifier;

    public StateRecord(String stateId, Object stateInstance) {
        this.stateId = stateId;
        this.stateInstance = stateInstance;
        this.mgtState = new SNStateManagementState(stateId);
    }

    public StateRecord() {
        this.mgtState = new SNStateManagementState(stateId);
    }

    public StateRecord(String stateId, Object stateInstance, Classifier classifier) {
        this.stateId = stateId;
        this.stateInstance = stateInstance;
        this.classifier = classifier;
    }

    public Object getStateInstance() {
        return stateInstance;
    }

    public void setStateInstance(Object stateInstance) {
        this.stateInstance = stateInstance;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
        this.mgtState.setId(stateId);
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public SNStateManagementState getMgtState() {
        return mgtState;
    }

    public void setMgtState(SNStateManagementState mgtState) {
        this.mgtState = mgtState;
    }
}
