import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAPEnvelope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JaxbFactory {

    public static OMElement toXml(Book book, Class[] classes) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(book, baos);
            return OMXMLBuilderFactory.createOMBuilder(
                    new ByteArrayInputStream(baos.toByteArray())).getDocumentElement();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Book toBook(OMElement omElement, Class[] classes) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            omElement.serialize(baos);
            byte[] bytes = baos.toByteArray();
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            ByteArrayInputStream file = new ByteArrayInputStream(bytes);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Book) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Book book = new Book();
        Author[] authors = new Author[2];
        Author author1 = new Author();
        author1.setName("John");
        Author author2 = new Author();
        author2.setName("Jim");
        authors[0] = author1;
        authors[1] = author2;
        book.setAuthors(authors);
        book.setName("SOA");
        book.setIsbn("SD11");
        OMElement bookXML = JaxbFactory.toXml(book, new Class[]{Book.class, Author.class});
        System.out.println(bookXML);
        SOAPEnvelope soapEnvelope = createSOAPEnvelope(book);
        System.out.println(soapEnvelope);
        Book book1 =
                JaxbFactory.toBook(soapEnvelope.getBody().getFirstElement().getFirstElement(),
                                   new Class[]{Book.class, Author.class});
        System.out.println(book1.getIsbn());
    }

    public static SOAPEnvelope createSOAPEnvelope(Book book) {
        org.apache.axiom.soap.SOAPFactory factory = OMAbstractFactory.getSOAP11Factory();
        OMDocument omDocument = factory.createOMDocument();
        SOAPEnvelope defaultEnvelope = factory.getDefaultEnvelope();
        omDocument.addChild(defaultEnvelope);
        OMNamespace ns = factory.createOMNamespace("htt://waa.org", "snns");
        OMElement operationElement = factory.createOMElement("OpName", ns);
        OMElement record = JaxbFactory.toXml(book, new Class[]{Book.class, Author.class});
        operationElement.addChild(record);
        defaultEnvelope.getBody().addChild(operationElement);
        return defaultEnvelope;
    }
}
