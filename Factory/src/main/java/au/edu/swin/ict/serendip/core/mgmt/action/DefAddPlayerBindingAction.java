package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefAddPlayerBindingAction implements DefAdaptAction {
    private String id = null;
    private String rId = null;
    private String endpoint = null;

    public DefAddPlayerBindingAction(String pbId, String rId, String endpoint) {
        super();
        this.id = pbId;
        this.rId = rId;
        this.endpoint = endpoint;
    }

    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
//        OrganiserMgtOpResult res = ((OrganiserRole) comp.getOrganiserRole()).addServiceBinding(id, rId, endpoint);
//        return res.getResult();
        return false;
    }

}
