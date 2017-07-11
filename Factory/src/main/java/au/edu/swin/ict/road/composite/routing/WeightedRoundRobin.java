package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.common.Capacity;
import au.edu.swin.ict.road.common.ProcessState;
import au.edu.swin.ict.road.common.VSNState;
import au.edu.swin.ict.road.common.Weight;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class WeightedRoundRobin implements RoutingFunction {

    private static Logger log = Logger.getLogger(WeightedRoundRobin.class);
    private final Lock lock = new ReentrantLock();
    private List<Route> routes = new ArrayList<Route>();
    private List<String> ids = new ArrayList<String>();
    private AtomicInteger count = new AtomicInteger(0);

    public WeightedRoundRobin(String conf) {
        initFromConf(conf);
    }

    public WeightedRoundRobin() {
    }

    private void initFromConf(String conf) {
        String[] routes = conf.split(",");
        for (String route : routes) {
            String[] pars = route.split(":");
            addTarget(new Route(pars[0].trim(), Integer.parseInt(pars[1].trim())));
        }
    }

    @Override
    public void execute(RoleServiceMessage event) {
        // TODO Need to consider the feasibility before selecting a path;
        MessageWrapper mw = event.getMessageWrapper();
        if (routes.size() == 1) {
            mw.getClassifier().setProcessId(routes.get(0).getProcessId());
        } else {
            lock.lock();
            try {
                int index = 0;
                index = count.getAndIncrement();
                if (index == (routes.size() - 1)) {
                    count.set(0);
                }
                Route route = routes.get(index);
                if (log.isInfoEnabled()) {
                    log.info("The process alternative : " + route.getProcessId() +
                            " has been selected for the instantiation request : " + ((SOAPEnvelope) mw.getMessage()).getBody());
                }
                mw.getClassifier().setProcessId(route.getProcessId());
            } finally {
                lock.unlock();
            }
        }
//        Classifier classifier = event.getMessageWrapper().getClassifier();
//        VSNState tenantState =
//                ServiceNetworkState.getInstance().getVsnState(classifier.getVsnId());
//        Route route = getNextRoute(tenantState, routes.size() * 3);
//        if (route != null) {
//            if (log.isInfoEnabled()) {
//                log.info("Process alternative : " + route.getProcessId());
//            }
//            MessageWrapper mw = event.getMessageWrapper();
//            mw.getClassifier().setProcessId(route.getProcessId());
//        } else {
//            log.error("No Route for : " + classifier.getVsnId());
//        }
    }

    public String getTagName() {
        return "WeightedRoundRobin";
    }

    public void addTarget(Route route) {
        if (!ids.contains(route.getProcessId())) {
            routes.add(route);
            ids.add(route.getProcessId());
        }
    }

    private Route getNextRoute(VSNState VSNState, int noOfInteractions) {
        if (noOfInteractions <= 0) {
            log.error("Cannot find routes after 3 iterations.");
            for (ProcessState state : VSNState.getProcessStates()) {
                Capacity capacity =
                        (Capacity) state.retrieveFromCache(state.getProcessId() + "_cap").getStateInstance();
                Weight weight = (Weight) state.retrieveFromCache(state.getProcessId() + "_wgt").getStateInstance();
                log.error(state.getProcessId() + " " + capacity.getUsed() + weight.getAndDecrementCurrentAllowed());
            }
            return null;
        }
        synchronized (this) {
            int index = count.get();
            Route route = routes.get(index);
            ProcessState processState = VSNState.getProcessState(route.getProcessId());
            Capacity capacity =
                    (Capacity) processState.retrieveFromCache(processState.getProcessId() + "_cap").getStateInstance();
            Weight weight = (Weight) processState.retrieveFromCache(processState.getProcessId() + "_wgt").getStateInstance();
            processState.getLock().lock();
            try {
                if (route.getThreshold() == capacity.getUsed()) {
                    count.incrementAndGet();
                    return getNextRoute(VSNState, --noOfInteractions);
                }
                if (weight.getAndDecrementCurrentAllowed() > 0) {
                    capacity.increaseUsedCapacity();
                    return route;
                }
                if (index == (routes.size() - 1)) {
                    count.set(0);
                    for (ProcessState state : VSNState.getProcessStates()) {
                        Weight localWeight1 = (Weight) state.retrieveFromCache(state.getProcessId() + "_wgt").getStateInstance();
                        localWeight1.resetCurrentAllowed();
                    }
                } else {
                    count.incrementAndGet();
                }
            } finally {
                processState.getLock().unlock();
            }
            return getNextRoute(VSNState, --noOfInteractions);
        }
    }
}
