package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.EventRecord;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.ContractEvaluationResult;
import au.edu.swin.ict.road.composite.rules.drools.DroolsContractRules;
import au.edu.swin.ict.road.composite.rules.events.contract.MessageReceivedEvent;
import org.apache.log4j.Logger;

import java.util.List;

public class DroolsTest {
    private static Logger log = Logger.getLogger(DroolsTest.class.getName());

    public static void main(String[] args) {

        test2();
        System.exit(0);
    }

    public static void test2() {
        DroolsContractRules dcri;
        try {
            dcri = new DroolsContractRules("C:\\research\\trunk\\research\\projects\\case-study-road-feature\\Scenario3\\data\\rules\\CaseOfficer1-TowCompany1.drl", "");


            MessageWrapper mw = new MessageWrapper();
            mw.setCorrelationId("p002");
            mw.setMessage("Some  Message Object");
            mw.setOperationName("towVehicle");
            ContractEvaluationResult mpr;

            mpr = (ContractEvaluationResult) dcri.insertEvent(new MessageReceivedEvent(mw));
            System.out.println(mpr.getAllInterprettedEvents());
            System.out.println("*********");
            log.debug("Done");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void test1() {
        DroolsContractRules dcri;
        try {
            dcri = new DroolsContractRules("E://ROAD/workspace/SerendipMerge/sample/Scenario1/data/rules/co-tc.drl", "");


            MessageWrapper mw = new MessageWrapper();
            mw.setCorrelationId("p002");
            mw.setMessage("Some  Message Object");
            mw.setOperationName("orderTow");
            ContractEvaluationResult mpr;

            mpr = (ContractEvaluationResult) dcri.insertEvent(new MessageReceivedEvent(mw));
            List<EventRecord> events = mpr.getAllInterprettedEvents();

            for (EventRecord e : events) {
                log.debug("Event found " + e.getEventId());
            }
            log.debug("Done");
        } catch (RulesException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


}
