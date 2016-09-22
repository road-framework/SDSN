package au.edu.swin.ict.road.composite.listeners;

import au.edu.swin.ict.road.composite.IRole;
import au.edu.swin.ict.road.composite.message.MessageWrapper;

public interface RolePushMessageListener {

    public void pushMessageRecieved(IRole role, MessageWrapper messageWrapper);

    public MessageWrapper sendReceiveMessage(MessageWrapper messageWrapper);

    public void sendMessage(MessageWrapper messageWrapper);

    public String getId();
}
