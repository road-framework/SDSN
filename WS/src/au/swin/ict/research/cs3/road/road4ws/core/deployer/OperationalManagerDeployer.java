package au.swin.ict.research.cs3.road.road4ws.core.deployer;

import au.edu.swin.ict.road.common.IOperationalManagerRole;
import au.edu.swin.ict.road.composite.Composite;
import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr.OpMgtInOnlyMsgRcvr;
import au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr.OpMgtInOutMsgRcvr;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * TODO documentation
 */
public class OperationalManagerDeployer {
    private static final Logger log = Logger.getLogger(OperationalManagerDeployer.class);
    private AxisConfiguration axisConfig;
    private ConfigurationContext configCtx;
    private IOperationalManagerRole operationalManagerRole;
    private Composite composite;

    public OperationalManagerDeployer(ConfigurationContext configurationContext,
                                      Composite composite) {
        log.debug("OperationalManagerRoleDeployer Initialized");
        this.configCtx = configurationContext;
        this.axisConfig = configCtx.getAxisConfiguration();
        this.composite = composite;
        this.operationalManagerRole = composite.getOperationalManagerRole();
    }

    /**
     * Creates the operationalManagerRole service
     *
     * @throws org.apache.axis2.AxisFault
     */
    public void createOrgService() throws AxisFault {
        log.debug("Creating the operational manager ole to composite "
                + composite.getName());

        String svcName = this.composite.getName()
                + ROADConstants.ROAD4WS_SVC_NAME_SEPERATOR
                + ROADConstants.ROAD4WS_OPMANAGER_NAME;
        svcName = svcName.toLowerCase();

        Class clazz = this.operationalManagerRole.getClass();
        AxisService orgService = AxisService.createService(clazz.getName(),
                this.axisConfig);
        orgService.setName(svcName);

        orgService.setDocumentation("This is the operational manager service of composite "
                + this.composite.getName());

        this.setOrgMessageReceivers(orgService);

        //We might need to remove an existing operationalManagerRole service for the same composition in case of re-deployment before adding the new one
        removeExistingOrgService(svcName, this.axisConfig);

        // At this point we add the org service
        this.axisConfig.addService(orgService);
        log.info("Operational Manager role added to composite " + composite.getName());


    }

    /**
     * In case of re-deployment, we need to remove the old operationalManagerRole service
     *
     * @param svcName
     */
    public static void removeExistingOrgService(String svcName, AxisConfiguration axisConfiguration) {
        try {
            if (null != (axisConfiguration.getService(svcName))) {

                axisConfiguration.removeService(svcName);
            }
        } catch (AxisFault e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the composite of the operationalManagerRole
     *
     * @return
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Sets the message receivers for operationalManagerRole.
     * We use two types of message receivers to support.
     * 1. In Only MEP
     * 2. In Out MEP
     * Please refer Axis2 documentation for more details of MEP
     *
     * @param service
     */
    public void setOrgMessageReceivers(AxisService service) {
        Iterator<AxisOperation> iterator = service.getOperations();
        while (iterator.hasNext()) {
            AxisOperation operation = iterator.next();
            String MEP = operation.getMessageExchangePattern();
            if (MEP != null) {
                log.info(MEP + "->" + operation.getName());
                if (WSDL2Constants.MEP_URI_IN_ONLY.equals(MEP)) {
                    OpMgtInOnlyMsgRcvr inOnly = new OpMgtInOnlyMsgRcvr(this.operationalManagerRole);
                    operation.setMessageReceiver(inOnly);
                } else {
                    OpMgtInOutMsgRcvr inOut = new OpMgtInOutMsgRcvr(this.operationalManagerRole);
                    operation.setMessageReceiver(inOut);
                }

            }
        }
    }
}
