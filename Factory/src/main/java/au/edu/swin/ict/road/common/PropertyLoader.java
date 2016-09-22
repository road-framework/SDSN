package au.edu.swin.ict.road.common;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * TODO
 */
public class PropertyLoader {
    private static Logger log = Logger.getLogger(PropertyLoader.class.getName());
    private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

    public static Properties loadProperties(String filePath) {
        if (propertiesMap.containsKey(filePath)) {
            return propertiesMap.get(filePath);
        }
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
        if (!properties.isEmpty()) {
            propertiesMap.put(filePath, properties);
        }
        return properties;
    }
}
