package au.edu.swin.ict.road.composite.rules.events.composite;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.VSN;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.routing.OutRouterPort;
import org.drools.runtime.rule.AgendaFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleServiceMessage implements IEvent {

    private String operationName;
    private MessageWrapper messageWrapper;
    private String roleId;
    private boolean blocked;
    private List<OutRouterPort> outPorts = new ArrayList<OutRouterPort>();
    public final static String STATE_RECEIVED = "received";
    public final static String STATE_CLASSIFIED = "classified";
    public final static String STATE_ADMITTABLE = "admittable";
    public final static String STATE_ADMITTED = "admitted";
    public final static String STATE_DROPPABLE = "droppable";
    public final static String STATE_DROPPED = "dropped";
    public final static String STATE_INSTANTAIBLE = "instantiable";
    public final static String STATE_FORWARDABLE = "forwardable";
    public final static String STATE_ROUTED = "routed";
    public final static String STATE_FORWADED = "forwarded";
    public final static String STATE_DELAYED = "delayed";
    private String state = STATE_RECEIVED;
    private AgendaFilter agendaFilter;
    private String errorMessage;
    private VSN vsn;

    public RoleServiceMessage(MessageWrapper mw, String roleId) {
        operationName = mw.getOperationName();
        messageWrapper = mw;
        this.roleId = roleId;

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<OutRouterPort> getOutPorts() {
        return outPorts;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void addAllOutPorts(Collection<OutRouterPort> ports) {
        this.outPorts.addAll(ports);
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Classifier getClassifier() {
        return messageWrapper.getClassifier();
    }

    public VSN getVsn() {
        return vsn;
    }

    public void setVsn(VSN vsn) {
        this.vsn = vsn;
    }
}
