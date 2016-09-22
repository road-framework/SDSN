package workload;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.testing.TestMessageContextBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;

/**
 * TODO documentation
 */
public class UserCall implements Runnable {

    private String tenantName;
    private TenantStat tenantStat;
    private Composite composite;

    public UserCall(String tenantName, TenantStat tenantStat, Composite composite) {
        this.tenantName = tenantName;
        this.tenantStat = tenantStat;
        this.composite = composite;
    }

    @Override
    public void run() {
        try {
            Role role = (Role) composite.getRoleByID("MM");
            MessageWrapper mw = new MessageWrapper();
            SOAPEnvelope envelope = TestMessageContextBuilder.build("\n" +
                    "                <q0:requestAssistRequest xmlns:q0=\"http://ws.apache.org/axis2\">\n" +
                    "\t\t\t\t\t<q0:memId>T1</q0:memId>\n" +
                    "\t\t\t\t\t<q0:complain>G1</q0:complain>\n" +
                    "\t\t\t\t\t<q0:pickUpLocation>G1</q0:pickUpLocation>\n" +
                    "\t\t\t\t</q0:requestAssistRequest>");
            mw.setMessage(envelope);
            mw.setOperationName("requestAssistRequest");
            mw.setOriginRole(role);
            mw.setClientID(UIDGenerator.generateURNString());
            String roleID = mw.getOriginRoleId();
            Classifier classifier = new Classifier();
            classifier.setVsnId(tenantName);
            classifier.setProcessRole(roleID);
            mw.setClassifier(classifier);

            MessageWrapper result = role.putSyncMessage(mw);
            if (result != null) {
                SOAPEnvelope envelope1 = (SOAPEnvelope) result.getMessage();
                if (envelope1.hasFault()) {
                    tenantStat.increaseFailures();
                } else {
                    tenantStat.increaseCompleted();
                }
            }
        } catch (AxisFault axisFault) {
            tenantStat.increaseFailures();
        } catch (Exception e) {
            tenantStat.increaseFailures();
        }
    }
}
