package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.serendip.core.SerendipEngine;
import au.edu.swin.ict.serendip.core.mgmt.action.DefAdaptAction;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Malinda
 */
public class DefAdaptationEngine {
    private SerendipEngine engine = null;
    private static Logger log = Logger.getLogger(DefAdaptationEngine.class.getName());


    public DefAdaptationEngine(SerendipEngine engine) {
        this.engine = engine;
    }

    public void executeAdaptation(List<DefAdaptAction> adaptationList) throws AdaptationException {
        this.backup();
        for (DefAdaptAction daa : adaptationList) {
            boolean res = daa.adapt(this.engine.getComposition().getComposite());

            if (!res) {
                this.restore();
                throw new AdaptationException("Problem in adapting the definition of " + engine.getCompositionName());
            }
        }
    }

    private void backup() {
        // TODO Auto-generated method stub

    }

    private void restore() {
        // TODO Auto-generated method stub

    }
}
