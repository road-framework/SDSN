package au.edu.swin.ict.road.composite.utills;


import au.edu.swin.ict.serendip.core.ProcessInstance;
import au.edu.swin.ict.serendip.core.SerendipEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class RoadCleaner extends TimerTask {
    private static final Log log = LogFactory.getLog(RoadCleaner.class);
    private SerendipEngine serendipEngine;
    private LinkedBlockingQueue<String> processIds = new LinkedBlockingQueue<String>();
    private int size = Integer.parseInt(ROADProperties.getInstance().getProperty("cleaner.size", "100"));

    public RoadCleaner(SerendipEngine serendipEngine) {
        this.serendipEngine = serendipEngine;
    }

    public void run() {
        try {
            processCallbacks();
        } catch (Exception ignore) {
        }
    }

    private void processCallbacks() {
        int limit;
        if (size < processIds.size()) {
            limit = size;
        } else {
            limit = processIds.size();
        }
        List<String> tobeRemoved = new ArrayList<String>();
        for (int i = 0; i < limit; i++) {
            String pid = processIds.poll();
            ProcessInstance processInstance =
                    serendipEngine.getProcessInstanceByInsId(pid);
            if (processInstance == null) {
                tobeRemoved.add(pid);
                continue;
            }
            if (processInstance.canTerminate()) {
                processInstance.terminate();
                tobeRemoved.add(pid);
            }
        }
        processIds.removeAll(tobeRemoved);
    }

    public void addCompletedProcessInstance(String processId) {
        processIds.offer(processId);
    }
}