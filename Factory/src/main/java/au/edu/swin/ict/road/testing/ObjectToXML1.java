package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.*;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.stream.XMLStreamException;

/**
 * TODO
 */
public class ObjectToXML1 {

    public static void main(String[] args) throws Exception {
//        EventRecord mb = new EventRecord("ddd", new Classifier("sss","ff","dd","gg"),"mm","fgg");
        String xml = jaxbBased();
        System.out.println(xml);
        EventRecords eventRecords = jaxBEventRecord(xml);
        for (EventRecord eventRecord : eventRecords.getEventRecords()) {
            System.out.println(eventRecord.getEventId() + " >>>");
        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        XMLEncoder xmlEncoder = new XMLEncoder(baos);
//        xmlEncoder.writeObject(mb);
//        xmlEncoder.close();
//        String xml = baos.toString();
//        System.out.println(xml);
    }

    private static String jaxbBased() {
        EventRecord mb = new EventRecord("ddd", new Classifier("sss", "ff", "dd", "gg"), "mm", "fgg");
        EventRecord mb2 = new EventRecord("fff", new Classifier("vvvs", "3434", "343d", "g3434g"), "sc", "ggtg");
        EventRecords eventRecords = new EventRecords();
        eventRecords.addEventRecord(mb);
        eventRecords.addEventRecord(mb2);
        return JaxbFactory.toXml(eventRecords, new Class[]{EventRecords.class, EventRecord.class, Weight.class});
    }

    private static EventRecords jaxBEventRecord(String xml) {
        try {
            return JaxbFactory.toEventRecords(AXIOMUtil.stringToOM(xml), new Class[]{EventRecords.class, EventRecord.class});
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }
}
