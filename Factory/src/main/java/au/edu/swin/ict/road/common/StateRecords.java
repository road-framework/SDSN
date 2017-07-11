package au.edu.swin.ict.road.common;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
@XmlRootElement
public class StateRecords {

    private List<StateRecord> stateRecords = new ArrayList<StateRecord>();

    public void addStateRecord(StateRecord stateRecord) {
        stateRecords.add(stateRecord);
    }

    public StateRecord[] getStateRecords() {
        return stateRecords.toArray(new StateRecord[stateRecords.size()]);
    }

    public void setStateRecords(StateRecord[] stateRecordArray) {
        for (StateRecord stateRecord : stateRecordArray) {
            addStateRecord(stateRecord);
        }
    }
}
