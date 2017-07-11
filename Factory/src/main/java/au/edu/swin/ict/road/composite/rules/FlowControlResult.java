package au.edu.swin.ict.road.composite.rules;

import au.edu.swin.ict.road.common.RuleExecutionResult;

/**
 * TODO documentation
 */
public class FlowControlResult extends RuleExecutionResult {

    public static int ALLOWED = 0;
    public static int DENIED = 1;
    public static int SENDTOCONTROLLER = 2;
    private String message;
    private int state;

    public FlowControlResult(int state, String message) {
        this.message = message;
        this.state = state;
    }
    public FlowControlResult(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
