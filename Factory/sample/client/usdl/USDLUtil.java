package usdl;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;


public class USDLUtil {

    private static Logger log = Logger.getLogger(USDLUtil.class.getName());
    public static final String sp1_userid = "sp_001";
    public static final String sp1_guid = "Swinburne";
    public static final String sp1_provider = sp1_guid;
    public static final String sp1_version = "1.0";
    public static final String sp1_nature = "AUTOMATED";
    public static final String sp1_tinterfacetype = "http://schemas.xmlsoap.org/wsdl/";
    public static final String sp1_tinterfacename = "admission";
    public static final String sp1_capabilityDetails = "admissionPolicy";

    public static final String svc1_id = "_j8pK0MLqEeCZVJ07JoM73A";
    public static final String svc2_id = "_Jl5OoMLsEeCZVJ07JoM73A";

    public static int sp1_invoke_count = 0;
    public static int sc1_invoke_count = 0;
    public static int sc2_invoke_count = 0;

    public static int svc1_invoke_count = 0;
    public static boolean isSp1Blocked = false;

    public static final boolean TESTING = false;//false for normal operations


    //For real demo
    public static final String anonUserName = "anon";

    public static String displayebleServiceInfo() {
        String serviceDetails = "" +
                "\n SERVICE DETAILS" +
                "\n -------------------------------\n" +
                "\n RaaS ref: \t" + svc1_id +
                "\n Provider: \t" + sp1_provider +
                "\n Version: \t" + sp1_version +
                "\n Nature: \t" + sp1_nature +
                "\n Technical Interface Type: \t" + sp1_tinterfacetype +
                "\n Technical Interface Details: \t" + sp1_tinterfacename +
                "\n Capability Details: \t" + sp1_capabilityDetails;
        return serviceDetails;
    }

    public static String displayebleCapabilityInfo() {
        String capaDetails = "" +
                "\n CAPABILITY DETAILS" +
                "\n-------------------------------\n" +
                "\n Provider: \t\t" + sp1_provider +
                "\n Capability Details: \t" + sp1_capabilityDetails;
        return capaDetails;
    }

    public static String displayStatisticsFor(String userId) {
        String line = "";
        if (userId.equals("SP1")) {
            line = "\n # invocations for service (" + svc1_id + "): \t" + svc1_invoke_count;
        } else if (userId.equals("SC1")) {
            line = "\n # invocations you've made: \t" + sc1_invoke_count;
        } else if (userId.equals("SC2")) {
            line = "\n # invocations you've made: \t" + sc2_invoke_count;
        } else if (userId.equals("All")) {
            line = "\n Number of invocations so far for Anon: \t" + sc1_invoke_count +
                    "\n Number of invocations so far for Reg Users: \t" + sc2_invoke_count +
                    "\n Number of invocations so far for Service (" + svc1_id + "): \t" + sp1_invoke_count +
                    "";

        } else {

        }
        String stats = "" +
                "\n STATISTICS" +
                "\n----------------------------------------\n" +
                line;
        return stats;
    }

    public static String getCurrentTime() {
        Date dateNow = new Date();
        //2011-08-10T01:48:52.498Z
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        return new StringBuilder(isoFormat.format(dateNow)).toString();

    }

    public static void main(String[] args) {
        getCurrentTime();
        log.debug(getCurrentTime());
    }
}
