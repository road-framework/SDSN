package au.edu.swin.ict.road.composite.message.containers;

import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.exceptions.MessageException;
import au.edu.swin.ict.road.composite.utills.ProcessMessages;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * The class to keep messages in a buffer.
 * Messages can be picked irrespective of the order they come.
 *
 * @author Malinda
 */
public class MessageBuffer {
    public static final int DEFAULT_MEX_TIMEOUT = 2 * 60 * 1000;
    private static Logger log = Logger.getLogger(MessageBuffer.class.getName());
    //private Hashtable<String, MessageWrapper> ht = new Hashtable<String, MessageWrapper>();
    private final ConcurrentHashMap<String, ProcessMessages> collection =
            new ConcurrentHashMap<String, ProcessMessages>();
    private ExecutorService executor;
    private BufferType type = BufferType.NONE;
    private Role role = null;

    public MessageBuffer() {
//        pendingMsgBufTimeout =
//                Long.parseLong(ROADProperties.getInstance().getProperty("pendingMsgBufTimeout", String.valueOf(DEFAULT_MEX_TIMEOUT)));
    }
//    private long pendingMsgBufTimeout;

    public MessageBuffer(Role role, BufferType bufferType) {
        this.role = role;
        this.type = bufferType;
    }

    public ConcurrentHashMap<String, ProcessMessages> getCollection() {
        return collection;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Return the message for a given msgId and the correlation id (process instance id)
     *
     * @param msgId
     * @param correlationId
     * @return
     * @throws MessageException
     */
    public MessageWrapper getMessage(final String msgId, final String correlationId) throws MessageException {
        if (correlationId == null) {
            throw new RuntimeException("No correlation ID : " + msgId);
        }
        ProcessMessages processMessages = collection.get(correlationId);
        if (processMessages == null) {
            throw new RuntimeException("NO Error : getMessage: " + msgId);
        }
        return processMessages.getMessage(msgId);
    }

    public MessageWrapper getAndRemoveMessage(final String msgId, final String correlationId) throws MessageException {
        if (correlationId == null) {
            throw new RuntimeException("No correlation ID : " + msgId);
        }
        ProcessMessages processMessages = collection.get(correlationId);
        if (processMessages == null) {
            throw new RuntimeException("NO Error : getAndRemoveMessage : " + msgId);
        }
        MessageWrapper messageWrapper =
                processMessages.removeAndGetMessage(msgId);
        if (processMessages.canRemove() && processMessages.isEmpty()) {
            collection.remove(correlationId);
        }
        return messageWrapper;
    }

    public void dropMessage(MessageWrapper mw) throws MessageException {
        if (log.isInfoEnabled()) {
            log.info("Message " + mw.getMessageId() + "," + mw.getCorrelationId() + " dropped to " +
                    this.role.getId() + "." + this.getType());
        }
        String correlationId = mw.getCorrelationId();
        if (correlationId == null) {
            throw new RuntimeException("No correlation ID ");
        }
        ProcessMessages processMessages = collection.get(correlationId);
        if (processMessages == null) {
            processMessages = new ProcessMessages();
            collection.putIfAbsent(correlationId, processMessages);
            processMessages = collection.get(correlationId);
        }
        processMessages.putMessage(mw.getMessageId(), mw);
    }

    public void removeMessage(String msgId, String correlationId) throws MessageException {
        if (correlationId == null) {
            throw new RuntimeException("No correlation ID : " + msgId);
        }
        ProcessMessages processMessages = collection.get(correlationId);
        if (processMessages == null) {
            throw new RuntimeException("NO Error : removeMessage: " + msgId);
        }
        processMessages.removeAndGetMessage(msgId);
        if (processMessages.canRemove() && processMessages.isEmpty()) {
            collection.remove(correlationId);
        }
    }

    public void removeMessage(MessageWrapper mw) throws MessageException {
        if (log.isInfoEnabled()) {
            log.info("Message " + mw.getMessageId() + "," + mw.getCorrelationId() + " removed from " +
                    this.role.getId() + "." + this.getType());
        }
        String correlationId = mw.getCorrelationId();
        if (correlationId == null) {
            throw new RuntimeException("No correlation ID ");
        }
        ProcessMessages processMessages = collection.get(correlationId);
        if (processMessages == null) {
            throw new RuntimeException("NO Error : removeMessage mw");
        }
        processMessages.removeAndGetMessage(mw.getMessageId());
        if (processMessages.canRemove() && processMessages.isEmpty()) {
            collection.remove(correlationId);
        }
    }

    public Iterator<MessageWrapper> getAllMessages() {
        //return ht.elements();
        List<MessageWrapper> messageWrappers = new ArrayList<MessageWrapper>();
        for (ProcessMessages messages : collection.values()) {
            messageWrappers.addAll(messages.getCollection());
        }
        return messageWrappers.iterator();
    }

    public BufferType getType() {
        return type;
    }

    public void setType(BufferType type) {
        this.type = type;
    }

    public void cleanProcessMessages(String id) {
        ProcessMessages tobeRemoved = collection.get(id);
        if (tobeRemoved == null) {
            return;
        }
        if (tobeRemoved.isEmpty()) {
            collection.remove(id);
        } else {
            tobeRemoved.setTobeRemoved(true);
        }
    }

    public enum BufferType {
        NONE, ROUTER, PENDINGOUT
    }
}
