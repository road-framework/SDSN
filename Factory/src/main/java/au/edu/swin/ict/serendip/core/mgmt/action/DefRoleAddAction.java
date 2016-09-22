package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefRoleAddAction implements DefAdaptAction {
    private String id = null;
    private String name = null;
    private String synRules = null;
    private String routingRules = null;


    public DefRoleAddAction(String id, String name, String description) {
        super();
        this.id = id;
        this.name = name;
    }


    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
//        OrganiserMgtOpResult res = comp.getOrganiserRole().addRole(id, name, synRules, routingRules);
//        return res.getResult();
        return false;
    }

}
