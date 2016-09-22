package usdl;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import usdl.Usdl_sc2Stub.*;

import java.rmi.RemoteException;


public class C2Client {
    private static Logger log = Logger.getLogger(C2Client.class.getName());
    String epr = null;
    Usdl_sc2Stub stub = null;

    public C2Client(String epr) throws AxisFault {
        this.epr = epr;
        this.stub = new Usdl_sc2Stub(epr);
        this.stub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        this.stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
    }

    public String getServiceDetails(String userId, String serviceId) throws RemoteException {


        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            USDLUtil.svc1_invoke_count++;
            USDLUtil.sc2_invoke_count++;
            return USDLUtil.displayebleServiceInfo();
        }

        GetServiceReturn service = this.stub.getServiceUser(userId, serviceId);
        //Milk it to get the details and construct service details
        StringBuffer sb = new StringBuffer();
        sb.append("\n SERVICE DETAILS");
        sb.append("\n -------------------------------\n");
        sb.append("\n Service Id: \t" + service.getRaasRef());
        sb.append("\n Provider: \t" + service.getProvider());
        sb.append("\n Version:\t" + service.getVersion());
        sb.append("\n Nature:\t" + service.getNature());
        sb.append("\n PubDate:\t" + service.getPublicationTime());
        sb.append("\n Technical Interface Type:\t" + service.getTechnicalInterfaces().getType());
        sb.append("\n Technical Interface Ref:\t" + service.getTechnicalInterfaces().getRaasRef());
        sb.append("\n Technical Interface Ref:\t" + service.getCapabilities().getName());

        return sb.toString();
    }

    public String getStatistics(String userId) throws RemoteException {

        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return USDLUtil.displayStatisticsFor("SC2");
        }

        StringBuffer statsBuf = new StringBuffer();
        statsBuf.append("\n User Invocation Stats.");
        statsBuf.append("\n=======================");
        Fact f = this.stub.getUserInvocationStatFact(userId);

        String idKey = f.getIdentifier().getIdentifierKey();
        String idVal = f.getIdentifier().getIdentifierValue();
        statsBuf.append("\n" + idKey + "=" + idVal);
        FactAttributes factAttribs = f.getAttributes();
        FactAttribute[] factAttribArr = factAttribs.getAttribute();
        for (FactAttribute fa : factAttribArr) {
            String attKey = fa.getAttributeKey();
            String attVal = fa.getAttributeValue();
            statsBuf.append("\n" + attKey + "=" + attVal);
        }


        return statsBuf.toString();
    }

    /**
     * @param userId
     * @param serviceId
     * @return
     * @throws Exception
     */
    public String getTechnicalInterface(String userId, String serviceId) throws Exception {
        //e.g., ref = _2eIgUML7EeCZVJ07JoM73A
        String serviceDetails = null;
        //TODO invoke proxy
        try {

            GetTechnicalInterfaceReturn ti = stub.getServiceTechnicalInterface(userId, serviceId);

            StringBuffer sb = new StringBuffer();
            sb.append("\nTechnical Interface Details");
            sb.append("\n===============================");
            sb.append("\nType:\t" + ti.getType());
            sb.append("\nRef:\t" + ti.getRaasRef());

            TechnicalOperations top = ti.getTechnicalOperations();
            sb.append("Op:\t" + top.getName());

            serviceDetails = sb.toString();

        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new Exception("Problem conntecting to service " + epr);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new Exception("Cannot invoke  getTechnicalInterface in " + epr);
        }

        return serviceDetails;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String epr = "http://localhost:7070/axis2/services/usdl_sc2";
        C2Client c2 = new C2Client(epr);
        String output = c2.getServiceDetails("sc2user_1", "_lWH2EM05EeCZVJ07JoM73A");
        log.debug(output);

    }

}
