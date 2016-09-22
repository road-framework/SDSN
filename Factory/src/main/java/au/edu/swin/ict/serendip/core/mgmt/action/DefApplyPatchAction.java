package au.edu.swin.ict.serendip.core.mgmt.action;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.serendip.core.mgmt.AdaptationException;

public class DefApplyPatchAction implements DefAdaptAction {
    private String patchFile = null;

    public DefApplyPatchAction(String patchFile) {
        super();
        this.patchFile = patchFile;
    }

    @Override
    public boolean adapt(Composite comp) throws AdaptationException {
        //OrganiserMgtOpResult res = comp.getOrganiserRole().applyPatch(patchFile);
//        OrganiserMgtOpResult res = comp.getSerendipEngine().getSerendipOrgenizer().applyPatch(patchFile);
//        return res.getResult();
        return false;
    }

}
