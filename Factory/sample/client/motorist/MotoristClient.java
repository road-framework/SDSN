package motorist;


import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;


/**
 * <?xml version='1.0' encoding='UTF-8'?>
 * <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
 * <soapenv:Body>
 * <ns1:complain xmlns:ns1="http://ws.apache.org/axis2">
 * <args0>0001</args0>
 * <args1>Its all smoke</args1>
 * </ns1:complain>
 * </soapenv:Body>
 * </soapenv:Envelope>
 *
 * @author Malinda
 */
public class MotoristClient {
    private static Logger log = Logger.getLogger(MotoristClient.class.getName());
    private static Rosassmc_memberStub clientStub = null;

    public static void main(String[] args1) {
        String epr = "http://localhost:8080/axis2/services/rosassmc_member/";
        log.info("Motorist client start");
        try {
            clientStub = new Rosassmc_memberStub(epr);
            //Set SOAP 1.1
            clientStub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
            clientStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Motorist client: Stub created");
        try {
            System.out.println("Sending complain to " + epr);
            String responseMsg = clientStub.complain("m001", "3122");
            System.out.println("Response Rcvd:" + responseMsg);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
