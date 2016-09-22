package au.edu.swin.ict.road.common;

import java.io.Serializable;

/**
 * TODO
 */
public class State implements Serializable, StateMBean {
    private String name = "nddd";

    public State() {
    }

    @Override
    public String getName() {
        return name;
    }
}
