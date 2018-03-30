package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLPrettyPrinter;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SNXMLGenerator {
    public static void main(String args[]) {
        for (int i = 1; i < 11; i++) {
            generateXML("C:\\post-doc\\projects\\SDSN\\Factory\\src\\main\\resources\\overhead\\os" + i + "\\smc_target.xml",
                    "C:\\post-doc\\spe_test\\sns\\os" + i + "\\", 101);

        }
    }

    public static void generateXML(String pathFrom, String pathTo, int size) {
        OMElement root = OMUtils.getOM(pathFrom);
        String name = root.getAttributeValue(new QName("name"));
        for (int i = 0; i < size; i++) {
            OMElement clonedSN = root.cloneOMElement();
            clonedSN.getAttribute(new QName("name")).setAttributeValue(name + i);
            OutputStream outputStream = null;
            try {
                String path = pathTo + "smc_target" + i + ".xml";
                new File(pathTo).mkdirs();
                File yourFile = new File(path);
                yourFile.createNewFile(); // if file already exists will do nothing
                outputStream = new FileOutputStream(path, false);
                XMLPrettyPrinter.prettify(clonedSN, outputStream);
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
}
