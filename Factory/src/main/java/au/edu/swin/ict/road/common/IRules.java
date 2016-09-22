package au.edu.swin.ict.road.common;

/**
 * TODO documentation
 */
public interface IRules {

    public boolean addRule(String newRule);

    public boolean removeRule(String ruleName);

    public String getRuleFile();

    public void cleanUp();

    public RuleExecutionResult insertEvent(IEvent iEvent) throws RulesException;
}
