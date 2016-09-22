import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.commons.io.FileUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO documentation
 */

public class SNDeploymentService {

    private String axis2Home;
    private String deploymentDir;

    public SNDeploymentService() {
        axis2Home = System.getenv("AXIS2_HOME");
        if (axis2Home.endsWith("WEB-INF/")) {
            deploymentDir = axis2Home + "road_composites/";
        } else if (axis2Home.endsWith("WEB-INF")) {
            deploymentDir = axis2Home + "/road_composites/";
        } else if (axis2Home.endsWith("/")) {
            deploymentDir = axis2Home + "WEB-INF/road_composites";
        } else {
            deploymentDir = axis2Home + "/WEB-INF/road_composites";
        }
    }

    public void deployServiceNetwork(OMElement snXml, String smcName) throws AxisFault {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(deploymentDir + smcName + ".xml");
            XMLStreamWriter writer =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            snXml.serialize(writer, false);
            writer.flush();
        } catch (IOException e) {
            throw new AxisFault(e.getMessage());
        } catch (XMLStreamException e) {
            throw new AxisFault(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void deployServiceNetworkFromURL(String url, String snName) throws AxisFault {
        try {
            FileUtils.copyFile(new File(url), new File(deploymentDir + snName + ".xml"));
        } catch (IOException e) {
            throw new AxisFault(e.getMessage());
        }
    }

    public void unDeployServiceNetwork(String snName) throws AxisFault {
        try {
            FileUtils.forceDelete(new File(deploymentDir + snName + ".xml"));
        } catch (IOException e) {
            throw new AxisFault(e.getMessage());
        }
    }
}
