package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefRoleUpdateAction implements DefAdaptAction {
    private String id = null;
    private String name = null;
    private String description = null;
    private String synRules = null;
    private String routingRules = null;


    public DefRoleUpdateAction(String id, String name, String description) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
    }


    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
        OrganiserMgtOpResult res = comp.getOrganiserRole().addRole(id, name);
        return res.getResult();
    }

}
