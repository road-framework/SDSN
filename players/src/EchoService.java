import org.apache.log4j.Logger;

/**
 * TODO
 */
public class EchoService {
    private static Logger log = Logger.getLogger(EchoService.class.getName());

    public String echo(String content) {
        if (log.isInfoEnabled()) {
            log.info("Operation : Echo; Parameter: " + content);
        }
        return content;
    }
}
