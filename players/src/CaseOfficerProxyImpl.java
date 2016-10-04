import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class CaseOfficerProxyImpl implements CaseOfficerProxy {
    @Override
    public AnalyzeReturn analyze(String memId, String complainDetails, long avgTime) throws AxisFault {
        sleep(avgTime);
        AnalyzeReturn order = new AnalyzeReturn();
        order.setOrderTow("OrderTowRequest");
        order.setOrderRepair("OrderRepairRequest");
        order.setOrderRoom("OrderRoomRequest");
        order.setOrderVehicle("OrderVehicleRequest");
        order.setOrderLegalAid("OrderLegalAidRequest");
        order.setOrderTaxi("OrderTaxRequest");
        return order;
    }

    @Override
    public String payTC(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayTowResponse";
    }

    @Override
    public String payGC(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayRepairResponse";
    }

    @Override
    public String payPS(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayPartsResponse";
    }

    @Override
    public String payVC(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayVehicleRentResponse";
    }

    @Override
    public String payHC(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayHotelRentResponse";
    }

    @Override
    public String payTX(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayTaxiHireResponse";
    }

    @Override
    public String payLF(String content, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "PayLawFirmResponse";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
