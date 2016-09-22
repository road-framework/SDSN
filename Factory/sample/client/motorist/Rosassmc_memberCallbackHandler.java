/**
 * Rosassmc_memberCallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */

package motorist;

/**
 * Rosassmc_memberCallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class Rosassmc_memberCallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public Rosassmc_memberCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Rosassmc_memberCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for complain method
     * override this method for handling normal response from complain operation
     */
    public void receiveResultcomplain(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from complain operation
     */
    public void receiveErrorcomplain(java.lang.Exception e) {
    }


}
    