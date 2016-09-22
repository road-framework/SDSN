package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

/**
 * TODO documentation
 */
public class DefTaskRefRemoveAction implements DefAdaptAction {
    private String tId;
    private String bId;

    public DefTaskRefRemoveAction(String tId, String bId) {
        this.tId = tId;
        this.bId = bId;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
//        OrganiserMgtOpResult res = ((OrganiserRole) comp.getOrganiserRole()).removeTaskFromBehavior(tId, bId);
//        return res.getResult();
        return false;
    }
}
