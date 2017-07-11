package au.edu.swin.ict.serendip.util;

import au.edu.swin.ict.serendip.core.ModelProviderFactory;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class BenchUtil {
    public static boolean BENCH_ON = false;
    private static Logger log = Logger.getLogger(BenchUtil.class.getName());
    private static String path = null;// System.getenv("AXIS2_HOME")+System.getProperty("file.separator")+"test.csv";
    private FileWriter writer = null;
    private long begintime = System.currentTimeMillis();

    public BenchUtil(String name) {
        try {
            String benchStr = ModelProviderFactory.getProperty("BENCH_ON");
            if (null != benchStr) {
                if (benchStr.equalsIgnoreCase("true")) {
                    BENCH_ON = true;
                    path = System.getenv("AXIS2_HOME") + System.getProperty("file.separator") + name + ".csv";
                    writer = new FileWriter(path);
                    writer.append("Key, Property, Timestamp \n");
                } else {
                    BENCH_ON = false;
                    if (log.isInfoEnabled()) {
                        log.info("Bench mark OFF");
                    }
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Bench mark OFF");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private long getElapsedTime() {
        return (System.currentTimeMillis() - begintime);
    }

    public void addBenchRecord(String key, String property) {
        if (!BENCH_ON) {
            return;
        }

        try {
            if (null != writer) {
                long time = getElapsedTime();
                String str = key + "," + property + "," + time + "\n";
                writer.append(str);
                //log.info("::::::"+str);
            } else {
                System.out.println("ERROR: CSV file is null " + path);
                if (log.isInfoEnabled()) {
                    log.info("ERROR: CSV file is null " + path);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void finalize() {
        if (null != writer) {
            try {
                writer.flush();
                writer.close();
                if (log.isInfoEnabled()) {
                    log.info("CSV written to " + path);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: CSV file is null");
        }
    }
}
