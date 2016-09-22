package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefBehaviorRemoveAction implements DefAdaptAction {
    String id;

    public DefBehaviorRemoveAction(String id) {
        this.id = id;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
//        OrganiserMgtOpResult res = ((OrganiserRole) comp.getOrganiserRole()).removeBehavior(id);
//        return res.getResult();
        return false;
    }

}
