package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class ManagementPolicyState extends ManagementState implements IEvent {

    private IOrganiserRole iOrganiserRole;
    private IOperationalManagerRole iOperationalManagerRole;
    public static String STATE_INCIPIENT = "incipient";
    public static String STATE_COMPLETED = "enacted";

    public ManagementPolicyState(String id) {
        super(id);
        setState(STATE_INCIPIENT);
    }

    public ManagementPolicyState() {
        setState(STATE_INCIPIENT);
    }

    public IOrganiserRole getiOrganiserRole() {
        return iOrganiserRole;
    }

    public void setiOrganiserRole(IOrganiserRole iOrganiserRole) {
        this.iOrganiserRole = iOrganiserRole;
    }

    public IOperationalManagerRole getiOperationalManagerRole() {
        return iOperationalManagerRole;
    }

    public void setiOperationalManagerRole(IOperationalManagerRole iOperationalManagerRole) {
        this.iOperationalManagerRole = iOperationalManagerRole;
    }
}
