package au.edu.swin.ict.serendip.event;


import au.edu.swin.ict.road.composite.utills.ROADThreadPool;
import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.log4j.Logger;

public class EventCloudProcessor extends Thread {
    private EventCloud eventCloud;
    private ROADThreadPool eventCloudPool;
    private boolean terminated = false;
    private static Logger log = Logger.getLogger(EventCloud.class);

    public EventCloudProcessor(String name, EventCloud eventCloud, ROADThreadPool eventCloudPool) {
        super(name);
        this.eventCloud = eventCloud;
        this.eventCloudPool = eventCloudPool;
    }

    public void run() {
        while (!terminated) {
            try {
                fire();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void terminate() {
        this.eventCloudPool.shutdown();
        this.terminated = true;
    }

    private void fire() throws SerendipException {
        //Iterate thru the event listeners
//        Map<String, SerendipEventListener> eventListeners = eventCloud.getCurrentSubscriptionsAsMap();
//        for (String key : eventListeners.keySet()) {
//            final SerendipEventListener sel = eventListeners.get(key);
//            if (sel == null) {
//                continue;
//            }
//            if (null == sel.getEventPattern()) {
//                log.error("Ignored event listener " + sel.getId() + " . The event pattern is not set");
//                continue;//For some reason the event pattern is null
//            }
//
//            if (sel.isAlive()) {
//                Lock lock = sel.getLock();
//                boolean isMatched = false;
//                try {
//                    lock.lock();
//                    if (sel.isAlive()) {
//                        if (eventCloud.isPatternMatched(sel.eventPattern, sel.pId)) {
//                            sel.setAlive(false);
//                            isMatched = true;
//                        } else {
//                            if (log.isDebugEnabled()) {
//                                log.debug("Pattern did not match " + sel.eventPattern);
//                            }
//                        }
//                    }
//                } finally {
//                    lock.unlock();
//                }
//                if (isMatched) {
//                    eventCloudPool.admit(new Runnable() {
//                        public void forward() {
//                            try {
//                                sel.eventPatternMatched(sel.eventPattern, sel.pId);
//                            } catch (SerendipException e1) {
//                                log.error(e1);
//                            } catch (Exception ignored) {
//                                log.warn(ignored);
//                            }
//                        }
//                    });
//                }
//            } else {
//                //ignore the sel
////                if (log.isDebugEnabled()) {
////                    log.debug(sel.getId() + " ignored. Its not live.");
////                }
//            }
//        }
    }

}
