package au.edu.swin.ict.road.common;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public abstract class ManagementState implements IEvent {
    public static String STATE_ACTIVE = "active";
    public static String STATE_PASSIVE = "passive";
    public static String STATE_QUIESCENCE = "quiescence";
    private String id;
    private String state = STATE_PASSIVE;
    private List<ManagementStateChangeListener> listeners = new ArrayList<ManagementStateChangeListener>();

    public ManagementState(String id) {
        this.id = id;
    }

    public ManagementState(String id, String state) {
        this.id = id;
        this.state = state;
    }

    public ManagementState() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        List<ManagementStateChangeListener> list = new ArrayList<ManagementStateChangeListener>();
        list.addAll(listeners);
        for (ManagementStateChangeListener listener : list) {
            listener.notify(this);
        }
    }

    public void subscribe(ManagementStateChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void notifyRemoval() {
        for (ManagementStateChangeListener listener : listeners) {
            listener.notifyRemoval(this);
        }
    }
}
