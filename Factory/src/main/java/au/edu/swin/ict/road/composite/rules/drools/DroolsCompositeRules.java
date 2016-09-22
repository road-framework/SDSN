package au.edu.swin.ict.road.composite.rules.drools;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.IInternalOrganiserView;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.ICompositeRules;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * @author The ROAD team, Swinburne University of Technology
 */
public class DroolsCompositeRules extends DroolsRules implements ICompositeRules {

    protected String ruleFile;
    protected KnowledgeBuilder kBuilder;
    protected KnowledgeBase kb;
    protected StatefulKnowledgeSession session;
    protected DroolsRulesFirer firer;
    protected Thread firerThread;
    protected IInternalOrganiserView organiser;

    public DroolsCompositeRules(String ruleFile, String ruleDir, IInternalOrganiserView organiser) throws RulesException {
        super(ruleFile, ruleDir);
        this.ruleFile = ruleFile;
        this.organiser = organiser;
    }

    @Override
    public void insertMessageReceivedAtSourceEvent(MessageWrapper msg,
                                                   String roleId) {
//        RoleServiceMessage event = new RoleServiceMessage(msg, roleId);
        //session.insert(event);
    }

    @Override
    public void insertRoutingFailureEvent(MessageWrapper msg, String roleId) {
//        RoutingFailureEvent event = new RoutingFailureEvent(msg, roleId);
        //session.insert(event);
    }

    @Override
    public void insertRoutingSuccessEvent(MessageWrapper msg, String roleId, String contractId) {
//        RoutingSuccessEvent event = new RoutingSuccessEvent(msg, roleId, contractId);
        //session.insert(event);
    }

    @Override
    public void insertMessageReceivedAtDestinationEvent(MessageWrapper msg, String roleId) {
//        MessageReceivedAtDestinationEvent event = new MessageReceivedAtDestinationEvent(msg, roleId);
        //session.insert(event);
    }

    @Override
    public void insertMessageReceivedAtContractEvent(MessageWrapper msg, String contractId) {
//        MessageReceivedAtContractEvent event = new MessageReceivedAtContractEvent(msg, contractId);
        //session.insert(event);
    }

    private void createAndRunNewSession() {
        if (session != null) {
            session.dispose();
        }

        session = kb.newStatefulKnowledgeSession();
        session.setGlobal("organiser", organiser);

        firer = new DroolsRulesFirer(session);
        firerThread = new Thread(firer);
        firerThread.start();
    }

    @Override
    public void cleanUp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
