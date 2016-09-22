package workload;

/**
 * TODO documentation
 */
public class TenantUnit {
    private String tenantName;
    private int users;

    public TenantUnit(String tenantName, int users) {
        this.tenantName = tenantName;
        this.users = users;
    }

    public int getUsers() {
        return users;
    }

    public String getTenantName() {
        return tenantName;
    }
}
