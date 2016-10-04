import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public interface HotelServiceProxy {

    public String rentRooms(String content, long avgTime) throws AxisFault;
}
