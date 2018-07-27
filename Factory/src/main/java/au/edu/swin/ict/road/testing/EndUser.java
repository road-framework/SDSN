package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.util.UIDGenerator;

/**
 * TODO documentation
 */

public class EndUser implements Runnable {
    private String tenantName;
    private int retry = 0;
    private String name;
    private String complain;
    private String pickUplocation;
    private String userRole = "MM";
    private Composite composite;

    public EndUser(String tenantName, Composite composite) {
        this.tenantName = tenantName;
        this.composite = composite;
    }

    public EndUser(String tenantName, Composite composite, String name, String complain, String pickUplocation) {
        this.complain = complain;
        this.pickUplocation = pickUplocation;
        this.composite = composite;
        this.name = name;
        this.tenantName = tenantName;
    }

    public EndUser(String tenantName, Composite composite, String name, String complain, String pickUplocation, String userRole) {
        this.tenantName = tenantName;
        this.name = name;
        this.complain = complain;
        this.pickUplocation = pickUplocation;
        this.userRole = userRole;
        this.composite = composite;
    }

    public EndUser(String tenantName, Composite composite, String userRole) {
        this.composite = composite;
        this.userRole = userRole;
        this.tenantName = tenantName;
    }

    @Override
    public void run() {
        try {
            Role role = (Role) composite.getRoleByID(userRole);
            if (role == null) {
                System.out.println("There is no role with name : " + userRole);
            } else {
                MessageWrapper mw = new MessageWrapper();
                SOAPEnvelope envelope;
                switch (userRole) {
                    case "MM":
                        envelope = TestMessageContextBuilder.build("\n" +
                                "                <q0:requestAssist xmlns:q0=\"http://ws.apache.org/axis2\">\n" +
                                "\t\t\t\t\t<q0:memId>" + name + " </q0:memId>\n" +
                                "\t\t\t\t\t<q0:complain>" + complain + "</q0:complain>\n" +
                                "\t\t\t\t\t<q0:pickUpLocation>" + pickUplocation + "</q0:pickUpLocation>\n" +
                                "\t\t\t\t</q0:requestAssist>");
                        mw.setOperationName("requestAssist");
                        break;
                    case "CM":
                        envelope = TestMessageContextBuilder.build("\n" +
                                "                <q0:requestAssist xmlns:q0=\"http://ws.apache.org/axis2\">\n" +
                                "\t\t\t\t\t<q0:memId>" + name + " </q0:memId>\n" +
                                "\t\t\t\t\t<q0:complain>" + complain + "</q0:complain>\n" +
                                "\t\t\t\t</q0:requestAssist>");
                        mw.setOperationName("requestAssist");
                        break;
                    default:
                        envelope = TestMessageContextBuilder.build("\n" +
                                "<q0:echo xmlns:q0=\"http://ws.apache.org/axis2\">\n" +
                                "\t\t\t\t\t<q0:content>" + name + " </q0:content>\n" +
                                "\t\t\t\t</q0:echo>");
                        mw.setOperationName("echo");
                        break;
                }

                mw.setMessage(envelope);
                mw.setOriginRole(role);
                mw.setClientID(UIDGenerator.generateURNString());
                mw.setTargetVSN(tenantName);

                long startTime = System.nanoTime();

                MessageWrapper result = role.putSyncMessage(mw);
                if (result != null) {

                    SOAPEnvelope envelope1 = (SOAPEnvelope) result.getMessage();

                    long endTime = System.nanoTime();

                    if (envelope1.hasFault()) {
//                    System.out.println("[Tenant : " + tenantName + " ] [User : " + name + " ] [Request Message  " + envelope.getBody() + " ]" + "[ Process : " +
//                                       result.getClassifier().getProcessInsId() + " ] " +
//                                       "[ Status : FAILURE ] [Reason  " + envelope1.getBody() + " ]");
                        System.out.println("[Tenant : " + tenantName + " ] " + "[ Process : " +
                                result.getClassifier().getProcessInsId() + " ] " +
                                "[ Status : FAILURE ] [Reason  " + ((SOAPFault) envelope1.getBody().getFirstElement()).getReason().getText() + " ]");
                        if (retry == 0) {
                            Thread.sleep(1000 * 60);
                            retry++;
                            run();
                        }
                    } else {
//                    System.out.println("[Tenant : " + tenantName + " ] [User : " + name + " ] [Request Message  " + envelope.getBody() + " ]" + "[ Process : " +
//                                       result.getClassifier().getProcessInsId() + " ] " +
//                                       "[ Status : SUCCESS  ] [Response Message  " + envelope1.getBody() + " ] [Response Time : " + (endTime - startTime) + " ms]");
                        System.out.println("[Tenant : " + tenantName + " ] " + "[ Process : " +
                                result.getClassifier().getProcessInsId() + " ] " +
                                "[ Status : SUCCESS  ] [Response Message  " + ((OMElementImpl) envelope1.getBody().getFirstElement().getFirstOMChild()).getText() + " ] [Response Time : " + (endTime - startTime) / 1000000 + " ms]");

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
