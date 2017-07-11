package au.edu.swin.ict.road.composite.utills;

/**
 *
 */
public class ROADThreadPoolFactory {

    public static ROADThreadPool createROADThreadPool(String profPrefix) {
        ROADProperties roadProperties = ROADProperties.getInstance();
        int corePoolSize = Integer.parseInt(roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_CORE,
                String.valueOf(ROADThreadPool.ROAD_CORE_THREADS)));
        int maxPoolSize = Integer.parseInt(roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_MAX,
                String.valueOf(ROADThreadPool.ROAD_MAX_THREADS)));
        long keepAliveTime = Long.parseLong(roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_ALIVE,
                String.valueOf(ROADThreadPool.ROAD_KEEP_ALIVE)));
        int qlen = Integer.parseInt(roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_QLEN,
                String.valueOf(ROADThreadPool.ROAD_THREAD_QLEN)));
        String threadGroup =
                roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_GROUP, profPrefix + "Pool");
        String threadIdPrefix =
                roadProperties.getProperty(profPrefix + "." + ROADThreadPool.THREAD_IDPREFIX, profPrefix + "Worker");
        return new ROADThreadPool(corePoolSize, maxPoolSize, keepAliveTime, qlen, threadGroup, threadIdPrefix);
    }

}
