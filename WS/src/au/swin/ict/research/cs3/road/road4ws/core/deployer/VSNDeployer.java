package au.swin.ict.research.cs3.road.road4ws.core.deployer;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.testing.OMUtils;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Deploys the ROAD composites in Axis2 web services engine.
 * This class is abstract and need to be extended using a suitable technology to deploy ROAD composites.
 *
 * @author Malinda Kapuruge
 */
public abstract class VSNDeployer implements Deployer {

    protected static final Logger log = Logger.getLogger(VSNDeployer.class.getName());
    protected AxisConfiguration axisConfig;
    private ConfigurationContext configCtx;

    /**
     * The init methos of the deployer
     */
    @Override
    public void init(ConfigurationContext configurationContext) {
        this.configCtx = configurationContext;
        this.axisConfig = configCtx.getAxisConfiguration();

    }

    /**
     * The deploy method of the deployer
     */
    @Override
    public void deploy(DeploymentFileData deploymentFileData)
            throws DeploymentException {
        File file = deploymentFileData.getFile();

        if (log.isInfoEnabled()) {
            log.info("[ROAD4WS] Deploying VSNs from the file : "
                    + file.getAbsoluteFile());
        }

        if (log.isInfoEnabled()) {
            log.info("Loading VSNs conf from " + file.getAbsolutePath());
        }

        OMElement vsnsOm = OMUtils.getOM(file.getAbsolutePath());
        if (null == vsnsOm) {
            throw new DeploymentException(
                    "ROAD4WS: Cannot instantiate the VSNS from file "
                            + file.getAbsoluteFile());
        }
        Composite composite = (Composite) this.axisConfig.getParameterValue(vsnsOm.getAttributeValue(new QName("name")));
        composite.getOrganiserRole().deployVSNAsXML(vsnsOm);
    }


    @Override
    public void setDirectory(String arg0) {
        // TODO Do nothing

    }

    @Override
    public void setExtension(String arg0) {
        // TODO Do nothing

    }

    @Override
    public void cleanup() throws DeploymentException {
        // TODO Auto-generated method stub

    }

    /**
     * Undeply method of the deployer
     */
    @Override
    public void undeploy(String fileName) throws DeploymentException {
        OMElement vsnsOm = OMUtils.getOM(fileName);
        Composite composite = (Composite) this.axisConfig.getParameterValue(vsnsOm.getAttributeValue(new QName("name")));
        Iterator it = vsnsOm.getChildElements();
        while (it.hasNext()) {
            OMElement vsns = (OMElement) it.next();
            composite.getOrganiserRole().removeVSN(vsns.getAttributeValue(new QName("id")));
        }
    }
}
