package au.edu.swin.ict.road.composite.rules.drools;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;
import au.edu.swin.ict.road.regulator.FactTupleSpace;
import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * TODO documentation
 */
public class DroolsMonitoringRules extends DroolsRules implements IMonitoringRules {
    private static Logger log = Logger.getLogger(DroolsMonitoringRules.class.getName());
    private KieSession session;
    private FactTupleSpace fts;

    public DroolsMonitoringRules(String ruleFile, String ruleDir, FactTupleSpace fts) {
        super(ruleFile, ruleDir);
        this.fts = fts;
        createNewSession();
    }

    private void createNewSession() {
        if (session != null) {
            session.dispose();
        }

        session = kieContainer.newKieSession();
        if (fts != null) {
            session.setGlobal("fts", fts);
        }
    }

    @Override
    public RuleExecutionResult insertEvent(IEvent event) throws RulesException {
        FactHandle factHandle = (FactHandle) session.insert(event);
        session.fireAllRules();
        session.delete(factHandle);
        return null;
    }

    public void cleanUp() {
        // TODO Auto-generated method stub
        if (log.isInfoEnabled()) {
            log.info("Disposing rules session for monitor : "
                     + getRuleFile());
        }
        if (session != null) {
            session.dispose();
        }
    }
}
