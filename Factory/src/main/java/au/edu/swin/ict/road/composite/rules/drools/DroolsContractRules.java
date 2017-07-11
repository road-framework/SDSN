package au.edu.swin.ict.road.composite.rules.drools;

import au.edu.swin.ict.road.common.DroolsRules;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.message.IMessageExaminer;
import au.edu.swin.ict.road.composite.message.MessageTypeExaminer;
import au.edu.swin.ict.road.composite.rules.ContractEvaluationResult;
import au.edu.swin.ict.road.composite.rules.IContractRules;
import au.edu.swin.ict.road.composite.rules.events.contract.MessageReceivedEvent;
import org.apache.log4j.Logger;
import org.kie.api.command.Command;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;


public class DroolsContractRules extends DroolsRules implements IContractRules {
    private static Logger log = Logger.getLogger(DroolsContractRules.class
            .getName());
    private static int firerThreadCount = 0;
    protected DroolsRulesFirer firer;
    protected Contract contract;
    protected Thread firerThread;

    public DroolsContractRules(String ruleFile, String ruleDir, Contract contract)
            throws RulesException {
        super(ruleFile, ruleDir);
        this.contract = contract;
    }

    public DroolsContractRules(String ruleFile, String ruleDir) throws RulesException {
        this(ruleFile, ruleDir, null);
    }

//    private void createAndRunNewSession() {
//
//        if (session != null) {
//            session.dispose();
//        }
//
//        session = kb.newStatefulKnowledgeSession();
//        if (contract != null) {
//            session.setGlobal("contract", contract);
//        }
//        // This is where we start firing rules (or lettig it fire???) May be not
//        // a good way to do.:-(
//        // e.g., how to know the end of firing rules in a session?
//        // is this reason for hack in insertMessageReceivedAtContractEvent??
//        // But live with the current implementation. -Malinda
//        firer = new DroolsRulesFirer(session);
//        firerThread = new Thread(firer);
//        firerThreadCount++;
//        if (log.isInfoEnabled()) {
//            log.info("Thread " + firerThreadCount
//                     + " started for a new stateful knowledge session for "
//                     + getRuleFile());
//        }
//        firerThread.start();
//    }

    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException {
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
        IMessageExaminer typeCheck = new MessageTypeExaminer(mre.getMessageWrapper(),
                null);
        //Retrieve the specific type of message checker
        IMessageExaminer check = typeCheck.getMessageExaminer();
        List<Command> cmds = new ArrayList<Command>();
        mre.setExaminer(check);           // TODO check selection in the rules did not work
//
        cmds.add(CommandFactory.newInsert(check, "check"));
//
        cmds.add(CommandFactory.newInsert(mre, "msg"));
//
        knowledgeSession.execute(mre);
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

//    private RuleBase readRules() throws RulesException {
//        try {
//            Reader source = new InputStreamReader(new FileInputStream(
//                    getRuleFile()));
//            PackageBuilder builder = new PackageBuilder();
//            builder.addPackageFromDrl(source);
//
//            Package pkg = builder.getPackage();
//
//            // add the package to a rulebase (deploy the rule package).
//            RuleBase ruleB = RuleBaseFactory.newRuleBase();
//            ruleB.addPackage(pkg);
//            return ruleB;
//        } catch (Exception e) {
//            throw new RulesException("Cannot read rules from " + getRuleFile());
//        }
//    }
//
//    /*
//      * (non-Javadoc)
//      *
//      * @see au.edu.swin.ict.road.composite.rules.IContractRules#getSession()
//      */
//    @Override
//    public StatefulKnowledgeSession getSession() {
//        // TODO Auto-generated method stub
//        return this.session;
//    }

    @Override
    public void cleanUp() {

        // TODO Auto-generated method stub
        if (log.isInfoEnabled()) {
            if (contract != null) {
                log.info("Disposing contractual rules session aka Knowledge Base for Contract "
                        + this.contract.getId());
            }
        }
//        if (session != null) {
//            session.dispose();
//        }
        if (firer != null) {
            this.firer.setTerminate(true);
        }
    }
}
