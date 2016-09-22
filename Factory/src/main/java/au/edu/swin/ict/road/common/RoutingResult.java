package au.edu.swin.ict.road.common;

import au.edu.swin.ict.road.composite.routing.OutRouterPort;

import java.util.List;

/**
 * TODO documentation
 */

public class RoutingResult extends RuleExecutionResult {

    List<OutRouterPort> outRouterPorts;

    public RoutingResult(List<OutRouterPort> outRouterPorts) {
        this.outRouterPorts = outRouterPorts;
    }

    public List<OutRouterPort> getOutRouterPorts() {
        return outRouterPorts;
    }
}
