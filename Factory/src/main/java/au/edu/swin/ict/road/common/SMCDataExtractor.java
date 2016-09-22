package au.edu.swin.ict.road.common;

import au.edu.swin.ict.road.xml.bindings.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO documentation
 */
public class SMCDataExtractor {
    private static Logger logger = Logger.getLogger(SMCDataExtractor.class);

    private ServiceNetwork smc = null;

    public SMCDataExtractor(ServiceNetwork smc) {
        this.smc = smc;
    }

    /**
     * Iterate through all the process definitions.
     * Returns the matching process definition for CoS.
     *
     * @param cos condition of start (event)
     * @return the process definition
     */
    public String getPDforCoS(String cos) {
        if (this.smc.getVirtualServiceNetwork() == null) {
            return null;
        }
//        List<ProcessDefinitionType> pdTypeList = this.smc.getVirtualServiceNetwork().getProcessDefinition();
//        for (ProcessDefinitionType pdt : pdTypeList) {
//            if (pdt.getCoS().equals(cos)) {
//                return pdt.getId();
//            }
//        }

        return null;

    }

    public List<String> getAllProcessDefIds() {
        ArrayList<String> idList = new ArrayList<String>();

        if (null != this.smc.getVirtualServiceNetwork()) {
            List<ProcessDefinitionsType> pdTypeList = this.smc.getVirtualServiceNetwork();
            for (ProcessDefinitionsType pdsType : pdTypeList) {
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
    public ProcessDefinitionType getProcessDefinition(String pgId, String defId) {
        ProcessDefinitionsType pdsType = getProcessDefinitionGroup(pgId);
        if (pdsType != null) {
            List<ProcessDefinitionType> pdTypeList = pdsType.getProcess();
            for (ProcessDefinitionType pdType : pdTypeList) {
                if (pdType.getId().equals(defId)) {
                    return pdType;
                }
            }
        }
        return null;
    }

    public ProcessDefinitionsType getProcessDefinitionGroup(String pgId) {
        List<ProcessDefinitionsType> pdsList = smc.getVirtualServiceNetwork();
        for (ProcessDefinitionsType pdsType : pdsList) {
            if (pdsType.getId().equals(pgId)) {
                return pdsType;
            }
        }
        return null;
    }

    public List<ProcessDefinitionsType> getAllProcessDefinitions() {
        return smc.getVirtualServiceNetwork();
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

    public List<InterProcessRegulationUnitType> getAllInterProcessRegulationUnitTypes() {
        InterProcessRegulationUnitsType bts = this.smc.getInterProcessRegulationUnits();
        return bts.getInterProcessRegulationUnit();
    }

    public List<InterCollaborationRegulationUnitType> getAllInterCollaborationRegulationUnitTypes() {
        InterCollaborationRegulationUnitsType bts = this.smc.getInterCollaborationRegulationUnits();
        return bts.getInterCollaborationRegulationUnit();
    }

    public PlayerBindingType getServiceBinding(String id) {
        for (PlayerBindingType type : smc.getServiceBindings().getServiceBinding()) {
            if (type.getId().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
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
//        ProcessDefinitionType pdef = this.getProcessDefinition(defId);
//        return pdef.getBehaviorTermRefs().getBehavirTermId();
        return null;

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

    public InterProcessRegulationUnitType getInterProcessRegulationUnitTypeById(String btId) {
        List<InterProcessRegulationUnitType> btList = getAllInterProcessRegulationUnitTypes();
        for (InterProcessRegulationUnitType btt : btList) {
            if (btt.getId().equals(btId)) {
                return btt;
            }
        }
        return null;
    }

    public InterCollaborationRegulationUnitType getInterCollaborationRegulationUnitTypeById(String btId) {
        List<InterCollaborationRegulationUnitType> btList = getAllInterCollaborationRegulationUnitTypes();
        for (InterCollaborationRegulationUnitType btt : btList) {
            if (btt.getId().equals(btId)) {
                return btt;
            }
        }
        return null;
    }

    public List<String> getInterCollaborationRegulationUnitsOfProcess(String vsnId, String processId) {
        for (ProcessDefinitionsType vsn : smc.getVirtualServiceNetwork()) {
            if (vsn.getId().equalsIgnoreCase(vsnId)) {
                for (ProcessDefinitionType pd : vsn.getProcess()) {
                    if (pd.getId().equalsIgnoreCase(processId)) {
                        return pd.getInterCollaborationRegulationUnitRef();
                    }
                }
            }
        }
        return new ArrayList<String>();
    }

    public List<String> getInterProcessRegulationUnitsOfVSN(String vsnId) {
        for (ProcessDefinitionsType vsn : smc.getVirtualServiceNetwork()) {
            if (vsn.getId().equalsIgnoreCase(vsnId)) {
                return vsn.getInterProcessRegulationUnitRef();
            }
        }
        return new ArrayList<String>();
    }
}
