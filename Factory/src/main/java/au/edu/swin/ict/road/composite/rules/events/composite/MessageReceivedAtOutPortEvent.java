package au.edu.swin.ict.road.composite.rules.events.composite;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.routing.OutRouterPort;

public class MessageReceivedAtOutPortEvent implements IEvent {

    private String operationName;
    private MessageWrapper messageWrapper;

    public Role getRole() {
        return role;
    }

    public OutRouterPort getOutPort() {
        return outPort;
    }

    private Role role;
    private boolean blocked;
    private OutRouterPort outPort;

    public MessageReceivedAtOutPortEvent(Role role, OutRouterPort outPort, MessageWrapper mw) {
        this.role = role;
        this.outPort = outPort;
        this.messageWrapper = mw;
        this.operationName = mw.getOperationName();
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
}
