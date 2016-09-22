package au.edu.swin.ict.serendip.composition;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.xml.bindings.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the composite representation at the Serendip level. The additional
 * information about a composite that we need to keep, we will keep in here.
 * <p/>
 * Note that we do not create models, e.g. process definitions at the time of
 * enactment. Rather we keep the types and dynamically generate the models on
 * demand. This will be the design principle for the rest of the sections such
 * as MPF
 * In this way the code uses the latest version of SMC
 *
 * @author Malinda Kapuruge
 */
public class Composition {
    static Logger logger = Logger.getLogger(Composition.class);
    private String id = null;

    private ServiceNetwork smc = null;
    private Composite composite = null;

    /**
     * Create a new composition from the type loaded via the ROAD
     *
     * @param composite
     */
    public Composition(Composite composite) {

        this.smc = composite.getSmcBinding();
        this.composite = composite;
        if ((null == this.composite) || (null == this.smc)) {
            logger.error("Cannot instantiate the Serendip composite ");
        }

    }

    /**
     * Iterate through all the process definitions.
     * Returns the matching process definition for CoS.
     *
     * @param cos condition of start (event)
     * @return the process definition
     */
    public String getPDforCoS(Classifier classifier, String cos) {
        if (this.smc.getVirtualServiceNetwork() == null) {
            return null;
        }
        if (this.smc.getVirtualServiceNetwork() == null) {
            return null;
        }
        List<ProcessDefinitionsType> pdsTypeList = this.smc.getVirtualServiceNetwork();
        for (ProcessDefinitionsType pdsType : pdsTypeList) {
            if (pdsType.getId().equals(classifier.getVsnId())) {
                for (ProcessDefinitionType pdt : pdsType.getProcess()) {
                    if (pdt.getId().equals(classifier.getProcessId()) && pdt.getCoS().equals(cos)) {
                        return pdt.getId();
                    }
                }
            }
        }
        return null;

    }

    public List<String> getAllProcessDefIds() {
        ArrayList<String> idList = new ArrayList<String>();

        if (null != this.smc.getVirtualServiceNetwork()) {
            List<ProcessDefinitionsType> pdsTypeList = this.smc.getVirtualServiceNetwork();
            for (ProcessDefinitionsType pdsType : pdsTypeList) {

                for (ProcessDefinitionType pdt : pdsType.getProcess()) {
                    idList.add(pdt.getId());
                }
            }
        }

        return idList;
    }

    /**
     * Returns a specific process definition type on demand
     *
     * @param defId
     * @return
     */
    public ProcessDefinitionType getProcessDefinition(String defId) {
        List<ProcessDefinitionsType> pdsTypeList = this.smc.getVirtualServiceNetwork();
        for (ProcessDefinitionsType pdsType : pdsTypeList) {

            for (ProcessDefinitionType pdt : pdsType.getProcess()) {
                if (pdt.getId().equals(defId)) {
                    return pdt;
                }
            }
        }
        return null;
    }

    /**
     * Gets the composite
     *
     * @return
     */
    public Composite getComposite() {
        return this.composite;
    }

    /**
     * Get all the behavior term types in the composition
     *
     * @return
     */
    public List<CollaborationUnitType> getAllCollaborationUnitTypes() {
        CollaborationUnitsType bts = this.smc.getCollaborationUnits();
        return bts.getCollaborationUnit();
    }

    /**
     * Get all the behavior term types for a given process definitions id
     *
     * @param defId
     * @return
     */
    public List<CollaborationUnitType> getAllCollaborationUnitTypesForPD(String defId) {

        List<String> btIdList = getAllBehaviorTermIdsForPD(defId);
        ArrayList<CollaborationUnitType> btList = new ArrayList<CollaborationUnitType>();
        for (String s : btIdList) {
            CollaborationUnitType btt = this.getCollaborationUnitTypeById(s);
            btList.add(btt);
        }

        return btList;
    }

    /**
     * Get al the behavior term ids referenced given process definition
     *
     * @param defId
     * @return
     */
    public List<String> getAllBehaviorTermIdsForPD(String defId) {
        ProcessDefinitionType pdef = this.getProcessDefinition(defId);
        return pdef.getCollaborationUnitRef();

    }

    /**
     * Gets a behavior term for a given id
     *
     * @param btId
     * @return
     */
    public CollaborationUnitType getCollaborationUnitTypeById(String btId) {
        List<CollaborationUnitType> btList = getAllCollaborationUnitTypes();
        for (CollaborationUnitType btt : btList) {
            if (btt.getId().equals(btId)) {
                return btt;
            }
        }

        return null;
    }

}
