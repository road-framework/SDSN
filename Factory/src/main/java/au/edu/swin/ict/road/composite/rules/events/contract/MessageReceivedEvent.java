package au.edu.swin.ict.road.composite.rules.events.contract;

import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.message.IMessageExaminer;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.ArrayList;
import java.util.Date;

public class MessageReceivedEvent implements IEvent {

    //This list keeps event records, triggered by the rule as an interpretation of a message
    private final ArrayList<EventRecord> triggeredEvents = new ArrayList<EventRecord>();
    public String operationName;
    public boolean response;
    private MessageWrapper messageWrapper;
    private boolean blocked = false;
    private String correlationId;
    private IMessageExaminer examiner;
    private AgendaFilter agendaFilter;

    public MessageReceivedEvent(MessageWrapper mw) {
        operationName = mw.getOperationName();
        //MUST for Process Driven task enactment
        correlationId = mw.getCorrelationId();
        messageWrapper = mw;
        response = mw.isResponse();
    }

    public IMessageExaminer getExaminer() {
        return examiner;
    }

    public void setExaminer(IMessageExaminer examiner) {
        this.examiner = examiner;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public MessageWrapper getMessageWrapper() {
        return messageWrapper;
    }

    public void setMessageWrapper(MessageWrapper messageWrapper) {
        this.messageWrapper = messageWrapper;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * The rule writer should call this method to trigger events, upon
     * a message interpretation.
     * We will use the pid of the message to relate events to a particular process instance
     *
     * @param eid Mandatory
     */
    public void triggerEvent(String eid) {
        this.triggeredEvents.add(new EventRecord(eid, messageWrapper.getClassifier()));
    }

    public void triggerEvent(String eid, String target) {
        EventRecord eventRecord = new EventRecord(eid, messageWrapper.getClassifier());
        eventRecord.setPlace(target);
        this.triggeredEvents.add(eventRecord);
    }

    /**
     * The rule writer should call this method to trigger events, upon
     * a message interpretation.
     * Can be used to expire events by setting the a past date or let the event to be expired in a future date.
     * Can be used to set a different or a null pid
     * NOTE: null pids can lead to new process instance if the event is a condition of start(CoS) of a process definition
     *
     * @param eid  Mandatory
     * @param date Mandatory
     */
    public void triggerEvent(String eid, Date date) {
        EventRecord er = new EventRecord(eid, messageWrapper.getClassifier());
//        er.setExpiration(date);
        this.triggeredEvents.add(er);
    }

    /**
     * To get all the triggered events during the rule evaluation
     *
     * @return all the triggered events
     */
    public ArrayList<EventRecord> getAllTriggeredEvents() {
        return this.triggeredEvents;
    }


    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }
}
