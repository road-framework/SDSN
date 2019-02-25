package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.AttributedSelectedEvent;
import au.edu.swin.ict.road.common.FeatureSelectedEvent;
import au.edu.swin.ict.road.common.IEvent;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

public class CusEventGenerator {
    private static CusEventGenerator ourInstance = new CusEventGenerator();

    public static CusEventGenerator getInstance() {
        return ourInstance;
    }

    private CusEventGenerator() {
    }

    public List<IEvent> generateEventsForROSAS(OMElement featureConf) {
        List<IEvent> iEvents = new ArrayList<>();
        iEvents.add(new FeatureSelectedEvent("CaseHandling","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("CaseHandling.Throughput","1000","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("Reimbursement","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("Reimbursement.Throughput","1000","TestTenant"));
        iEvents.add( new FeatureSelectedEvent("Tow","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("Tow.Throughput","100", "TestTenant"));
        iEvents.add(new FeatureSelectedEvent("Repair","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("Repair.Throughput","150","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("External","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("VehicleAssessment","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("LegalAssistance","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("TaxiHire","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("RentalVehicle","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("Accommodation","TestTenant"));
        return iEvents;
    }
    public List<IEvent> generateEventsForCASAS(OMElement featureConf) {
        List<IEvent> iEvents = new ArrayList<>();
        iEvents.add(new FeatureSelectedEvent("CaseHandling","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("CaseHandling.Throughput","1000","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("TourArrangement","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("TourArrangement.Throughput","1000","TestTenant"));
        iEvents.add( new FeatureSelectedEvent("EquipmentRental","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("EquipmentRental.Throughput","100", "TestTenant"));
        iEvents.add(new FeatureSelectedEvent("GroceryDelivery","TestTenant"));
        iEvents.add(new AttributedSelectedEvent("GroceryDelivery.Throughput","150","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("BikeRental","TestTenant"));
        iEvents.add(new FeatureSelectedEvent("TaxiHire","TestTenant"));
        return iEvents;
    }
}
