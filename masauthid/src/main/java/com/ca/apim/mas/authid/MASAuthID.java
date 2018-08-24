package com.ca.apim.mas.authid;

import com.arcot.aid.lib.AIDException;
import com.arcot.aid.lib.Account;
import com.ca.apim.mas.authid.model.MASAIDIssuanceRequestParams;
import com.ca.apim.mas.authid.model.MASAuthIDCustomRequestData;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthCredentials;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.notify.Callback;

import org.json.JSONObject;

import java.util.Map;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */


public class MASAuthID {

    //private static AID_AID aid_aid;
    private AuthIdOrchestrator authIdOrchestrator;

    private static MASAuthID instance;

    private MASAuthID() {
        if (authIdOrchestrator == null) {
            authIdOrchestrator = new AuthIdOrchestrator(MAS.getContext());
        }
    }

    /*This returns MASAdvanced instance
     */
    public static MASAuthID getInstance() {
        if (MAS.getContext() == null) {
            throw new IllegalStateException(MASAuthIDConsts.MAS_INITIALIZATION_ERROR);
        }

        if (instance == null) {
            instance = new MASAuthID();
        }
        return instance;
    }

    /**
     * @deprecated use {@link #loginWithAIDGetSMSession(String, String, String, Map, boolean, MASCallback)} instead.
     * This Method allows the user to authenticate with the user credentials and returns the SMSESSION Token back to the application.
     *
     * @param userID   The userID of the user account.
     * @param pin      The Password of the user account.
     * @param callback The callback function to return the response message back to the application
     */
    @Deprecated
    public void loginWithAIDSMSession(final String userID, final String pin, final MASCallback<JSONObject> callback) {

        loginWithAIDGetSMSession(userID, pin, false, callback);

    }


    /**
     * @deprecated use {@link #loginWithAIDGetSMSession(String, String, String, Map, boolean, MASCallback)} instead.
     * This Method allows the user to authenticate with the user credentials and returns the SMSESSION Token back to the application.
     *
     * @param userID     The userID of the user account.
     * @param pin        The Password of the user account.
     * @param doMAGLogin Setting this value as true will also do the user MAG Login
     * @param callback   The callback function to return the response message back to the application
     */
    @Deprecated
    public void loginWithAIDGetSMSession(final String userID, final String pin, final boolean doMAGLogin, final MASCallback<JSONObject> callback) {
        loginWithAIDGetSMSession(userID, pin, null, null, doMAGLogin, callback);
    }

    /**
     * This Method allows the user to authenticate with the user credentials and returns the SMSESSION Token back to the application.
     *
     * @param userID     The userID of the user account.
     * @param pin        The Password of the user account.
     * @param policyName The CA Advanced Authentication policy .
     * @param additionParams A map of key value pairs.
     * @param doMAGLogin Setting this value as true will also do the user MAG Login.
     * @param callback   The callback function to return the response message back to the application
     */
    public void loginWithAIDGetSMSession(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final boolean doMAGLogin, final MASCallback<JSONObject> callback) {


            authIdOrchestrator.loginWithAIDGetSMSession(userID, pin, policyName, additionParams, doMAGLogin, callback);

    }


    /**
     * This Method allows the user to authenticate with the user AuthID credentials and returns the SMSESSION Token back to the application.
     *
     * @param userID     The userID of the user account.
     * @param pin        The Password of the user account.
     * @param policyName The CA Advanced Authentication policy .
     * @param additionParams A map of key value pairs.
     * @param callback   The callback function to return the response message back to the application
     */
    public void getSMSession(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final MASCallback<JSONObject> callback) {


        authIdOrchestrator.loginWithAIDGetSMSession(userID, pin, policyName, additionParams, false, callback);

    }


