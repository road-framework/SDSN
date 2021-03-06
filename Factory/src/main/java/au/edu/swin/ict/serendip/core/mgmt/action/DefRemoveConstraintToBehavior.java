package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

/**
 * TODO documentation
 */
public class DefRemoveConstraintToBehavior implements DefAdaptAction {
    private String bId;
    private String cId;

    public DefRemoveConstraintToBehavior(String bId, String cId) {
        this.bId = bId;
        this.cId = cId;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
        OrganiserMgtOpResult res =
                comp.getSerendipOrganizer().removeBehaviorConstraint(bId, cId);
        return res.getResult();
    }
}
