package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefAddBehaviorRefToProcessDef implements DefAdaptAction {
    private String pdId = null;
    private String bId = null;

    public DefAddBehaviorRefToProcessDef(String pdId, String bId) {
        this.pdId = pdId;
        this.bId = bId;
    }

    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
        OrganiserMgtOpResult res = comp.getSerendipOrganizer().addBehaviorRefToPD(pdId, bId);
        return res.getResult();
    }
}
