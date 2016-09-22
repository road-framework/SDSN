package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public interface ManagementStateChangeListener {

    public void notify(ManagementState managementState);

    public void notifyRemoval(ManagementState managementState);

}
