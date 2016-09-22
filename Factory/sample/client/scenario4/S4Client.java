package scenario4;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Malinda
 */
public class S4Client {

    static final long timeout = 700000;

    public static void main(String[] args1) {

        testSerendip();
    }


    public static void testSerendip() {
        String epr = "http://localhost:8080/axis2/services/testsmc_member";//136.186.6.148
        FileWriter writer = null;
        try {
            OMElement payload = createOrder();
            EndpointReference targetEPR = new EndpointReference(epr);
            Options options = new Options();

            options.setTo(targetEPR); // this sets the location of MyService
            // service
            options.setTimeOutInMilliSeconds(timeout);
            options.setAction("complain");
            //options.setProperty(Constants.Configuration.DISABLE_ADDRESSING_FOR_OUT_MESSAGES,true);
            ServiceClient serviceClient = new ServiceClient();
            serviceClient.setOptions(options);


            System.out.println("Sending message :" + payload.toString());

            writer = new FileWriter(System.getenv("AXIS2_HOME") + System.getProperty("file.separator") + "road_client.csv");
            writer.append("Step,Begin,End,Duration\n");


            for (int i = 0; i < 100; i++) {

                long start = System.currentTimeMillis();
                OMElement response = serviceClient.sendReceive(payload);
                long stop = System.currentTimeMillis();
                writer.append(i + "," + start + "," + stop + "," + (stop - start) + "\n");
                System.out.println("#" + i + ": Time elapsed :" + (stop - start));
//			    System.out.println("Got message :"+response.toString());
            }

//		    Thread.sleep(timeout);


        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    private static OMElement createOrder() {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns1");
        OMElement method = fac.createOMElement("complain", omNs);

        //parameter
        OMElement foodId = fac.createOMElement("content", omNs);

        //parameter value
        foodId.addChild(fac.createOMText(foodId, "location: -37.751172,145.69519 "));

        method.addChild(foodId);

        return method;
    }

}
