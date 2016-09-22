package au.edu.swin.ict.road.composite.regulation.sglobal;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.Composite;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class GlobalKnowledgebase extends DroolsRules {
    private Composite composite;

    public GlobalKnowledgebase(Composite composite, String ruleDir, String ruleFile) {
        super(ruleFile, ruleDir);
        this.composite = composite;
    }

    public GlobalKnowledgebase(Composite composite, String ruleDir) {
        super(ruleDir);
        this.composite = composite;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
        StatelessKieSession knowledgeSession = kieContainer.newStatelessKieSession();
        GlobalEvents mre = (GlobalEvents) iEvent;
        GlobalActions actions = new GlobalActions(composite);
        List<Command> cmds = new ArrayList<Command>();
        for (EventRecord eventRecord : mre.getEventRecords()) {
            cmds.add(CommandFactory.newInsert(eventRecord));
        }
        cmds.add(CommandFactory.newSetGlobal("actions", actions));
        cmds.add(CommandFactory.newSetGlobal("disabledSet", mre.getDisabledRuleSet()));
        cmds.add(new FireAllRulesCommand(mre.getAgendaFilter()));
        knowledgeSession.execute(CommandFactory.newBatchExecution(cmds));
        return null;
    }
}
