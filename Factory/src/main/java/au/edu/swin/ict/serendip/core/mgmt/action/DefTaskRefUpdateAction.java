package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

/**
 * TODO documentation
 */
public class DefTaskRefUpdateAction implements DefAdaptAction {
    String bId;
    String tId;
    String property;
    String value;

    public DefTaskRefUpdateAction(String bId, String tId, String property, String value) {
        this.bId = bId;
        this.tId = tId;
        this.property = property;
        this.value = value;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
//        OrganiserMgtOpResult res = ((OrganiserRole) comp.getOrganiserRole()).updateTaskFromBehavior(tId, bId, property, value);
//        return res.getResult();
        return false;
    }
}
