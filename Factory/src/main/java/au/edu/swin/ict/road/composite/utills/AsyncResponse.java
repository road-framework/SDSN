package au.edu.swin.ict.road.composite.utills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

public class AsyncResponse<MessageWrapper> implements Future<MessageWrapper> {

    private static final Log log = LogFactory.getLog(AsyncResponse.class);

    private boolean cancelled;
    private MessageWrapper responseContext;

    private CountDownLatch latch;
    private String clientID;

    public AsyncResponse(String clientID) {
        this.clientID = clientID;
        this.latch = new CountDownLatch(1);
    }

    public String getClientID() {
        return clientID;
    }

    public void onComplete(MessageWrapper mc) {
        if (log.isDebugEnabled()) {
            log.debug("AsyncResponse received a MessageContext : " + clientID);
        }
        responseContext = mc;
        // Countdown so that the Future object will know that processing is complete.
        latch.countDown();
        if (log.isDebugEnabled()) {
            log.debug("New latch count = [" + latch.getCount() + "]");
        }
    }

    //-------------------------------------
    // javax.xml.ws.Response APIs
    //-------------------------------------

    public boolean cancel(boolean mayInterruptIfRunning) {
        // The task cannot be cancelled if it has already been cancelled
        // before or if it has already completed.
        if (cancelled || latch.getCount() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Cancellation attempt failed.");
            }
            return false;
        }

        cancelled = true;
        return cancelled;
    }

    public MessageWrapper get() throws InterruptedException, ExecutionException {
        if (cancelled) {
            throw new CancellationException("getErr");
        }

        // Wait for the response to come back
        if (log.isDebugEnabled()) {
            log.debug("Waiting for async response delivery.");
        }

        // If latch count > 0, it means we have not yet received
        // and processed the async response, and must block the
        // thread.
        latch.await();
        return responseContext;
    }

    public MessageWrapper get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (cancelled) {
            throw new CancellationException("Asyn response is already cancelled");
        }

        // Wait for the response to come back
        if (log.isDebugEnabled()) {
            log.debug("Waiting for async response delivery with time out.");
            log.debug("timeout = " + timeout);
            log.debug("units   = " + unit);
        }

        // latch.await will only block if its count is > 0
        latch.await(timeout, unit);


        // If the response still hasn't been returned, then we've timed out
        // and must throw a TimeoutException
        if (latch.getCount() > 0) {
            throw new TimeoutException("Timeout Asyn response for request id: " + clientID + " after " + timeout);
        }

        return responseContext;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDone() {
        return (latch.getCount() == 0);
    }
}
