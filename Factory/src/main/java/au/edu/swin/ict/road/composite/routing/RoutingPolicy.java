package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.common.RoutingResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.IRoutingRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsRoutingRules;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */
public class RoutingPolicy {

    private List<RoutingFunction> routingDecisions = new ArrayList<RoutingFunction>();
    private String processGroupId;
    private String role;

    private IRoutingRules iRoutingRules;

    public void setRoutingRules(IRoutingRules iRoutingRules) {
        this.iRoutingRules = iRoutingRules;
    }

    public RoutingPolicy(IRoutingRules iRoutingRules, String processGroupId, String role) {
        this.iRoutingRules = iRoutingRules;
        this.processGroupId = processGroupId;
        this.role = role;
        if (this.iRoutingRules != null) {
            ((DroolsRoutingRules) this.iRoutingRules).setRoutingPolicy(this);
        }
    }

    public String getProcessGroupId() {
        return processGroupId;
    }

    public String getRole() {
        return role;
    }

    public void add(RoutingFunction decision) {
        if (!contains(decision.getTagName())) {
            routingDecisions.add(decision);
        }
    }

    public boolean contains(String id) {
        for (RoutingFunction fun : routingDecisions) {
            if (fun.getTagName().equals(id))
                return true;
        }
        return false;
    }

    public RoutingFunction getRoutingFunction(String id) {
        for (RoutingFunction fun : routingDecisions) {
            if (fun.getTagName().equals(id))
                return fun;
        }
        return null;
    }

    public RoutingResult apply(MessageWrapper msg) throws RulesException {
        if (iRoutingRules != null) {
            return (RoutingResult) iRoutingRules.insertEvent(new RoleServiceMessage(msg, role));
        } else {
            RoleServiceMessage mre = new RoleServiceMessage(msg, role);
            mre.setBlocked(false);
            for (RoutingFunction fun : routingDecisions) {
                fun.execute(mre);
            }
            return new RoutingResult(mre.getOutPorts());
        }
    }

    public IRoutingRules getRoutingRules() {
        return iRoutingRules;
    }
}
