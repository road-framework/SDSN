package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefRemovePlayerBindingAction implements DefAdaptAction {
    private String id = null;

    public DefRemovePlayerBindingAction(String pbId) {
        super();
        this.id = pbId;
    }

    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
//        OrganiserMgtOpResult res = ((OrganiserRole) comp.getOrganiserRole()).removeServiceBinding(id);
//        return res.getResult();
        return false;
    }
}
