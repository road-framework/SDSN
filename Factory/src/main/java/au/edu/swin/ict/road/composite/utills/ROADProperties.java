package au.edu.swin.ict.road.composite.utills;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ROAD configuration properties
 */
public class ROADProperties {
    private static ROADProperties ourInstance = new ROADProperties();
    private static Logger log = Logger.getLogger(ROADProperties.class.getName());
    private static final Properties properties = loadProperties("road.properties");

    public static ROADProperties getInstance() {
        return ourInstance;
    }

    private ROADProperties() {
    }

    private static Properties loadProperties(String filePath) {

        Properties properties = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (log.isDebugEnabled()) {
            log.debug("Loading a file '" + filePath + "' from classpath");
        }

        InputStream in = cl.getResourceAsStream(filePath);
        if (in == null) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to load file  '" + filePath + "'");
            }

            filePath = "conf" +
                       File.separatorChar + filePath;
            if (log.isDebugEnabled()) {
                log.debug("Loading a file '" + filePath + "' from classpath");
            }

            in = cl.getResourceAsStream(filePath);
            if (in == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to load file  '" + filePath + "'");
                }
            }
        }
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException e) {
                String msg = "Error loading properties from a file at : " + filePath;
                log.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
        return properties;
    }

    /**
     * Helper method to get the value of the property from a given property bag
     *
     * @param name         The name of the property
     * @param defaultValue The default value for the property
     * @return The value of the property if it is found , otherwise , default value
     */
    public String getProperty(String name, String defaultValue) {

        String result = properties.getProperty(name);
        if ((result == null || result.length() == 0) && defaultValue != null) {
            if (log.isDebugEnabled()) {
                log.debug("The name with '" + name + "' cannot be found. " +
                          "Using default value : " + defaultValue);
            }
            result = defaultValue;
        }
        if (result != null) {
            return result.trim();
        } else {
            return defaultValue;
        }
    }


}
