import org.apache.axis2.AxisFault;

public interface EquipmentProviderProxy {
    public String rentEquipment(String equipmentRequirements, long avgTime) throws AxisFault;
}
