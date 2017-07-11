package au.edu.swin.ict.road.composite.utills;

import au.edu.swin.ict.road.composite.message.MessageWrapper;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessMessages {

    private ConcurrentHashMap<String, MessageWrapper> collection =
            new ConcurrentHashMap<String, MessageWrapper>();
    private boolean canRemove;

    public boolean canRemove() {
        return canRemove;
    }

    public void setTobeRemoved(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public void putMessage(String messageID, MessageWrapper messageWrapper) {
        collection.put(messageID, messageWrapper);
    }

    public MessageWrapper removeAndGetMessage(String messageID) {
        if (collection.containsKey(messageID)) {
            return collection.remove(messageID);
        } else {
            return null;
        }
    }

    public MessageWrapper getMessage(String messageID) {
        return collection.get(messageID);
    }

    public Collection<MessageWrapper> getCollection() {
        return collection.values();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public Collection<String> getMessageIds() {
        return collection.keySet();
    }
}
