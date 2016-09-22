package au.edu.swin.ict.road.common;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Represent an event record in Serendip. Each event associated with a process
 * instance. Once an event is created, a timestamp is set.
 * The duration can be set to expire the events.
 *
 * @author Malinda Kapuruge
 */
@XmlRootElement(name = "EventRecord")
public class EventRecord implements IEvent, Serializable {
    private static Logger log = Logger.getLogger(EventRecord.class.getName());
    private SNEventManagementState mgtState;

    private String eventId = null;
    //    private Date callDateTime = null;
//    private Date expDateTime = null;

    private Classifier classifier;
    //    private String destinationRoleId;

    private String place;

    public EventRecord(String eventId, Classifier classifier, String state) {
        this.mgtState = new SNEventManagementState(eventId);
        this.mgtState.setState(state);
        this.classifier = classifier;
        this.eventId = eventId;
    }

    /**
     * An event record.
     *
     * @param eventId    Event id should be surrounded by []. e.g, [MyEvent]
     * @param classifier the process instacne in which this event belong
     */
    public EventRecord(String eventId, Classifier classifier) {
        if (null != eventId) {
            this.eventId = eventId.trim();
        }
        this.classifier = classifier;
        this.mgtState = new SNEventManagementState(eventId);
//        this.callDateTime = new Date();


        //Set expiration to inifinity as default
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.YEAR, 9999);//Date(Long.MAX_VALUE) is buggy
//        expDateTime = cal.getTime();
    }

    public EventRecord() {
        this.mgtState = new SNEventManagementState(eventId);
    }

    public EventRecord(String eventId, Classifier classifier, String state, String place) {
        this.mgtState = new SNEventManagementState(eventId);
        this.mgtState.setState(state);
        this.eventId = eventId;
        this.classifier = classifier;
        this.place = place;
    }

    public Classifier getClassifier() {
        return classifier;
    }

//    public boolean hasExpired() {
//        return this.expDateTime.compareTo(new Date()) < 0;
//    }
//
//    public Date getCallDateTime() {
//        return callDateTime;
//    }
//
//    public void setCallDateTime(Date callDateTime) {
//        this.callDateTime = callDateTime;
//    }
//
//    public Date getExpiration() {
//        return this.expDateTime;
//    }

//    /**
//     * Use this method to specifically set the expiration time.
//     * By default the event expires when the process instance is complete.
//     *
//     * @param expirationDate
//     */
//    public void setExpiration(Date expirationDate) {
//        this.expDateTime = expirationDate;
//    }

//    /**
//     * Set/Reset the expiration time using a number
//     *
//     * @param field  e.g. Calendar.DATE
//     * @param amount e.g., 10 (ten days)
//     * @see java.util.Calendar#add(int field, int amount)
//     */
//    public void setExpiration(int field, int amount) {
//        Calendar cal = Calendar.getInstance();
//        cal.add(field, amount);
//        this.setExpiration(cal.getTime());
//    }

    public String getEventId() {
        return eventId;
    }

    public String toString() {
        return this.eventId;
    }

    @XmlElement(name = "EventId")
    public void setEventId(String eventId) {
        if (null != eventId) {
            this.eventId = eventId.trim();
        }
        mgtState.setId(eventId);
    }

    //    public String getDestinationRoleId() {
//        return destinationRoleId;
//    }
//
//    public void setDestinationRoleId(String destinationRoleId) {
//        this.destinationRoleId = destinationRoleId;
//    }
    public String getPlace() {
        return place;
    }

    @XmlElement(name = "Place")
    public void setPlace(String place) {
        this.place = place;
    }

    @XmlElement(name = "Classifier")
    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public SNEventManagementState getMgtState() {
        return mgtState;
    }

    public void setMgtState(SNEventManagementState mgtState) {
        this.mgtState = mgtState;
    }

    public boolean isPlaceSet (){
        return place != null;
    }
}
