package au.edu.swin.ict.road.composite.rules;

import au.edu.swin.ict.road.common.IRules;
import au.edu.swin.ict.road.composite.message.MessageWrapper;

public interface ICompositeRules extends IRules {

    public void insertMessageReceivedAtSourceEvent(MessageWrapper msg, String roleId);

    public void insertRoutingFailureEvent(MessageWrapper msg, String roleId);

    public void insertRoutingSuccessEvent(MessageWrapper msg, String roleId, String contractId);

    public void insertMessageReceivedAtDestinationEvent(MessageWrapper msg, String roleId);

    public void insertMessageReceivedAtContractEvent(MessageWrapper msg, String contractId);
}
