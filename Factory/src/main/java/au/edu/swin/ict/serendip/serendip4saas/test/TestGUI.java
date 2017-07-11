package au.edu.swin.ict.serendip.serendip4saas.test;

import au.edu.swin.ict.serendip.composition.BehaviorTerm;
import au.edu.swin.ict.serendip.composition.Task;
import au.edu.swin.ict.serendip.serendip4saas.gui.Serendip4SaasToolFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TestGUI {
    public static void main(String[] args) {

        // Create sample behavior terms
        List<BehaviorTerm> behaviorTerms = createBTs();

        try {
            Serendip4SaasToolFrame tool = new Serendip4SaasToolFrame(
                    behaviorTerms);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static List<BehaviorTerm> createBTs() {
        List<BehaviorTerm> behaviorTerms = new ArrayList<BehaviorTerm>();
        // BT_Dummy
        List<Task> btDummyTasks = new ArrayList<Task>();
        btDummyTasks.add(new Task("[e1]", "R1", "ttt1", "[e2]"));
        btDummyTasks.add(new Task("[e2]", "R1", "ttt2", "[e3]"));
        behaviorTerms
                .add(new BehaviorTerm("Duummy", btDummyTasks, null, false));

        // BT_Complain
        List<Task> btComplainTasks = new ArrayList<Task>();
        btComplainTasks.add(new Task("[ANY]", "MM", "Complain",
                "[ComplaintRcvd]"));
        btComplainTasks.add(new Task("[ComplaintRcvd]", "CO", "SendAck",
                "[ComplaintAcked]"));
        btComplainTasks.add(new Task("[ComplaintRcvd]", "CO", "Analyze",
                "[TowReqd] OR [RepairReqd]"));
        behaviorTerms.add(new BehaviorTerm("Complain", btComplainTasks, null,
                false));
        // BT_Towing
        List<Task> btTowingTasks = new ArrayList<Task>();
        btTowingTasks.add(new Task("[TowReqd]", "CO", "SendTowReq",
                "[TowReqSent] AND [PickupLocKnown]"));
        btTowingTasks.add(new Task("[TowReqSent]", "GR", "SendGRLocation",
                "[DestinationKnown]"));
        btTowingTasks.add(new Task("[PickupLocKnown] AND [DestinationKnown]",
                "TC", "Tow", "[CarTowSuccess] OR [CarTowFailed]"));
        btTowingTasks.add(new Task("[CarTowSuccess]", "GR", "TowingAck",
                "[TowingAckedByGR]"));
        btTowingTasks.add(new Task("[CarTowSuccess] AND [TowingAckedByGR]",
                "CO", "PayTow", "[TCPaid]"));
        btTowingTasks.add(new Task("[CarTowSuccess]", "GR", "PayIncentive",
                "[IncentivePaid]"));
        behaviorTerms
                .add(new BehaviorTerm("Towing", btTowingTasks, null, false));
        // BT_Repair
        List<Task> btRepairTasks = new ArrayList<Task>();
        btRepairTasks.add(new Task("[RepairReqd]", "CO", "SendGRReq",
                "[GRReqSent]"));
        btRepairTasks.add(new Task("[GRReqSent] AND [CarTowSuccess]", "GR",
                "ReqAdvPay", "[AdvPayReqSent]"));
        btRepairTasks.add(new Task("[CarTowSuccess] AND [AdvPayReqSent]", "CO",
                "PayAdvToGR", "[AdvToGRPaid]"));
        btRepairTasks.add(new Task("[CarTowSuccess] AND [AdvToGRPaid]", "GR",
                "Repair", "[CarRepairSuccess] AND [CarRepairFailed]"));
        btRepairTasks.add(new Task("[CarRepairSuccess]", "MM", "Inspect",
                "[CarRepairOKConfirmed] OR [CarRepairFailureNoified]"));
        btRepairTasks.add(new Task(
                "[CarRepairSuccess] AND [CarRepairOKConfirmed] ", "CO",
                "PayGR", "[GRPaid]"));
        behaviorTerms
                .add(new BehaviorTerm("Repair", btRepairTasks, null, false));
        // BT_ProvideTaxi
        List<Task> btProvideTaxiTasks = new ArrayList<Task>();
        btProvideTaxiTasks.add(new Task("[ANY]", "MM", "CallTaxi",
                "[TaxiCallRcvd]"));
        btProvideTaxiTasks.add(new Task("[ComplaintRcvd] AND [TaxiCallRcvd]",
                "CO", "SendTaxiReq", "[TaxiRequested]"));
        btProvideTaxiTasks.add(new Task("[TaxiRequested]", "TX",
                "AckTaxiReqRcvd", "[TaxiReqAcked]"));
        btProvideTaxiTasks.add(new Task("[TaxiReqAcked]", "TX",
                "ProvideTaxiService", "[TaxiSvcProvided]"));
        btProvideTaxiTasks.add(new Task("[TaxiSvcProvided]", "TX",
                "ProvideTaxiService", "[PayTaxi]"));
        behaviorTerms.add(new BehaviorTerm("ProvidTaxi", btProvideTaxiTasks,
                null, false));
        // BT_ProvideHotel
        List<Task> btProvideHotelTasks = new ArrayList<Task>();
        btProvideHotelTasks.add(new Task("[ANY]", "MM", "RequestAccommodation",
                "[HotelReqd]"));
        btProvideHotelTasks.add(new Task("[ComplaintRcvd] AND [HotelReqd]",
                "CO", "BookHotel", "[HotelReqSent]"));
        btProvideHotelTasks.add(new Task("[HotelReqSent]", "HT",
                "SendBookingConfirmation", "[HotelBooked]"));
        btProvideHotelTasks.add(new Task("[HotelBooked]", "CO",
                "NotifyMemberOfHotel", "[MemberNotifiedAboutHotel]"));
        behaviorTerms.add(new BehaviorTerm("ProvideHotel", btProvideHotelTasks,
                null, false));

        // Add sample but empty behavior terms too
        behaviorTerms.add(new BehaviorTerm("Towing2", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("Towing3", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("Repairing2", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("Repairing3", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("HotelBooking", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("HotelBooking2",
                btProvideHotelTasks, null, false));
        behaviorTerms.add(new BehaviorTerm("HotelBooking3",
                btProvideHotelTasks, null, false));
        behaviorTerms.add(new BehaviorTerm("TaxiOrdering", btProvideHotelTasks,
                null, false));
        behaviorTerms.add(new BehaviorTerm("TaxiOrdering2",
                btProvideHotelTasks, null, false));
        behaviorTerms.add(new BehaviorTerm("CreditBasedPayment",
                btProvideHotelTasks, null, false));
        behaviorTerms.add(new BehaviorTerm("MoneyTransfer",
                btProvideHotelTasks, null, false));

        return behaviorTerms;

    }

}
