package au.swin.ict.research.cs3.road.road4ws.core.util;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.testing.CusEventGenerator;
import org.apache.axiom.om.OMElement;

import java.io.File;
import java.util.List;

public class ControlPlaneAPI implements IControlPlaneAPI {
    private IOperationalManagerRole opMgt;
    private IOrganiserRole orMgt;
    private String rulesDir;
    private CustomizationPolicyExecutor executor;

    public ControlPlaneAPI(IOperationalManagerRole opMgt, IOrganiserRole orMgt, String rulesDir) {
        this.opMgt = opMgt;
        this.orMgt = orMgt;
        this.rulesDir = rulesDir;
        this.executor = loadDroolsCustomizationPolicyExecutor();
    }

    private CustomizationPolicyExecutor loadDroolsCustomizationPolicyExecutor() {
        String ruleFile = "cus_policy.drl";
        if (new File(ruleFile).exists()) {
            return new CustomizationPolicyExecutor(ruleFile, rulesDir);
        } else {
            String mgtPolicyDir = "mgtpolicies" + "/";
            return new CustomizationPolicyExecutor(ruleFile, rulesDir + mgtPolicyDir);
        }
    }

    private boolean customize(List<IEvent> events) {
        long startTime = System.currentTimeMillis();
        EventCollection eventCollection = new EventCollection();
        DisabledRuleSet disabledRuleSet = new DisabledRuleSet("none");
        eventCollection.setDisabledRuleSet(disabledRuleSet);
        eventCollection.setiOperationalManagerRole(opMgt);
        eventCollection.setiOrganiserRole(orMgt);
        eventCollection.addAllIEvents(events);
        //generate events from feature configuration
        try {
            executor.insertEvent(eventCollection);
        } catch (RulesException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        StatWriter.writeResTime("CUS", endTime - startTime);
        return true;
    }

    @Override
    public boolean customizeA(String featureConf) {
        customize(CusEventGenerator.getInstance().generateEventsForROSAS(null));
        return true;
    }

    @Override
    public boolean customizeB(String featureConf) {
        customize(CusEventGenerator.getInstance().generateEventsForCASAS(null));
        return true;
    }
}
