package au.edu.swin.ict.road.composite.rules;

import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.RuleExecutionResult;
import au.edu.swin.ict.road.composite.contract.Contract;
import au.edu.swin.ict.road.composite.contract.Term;

import java.util.List;

/**
 * When a contractual rule is evaluated, the contract/rule triggers events.
 * The contract uses current facts (contextual+messages) to interpret the events.
 * This class will store all such interpreted events.
 *
 * @author Malinda
 */
public class ContractEvaluationResult extends RuleExecutionResult {
    private boolean isBlocked = false; //To comply with the current implementation
    private List<EventRecord> interpretedEvents;
    private Term term = null;
    private Contract contract = null;
    private String correlationId = null;

    public ContractEvaluationResult() {
        super();
    }

    public List<EventRecord> getAllInterprettedEvents() {
        return this.interpretedEvents;
    }

    public void setInterpretedEvents(List<EventRecord> events) {
        this.interpretedEvents = events;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
