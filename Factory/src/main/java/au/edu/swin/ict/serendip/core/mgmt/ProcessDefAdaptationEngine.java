package au.edu.swin.ict.serendip.core.mgmt;

import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionType;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionsType;
import au.edu.swin.ict.road.xml.bindings.ServiceNetwork;
import au.edu.swin.ict.serendip.composition.Composition;
import au.edu.swin.ict.serendip.core.SerendipEngine;

import java.util.List;

public abstract class ProcessDefAdaptationEngine {
    private SerendipEngine engine = null;
    private Composition compo = null;
    private ServiceNetwork smc = null;

    public ProcessDefAdaptationEngine(SerendipEngine engine) {
        this.engine = engine;
        this.compo = engine.getComposition();
        this.smc = this.compo.getComposite().getSmcBinding();
    }

    ///////////////////////////////////INSTANCE LEVEL/////////////////////////////////////////////////
    public void changeProcessInstance(String property, String value) {
        //Possibilities
        //Parse the property string and access the object via the engine.

        //Modify object
    }

    public Object pathQuery(String path) {
        path = "PI:p021/BT:towing/TR:tow/@PreEP";
        path = "PI:p021/BT:towing/TR:tow/@PreEP";

        String[] steps = path.split("//");
        this.engine.getProcessInstanceByInsId("p021");

        return null;
    }

    ///////////////////////////////////DEFINITION LEVEL ////////////////////////////////////////////////////
    public void addNewBehaviorTermRefToDefinition() {

    }

    public ProcessDefinitionType addNewProcessDefinition(String gid, String id, String cos, String cot, String descr) {
        //Create a new ProcessDefinitionType
        ProcessDefinitionType pdt = new ProcessDefinitionType();
        pdt.setId(id);
        pdt.setCoS(cos);
        pdt.setCoT(cot);
        pdt.setDescr(descr);

        List<ProcessDefinitionsType> pdtsList = this.smc.getVirtualServiceNetwork();
        for (ProcessDefinitionsType pdts : pdtsList) {
            if (pdts.getId().equals(gid)) {
                pdts.getProcess().add(pdt);
            }
        }
        return pdt;
    }
}
