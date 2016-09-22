//package au.edu.swin.ict.road.testing;
//
//import au.edu.swin.ict.road.composite.Composite;
//import au.edu.swin.ict.road.composite.Role;
//import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
//import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
//import org.apache.axiom.om.*;
//import org.apache.axiom.om.util.StAXUtils;
//import org.apache.axis2.context.ConfigurationContextFactory;
//
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
///**
// * TODO
// */
//public class SMCTest3 {
//    public static void main(String args[]) {
//        try {
//            MessagePusher messagePusher = new MessagePusher(
//                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
//                    "AXIS2PushListener", false);
//            CompositeDemarshaller dm = new CompositeDemarshaller();
//            Composite composite =
//                    dm.demarshalSMC("evolution\\cs4\\smc_original.xml");
//            for (Role role : composite.getRoleMap().values()) {
//                role.registerNewPushListener(messagePusher);
//            }
//            composite.setDefaultPushMessageListener(messagePusher);
//            Thread compo = new Thread(composite);
//            compo.start();
//            Thread.sleep(1000);
////
////            composite.getOrganiserRole().deployVSNAsXML(createVSN("evolution\\legalassistence\\vsn_happytours.xml"));
////            composite.getOrganiserRole().deployVSNAsXML(createVSN("evolution\\legalassistence\\vsn_eurocars.xml"));
////            composite.getOrganiserRole().deployVSNAsXML(createVSN("evolution\\legalassistence\\vsn_anytrucs.xml"));
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
////            Thread tenant14 = new Thread(new Tenant("HappyTours", composite, 2));
////            tenant14.start();
////            Thread tenant11 = new Thread(new Tenant("EuroCars", composite, 3));
////            tenant11.start();
////            Thread tenant10 = new Thread(new Tenant("AnyTrucks", composite, 3));
////            tenant10.start();
////            Thread.sleep(1000);
////            long start = System.nanoTime();
//            composite.getOrganiserRole().enactOrganizationalManagementPolicy("firststep_per", "D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\cs4\\firststep_per.drl");
//            Thread.sleep(3000);
////            composite.getOrganiserRole().enactOrganizationalManagementPolicy("secondstep", "D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\cs10\\secondstep.drl");
////            Thread.sleep(3000);
//            composite.getOrganiserRole().enactOrganizationalManagementPolicy("add", "D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\cs4\\cs4-per-add.drl");
////            long stop = System.nanoTime();
////            System.out.println("Add : Management policy applied in ." + (stop - start) + "nanoseconds");
//
//            Thread.sleep(2000);
////            long start1 = System.nanoTime();
//            composite.getOrganiserRole().enactOrganizationalManagementPolicy("remove", "D:\\repos\\research\\projects\\paper3\\newROAD\\Factory\\evolution\\cs4\\cs4-per-remove.drl");
////            long stop1 = System.nanoTime();
////            System.out.println("Remove : Management policy applied in ." + (stop1 - start1) + "nanoseconds");
//
////            Thread.sleep(101);
////            Thread pe2 = new Thread(new PolicyExecutor(composite, "addtaxihire_opr", "addtaxihire_opr2.drl", false));
////            pe2.start();
////            Thread.sleep(1000);
//////
////            Thread pe2 = new Thread(new PolicyExecutor(composite, "accidenttow_opr", "accidenttow_opr.drl", false));
////            pe2.start();
//
////            Thread.sleep(1000);
////            Thread tenant11 = new Thread(new Tenant("AnyTrucks", composite, 2));
////            tenant11.start();
////            Thread tenant21 = new Thread(new Tenant("AnyTrucks1", composite, 5));
////            tenant21.start();
////            Thread tenant31 = new Thread(new Tenant("EuroCars", composite, 2));
////            tenant31.start();
//
////            Thread pe3 = new Thread(new PolicyExecutor(composite, "addhire_org2", "happytours_org_1.drl", true));
////            pe3.start();
////            Thread tenant12 = new Thread(new Tenant("HappyTours", composite, 1));
////            tenant12.start();
//////            Thread tenant22 = new Thread(new Tenant("AnyTrucks2", composite, 5));
//////            tenant22.start();
////            Thread tenant32 = new Thread(new Tenant("EuroCars", composite, 5));
////            tenant32.start();
////            Thread tenant13 = new Thread(new Tenant("HappyTours", composite, 2));
////            tenant13.start();
////            Thread tenant23 = new Thread(new Tenant("AnyTrucks", composite, 3, "Driver1", "My truck ( TCModel 1244) has broken down", "Camberwell"));
////            tenant23.start();
////            Thread tenant20 = new Thread(new Tenant("EuroCars", composite, 3, "Driver1", "My truck ( TCModel 1244) has broken down", "Camberwell"));
////            tenant20.start();
////            Thread tenant25 = new Thread(new Tenant("HappyTours", composite, 3, "Driver1", "My truck ( TCModel 1244) has broken down", "Camberwell"));
////            tenant25.start();
////            Thread tenant21 = new Thread(new Tenant("AnyTrucks", composite, 1, "Driver2", "My truck ( TCModel 4569) has involved in an accident", "Melbourne"));
////            tenant21.start();
////            Thread tenant24 = new Thread(new Tenant("AnyTrucks", composite, 2, "Driver3", "My truck ( TCModel 9000) cannot be moved", "Doncaster"));
////            tenant24.start();
////            Thread tenant24 = new Thread(new Tenant("AnyTrucks", composite, 1, "Drive1", "My truck ( TCModel 1244) has broken down", "Camberwell"));
////            tenant24.start();
////            Thread tenant25 = new Thread(new Tenant("AnyTrucks", composite, 1, "Drive1", "My truck ( TCModel 1244) has broken down", "Camberwell"));
////            tenant23.start();
////            Thread tenant33 = new Thread(new Tenant("EuroCars", composite, 5));
////            tenant33.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static OMElement createVSN(String path) throws FileNotFoundException, XMLStreamException {
//        OMFactory fac = OMAbstractFactory.getOMFactory();
//        OMElement vsnConf = fac.createOMElement("vsnXML", fac.createOMNamespace("vsnXML", "q1"));
//        XMLStreamReader parser = StAXUtils.createXMLStreamReader(new FileInputStream(path));
//        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(parser);
//        vsnConf.addChild(builder.getDocumentElement());
//        return vsnConf;
//    }
//}
