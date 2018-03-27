package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO documentation
 */
public class OMUtils {

    public static OMElement createOMElement(String path) throws FileNotFoundException {
        InputStream in = new FileInputStream(path);
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(in);

        return builder.getDocumentElement();
    }

    public static OMElement getOM(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            XMLStreamReader parser = StAXUtils.createXMLStreamReader(inputStream);
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(parser);
            OMElement omElement = builder.getDocumentElement();
            omElement.build();
            return omElement;
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
