package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
import org.apache.axis2.context.ConfigurationContextFactory;

/**
 * TODO
 */
public class SMCTest7 {
    public static void main(String args[]) {
        try {
            MessagePusher messagePusher = new MessagePusher(
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
                    "AXIS2PushListener", false);
            CompositeDemarshaller dm = new CompositeDemarshaller();
            Composite composite = dm.demarshalSMC("src\\main\\resources\\campsass\\smc_original.xml");
            for (Role role : composite.getRoleMap().values()) {
                role.registerNewPushListener(messagePusher);
            }
            composite.setDefaultPushMessageListener(messagePusher);
            Thread compo = new Thread(composite);
            compo.start();
            Thread.sleep(5000);
            Thread tenant14 = new Thread(new Tenant("HappyTours", composite, 2, "CM"));
            tenant14.start();
            Thread tenant11 = new Thread(new Tenant("UniUvtClub", composite, 2, "CM"));
            tenant11.start();
            Thread tenant10 = new Thread(new Tenant("SunCampsites", composite, 7, "CM"));
            tenant10.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
