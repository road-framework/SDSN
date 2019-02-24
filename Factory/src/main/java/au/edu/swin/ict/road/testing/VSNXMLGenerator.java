package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLPrettyPrinter;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VSNXMLGenerator {
    public static void main(String args[]) {
        for (int i = 1; i < 11; i++) {
            generateXML("C:\\post-doc\\projects\\SDSN\\Factory\\src\\main\\resources\\overhead\\os" + i + "\\vsn.xml",
                    "C:\\post-doc\\spe_test\\vsns\\os" + i + "\\", 101);

        }
    }

    public static void generateXML(String pathFrom, String pathTo, int size) {
        OMElement root = OMUtils.getOM(pathFrom);
        OMElement firstVSN = root.getFirstElement();
        String name = firstVSN.getAttributeValue(new QName("id"));
        for (int i = 1; i < size; i++) {
            OMElement clonedVSN = firstVSN.cloneOMElement();
            clonedVSN.getAttribute(new QName("id")).setAttributeValue(name + i);
            root.addChild(clonedVSN);
        }
        OutputStream outputStream = null;
        try {
            String path = pathTo + "vsn.xml";
            new File(pathTo).mkdirs();
            File yourFile = new File(path);
            yourFile.createNewFile(); // if file already exists will do nothing
            outputStream = new FileOutputStream(path, false);
            XMLPrettyPrinter.prettify(root, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
