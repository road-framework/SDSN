package au.edu.swin.ict.road.common;

import java.util.HashMap;
import java.util.Map;

public class CustomizationManager {
    private Map map = new HashMap();
    private IOperationalManagerRole opMgt;
    private IOrganiserRole orgMgt;

    public CustomizationManager(IOperationalManagerRole opMgt, IOrganiserRole orgMgt) {
        this.opMgt = opMgt;
        this.orgMgt = orgMgt;
    }

    private void createVSN(String vsnId) {
        if (!orgMgt.containsVSN(vsnId)) {
            orgMgt.addVSN(vsnId);
            orgMgt.addProcessToVSN(vsnId, vsnId, "CoS", "CoT");
        }
    }

    public void addCollaboration(String name, String vsnId, Map<String, String> parameters) {
        createVSN(vsnId);
        opMgt.addRegulationUnitsToProcessRegulationPolicy(vsnId, vsnId, name);
    }

    public void addCollaboration(String name, String vsnId) {
        createVSN(vsnId);
        opMgt.addRegulationUnitsToProcessRegulationPolicy(vsnId, vsnId, name);
    }

    public void removeCollaboration(String name, String vsnId) {
        opMgt.removeRegulationUnitsFromProcessRegulationPolicy(vsnId, vsnId, name);
    }

    public void updateCollaboration(String name, String vsnId, Map<String, String> parameters) {
        //  opMgt.updateRegulationUnitsOfProcessRegulationPolicy()
    }

    public void updateCollaboration(String name, String vsnId) {
        //  opMgt.updateRegulationUnitsOfProcessRegulationPolicy()
    }

    public void addInterCollaborationRegulationUnit(String name, String vsnId, Map<String, String> parameters) {
        createVSN(vsnId);
        opMgt.addInterCollaborationRegulationUnitToVSN(vsnId, vsnId, name);
    }

    public void addInterCollaborationRegulationUnit(String name, String vsnId) {
        createVSN(vsnId);
        opMgt.addInterCollaborationRegulationUnitToVSN(vsnId, vsnId, name);
    }

    public void removeInterCollaborationRegulationUnit(String name, String vsnId) {
        createVSN(vsnId);
        opMgt.removeInterCollaborationRegulationUnitFromVSN(vsnId, vsnId, name);
    }


    public void updateInterCollaborationRegulationUnit(String name, String vsnId, Map<String, String> parameters) {
        //   createVSN(vsnId);
        //opMgt.addInterCollaborationRegulationUnitToVSN(vsnId, vsnId, name);
    }

    public void updateInterCollaborationRegulationUnit(String name, String vsnId) {
        //  createVSN(vsnId);
        //  opMgt.addInterCollaborationRegulationUnitToVSN(vsnId, vsnId, name);
    }
}

