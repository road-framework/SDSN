package usdl;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import usdl.Usdl_sc1Stub.GetCapabilityReturn;

import java.rmi.RemoteException;


public class C1Client {
    private static Logger log = Logger.getLogger(C1Client.class.getName());
    private String epr = null;
    private Usdl_sc1Stub stub = null;

    public C1Client(String epr) throws AxisFault {
        this.epr = epr;
        this.stub = new Usdl_sc1Stub(epr);
        this.stub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        this.stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
    }

    public String getCapability(String serviceId) throws Exception {
        //e.g., ref = _2eIgUML7EeCZVJ07JoM73A
        String capabilityDetails = null;

        if (USDLUtil.TESTING) {//For testing.
            Thread.sleep(4000);
            USDLUtil.svc1_invoke_count++;
            USDLUtil.sc1_invoke_count++;
            return USDLUtil.displayebleCapabilityInfo();
        }

        try {

            GetCapabilityReturn capability = stub.getServiceCapabilityAnon(USDLUtil.anonUserName, serviceId);

            StringBuffer sb = new StringBuffer();
            sb.append("\nTechnical Interface Details");
            sb.append("\n===============================");
            sb.append("\n Name:\t" + capability.getName());
            sb.append("\n Reference:\t" + capability.getRaasRef());

            capabilityDetails = sb.toString();
        } catch (AxisFault e) {
            e.printStackTrace();
            throw new Exception("Problem conntecting to service " + epr);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new Exception("Cannot invoke  getCapabilityAnon in " + epr);
        }
        return capabilityDetails;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String epr = "http://localhost:7070/axis2/services/usdl_sc1";
        C1Client c1 = new C1Client(epr);
        log.debug(c1.getCapability("_lWH2EM05EeCZVJ07JoM73A"));
    }

}
