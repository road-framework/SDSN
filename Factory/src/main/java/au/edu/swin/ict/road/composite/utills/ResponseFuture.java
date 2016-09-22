package au.edu.swin.ict.road.composite.utills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResponseFuture<MessageWrapper> implements Future<MessageWrapper> {
    private static final Log __log = LogFactory.getLog(ResponseFuture.class);
    private boolean _done = false;
    private ResponseCallback callback;

    public ResponseFuture(ResponseCallback _waitingCallback) {
        this.callback = _waitingCallback;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public MessageWrapper get() throws InterruptedException, ExecutionException {
        try {
            return get(0, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // If it's thrown it's definitely a bug
            throw new ExecutionException(e);
        }
    }

    public MessageWrapper get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        if (callback != null) {
            callback.waitResponse(timeout);
            _done = true;
            if (callback.is_timedout()) {
                throw new TimeoutException("Message exchange " + this + " timed out(" + timeout + " ms) when waiting for a response!");
            }
            return (MessageWrapper) callback.getResponse();
        }
        return null;

    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return _done;
    }
}
