package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.common.OrganiserMgtOpResult;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefUpdatePlayerBindingAction implements DefAdaptAction {
    private String id = null;
    private String key = null;
    private String value = null;


    public DefUpdatePlayerBindingAction(String pbId, String key, String value) {
        super();
        this.id = pbId;
        this.key = key;
        this.value = value;
    }

    @Override

    public boolean adapt(Composite comp) throws AdaptationException {
        // TODO Auto-generated method stub
        OrganiserMgtOpResult res = comp.getOrganiserRole().updateServiceBinding(id, key, value);
        return res.getResult();
    }
}
