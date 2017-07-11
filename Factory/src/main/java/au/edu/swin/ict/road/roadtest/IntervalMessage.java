package au.edu.swin.ict.road.roadtest;

import java.util.*;

/**
 * Interval MEssage which extends Message. Will store additional informaiton
 * about times the message was sent, uuid, and interval
 *
 * @author Ufuk Altin (altin.ufuk@gmail.com)
 */
public class IntervalMessage extends Message {

    private List<Date> timeStamps;
    private long intervalUnit;
    private UUID uid;

    /**
     * Constructor
     *
     * @param msgContent
     * @param operationName
     * @param originRoleId
     * @param interval
     */
    public IntervalMessage(String msgContent, String operationName,
                           String originRoleId, long interval) {
        super(msgContent, operationName, originRoleId, false);
        this.intervalUnit = interval;
        this.timeStamps = new ArrayList<Date>();
    }

    /**
     * Gets the interval in which a message is going to be sent
     *
     * @return The value of the interval in which a the messages are going to be
     * sent
     */
    public long getIntervalUnit() {
        return this.intervalUnit;
    }

    /**
     *
     */
    public void addTimeStamp() {
        Calendar cal = Calendar.getInstance();
        Date t = cal.getTime();
        timeStamps.add(t);
        setNewTimeStamp(t);
    }

    /**
     * Gets the time stamps
     *
     * @return timestamps
     */
    public List<Date> getTimeStamps() {
        return this.timeStamps;
    }

    /**
     * Gets the amount of messages which has been already sent
     *
     * @return The size of the sent messages
     */
    public int getSentMessageCount() {
        return this.timeStamps.size();
    }

    /**
     * @return the uid
     */
    public UUID getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(UUID uid) {
        this.uid = uid;
    }

}