    /**
     *  @deprecated use {@link #loginWithAID(String, String, String, Map, MASCallback)} instead
     */
    public void loginWithAID(final String userID, final String pin, final MASCallback<MASUser> callback) {

            loginWithAID(userID, pin, null, null, callback);

    }
    /**
     * This Method allows the user to authenticate with the user credentials and log in the user with id_Token.
     *
     * @param userID   The userID of the user account.
     * @param pin      The Password of the user account.
     * @param policyName The CA Advanced Authentication policy .
     * @param additionParams A map of key value pairs.
     * @param callback The callback function to return the response message back to the application.
     */
    public void loginWithAID(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final MASCallback<MASUser> callback) {

        if (MASUser.getCurrentUser() != null && MASUser.getCurrentUser().isAuthenticated()) {

            Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.USER_ALREADY_AUTHENTICATED, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9011));
        } else {
            authIdOrchestrator.loginWithAID(userID, pin, policyName, additionParams,  callback);
        }
    }

    /**
     * This method creates and saves the account based on given parameters and returns that account.
     *
     * @param b64aid    The base64 authID of the user account.
     * @param namespace The namespace of the user account.
     * @param deviceID The deviceID to be associated with this credential
     */

    public AIDAccount provisionAIDAccount(String b64aid, String namespace, String deviceID) throws MASAuthIDException {

        if(MASAuthIDUtil.checkForSpecialCharacters(namespace)){
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, MASAuthIDConsts.NAMESPACE_ERROR_DESCRIPTION, MASAuthIDConsts.NAMESPACE_INVALID_ERROR, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9031);
        }
        try {
            Account account = authIdOrchestrator.provisionAccount(b64aid, namespace, deviceID);
            AIDAccount aidAccount = null;
            if (account != null) {
                aidAccount = new AIDAccount(account);
            }
            return aidAccount;
        } catch (AIDException e) {
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns the account object of the userID.
     *
     * @param userID The userID of the user account.
     */
    public AIDAccount getAIDAccount(String userID) throws MASAuthIDException {
        try {
            Account account = authIdOrchestrator.getAIDAccount(userID);
            AIDAccount aidAccount = null;
            if (account != null) {
                aidAccount = new AIDAccount(account);
            }
            return aidAccount;
        } catch (AIDException e) {
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns array of all the accounts provisioned in the application.
     */
    public AIDAccount[] getAllAIDAccounts() throws MASAuthIDException {
        try {
            Account[] accounts = authIdOrchestrator.getAllAIDAccounts();

            AIDAccount[] aidAccounts = null;
            if (accounts != null && accounts.length > 0) {
                aidAccounts = new AIDAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    aidAccounts[i] = new AIDAccount(accounts[i]);
                }
            }

            return aidAccounts;
        } catch (AIDException e) {
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns all the accounts provisioned in the application which has the same nameSpace provided.
     *
     * @param nameSpace of the user accounts.
     */
    private AIDAccount[] getAllAIDAccounts(String nameSpace) throws MASAuthIDException {

        try {
            Account[] accounts = authIdOrchestrator.getAllAIDAccounts();

            AIDAccount[] aidAccounts = null;
            if (accounts != null && accounts.length > 0) {
                aidAccounts = new AIDAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    aidAccounts[i] = new AIDAccount(accounts[i]);
                }
            }

            return aidAccounts;

        } catch (AIDException e) {
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method removes the account provisioned in the memory.
     *
     * @param userID The userID of the account to be removed.
     */
    public void removeAIDAccount(String userID) throws MASAuthIDException {
        try {
            authIdOrchestrator.removeAIDAccount(userID);
        } catch (MASAuthIDException e) {
            throw e;
        }
    }

    private String signWithAIDAccount(String challenge, String userID, String pin) throws MASAuthIDException {
        try {
            return authIdOrchestrator.signWithAccount(challenge, userID, pin);
        } catch (MASAuthIDException e) {
            throw e;
        }
    }

    public void close() {
        authIdOrchestrator.close();
    }


    /**
     *   This method used to create a new Auth ID.
     *   @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     *   @param callback The callback function to return the response message back to the application
     *
     */
    public void createAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.createAID(masAidIssuanceRequestParams ,callback);
    }

    /**  This method used to delete an existing Auth ID.
     *   @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     *   @param callback The callback function to return the response message back to the application
     */
    public void deleteAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.deleteAID(masAidIssuanceRequestParams,callback);
    }

    /**
     *   This method used to disable an existing  Auth ID
     * @param username  The username of the user account.
     * @param authIDCustomRequestData authIDCustomRequestData The authIDCustomRequestData holds headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void disableAID(final String username, MASAuthIDCustomRequestData authIDCustomRequestData, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.disableAID(username, authIDCustomRequestData ,callback);
    }


    /**  This method used to download and existing Auth ID
     *  @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     *   @param callback The callback function to return the response message back to the application
     */
    public void downloadAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.downloadAID(masAidIssuanceRequestParams,callback);
    }

    /**
     *   This method used to  enable an existing Auth ID
     *   @param username  The username of the user account.
     *   @param authIDCustomRequestData The authIDCustomRequestData holds headers and parameters
     *   @param callback The callback function to return the response message back to the application
     */
    public void enableAID(final String username, MASAuthIDCustomRequestData authIDCustomRequestData, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.enableAID(username, authIDCustomRequestData,  callback);
    }


    /**
     *    This method used to retrieve information about the Auth ID
     *  @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void fetchAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams ,final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.fetchAID(masAidIssuanceRequestParams,callback);
    }

    /**
     *   This method used to issue a new Auth ID
     *  @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void reIssueAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.reIssueAID(masAidIssuanceRequestParams,callback);
    }

    /**
     *   This method used to reset an existing Auth ID
     *  @param masAidIssuanceRequestParams The masAidIssuanceRequestParams holds optional parameters , List of Custom headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void resetAID( MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        authIdOrchestrator.resetAID(masAidIssuanceRequestParams, callback);
    }

}
