package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO documentation
 */
public class Forward implements RoutingFunction {

    private Map<String, Map<String, OutRouterPort>> outPortsMap = new HashMap<String, Map<String, OutRouterPort>>();

    @Override
    public void execute(RoleServiceMessage msg) {
        String processId =
                msg.getMessageWrapper().getClassifier().getProcessId();
        if (processId != null) {
            Collection<OutRouterPort> outRouterPorts = outPortsMap.get(processId).values();
            msg.addAllOutPorts(outRouterPorts);
        }
    }

    @Override
    public String getTagName() {
        return "Forward";
    }

    public void addOutPort(String processId, OutRouterPort port) {
        Map<String, OutRouterPort> outRouterPorts = outPortsMap.get(processId);
        if (outRouterPorts == null) {
            outRouterPorts = new HashMap<String, OutRouterPort>();
            outPortsMap.put(processId, outRouterPorts);
        }
        if (!outRouterPorts.containsKey(port.getInteraction())) {
            outRouterPorts.put(port.getInteraction(), port);
        }
    }
}
