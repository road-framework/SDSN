package au.edu.swin.ict.road.composite.regulation.passthrough;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.rules.ContractEvaluationResult;
import au.edu.swin.ict.road.composite.rules.IContractRules;
import au.edu.swin.ict.road.composite.rules.events.contract.MessageReceivedEvent;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;

public class PassthroughKnowledgebase extends DroolsRules implements IContractRules {
    private Contract contract;

    public PassthroughKnowledgebase(String ruleFile, String ruleDir) {
        super(ruleFile, ruleDir);
    }

    public PassthroughKnowledgebase(String ruleFile, String ruleDir, Contract contract) {
        super(ruleFile, ruleDir);
        this.contract = contract;
    }

    public PassthroughKnowledgebase(String ruleDir, Contract contract) {
        super();
        this.ruleDir = ruleDir;
        this.contract = contract;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
        PassthroughActions actions = new PassthroughActions(contract);
        //   long startTime = System.nanoTime();
        StatelessKieSession knowledgeSession = kieContainer.newStatelessKieSession();
        MessageReceivedEvent mre = (MessageReceivedEvent) iEvent;
        //   long endTime = System.nanoTime();
        //     log.error(contract.getId() + " : create time "+ ((endTime-startTime)/1000000));
        mre.setBlocked(false);

//        if (messageWrapper.isOrigin()) {
//            messageWrapper.setOrigin(false);
//            mre.triggerEvent("e" + messageWrapper.getOriginRoleId() + "Reqd");
//        } else {
//            String suffix = messageWrapper.isResponse() ? "Resd" : "Reqd";
//            mre.triggerEvent("e" + messageWrapper.getOperationName() + suffix);
//        }
//        final Lock lock = new ReentrantLock();
//        final Condition condition = lock.newCondition();
//        mre.setCondition(condition);
//        IMessageExaminer typeCheck = new MessageTypeExaminer(mre.getMessageWrapper(),
//                                                             null);
        //Retrieve the specific type of message checker
//        IMessageExaminer check = typeCheck.getMessageExaminer();
        List<Command> cmds = new ArrayList<Command>();
//        mre.setExaminer(check);           // TODO check selection in the rules did not work
//
//        cmds.add(CommandFactory.newInsert(check, "check"));
//
        cmds.add(CommandFactory.newSetGlobal("actions", actions));
        cmds.add(CommandFactory.newInsert(mre));
        cmds.add(new FireAllRulesCommand(mre.getAgendaFilter()));
        knowledgeSession.execute(CommandFactory.newBatchExecution(cmds));
//
//        knowledgeSession.admit(mre);
//
//
        // Inserting fact/event to the rules engine
//        if (null != contract) {
//            contract.injectFact();
//        }
//
//        FactHandle factHandle = (FactHandle) session.insert(check);
//        session.insert(check);
//        session.insert(mre);
//
//        lock.lock();
//        try {
//            condition.awaitNanos(TimeUnit.MICROSECONDS.toNanos(30));
//            // Here we create a new object that stores the message processing
//            // results
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } finally {
//            lock.unlock();
//        }
//        session.retract(factHandle);
//        long endTime2 = System.nanoTime();
//        log.error(contract.getId() + " : admit time "+ ((endTime2-endTime)/1000000));
        ContractEvaluationResult mpr = new ContractEvaluationResult();
        mpr.setBlocked(mre.isBlocked());
        mpr.setInterpretedEvents(mre.getAllTriggeredEvents());
        mpr.setCorrelationId(mre.getMessageWrapper().getCorrelationId());
        return mpr;
    }

    public Contract getContract() {
        return contract;
    }
}
