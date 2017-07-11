package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefUpdateProcessDef implements DefAdaptAction {
    String property;
    String value;
    private String pdId;

    public DefUpdateProcessDef(String pdId, String property, String value) {
        this.pdId = pdId;
        this.property = property;
        this.value = value;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
        OrganiserMgtOpResult res = comp.getSerendipOrganizer().updatePD(pdId, property, value);
        return res.getResult();
    }
}