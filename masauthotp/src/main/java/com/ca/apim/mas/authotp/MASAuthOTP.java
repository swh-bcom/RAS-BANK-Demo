package com.ca.apim.mas.authotp;

import android.content.Context;
import android.util.Log;

import com.arcot.aotp.lib.OTPException;
import com.ca.apim.mas.authotp.model.MASAOTPIssuanceRequestParams;
import com.ca.apim.mas.authotp.model.MASAuthOTPCustomRequestData;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.notify.Callback;
import org.json.JSONObject;
import java.util.Hashtable;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */


public class MASAuthOTP {

    private AOTP aOTP;

    private static MASAuthOTP instance;
    private static final String TAG = MASAuthOTP.class.getSimpleName();
    private MASAuthOTP() {
/*
        if (MAS.getContext() == null) {
            throw new IllegalStateException(MASAuthOTPConsts.MAS_INITIALIZATION_ERROR);
        }
        this.aOTP = new AOTP(MAS.getContext());
*/
        if (aOTP == null) {
            try {
                aOTP = new AOTP(MAS.getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*This returns MASAdvanced instance
     */
    public static MASAuthOTP getInstance() {

        try{
            if (MAS.getContext() == null) {
                throw new IllegalStateException(MASAuthOTPConsts.MAS_INITIALIZATION_ERROR);
            }

            if (instance == null) {
                instance = new MASAuthOTP();
            }
            return instance;
        } catch (Exception e) {
            Log.e(TAG, "Exception at getInstance of MASAuthOTP");
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * This method creates and saves the AOTP account based on given parameters and returns that account.
     *
     * @param data           The data of the user account.
     * @param namespace      The provision server URL of the account.
     * @param activationCode The one time activation code of the account.
     * @param pin            The user provided pin of the account
     * @param deviceID       The deviceID string for credential device binding
     * @return AOTPAccount object
     */
    public AOTPAccount
    provisionAOTPAccount(String data, String namespace, String activationCode, String pin, String deviceID) throws Exception {
        try {
            com.arcot.aotp.lib.Account account = aOTP.provisionAccount(data, namespace, activationCode, pin, deviceID);
            AOTPAccount aotpAccount = new AOTPAccount(account);
            return aotpAccount;
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method creates and saves the AOTP account based on given parameters and returns that account.
     *
     * @param userID The card string of the user account.
     * @param pin    The user pin of the account.
     * @param mode   mode is a Hashtable object containing key-value pairs of the mode types.
     *               There are 2 different modes supported by HOTP & TOTP account types:
     *               1. Identify 2. Sign
     * @return OTP String
     */
    public String generateAOTP(String userID, String pin, Hashtable mode) throws MASAuthOTPException {
        try {
            return aOTP.generateAOTP(userID, pin, mode);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns the AOTP account object of the userID.
     *
     * @param userID The userID of the user account.
     * @return AOTPAccount object
     */
    public AOTPAccount getAOTPAccount(String userID) throws MASAuthOTPException {
        try {
            com.arcot.aotp.lib.Account account = aOTP.getAOTPAccount(userID);
            AOTPAccount aotpAccount = null;
            if (account != null) {
                aotpAccount = new AOTPAccount(account);
            }
            return aotpAccount;
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method removes the AOTP account provisioned in the memory.
     *
     * @param userID The userID of the account to be removed.
     * @return void
     */
    public void removeAOTPAccount(String userID) throws MASAuthOTPException {
        try {
            aOTP.removeAccount(userID);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns array of all the AOTP accounts provisioned in the application.
     * @return Array of AOTPAccounts
     */
    public AOTPAccount[] getAllAOTPAccounts() throws MASAuthOTPException {
        try {
            com.arcot.aotp.lib.Account[] accounts = aOTP.getAllAccounts();

            AOTPAccount[] aOTPAccounts = null;
            if (accounts != null && accounts.length > 0) {
                aOTPAccounts = new AOTPAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    aOTPAccounts[i] = new AOTPAccount(accounts[i]);
                }
            }

            return aOTPAccounts;
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns the roaming keys of the AOTP account of the userID specified
     *
     * @param aotpAccount The account object of the AOTPAccount.
     * @return Roaming Key string
     */
    public String getRoamingKeys(AOTPAccount aotpAccount) throws MASAuthOTPException {
        try {
            return aOTP.getRoamingKeys(aotpAccount.account);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This method fetches and returns the roaming keys of the AOTP account of the userID specified
     *
     * @param aotpAccount The account object of the AOTPAccount.
     * @param syncValue Sync value.
     * @return void
     */
    public void resync(AOTPAccount aotpAccount, String syncValue) throws MASAuthOTPException {
        try {
            aOTP.resync(aotpAccount.account, syncValue);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    /**
     * This Method allows the user to authenticate with the OTP and log in the user with id_Token.
     *
     * @param userID   The userID of the user account.
     * @param pin      The Password of the user account.
     * @param callback The callback function to return the response message back to the application.
     * @return void
     */
    public void loginWithAOTP(final String userID, final String pin, final MASCallback<MASUser> callback) {

        if (MASUser.getCurrentUser() != null) {
            if (MASUser.getCurrentUser().isAuthenticated()) {
                /*MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        try {
                            aOTP.loginWithAOTP(userID, pin, callback);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Callback.onError(callback, e);
                        } catch (MASAuthOTPException e) {
                            Callback.onError(callback, e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            aOTP.loginWithAOTP(userID, pin, callback);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            Callback.onError(callback, e);
                        } catch (MASAuthOTPException e1) {
                            Callback.onError(callback, e);
                        }
                    }
                });*/
                Callback.onError(callback, new Exception("User is currently logged in. You can log the user out and try again."));

            }
        } else {
            try {
                aOTP.loginWithAOTP(userID, pin, callback);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Callback.onError(callback, e);
            } catch (MASAuthOTPException e) {
                Callback.onError(callback, e);
            }
        }
    }



    // Issuance Apis for the AOTP

    /**
     *   This method used to create a new Auth AOTP .
     *   @param callback The callback function to return the response message back to the application
     *
     */
    public void createAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.createAOTP(masAotpIssuanceRequestParams,callback);
    }


    /**  This method used to delete an existing Auth AOTP.
     *   @param callback The callback function to return the response message back to the application
     */
    public void deleteAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.deleteAOTP(masAotpIssuanceRequestParams, callback);
    }



    /**  This method used to disable an existing Auth AOTP.

     * @param callback The callback function to return the response message back to the application
     */
    public void disableAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.disableAOTP(masAotpIssuanceRequestParams, callback);
    }


    /**  This method used to download an existing Auth AOTP.
     *   @param callback The callback function to return the response message back to the application
     */
    public void downloadAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.downloadAOTP(masAotpIssuanceRequestParams,callback);
    }

    /**  This method used to enable an existing Auth AOTP.

     * @param callback The callback function to return the response message back to the application
     */
    public void enableAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.enableAOTP(masAotpIssuanceRequestParams,callback);
    }

    /**  This method used to fetch an existing Auth AOTP.

     *   @param callback The callback function to return the response message back to the application
     */
    public void fetchAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.fetchAOTP(masAotpIssuanceRequestParams, callback);
    }

    /**  This method used to re-issue an existing Auth AOTP.

     * @param callback The callback function to return the response message back to the application
     */
    public void reIssueAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.reIssueAOTP(masAotpIssuanceRequestParams,callback);
    }

    /**  This method used to reset an existing Auth AOTP.
     *   @param callback The callback function to return the response message back to the application
     */
    public void resetAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        aOTP.resetAOTP(masAotpIssuanceRequestParams,callback);
    }


}
