import org.apache.axis2.AxisFault;

public interface GroceryProviderProxy {
    public String orderGroceries(String groceries, long avgTime) throws AxisFault;
}
