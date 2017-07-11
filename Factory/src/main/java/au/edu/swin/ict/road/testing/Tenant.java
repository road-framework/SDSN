package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;

/**
 * TODO documentation
 */
public class Tenant implements Runnable {

    private String tenantName;
    private int round;
    private String userName;
    private String complain;
    private String pickUpLocation;
    private String userRole = "MM";
    private Composite composite;

    public Tenant(String tenantName, Composite composite, int round) {
        this.tenantName = tenantName;
        this.composite = composite;
        this.round = round;
    }

    public Tenant(String tenantName, Composite composite, int round, String userName, String complain, String pickUpLocation) {
        this.tenantName = tenantName;
        this.round = round;
        this.userName = userName;
        this.complain = complain;
        this.pickUpLocation = pickUpLocation;
        this.composite = composite;
    }

    public Tenant(String tenantName, Composite composite, int round, String userRole) {
        this.tenantName = tenantName;
        this.round = round;
        this.composite = composite;
        this.userRole = userRole;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < round; i++) {
                if (userName != null) {
                    Thread thread = new Thread(new EndUser(tenantName, composite, userName, complain, pickUpLocation, userRole));
                    thread.start();
                } else {
                    Thread thread = new Thread(new EndUser(tenantName, composite, userRole));
                    thread.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
