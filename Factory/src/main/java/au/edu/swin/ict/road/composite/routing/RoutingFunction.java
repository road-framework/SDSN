package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;

/**
 * TODO documentation
 */
public interface RoutingFunction {

    public void execute(RoleServiceMessage msg);

    public String getTagName();
}
