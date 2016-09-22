package au.edu.swin.ict.road.common;


import org.apache.log4j.Logger;

/**
 * TODO documentation
 */
public class StatWriter {

    public static void writeResTime(String tenantName, long timeMs) {
        Logger logger = Logger.getLogger(tenantName);
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(timeMs);
        }
    }
}
