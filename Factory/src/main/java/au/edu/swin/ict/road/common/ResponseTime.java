package au.edu.swin.ict.road.common;

import java.io.Serializable;

/**
 * TODO
 */

public class ResponseTime implements Serializable, ResponseTimeMBean {

    private double responseTime;

    public ResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public ResponseTime() {
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
}
