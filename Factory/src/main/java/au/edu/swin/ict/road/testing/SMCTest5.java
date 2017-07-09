package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axis2.context.ConfigurationContextFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * TODO documentation
 */
public class SMCTest5 {

    public static void main(String args[]) {
        try {
            MessagePusher messagePusher = new MessagePusher(
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
                    "AXIS2PushListener", false);
            CompositeDemarshaller dm = new CompositeDemarshaller();
            Composite composite = dm.demarshalSMC("src\\main\\resources\\management\\ms3\\smc_source.xml");
            for (Role role : composite.getRoleMap().values()) {
                role.registerNewPushListener(messagePusher);
            }
            composite.setDefaultPushMessageListener(messagePusher);
            Thread compo = new Thread(composite);
            compo.start();
            Thread.sleep(1000);
            Thread pe2 = new Thread(new PolicyExecutor(composite, "es3_org", "es3_org_add.drl", true));
            pe2.start();
            Thread pe3 = new Thread(new PolicyExecutor(composite, "es3_opr", "es3_opr_add.drl", false));
            pe3.start();
            Thread.sleep(5000);
            System.out.println("Management policy applied nanoseconds");
            Thread tenant14 = new Thread(new Tenant("HappyTours", composite, 1, "MM"));
            tenant14.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
