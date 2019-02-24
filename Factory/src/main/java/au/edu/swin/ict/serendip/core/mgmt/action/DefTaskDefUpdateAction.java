package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefTaskDefUpdateAction implements DefAdaptAction {
    String rId;
    String tId;
    String property;
    String value;

    public DefTaskDefUpdateAction(String rId, String tId, String property, String value) {
        this.rId = rId;
        this.tId = tId;
        this.property = property;
        this.value = value;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
        OrganiserMgtOpResult res = comp.getOrganiserRole().updateTask(rId, tId, "add", property, value);
        return res.getResult();
    }

}
