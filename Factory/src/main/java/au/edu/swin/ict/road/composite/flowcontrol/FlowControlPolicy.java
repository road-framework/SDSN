package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.IFlowControlRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsFlowControlRules;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */
public class FlowControlPolicy {

    private List<FlowControlFunction> flowControlFunctions = new ArrayList<FlowControlFunction>();
    private String processGroupId;
    private String role;

    private IFlowControlRules iFlowControlRules;

    public FlowControlPolicy(IFlowControlRules iFlowControlRules, String processGroupId, String role) {
        this.iFlowControlRules = iFlowControlRules;
        this.processGroupId = processGroupId;
        this.role = role;
        if (this.iFlowControlRules != null) {
            ((DroolsFlowControlRules) this.iFlowControlRules).setFlowControlPolicy(this);
        }
    }

    public String getProcessGroupId() {
        return processGroupId;
    }

    public String getRole() {
        return role;
    }

    public void add(FlowControlFunction decision) {
        flowControlFunctions.add(decision);
    }

    public FlowControlFunction getFlowControlFunction(String id) {
        for (FlowControlFunction fun : flowControlFunctions) {
            if (fun.getTagName().equals(id))
                return fun;
        }
        return null;
    }

    public boolean contains(String id) {
        for (FlowControlFunction fun : flowControlFunctions) {
            if (fun.getTagName().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public FlowControlResult apply(IEvent event) throws RulesException {
        if (iFlowControlRules != null) {
            return (FlowControlResult) iFlowControlRules.insertEvent(event);
        } else {
            for (FlowControlFunction fun : flowControlFunctions) {
                FlowControlResult result = fun.admit(event);
                if (result.getState() == FlowControlResult.DENIED) {
                    return result;
                }
            }
        }
        return new FlowControlResult(FlowControlResult.ALLOWED);
    }

    public IFlowControlRules getFlowControlRules() {
        return iFlowControlRules;
    }

    public void setFlowControlRules(IFlowControlRules iFlowControlRules) {
        this.iFlowControlRules = iFlowControlRules;
    }
}
