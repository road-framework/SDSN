package usdl;


import org.apache.log4j.Logger;
import usdl.Usdl_sp1Stub.Fact;
import usdl.Usdl_sp1Stub.FactAttribute;
import usdl.Usdl_sp1Stub.FactAttributes;
import usdl.Usdl_sp1Stub.GetServiceReturn;

import java.rmi.RemoteException;


public class SP1Client {
    private static Logger log = Logger.getLogger(SP1Client.class.getName());
    public static final String errorHead = "Service creation failed";

    private String epr = null;
    private Usdl_sp1Stub stub = null;

    public SP1Client(String epr) throws Exception {
        this.epr = epr;
        this.stub = new Usdl_sp1Stub(epr);
        this.stub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        this.stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
    }

    public String doAllAndServiceDetails(String userId, String guid, String name, String version, String nature, String capabilityName, String capabilityActionName, String tiName, String tiType) throws Exception {
        String serviceDetails = null;
        String errorMsg = "\nError:Service creation has been blocked for  " + userId +
                "\n Monthly fee has not been paid. " +
                "\n Please contact the administrator";
        if (USDLUtil.TESTING) {//For testing.
            Thread.sleep(6000);

            //is this blocked
            if (USDLUtil.isSp1Blocked) {
                return errorMsg;
            }

            USDLUtil.sp1_invoke_count++;
            return USDLUtil.displayebleServiceInfo();
        }
        //1. Create Provider
        String providerId = createProvider(userId, guid);
        if (null == providerId) {
            throw new Exception(errorHead + "Cannot get the provider details. ");//later replace this with the proper error message from the AxisFault
        }
        //2. Create service
        String serviceId = createService(userId, guid, name, providerId, version, nature);
        if (null == serviceId) {
            throw new Exception(errorHead + "Cannot create the service" + name);
        }

        //3. Create Technical Interface [Optional]
        this.createAndSetCapability(userId, serviceId, capabilityName, capabilityActionName);

        //4. Create Capability [Optional]
        this.createAndSetTechnicalInterface(userId, serviceId, tiName, tiType);

        //5. Get full service details
        serviceDetails = getServiceDetails(userId, serviceId);

        return serviceDetails;
    }


    public String createProvider(String userId, String guid) throws Exception {
        String providerId = null;

        providerId = this.stub.createProvider(userId, guid);
        log.debug("Create provider invoked");

        return providerId;
    }

    public String createAndSetCapability(String userId, String serviceId, String capabilityName, String action_name) throws Exception {
        String capabilityId = this.stub.createCapability(userId, capabilityName, action_name);
        if (null == capabilityId) {
            throw new Exception("Cannot create the capability");
        }
        //Use capability id and the service id to set the capability to the service
        return this.stub.setcapabilities(userId, serviceId, capabilityId);
    }

    public String createAndSetTechnicalInterface(String userId, String serviceId, String tiname, String type) throws Exception {
        String techIntId = this.stub.createTechnicalInterface(userId, tiname, type);
        if (null == techIntId) {
            throw new Exception("Cannot create the technical interface");
        }
        //Use capability id and the service id to set the capability to the service
        return this.stub.settechnicalInterfaces(userId, serviceId, techIntId);
    }

    public String createService(String userId, String guid, String name, String providerId, String version, String nature) throws Exception {
        String serviceId = null;

        //TODO invoke the proxy
        String publicationTime = USDLUtil.getCurrentTime();
        //serviceId = this.stub.createService(guid);//TODO, we need to support other params.
        serviceId = this.stub.createService(userId, guid, name, providerId, publicationTime, version, nature);
        log.debug("Create service invoked " + serviceId);
        return serviceId;
    }


    public String getServiceDetails(String userId, String serviceId) throws Exception {

        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return USDLUtil.displayebleServiceInfo();
        }

        GetServiceReturn service = this.stub.getService(userId, serviceId);
        log.debug("Get service invoked");


        //Milk it to get the details and construct service details
        StringBuffer sb = new StringBuffer();
        sb.append("\n SERVICE DETAILS");
        sb.append("\n -------------------------------\n");
        sb.append("\n Service Id: \t" + service.getRaasRef());
        sb.append("\n Provider: \t" + service.getProvider());
        sb.append("\n Version:\t" + service.getVersion());
        sb.append("\n Nature:\t" + service.getNature());
        sb.append("\n PubDate:\t" + service.getPublicationTime());
        sb.append("\n Technical-Interface Type:\t" + service.getTechnicalInterfaces().getType());
        sb.append("\n Technical-Interface Ref:\t" + service.getTechnicalInterfaces().getRaasRef());
        sb.append("\n Capability Ref:\t" + service.getCapabilities().getName());

        return sb.toString();
    }

    public String getStatisticsDetails(String svcId) throws RemoteException {

        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return USDLUtil.displayStatisticsFor("SP1");
        }

        StringBuffer statsBuf = new StringBuffer();
        statsBuf.append("\n Service Invocation Stats.");
        statsBuf.append("\n=======================");
        Fact f = this.stub.getServiceInvocationStatFact(svcId);

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

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String epr = "http://localhost:7070/axis2/services/usdl_sp1";
        SP1Client sp1 = new SP1Client(epr);
        String details = sp1.doAllAndServiceDetails("spuser_1", "Swinburne", "Swinburne", "1.0", "AUTOMATED", "someCapbility", "someCapbilityAction", "admission", "http://schemas.xmlsoap.org/wsdl/");
        log.debug(details);
    }

}
