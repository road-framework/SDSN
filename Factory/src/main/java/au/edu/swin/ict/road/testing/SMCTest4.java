package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.demarshalling.CompositeDemarshaller;
import au.swin.ict.research.cs3.road.road4ws.message.MessagePusher;
import org.apache.axiom.om.*;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axis2.context.ConfigurationContextFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * TODO
 */
public class SMCTest4 {
    public static void main(String args[]) {
        try {
            MessagePusher messagePusher = new MessagePusher(
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem("sample\\confs\\axis2.xml"),
                    "AXIS2PushListener", false);
            CompositeDemarshaller dm = new CompositeDemarshaller();
            Composite composite = dm.demarshalSMC("src\\main\\resources\\overhead\\os1\\smc_target.xml");
            for (Role role : composite.getRoleMap().values()) {
                role.registerNewPushListener(messagePusher);
            }
            composite.setDefaultPushMessageListener(messagePusher);
            Thread compo = new Thread(composite);
            compo.start();
            Thread.sleep(1000);
//            for(int i = 0; i < 20; i ++) {
//                composite.getOrganiserRole().deployVSNAsXML(createVSN("overhead\\os10\\vsn.xml"));
//                composite.getOrganiserRole().removeVSN("HappyTours");
//            }
//            System.out.println("Done");
            Thread.sleep(1000);
            Thread tenant14 = new Thread(new Tenant("HappyTours", composite, 1, "MM"));
            tenant14.start();
//            Thread tenant11 = new Thread(new Tenant("EuroCars", composite, 2));
//            tenant11.start();
//            Thread tenant10 = new Thread(new Tenant("AnyTrucks", composite, 4));
//            tenant10.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static OMElement createVSN(String path) throws FileNotFoundException, XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement vsnConf = fac.createOMElement("vsnXML", fac.createOMNamespace("vsnXML", "q1"));
        XMLStreamReader parser = StAXUtils.createXMLStreamReader(new FileInputStream(path));
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(parser);
        vsnConf.addChild(builder.getDocumentElement());
        return vsnConf;
    }
}
