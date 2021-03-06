package au.edu.swin.ict.serendip.romeo.test;

import au.edu.swin.ict.serendip.romeo.RomeoPNVerify;
import au.edu.swin.ict.serendip.verficiation.VerificationResult;
import org.apache.log4j.Logger;

public class TestPNVerify {
    public static String DIR = "C://romeo/examples/verify/";
    static Logger logger = Logger.getLogger(TestPNVerify.class);

    public static void main(String[] args) throws Exception {

        logger.debug("Start verification");
        RomeoPNVerify pnv = new RomeoPNVerify(
                "C://romeo/gui/bin/mercutio-tctl.exe");
        VerificationResult result = null;
        try {
            result = pnv.verifyAll(DIR, "src", new String[]{"src-prop",
                    "src-prop_false"});
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result.isValid()) {
            logger.debug("Valid");
        } else {
            logger.debug(result.getMessage());
        }
    }
}
