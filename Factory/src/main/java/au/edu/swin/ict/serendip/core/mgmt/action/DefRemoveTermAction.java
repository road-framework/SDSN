package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefRemoveTermAction implements DefAdaptAction {
    private String ctId = null;
    private String tmId = null;

    public DefRemoveTermAction(String ctId, String tmId) {
        this.ctId = ctId;
        this.tmId = tmId;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
        OrganiserMgtOpResult res =
                comp.getOrganiserRole().removeTerm(ctId, tmId);
        return res.getResult();
    }
}
