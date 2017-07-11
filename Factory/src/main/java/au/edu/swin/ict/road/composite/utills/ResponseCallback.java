package au.edu.swin.ict.road.composite.utills;

import au.edu.swin.ict.road.composite.message.MessageWrapper;

public class ResponseCallback {
    private boolean _timedout;
    private boolean _waiting = true;
    private MessageWrapper response;

    public boolean is_timedout() {
        return _timedout;
    }

    public MessageWrapper getResponse() {
        return response;
    }

    public synchronized boolean responseReceived(MessageWrapper messageWrapper) {
        if (_timedout) {
            return false;
        }
        _waiting = false;
        this.notify();
        this.response = messageWrapper;
        return true;
    }

    public synchronized void waitResponse(long timeout) {
        long etime = timeout == 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
        long ctime;
        try {
            while (_waiting && (ctime = System.currentTimeMillis()) < etime) {
                this.wait(etime - ctime);
            }
        } catch (InterruptedException ie) {
            // ignore
        }
        _timedout = _waiting;
    }
}
