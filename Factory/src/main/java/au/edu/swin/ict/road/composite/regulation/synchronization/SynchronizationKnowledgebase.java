package au.edu.swin.ict.road.composite.regulation.synchronization;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.Role;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

public class SynchronizationKnowledgebase extends DroolsRules {
    private Role role;

    public SynchronizationKnowledgebase(String ruleFile, String ruleDir) {
        super(ruleFile, ruleDir);
    }

    public SynchronizationKnowledgebase(String ruleFile, String ruleDir, Role role) {
        super(ruleFile, ruleDir);
        this.role = role;
    }

    public SynchronizationKnowledgebase(Role role, String ruleDir) {
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
        SynEvents mre = (SynEvents) iEvent;
        SynchronizationActions actions = new SynchronizationActions(role, mre.getClassifier());
        VSNInstance state =
                role.getComposite().getSerendipEngine().getProcessInstance(mre.getClassifier()).getMgtState();
        List<Command> cmds = new ArrayList<Command>();
        for (EventRecord eventRecord : mre.getEventRecords()) {
            cmds.add(CommandFactory.newInsert(eventRecord));
        }
        if (state != null) {
            cmds.add(CommandFactory.newInsert(state));
        }
        cmds.add(CommandFactory.newSetGlobal("actions", actions));
        cmds.add(CommandFactory.newSetGlobal("disabledSet", mre.getDisabledRuleSet()));
        cmds.add(new FireAllRulesCommand(mre.getAgendaFilter()));
        knowledgeSession.execute(CommandFactory.newBatchExecution(cmds));
        return null;
    }

    public Role getRole() {
        return role;
    }
}
