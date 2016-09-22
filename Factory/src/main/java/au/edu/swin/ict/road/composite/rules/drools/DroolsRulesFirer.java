package au.edu.swin.ict.road.composite.rules.drools;

import org.apache.log4j.Logger;
import org.drools.runtime.StatefulKnowledgeSession;

public class DroolsRulesFirer implements Runnable {
    private static Logger log = Logger.getLogger(DroolsRulesFirer.class.getName());
    private StatefulKnowledgeSession session;
    private boolean terminate = false;

    public DroolsRulesFirer(StatefulKnowledgeSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        if (log.isInfoEnabled()) {
            log.info("fireUntilHalt ");
        }
        try {
            if (terminate) {
                if (session != null) {
                    session.dispose();
                }
                return;
            }
            session.fireUntilHalt();
        } catch (Exception ignored) {
            log.error("Error executing rules ", ignored);
        }
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }
}
