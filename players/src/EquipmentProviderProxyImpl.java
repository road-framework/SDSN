import org.apache.axis2.AxisFault;

public class EquipmentProviderProxyImpl implements EquipmentProviderProxy {
    @Override
    public String rentEquipment(String equipmentRequirements, long avgTime) throws AxisFault {
        sleep(avgTime);
        return "rentedEquipment";
    }

    private void sleep(long avgTime) {
        try {
            Thread.sleep(avgTime);
        } catch (InterruptedException ignored) {
        }
    }
}
