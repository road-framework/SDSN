package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */
public class RoundRobin implements RoutingFunction {

    private static Logger log = Logger.getLogger(RoundRobin.class);
    private List<Route> routes = new ArrayList<Route>();
    private List<String> ids = new ArrayList<String>();
    private AtomicInteger count = new AtomicInteger(0);

    public RoundRobin() {
    }

    @Override
    public void execute(RoleServiceMessage event) {
        // TODO Need to consider the feasibility before selecting a path;
        int index = 0;
        synchronized (this) {
            index = count.getAndIncrement();
            if (index == (routes.size() - 1)) {
                count.set(0);
            }
        }
        Route route = routes.get(index);
        if (log.isInfoEnabled()) {
            log.info("Process alternative : " + route.getProcessId());
        }
        MessageWrapper mw = event.getMessageWrapper();
        mw.getClassifier().setProcessId(route.getProcessId());
    }

    public String getTagName() {
        return "RoundRobin";
    }

    public void addTarget(Route route) {
        if (!ids.contains(route.getProcessId())) {
            routes.add(route);
            ids.add(route.getProcessId());
        }
    }
}
