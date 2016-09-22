package au.swin.ict.research.cs3.road.road4ws.core.deployer;

import au.edu.swin.ict.road.common.IOrganiserRole;
import au.edu.swin.ict.road.composite.Composite;
import au.swin.ict.research.cs3.road.road4ws.core.ROADConstants;
import au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr.OrgInOnlyMsgRcvr;
import au.swin.ict.research.cs3.road.road4ws.core.mgsrcvr.OrgInOutMsgRcvr;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;

import java.util.Iterator;


/**
 * The class to deploy the Organizer service of the composition
 *
 * @author Malinda Kapuruge
 */
public class OrganizerDeployer {
    private static final Logger log = Logger.getLogger(OrganizerDeployer.class);
    private AxisConfiguration axisConfig;
    private ConfigurationContext configCtx;
    private IOrganiserRole organizer;
    private Composite composite;

    /**
     * Constructor
     *
     * @param configurationContext
     * @param composite
     */
    public OrganizerDeployer(ConfigurationContext configurationContext,
                             Composite composite) {
        log.debug("OrganizerRoleDeployer Initialized");
        this.configCtx = configurationContext;
        this.axisConfig = configCtx.getAxisConfiguration();
        this.composite = composite;
        this.organizer = composite.getOrganiserRole();
    }

    /**
     * Creates the organizer service
     *
     * @throws org.apache.axis2.AxisFault
     */
    public void createOrgService() throws AxisFault {
        log.debug("Creating the orgnizer role to composite "
                + composite.getName());

        String svcName = this.composite.getName()
                + ROADConstants.ROAD4WS_SVC_NAME_SEPERATOR
                + ROADConstants.ROAD4WS_ORGANIZER_NAME;
        svcName = svcName.toLowerCase();

        Class clazz = this.organizer.getClass();
        AxisService orgService = AxisService.createService(clazz.getName(),
                this.axisConfig);
        orgService.setName(svcName);
        orgService.setDocumentation("This is the organizer service of composite "
                + this.composite.getName());

        this.setOrgMessageReceivers(orgService);

        //We might need to remove an existing organizer service for the same composition in case of re-deployment before adding the new one
        removeExistingOrgService(svcName, this.axisConfig);

        // At this point we add the org service
        this.axisConfig.addService(orgService);
        log.info("Organizer role added to composite " + composite.getName());


    }

    /**
     * In case of re-deployment, we need to remove the old organizer service
     *
     * @param svcName
     */
    public static void removeExistingOrgService(String svcName, AxisConfiguration axisConfiguration) {
        try {
            if (null != (axisConfiguration.getService(svcName))) {

                axisConfiguration.removeService(svcName);

            }
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Get the composite of the organizer
     *
     * @return
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Set the composite of the organizer
     *
     * @param composite
     */
    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    /**
     * Sets the message receivers for organizer.
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
            AxisOperation operation = (AxisOperation) iterator.next();
            String MEP = operation.getMessageExchangePattern();
            if (MEP != null) {
                log.info(MEP + "->" + operation.getName());
                if (WSDL2Constants.MEP_URI_IN_ONLY.equals(MEP)) {
                    OrgInOnlyMsgRcvr inOnly = new OrgInOnlyMsgRcvr(this.organizer);
                    operation.setMessageReceiver(inOnly);
                } else {
                    OrgInOutMsgRcvr inOut = new OrgInOutMsgRcvr(this.organizer);
                    operation.setMessageReceiver(inOut);
                }

            }
        }
    }
}
