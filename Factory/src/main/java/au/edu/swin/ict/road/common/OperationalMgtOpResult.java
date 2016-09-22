package au.edu.swin.ict.road.common;

import org.apache.log4j.Logger;

public class OperationalMgtOpResult {

    private boolean result;
    private StringBuffer message = new StringBuffer();
    private static Logger log = Logger.getLogger(OperationalMgtOpResult.class.getName());

    public OperationalMgtOpResult(boolean result, String message) {
        this.result = result;
        this.message.append(message).append(".");
        if (result) {
            log.info(message);
        } else {
            log.error(message);
        }
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message.toString();
    }

    public void addMessage(String message) {
        this.message.append(message).append(".");
    }

}
