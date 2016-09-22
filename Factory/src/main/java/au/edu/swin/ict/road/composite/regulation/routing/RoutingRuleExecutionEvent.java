package au.edu.swin.ict.road.composite.regulation.routing;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.ProcessManagementState;
import au.edu.swin.ict.road.common.VSN;
import au.edu.swin.ict.road.common.VSNInstance;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import org.kie.api.runtime.rule.AgendaFilter;

/**
 * TODO
 */
public class RoutingRuleExecutionEvent implements IEvent {

    private RoleServiceMessage roleServiceMessage;
    private VSN vsn;
    private AgendaFilter agendaFilter;
    private ProcessManagementState processManagementState;
    private VSNInstance vsnInstance;

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage) {
        this.roleServiceMessage = roleServiceMessage;
    }

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage, VSN vsn, AgendaFilter agendaFilter) {
        this.roleServiceMessage = roleServiceMessage;
        this.vsn = vsn;
        this.agendaFilter = agendaFilter;
    }

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage, AgendaFilter agendaFilter) {
        this.roleServiceMessage = roleServiceMessage;
        this.agendaFilter = agendaFilter;
    }

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage, VSN vsn) {
        this.roleServiceMessage = roleServiceMessage;
        this.vsn = vsn;
    }

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage, VSN vsn, ProcessManagementState processManagementState) {
        this.roleServiceMessage = roleServiceMessage;
        this.vsn = vsn;
        this.processManagementState = processManagementState;
    }

    public RoutingRuleExecutionEvent(RoleServiceMessage roleServiceMessage, VSN vsn, ProcessManagementState processManagementState, VSNInstance vsnInstance) {
        this.roleServiceMessage = roleServiceMessage;
        this.vsn = vsn;
        this.processManagementState = processManagementState;
        this.vsnInstance = vsnInstance;
    }

    public RoleServiceMessage getRoleServiceMessage() {
        return roleServiceMessage;
    }

    public void setRoleServiceMessage(RoleServiceMessage roleServiceMessage) {
        this.roleServiceMessage = roleServiceMessage;
    }

    public VSN getVsn() {
        return vsn;
    }

    public void setVsn(VSN vsn) {
        this.vsn = vsn;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public ProcessManagementState getProcessManagementState() {
        return processManagementState;
    }

    public void setProcessManagementState(ProcessManagementState processManagementState) {
        this.processManagementState = processManagementState;
    }

    public VSNInstance getVsnInstance() {
        return vsnInstance;
    }

    public void setVsnInstance(VSNInstance vsnInstance) {
        this.vsnInstance = vsnInstance;
    }
}
