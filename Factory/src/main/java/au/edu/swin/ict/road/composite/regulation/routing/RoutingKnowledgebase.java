package au.edu.swin.ict.road.composite.regulation.routing;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.Role;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

public class RoutingKnowledgebase extends DroolsRules {
    private Role role;

    public RoutingKnowledgebase(String ruleFile, String ruleDir, Role role) {
        super(ruleFile, ruleDir);
        this.role = role;
    }

    public RoutingKnowledgebase(String ruleDir, Role role) {
        super();
        this.ruleDir = ruleDir;
        this.role = role;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
        StatelessKieSession knowledgeSession = kieContainer.newStatelessKieSession();
        RoutingRuleExecutionEvent executionEvent = (RoutingRuleExecutionEvent) iEvent;
        RoutingActions actions = new RoutingActions(role);
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(CommandFactory.newSetGlobal("actions", actions));

        if (executionEvent.getVsn() != null) {
            cmds.add(CommandFactory.newInsert(executionEvent.getVsn()));
        }
        if (executionEvent.getRoleServiceMessage() != null) {
            cmds.add(CommandFactory.newInsert(executionEvent.getRoleServiceMessage()));
        }
        if (executionEvent.getProcessManagementState() != null) {
            cmds.add(CommandFactory.newInsert(executionEvent.getProcessManagementState()));
        }
        if (executionEvent.getVsnInstance() != null) {
            cmds.add(CommandFactory.newInsert(executionEvent.getVsnInstance()));
        }
        cmds.add(new FireAllRulesCommand(executionEvent.getAgendaFilter()));
        knowledgeSession.execute(CommandFactory.newBatchExecution(cmds));
        return null;
    }

    public Role getRole() {
        return role;
    }
}
