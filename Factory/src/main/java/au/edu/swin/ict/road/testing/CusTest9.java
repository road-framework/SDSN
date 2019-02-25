//package au.edu.swin.ict.road.testing;
//
//import au.edu.swin.ict.road.composite.Composite;
//import au.edu.swin.ict.road.composite.Role;
//import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
//import au.swin.ict.research.cs3.road.road4ws.core.util.ControlPlaneAPI;
//import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
//import org.apache.axis2.context.ConfigurationContextFactory;
//
///**
// * TODO
// */
//public class CusTest9 {
//    public static void main(String args[]) {
//        try {
//            MessagePusher messagePusher = new MessagePusher(
//                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
//                    "AXIS2PushListener", false);
//            CompositeDemarshaller dm = new CompositeDemarshaller();
//            Composite composite = dm.demarshalSMC("src\\main\\resources\\campsaas_oh\\smc_target.xml");
//            for (Role role : composite.getRoleMap().values()) {
//                role.registerNewPushListener(messagePusher);
//            }
//            composite.setDefaultPushMessageListener(messagePusher);
//            Thread compo = new Thread(composite);
//            compo.start();
//            Thread.sleep(5000);
//            ControlPlaneAPI controlPlaneAPI = new ControlPlaneAPI(
//                    composite.getOperationalManagerRole(),composite.getOrganiserRole(),composite.getRulesDir());
//            controlPlaneAPI.customizeB(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
