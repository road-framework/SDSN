//package au.edu.swin.ict.road.testing;
//
//import au.edu.swin.ict.road.composite.Composite;
//import au.edu.swin.ict.road.composite.Role;
//import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
//import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
//import org.apache.axis2.context.ConfigurationContextFactory;
//
///**
// * TODO documentation
// */
//public class SMCTest {
//
//    public static void main(String args[]) {
//        try {
////            ClassPathHack classPathHack = new ClassPathHack();
////            classPathHack.addFile("D:\\temp-1\\testmech.jar");
////            CustomActions customActions = new CustomActions();
////            System.out.println(customActions.execute("testmech.RoundRobin", "AusTa,IndF", "ss"));
//            MessagePusher messagePusher = new MessagePusher(
//                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
//                    "AXIS2PushListener", false);
//            CompositeDemarshaller dm = new CompositeDemarshaller();
//            Composite composite =
//                    dm.demarshalSMC("evolution\\taxihire\\smc_original.xml");
//            for (Role role : composite.getRoleMap().values()) {
//                role.registerNewPushListener(messagePusher);
//            }
//            composite.setDefaultPushMessageListener(messagePusher);
//            Thread compo = new Thread(composite);
//            compo.start();
//            Thread.sleep(1000);
//
////            OMFactory fac = OMAbstractFactory.getOMFactory();
////            OMElement omElement = fac.createOMElement("rule", fac.createOMNamespace("rulefile", "q1"));
//////            DataHandler dataHandler = new DataHandler(new FileDataSource("evolution\\addTaxiHire\\data\\rules\\mgtpolicies\\statetestv1.drl"));
////            DataHandler dataHandler = new DataHandler(new FileDataSource("evolution\\addTaxiHire\\data\\rules\\mgtpolicies\\addtaxihire.drl"));
////
////            OMText textData = fac.createOMText(dataHandler, true);
////
////            omElement.addChild(textData);
////            Thread pe1 = new Thread(new PolicyExecutor(composite, "addhire_org1", "statetestv1.drl", true));
////            pe1.start();
//            Thread tenant33 = new Thread(new Tenant("EuroCars", composite, 4));
//            tenant33.start();
//            Thread.sleep(100);
//            Thread pe1 = new Thread(new PolicyExecutor(composite, "addhire_org", "addtaxihire_org.drl", true));
//            pe1.start();
//
//            Thread pe2 = new Thread(new PolicyExecutor(composite, "addhire_opr", "addtaxihire_opr.drl", false));
//            pe2.start();
//
//            Thread.sleep(100);
////            Thread tenant11 = new Thread(new Tenant("AnyTrucks", composite, 2));
////            tenant11.start();
////            Thread tenant21 = new Thread(new Tenant("AnyTrucks1", composite, 5));
////            tenant21.start();
//            Thread tenant31 = new Thread(new Tenant("EuroCars", composite, 2));
//            tenant31.start();
//
////            Thread pe3 = new Thread(new PolicyExecutor(composite, "addhire_org2", "happytours_org_1.drl", true));
////            pe3.start();
//////            Thread tenant12 = new Thread(new Tenant("HappyTours2", composite, 5));
//////            tenant12.start();f
//////            Thread tenant22 = new Thread(new Tenant("AnyTrucks2", composite, 5));
//////            tenant22.start();
//////            Thread tenant32 = new Thread(new Tenant("EuroCars2", composite, 5));
//////            tenant32.start();
////            Thread tenant13 = new Thread(new Tenant("HappyTours", composite, 2));
////            tenant13.start();
////            Thread tenant23 = new Thread(new Tenant("AnyTrucks3", composite, 5));
////            tenant23.start();
////            Thread tenant33 = new Thread(new Tenant("EuroCars3", composite, 5));
////            tenant33.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
