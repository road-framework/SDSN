package usdl;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import usdl.Usdl_monStub.Fact;
import usdl.Usdl_monStub.FactAttribute;
import usdl.Usdl_monStub.FactAttributes;
import usdl.Usdl_monStub.Facts;

import java.rmi.RemoteException;

public class MonitorClient extends Thread {
    private static Logger log = Logger.getLogger(MonitorClient.class.getName());
    private String epr = null;
    Usdl_monStub stub = null;

    public MonitorClient(String epr) throws AxisFault {
        this.epr = epr;
        this.stub = new Usdl_monStub(epr);
        this.stub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        this.stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
    }

    public String getStats() throws RemoteException {
        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return USDLUtil.displayStatisticsFor("All");
        }
        StringBuffer statsBuf = new StringBuffer();

        statsBuf.append("\n User Invocation Stats.");
        statsBuf.append("\n=======================");
        {//User
            Facts facts = this.stub.getAllUserInvocationStatFacts();
            Fact[] factArr = facts.getFact();
            for (Fact f : factArr) {
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
            }
        }

        {//Service
            statsBuf.append("\n\n Service Invocation Stats.");
            statsBuf.append("\n=======================");
            Facts facts = this.stub.getAllServiceInvocationStatFacts();
            Fact[] factArr = facts.getFact();
            for (Fact f : factArr) {
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
            }
        }

        return statsBuf.toString();
    }

    /**
     * @param args
     * @throws RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        // TODO Auto-generated method stub
        String epr = "http://localhost:7070/axis2/services/usdl_mon";
        MonitorClient mc = new MonitorClient(epr);
        log.debug(mc.getStats());

    }

}
