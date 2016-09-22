package au.edu.swin.ict.road.common;


import org.apache.axiom.om.OMElement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * TODO
 */
public class JaxbFactory {

    public static String toXml(StateRecord stateRecord, Class[] classes) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(stateRecord, baos);
            return baos.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toXml(StateRecords stateRecords, Class[] classes) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(stateRecords, baos);
            return baos.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StateRecord toStateRecord(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            // Capture the written byte array as a ByteArrayDataSource
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (StateRecord) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StateRecords toStateRecords(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            // Capture the written byte array as a ByteArrayDataSource
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (StateRecords) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object toObjects(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            // Capture the written byte array as a ByteArrayDataSource
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toXml(EventRecord eventRecord, Class[] classes) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(eventRecord, baos);
            return baos.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toXml(EventRecords eventRecords, Class[] classes) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(eventRecords, baos);
            return baos.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EventRecord toEventRecord(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            // Capture the written byte array as a ByteArrayDataSource
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (EventRecord) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EventRecords toEventRecords(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            // Capture the written byte array as a ByteArrayDataSource
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (EventRecords) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }
}
