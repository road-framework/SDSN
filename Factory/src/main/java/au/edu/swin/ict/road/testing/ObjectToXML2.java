package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.common.JaxbFactory;
import au.edu.swin.ict.road.common.StateRecord;
import au.edu.swin.ict.road.common.StateRecords;
import au.edu.swin.ict.road.common.Weight;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.stream.XMLStreamException;

/**
 * TODO
 */
public class ObjectToXML2 {

    public static void main(String[] args) throws Exception {
//        EventRecord mb = new EventRecord("ddd", new Classifier("sss","ff","dd","gg"),"mm","fgg");
        String xml = jaxbBased();
        System.out.println(xml);
        StateRecords stateRecords = jaxBEventRecords(xml);
        for (StateRecord stateRecord : stateRecords.getStateRecords()) {
            System.out.println(((Weight) stateRecord.getStateInstance()).getWeight());
        }
//        StateRecord stateRecord = jaxBEventRecord(xml);
//        System.out.println(((Weight) stateRecord.getStateInstance()).getWeight());
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        XMLEncoder xmlEncoder = new XMLEncoder(baos);
//        xmlEncoder.writeObject(mb);
//        xmlEncoder.close();
//        String xml = baos.toString();
//        System.out.println(xml);
    }

    private static String jaxbBased() {

        StateRecord mb = new StateRecord("ddd", new Weight(10));
        mb.getMgtState().setState("active");
        StateRecord mb2 = new StateRecord("fdfdf", new Weight(14550));
        mb2.getMgtState().setState("passive");
        StateRecords stateRecords = new StateRecords();
        stateRecords.addStateRecord(mb);
        stateRecords.addStateRecord(mb2);
        return JaxbFactory.toXml(stateRecords, new Class[]{StateRecords.class, StateRecord.class, Weight.class});
    }

    private static StateRecord jaxBEventRecord(String xml) throws XMLStreamException {
        return JaxbFactory.toStateRecord(AXIOMUtil.stringToOM(xml), new Class[]{StateRecord.class, Weight.class});
//        try {
//
//            JAXBContext jaxbContext = JAXBContext.newInstance(StateRecord.class, Weight.class);
//            ByteArrayInputStream file = new ByteArrayInputStream(xml.getBytes());
//
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            StateRecord stateRecord = (StateRecord) jaxbUnmarshaller.unmarshal(file);
//            System.out.println(((Weight) stateRecord.getStateInstance()).getWeight());
//            return stateRecord;
//
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private static StateRecords jaxBEventRecords(String xml) throws XMLStreamException {
        return JaxbFactory.toStateRecords(AXIOMUtil.stringToOM(xml), new Class[]{StateRecords.class, StateRecord.class, Weight.class});
//        try {
//
//            JAXBContext jaxbContext = JAXBContext.newInstance(StateRecord.class, Weight.class);
//            ByteArrayInputStream file = new ByteArrayInputStream(xml.getBytes());
//
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//            StateRecord stateRecord = (StateRecord) jaxbUnmarshaller.unmarshal(file);
//            System.out.println(((Weight) stateRecord.getStateInstance()).getWeight());
//            return stateRecord;
//
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//        return null;
    }
}
