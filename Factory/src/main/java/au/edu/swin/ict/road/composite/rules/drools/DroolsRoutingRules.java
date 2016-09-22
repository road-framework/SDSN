package au.edu.swin.ict.road.composite.rules.drools;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.routing.RoutingPolicy;
import au.edu.swin.ict.road.composite.rules.IRoutingRules;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.regulator.FactTupleSpace;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for routing the message if there are multiple same signature on the composite.
 * It will fire the routing rules that has been defined by developer
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class DroolsRoutingRules extends DroolsRules implements IRoutingRules {

    private RoutingPolicy rp;
    private FactTupleSpace fts;

    public DroolsRoutingRules(String ruleFile, String ruleDir, FactTupleSpace fts) throws RulesException {
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
        cmds.add(CommandFactory.newInsert(rp, "drs"));
        cmds.add(CommandFactory.newInsert(mre, "msg"));
        knowledgeSession.execute(mre);
        return new RoutingResult(mre.getOutPorts());
    }

    @Override
    public void cleanUp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setRoutingPolicy(RoutingPolicy rp) {
        this.rp = rp;
    }
}
