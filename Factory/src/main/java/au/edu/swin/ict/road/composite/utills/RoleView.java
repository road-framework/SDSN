package au.edu.swin.ict.road.composite.utills;

import au.edu.swin.ict.road.composite.Role;

public class RoleView implements RoleViewMBean {
    public Role role;

    public RoleView(Role role) {
        this.role = role;
    }

    @Override
    public int messageCount() {
        return role.getPendingOutBuf().getCollection().size();
    }
}
