package au.edu.swin.ict.serendip.core.mgmt.action;


import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefRemoveBehaviorRefFromProcessDef implements DefAdaptAction {
    private String pdId = null;
    private String bId = null;

    public DefRemoveBehaviorRefFromProcessDef(String pdId, String bId) {
        this.pdId = pdId;
        this.bId = bId;
    }

    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
        OrganiserMgtOpResult res = comp.getSerendipOrganizer().removeBehaviorRefFromPD(pdId, bId);
        return res.getResult();
    }
}
