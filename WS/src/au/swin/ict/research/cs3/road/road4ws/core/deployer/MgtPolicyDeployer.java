package au.swin.ict.research.cs3.road.road4ws.core.deployer;

import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.testing.OMUtils;
import au.edu.swin.ict.road.testing.PolicyExecutor;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.Deployer;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.Iterator;

/**
 * Deploys the VSNs
 */
public class MgtPolicyDeployer implements Deployer {

    protected static final Logger log = Logger.getLogger(MgtPolicyDeployer.class.getName());
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
            log.info("[ROAD4WS] Deploying management policies from the file : "
                    + file.getAbsoluteFile());
        }

        if (log.isInfoEnabled()) {
            log.info("Loading VSNs conf from " + file.getAbsolutePath());
        }

        OMElement vsnsOm = OMUtils.getOM(file.getAbsolutePath());
        if (null == vsnsOm) {
            throw new DeploymentException(
                    "ROAD4WS: Cannot instantiate the management policies  from file "
                            + file.getAbsoluteFile());
        }

        Composite composite = (Composite) this.axisConfig.getParameterValue(
                vsnsOm.getFirstChildWithName(new QName("composite")).getText().trim());
        Iterator iterator = vsnsOm.getChildrenWithName(new QName("policy"));
        while (iterator.hasNext()) {
            OMElement omElement = (OMElement) iterator.next();
            String policyFile = omElement.getText().trim();
            boolean isOrg = Boolean.parseBoolean(omElement.getAttributeValue(new QName("isOrg")));
            Thread pe2 = new Thread(new PolicyExecutor(composite, omElement.getAttributeValue(new QName("id")), policyFile, isOrg));
            pe2.start();
        }
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
        //TODO
    }
}
