package au.edu.swin.ict.road.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessEventRecord {

    private Map<String, EventRecord> eventRecords = new ConcurrentHashMap<String, EventRecord>();

    public void addEventRecord(EventRecord eventRecord) {
        eventRecords.put(eventRecord.getEventId(), eventRecord);
    }

    public boolean isContains(String eId) {
        return eventRecords.containsKey(eId);
    }

    public List<EventRecord> getEventRecords() {
        List<EventRecord> tobeReturn = new ArrayList<EventRecord>();
        tobeReturn.addAll(eventRecords.values());
        return tobeReturn;
    }

    public void remove(String eId) {
        eventRecords.remove(eId);
    }

    public EventRecord getEventRecord(String eID) {
        return eventRecords.get(eID);
    }

    public List<EventRecord> getEventRecordsByRole(String roleId) {
        List<EventRecord> tobeReturn = new ArrayList<EventRecord>();
        for (EventRecord eventRecord : eventRecords.values()) {
            if (eventRecord.getPlace().equals(roleId)) {
                tobeReturn.add(eventRecord);
            }
        }
        return tobeReturn;
    }
}
