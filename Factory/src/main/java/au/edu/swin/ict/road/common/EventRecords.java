package au.edu.swin.ict.road.common;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
@XmlRootElement
public class EventRecords {
    private List<EventRecord> stateRecords = new ArrayList<EventRecord>();

    public void addEventRecord(EventRecord eventRecord) {
        stateRecords.add(eventRecord);
    }

    public EventRecord[] getEventRecords() {
        return stateRecords.toArray(new EventRecord[stateRecords.size()]);
    }

    public void setEventRecords(EventRecord[] records) {
        for (EventRecord eventRecord : records) {
            addEventRecord(eventRecord);
        }
    }
}
