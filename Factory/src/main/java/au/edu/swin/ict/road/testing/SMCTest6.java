package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
import org.apache.axis2.context.ConfigurationContextFactory;

/**
 * TODO
 */
public class SMCTest6 {
    public static void main(String args[]) {
        try {

            SNUtils.addVSNToSN("src\\main\\resources\\roadside_ode\\smc_original.xml",
                    "src\\main\\resources\\roadside_ode\\HappyTours.xml","HappyTours",0);

            SNUtils.addVSNToSN("src\\main\\resources\\roadside_ode\\smc_original.xml",
                    "src\\main\\resources\\roadside_ode\\EuroCars.xml","EuroCars",0);

            SNUtils.addVSNToSN("src\\main\\resources\\roadside_ode\\smc_original.xml",
                    "src\\main\\resources\\roadside_ode\\AnyTrucks.xml","AnyTrucks",0);

            MessagePusher messagePusher = new MessagePusher(
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
                    "AXIS2PushListener", false);
            CompositeDemarshaller dm = new CompositeDemarshaller();
            Composite composite = dm.demarshalSMC("src\\main\\resources\\roadside_ode\\smc_original.xml");
            for (Role role : composite.getRoleMap().values()) {
                role.registerNewPushListener(messagePusher);
            }
            composite.setDefaultPushMessageListener(messagePusher);
            Thread compo = new Thread(composite);
            compo.start();
            Thread.sleep(5000);
//            for(int i = 0; i < 20; i ++) {
//                composite.getOrganiserRole().deployVSNAsXML(createVSN("overhead\\os10\\vsn.xml"));
//                composite.getOrganiserRole().removeVSN("HappyTours");
//            }
//            System.out.println("Done");
//            Thread.sleep(1000);
            Thread tenant14 = new Thread(new Tenant("HappyTours0", composite, 2, "MM"));
            tenant14.start();
            Thread tenant11 = new Thread(new Tenant("EuroCars0", composite, 2));
            tenant11.start();
            Thread tenant10 = new Thread(new Tenant("AnyTrucks0", composite, 4));
            tenant10.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
