package au.edu.swin.ict.road.composite.rules;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.events.contract.MessageReceivedEvent;
import org.apache.log4j.Logger;

public class DefaultIContractRules implements IContractRules {

    private static Logger log = Logger.getLogger(DefaultIContractRules.class
            .getName());

    protected Contract contract;

    public DefaultIContractRules(Contract contract) {
        this.contract = contract;
    }

    @Override
    public ContractEvaluationResult insertEvent(IEvent iEvent) throws RulesException {
        MessageReceivedEvent mre = (MessageReceivedEvent) iEvent;
        MessageWrapper messageWrapper = mre.getMessageWrapper();
        mre.setBlocked(false);
        if (messageWrapper.isOrigin()) {
            messageWrapper.setOrigin(false);
            mre.triggerEvent("e" + messageWrapper.getOriginRoleId() + "Reqd");
        }

        String suffix = messageWrapper.isResponse() ? "Resd" : "Reqd";
        mre.triggerEvent("e" + messageWrapper.getOperationName() + suffix);

        ContractEvaluationResult mpr = new ContractEvaluationResult();
        mpr.setBlocked(mre.isBlocked());
        mpr.setInterpretedEvents(mre.getAllTriggeredEvents());
        mpr.setCorrelationId(messageWrapper.getCorrelationId());
        return mpr;
    }

    public String getRuleFile() {
        return null;
    }

    @Override
    public void cleanUp() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addRule(String newRule) {
        return true;
    }

    @Override
    public boolean removeRule(String ruleName) {
        return true;
    }
}
