package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.*;
import org.apache.axiom.om.util.StAXUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

/**
 * TODO
 */
public class SNUtils {
    private static int number = 0;

    public static void addVSNToSN(String snPath, String vsnPath, String VSNName, int startNo) throws FileNotFoundException, XMLStreamException {
        number = startNo;
        OMFactory fac = OMAbstractFactory.getOMFactory();
        XMLStreamReader parser = StAXUtils.createXMLStreamReader(new FileInputStream(snPath));
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(parser);
        OMElement snOM = builder.getDocumentElement();

        XMLStreamReader parser1 = StAXUtils.createXMLStreamReader(new FileInputStream(vsnPath));
        OMXMLParserWrapper builder1 = OMXMLBuilderFactory.createStAXOMBuilder(parser1);
        OMElement vsnOM = builder1.getDocumentElement();
        vsnOM.getFirstElement().getAttribute(new QName("id")).setAttributeValue(VSNName + number);
        number++;
        snOM.addChild(vsnOM.getFirstElement());

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(snPath);
            snOM.serialize(outputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
