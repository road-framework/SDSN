package au.edu.swin.ict.road.composite.rules.drools;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.flowcontrol.FlowControlPolicy;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.IFlowControlRules;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.regulator.FactTupleSpace;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */
public class DroolsFlowControlRules extends DroolsRules implements IFlowControlRules {

    private FlowControlPolicy flowControlPolicy;
    private FactTupleSpace fts;

    public DroolsFlowControlRules(String ruleFile, String ruleDir, FactTupleSpace fts) throws RulesException {
        super(ruleFile, ruleDir);
        this.fts = fts;
    }

    /**
     * Function to admit the routing rule based on the message type and signature
     *
     * @return the destination contract
     * @throws RulesException
     */
    @Override
    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
        StatelessKieSession knowledgeSession = kieContainer.newStatelessKieSession();
        RoleServiceMessage mre = (RoleServiceMessage) iEvent;
        mre.setBlocked(false);
        //Retrieve the specific type of message checker
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(CommandFactory.newInsert(flowControlPolicy, "drs"));
        cmds.add(CommandFactory.newInsert(mre, "msg"));
        knowledgeSession.execute(mre);
        return new FlowControlResult(FlowControlResult.ALLOWED);
    }

    public void setFlowControlPolicy(FlowControlPolicy flowControlPolicy) {
        this.flowControlPolicy = flowControlPolicy;
    }

    @Override
    public void cleanUp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
