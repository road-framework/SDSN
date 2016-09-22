package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class StateView implements StateViewMXBean {

    protected State state;

    public StateView(State state) {
        this.state = state;
    }

    @Override
    public State getState() {
        return state;
    }
}
