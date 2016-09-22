package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
}
