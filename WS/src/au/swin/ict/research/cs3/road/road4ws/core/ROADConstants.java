package au.swin.ict.research.cs3.road.road4ws.core;

/**
 * This interface contains all the constants that are reffered in the ROAD4WS project.
 *
 * @author Malinda Kapuruge
 */
public interface ROADConstants {
    // For Module
    public static final String ROAD4WS_ROADMODULE = "ROADModule";

    // For SMC file
    public static final String ROAD4WS_AXIS2_PARAM_SMC_FILE = "SMCFile";
    public static final String ROAD4WS_AXIS2_PARAM_RULES_DIR = "RulesDir";

    // For axis2
    public static final String ROAD4WS_SVC_NAME_SEPERATOR = "_";
    public static final String ROAD4WS_ORGANIZER_NAME = "OrganizerRole";
    public static final String ROAD4WS_OPMANAGER_NAME = "OperationalManagerRole";
    public static final String ROAD4WS_CUSMANAGER_NAME = "CusManagerRole";
    public static final String ROAD4WS_GET_NEXT_MSG_OP = "getNextMessage"; //Should be sync with the ROAD Factory
    public static final String ROAD4WS_CURRENT_COMPOSITE = "CurrentComposite";
    public static final String ROAD4WS_CURRENT_ROLE = "CurrentRole";
    public static final String ROAD4WS_PROCESS_GROUP = "ProcessGroup";

    //For Message pushing
    public static final int ROAD4WS_DEFAULT_PUSH_ATTEMPTS = 3;

    //For Message Wrapper
    public static final String ROAD4WS_MW_SOAP_ACTION = "SOAPAction";
    public static final String ROAD4WS_MW_IS_RESPONSE = "IsReponse";
    public static final int ROAD4WS_MW_REQUEST = 0;
    public static final int ROAD4WS_MW_RESPONSE = 1;
    public static final String ROAD4WS_MW_ID = "Id";

    //Deployment
    public static final String ROAD4WS_ROLE_FILES_DIR = "/road_composites/roles";

    //MISC
    public static final long ROAD4WS_GET_NEXT_MSG_TIME_OUT = 0;

}
