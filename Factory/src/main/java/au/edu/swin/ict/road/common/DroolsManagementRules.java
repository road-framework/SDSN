package au.edu.swin.ict.road.common;

import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import javax.activation.DataHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class DroolsManagementRules extends DroolsRules {

    public DroolsManagementRules(String ruleFile, String ruleDir) {
        super(ruleFile, ruleDir);
    }

    public DroolsManagementRules(DataHandler ruleFileBinary) {
        super(ruleFileBinary);
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public RuleExecutionResult insertEvent(IEvent event) throws RulesException {
        StatelessKieSession knowledgeSession = kieContainer.newStatelessKieSession();
        List<Command> cmds = new ArrayList<Command>();

        EventCollection ec = (EventCollection) event;
        for (IEvent e1 : ec.getiEvents()) {
            cmds.add(CommandFactory.newInsert(e1));
        }
        cmds.add(CommandFactory.newSetGlobal("orgMgt", ec.getiOrganiserRole()));
        cmds.add(CommandFactory.newSetGlobal("oprMgt", ec.getiOperationalManagerRole()));
        cmds.add(CommandFactory.newSetGlobal("disabledSet", ec.getDisabledRuleSet()));
        cmds.add(new FireAllRulesCommand(new DisabledRuleSetAgendaFilter(ec.getDisabledRuleSet())));
        knowledgeSession.execute(CommandFactory.newBatchExecution(cmds));
        return null;
    }
}
