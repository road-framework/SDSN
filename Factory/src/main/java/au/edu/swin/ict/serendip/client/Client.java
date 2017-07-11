package au.edu.swin.ict.serendip.client;

import au.edu.swin.ict.serendip.message.Message;

/**
 * @author Malinda
 * @deprecated
 */
public class Client {
    private String id = null;
    private String roleId = null;


    public Client(String id, String roleId) {
        super();
        this.id = id;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void sendMessage(Message msg) {

    }
}
