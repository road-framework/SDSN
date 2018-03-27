package au.edu.swin.ict.road.testing;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.util.XMLPrettyPrinter;

import javax.xml.namespace.QName;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VSNXMLGenerator {
  public static void main(String args[]){
      generateXML("C:\\post-doc\\projects\\SDSN\\Factory\\src\\main\\resources\\overhead\\os10\\vsn.xml","C:\\post-doc\\projects\\SDSN\\Factory\\src\\main\\resources\\overhead\\os10\\vsnto.xml",10);
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
            outputStream = new FileOutputStream(pathTo);
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
