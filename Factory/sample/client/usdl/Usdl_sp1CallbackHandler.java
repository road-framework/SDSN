/**
 * Usdl_sp1CallbackHandler.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */

package usdl;

/**
 * Usdl_sp1CallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class Usdl_sp1CallbackHandler {


    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public Usdl_sp1CallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public Usdl_sp1CallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */

    public Object getClientData() {
        return clientData;
    }


    /**
     * auto generated Axis2 call back method for getService method
     * override this method for handling normal response from getService operation
     */
    public void receiveResultgetService(
            usdl.Usdl_sp1Stub.GetServiceReturn result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getService operation
     */
    public void receiveErrorgetService(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getUserInvocationStatFact method
     * override this method for handling normal response from getUserInvocationStatFact operation
     */
    public void receiveResultgetUserInvocationStatFact(
            usdl.Usdl_sp1Stub.Fact result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getUserInvocationStatFact operation
     */
    public void receiveErrorgetUserInvocationStatFact(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for settechnicalInterfaces method
     * override this method for handling normal response from settechnicalInterfaces operation
     */
    public void receiveResultsettechnicalInterfaces(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from settechnicalInterfaces operation
     */
    public void receiveErrorsettechnicalInterfaces(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceInvocationStatFact method
     * override this method for handling normal response from getServiceInvocationStatFact operation
     */
    public void receiveResultgetServiceInvocationStatFact(
            usdl.Usdl_sp1Stub.Fact result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceInvocationStatFact operation
     */
    public void receiveErrorgetServiceInvocationStatFact(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createCapability method
     * override this method for handling normal response from createCapability operation
     */
    public void receiveResultcreateCapability(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createCapability operation
     */
    public void receiveErrorcreateCapability(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createService method
     * override this method for handling normal response from createService operation
     */
    public void receiveResultcreateService(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createService operation
     */
    public void receiveErrorcreateService(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getAllUserInvocationStatFacts method
     * override this method for handling normal response from getAllUserInvocationStatFacts operation
     */
    public void receiveResultgetAllUserInvocationStatFacts(
            usdl.Usdl_sp1Stub.Facts result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getAllUserInvocationStatFacts operation
     */
    public void receiveErrorgetAllUserInvocationStatFacts(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createProvider method
     * override this method for handling normal response from createProvider operation
     */
    public void receiveResultcreateProvider(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createProvider operation
     */
    public void receiveErrorcreateProvider(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setcapabilities method
     * override this method for handling normal response from setcapabilities operation
     */
    public void receiveResultsetcapabilities(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setcapabilities operation
     */
    public void receiveErrorsetcapabilities(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createTechnicalInterface method
     * override this method for handling normal response from createTechnicalInterface operation
     */
    public void receiveResultcreateTechnicalInterface(
            java.lang.String result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createTechnicalInterface operation
     */
    public void receiveErrorcreateTechnicalInterface(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getAllServiceInvocationStatFacts method
     * override this method for handling normal response from getAllServiceInvocationStatFacts operation
     */
    public void receiveResultgetAllServiceInvocationStatFacts(
            usdl.Usdl_sp1Stub.Facts result
    ) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getAllServiceInvocationStatFacts operation
     */
    public void receiveErrorgetAllServiceInvocationStatFacts(java.lang.Exception e) {
    }


}
    