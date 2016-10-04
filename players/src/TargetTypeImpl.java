/**
 * TODO documentation
 */
public class TargetTypeImpl implements TargetType {

    public String analyse(String content, long averageResponseTime) {
        try {
            Thread.sleep(averageResponseTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return content;
    }
}
