/**
 * Usdl_rupCallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */

package usdl;

/**
 * Usdl_rupCallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class Usdl_rupCallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public Usdl_rupCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Usdl_rupCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for getRegisteredUsersFact method
     * override this method for handling normal response from getRegisteredUsersFact operation
     */
    public void receiveResultgetRegisteredUsersFact(
            usdl.Usdl_rupStub.Fact result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getRegisteredUsersFact operation
     */
    public void receiveErrorgetRegisteredUsersFact(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getAllRegisteredUsersFacts method
     * override this method for handling normal response from getAllRegisteredUsersFacts operation
     */
    public void receiveResultgetAllRegisteredUsersFacts(
            usdl.Usdl_rupStub.Facts result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getAllRegisteredUsersFacts operation
     */
    public void receiveErrorgetAllRegisteredUsersFacts(java.lang.Exception e) {
    }

    // No methods generated for meps other than in-out

    // No methods generated for meps other than in-out

    /**
     * auto generated Axis2 call back method for removeRegisteredUsersFact method
     * override this method for handling normal response from removeRegisteredUsersFact operation
     */
    public void receiveResultremoveRegisteredUsersFact(
            boolean result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from removeRegisteredUsersFact operation
     */
    public void receiveErrorremoveRegisteredUsersFact(java.lang.Exception e) {
    }


}
    