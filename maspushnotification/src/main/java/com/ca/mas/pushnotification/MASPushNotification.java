package com.ca.mas.pushnotification;

import android.content.Context;

import com.ca.mas.foundation.MAS;
import com.ca.sec.aa.mobileAuth.lib.AuthentcatorDelegate;
import com.ca.sec.aa.mobileAuth.lib.CAMobileAuthenticatorException;
import com.ca.sec.aa.mobileAuth.lib.network.CAMobileAuthenticatorCommException;
import com.ca.sec.aa.mobileAuth.lib.network.IArcotAppCallback;
import com.ca.sec.aa.mobileAuth.lib.store.Store;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */


public class MASPushNotification {

    //private AID_AID aid_aid;
    //private DDNA ddna;
    //private AOTP aOTP;
    private MobileAuthenticator mobileAuthenticator;
//    Context context;

    private static MASPushNotification instance = new MASPushNotification();

    private MASPushNotification() {
        if (MAS.getContext() == null) {
            throw new IllegalStateException(MASPushNotificationConsts.MAS_INITIALIZATION_ERROR);
        }
        //this.aid_aid = new AID_AID(MAS.getContext());
        //this.ddna = new DDNA(MAS.getContext());
        //this.aOTP = new AOTP(MAS.getContext());
        try {
            mobileAuthenticator = new MobileAuthenticator(MAS.getContext());

        } catch (Exception e) {
        }
    }

    /*This returns MASAdvanced instance
     */
    public static MASPushNotification getInstance() {
        return instance;
    }
/*
    *//**
     * This Method allows the user to authenticate with the user credentials and returns the SMSESSION Token back to the application.
     *
     * @param userID   The userID of the user account.
     * @param pin      The Password of the user account.
     * @param callback The callback function to return the response message back to the application
     *//*
    public void loginWithAIDSMSession(final String userID, final String pin, final MASCallback<JSONObject> callback) {

        if (MASUser.getCurrentUser() != null) {
            if (MASUser.getCurrentUser().isAuthenticated()) {
                MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        aid_aid.loginWithAIDSMSession(userID, pin, callback);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }
                });
            }
        } else {
            aid_aid.loginWithAIDSMSession(userID, pin, callback);
        }
    }

    *//**
     * This Method allows the user to authenticate with the user credentials and log in the user with id_Token.
     *
     * @param userID   The userID of the user account.
     * @param pin      The Password of the user account.
     * @param callback The callback function to return the response message back to the application.
     *//*
    public void loginWithAID(final String userID, final String pin, final MASCallback<MASUser> callback) {

        if (MASUser.getCurrentUser() != null && MASUser.getCurrentUser().isAuthenticated()) {
            //if (MASUser.getCurrentUser().isAuthenticated()) {
                MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        aid_aid.loginWithAID(userID, pin, callback);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e);
                    }
                });
            *//*} else {
                aid_aid.loginWithAID(userID, pin, callback);
            }*//*
        } else {
            aid_aid.loginWithAID(userID, pin, callback);
        }
    }

    *//**
     * This method creates and saves the account based on given parameters and returns that account.
     *
     * @param b64aid    The base64 authID of the user account.
     * @param namespace The namespace of the user account.
     *//*

    public AIDAccount provisionAIDAccount(String b64aid, String namespace) throws MASAdvanceAuthException {
        try {
            Account account = aid_aid.provisionAccount(b64aid, namespace);
            AIDAccount aidAccount = null;
            if (account != null) {
                aidAccount = new AIDAccount(account);
            }
            return aidAccount;
        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns the account object of the userID.
     *
     * @param userID The userID of the user account.
     *//*
    public AIDAccount getAIDAccount(String userID) throws MASAdvanceAuthException {
        try {
            Account account = aid_aid.getAIDAccount(userID);
            AIDAccount aidAccount = null;
            if (account != null) {
                aidAccount = new AIDAccount(account);
            }
            return aidAccount;
        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns array of all the accounts provisioned in the application.
     *//*
    public AIDAccount[] getAllAIDAccounts() throws MASAdvanceAuthException {
        try {
            Account[] accounts = aid_aid.getAllAIDAccounts();

            AIDAccount[] aidAccounts = null;
            if (accounts != null && accounts.length > 0) {
                aidAccounts = new AIDAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    aidAccounts[i] = new AIDAccount(accounts[i]);
                }
            }

            return aidAccounts;
        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns all the accounts provisioned in the application which has the same nameSpace provided.
     *
     * @param nameSpace of the user accounts.
     *//*
    private AIDAccount[] getAllAIDAccounts(String nameSpace) throws MASAdvanceAuthException {
        try {
            Account[] accounts = aid_aid.getAllAIDAccounts();

            AIDAccount[] aidAccounts = null;
            if (accounts != null && accounts.length > 0) {
                aidAccounts = new AIDAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    aidAccounts[i] = new AIDAccount(accounts[i]);
                }
            }

            return aidAccounts;

        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method removes the account provisioned in the memory.
     *
     *
     *//*
    public void removeAIDAccount(String userID) throws MASAdvanceAuthException {
        try {
            aid_aid.removeAIDAccount(userID);
        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    private String signWithAIDAccount(String challenge, String userID, String pin) throws MASAdvanceAuthException {
        try {
            return aid_aid.signWithAccount(challenge, userID, pin);
        } catch (AIDException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AID_AID.mapErrorCode(e.getCode()));
        }
    }

    public void close() {
        aid_aid.close();
    }*/

    /*
    * The method collectMASDeviceDNA collects the  device DNA (device signature) based on the
    * set of the device attributes. This method triggers device attribute collection and
    * notifies calling object when it is done with the generation of the device signature.
    * This calls back onResponse method of the RMDeviceInventoryResponseCallBack interface.
    *
    * @param  id : [in] This is reference to RMDeviceInventoryResponseCallBack implemented
    *                   by the application using DDNA SDK. DDNA SDK makes a call to an API
    *                   onResponse() once SDK is finished with
    *                   generating device signature. With this call back method, SDK returns
    *                   generated device DNA string in the calling object in the application.
    *
    *
    * @return void
    *
    *
    */
    /*public void collectMASDeviceDNA(RMDeviceInventoryResponseCallBack rmDeviceInventoryResponseCallBack) {
        ddna.collectMASDeviceDNA(rmDeviceInventoryResponseCallBack);
    }*/

    /**
     * This method creates and saves the AOTP account based on given parameters and returns that account.
     *
     * @param data           The data of the user account.
     * @param namespace      The provision server URL of the account.
     * @param activationCode The one time activation code of the account.
     * @param pin            The user provided pin of the account
     */
    /*public AOTPAccount provisionAOTPAccount(String data, String namespace, String activationCode, String pin) throws MASAdvanceAuthException {
        try {
            com.arcot.aotp.lib.Account account = aOTP.provisionAccount(data, namespace, activationCode, pin);
            AOTPAccount aotpAccount = new AOTPAccount(account);
            return aotpAccount;
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method creates and saves the AOTP account based on given parameters and returns that account.
     *
     * @param userID The card string of the user account.
     * @param pin    The user pin of the account.
     * @param mode   mode is a Hashtable object containing key-value pairs of the mode types.
     *               There are 2 different modes supported by HOTP & TOTP account types:
     *               1. Identify 2. Sign
     *//*
    public String generateAOTP(String userID, String pin, Hashtable mode) throws MASAdvanceAuthException {
        try {
            return aOTP.generateAOTP(userID, pin, mode);
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns the AOTP account object of the userID.
     *
     * @param userID The userID of the user account.
     *//*
    public AOTPAccount getAOTPAccount(String userID) throws MASAdvanceAuthException {
        try {
            com.arcot.aotp.lib.Account account = aOTP.getAOTPAccount(userID);
            AOTPAccount aotpAccount = null;
            if (account != null) {
                aotpAccount = new AOTPAccount(account);
            }
            return aotpAccount;
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method removes the AOTP account provisioned in the memory.
     *
     * @param userID The userID of the account to be removed.
     *//*
    public void removeAOTPAccount(String userID) throws MASAdvanceAuthException {
        try {
            aOTP.removeAccount(userID);
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns array of all the AOTP accounts provisioned in the application.
     *//*
    public AOTPAccount[] getAllAOTPAccounts() throws MASAdvanceAuthException {
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
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This method fetches and returns the roaming keys of the AOTP account of the userID specified
     *
     * @param aotpAccount The account object of the AOTPAccount.
     *//*
    public String getRoamingKeys(AOTPAccount aotpAccount) throws MASAdvanceAuthException {
        try {
            return aOTP.getRoamingKeys(aotpAccount.account);
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    public void resync(AOTPAccount aotpAccount, String syncValue) throws MASAdvanceAuthException {
        try {
            aOTP.resync(aotpAccount.account, syncValue);
        } catch (OTPException e) {
            throw new MASAdvanceAuthException(MASAdvanceAuthConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAdvanceAuthConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    *//**
     * This Method allows the user to authenticate with the OTP and log in the user with id_Token.
     *
     *//*
    public void loginWithAOTP(final String userID, final String pin, final MASCallback<MASUser> callback) {

        if (MASUser.getCurrentUser() != null) {
            if (MASUser.getCurrentUser().isAuthenticated()) {
                MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        try {
                            aOTP.loginWithAOTP(userID, pin, callback);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (MASAdvanceAuthException e) {
                            Callback.onError(callback, e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            aOTP.loginWithAOTP(userID, pin, callback);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (MASAdvanceAuthException e1) {
                            Callback.onError(callback, e);
                        }
                    }
                });
            }
        } else {
            try {
                aOTP.loginWithAOTP(userID, pin, callback);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MASAdvanceAuthException e) {
                Callback.onError(callback, e);
            }
        }
    }*/

    public void setMobileAuthenticatorStore(Store store) {
        mobileAuthenticator.setStore(store);
    }

    public void registerDevice(String userID, String provisionURL, String activationCode, Hashtable props) throws CAMobileAuthenticatorCommException {
        mobileAuthenticator.registerDevice(userID, provisionURL, activationCode, props);
    }

    public void authenticateUser(String authData, String userResponse) throws CAMobileAuthenticatorCommException {
        mobileAuthenticator.authenticateUser(authData, userResponse);
    }

    public void setCallback(IArcotAppCallback callback) throws CAMobileAuthenticatorCommException {
        mobileAuthenticator.setCallback(callback);
    }

    public String getMobileAuthenticatorVersion() {
        return mobileAuthenticator.getVersion();
    }

    public void setDeviceType(String deviceType) {
        mobileAuthenticator.setDeviceType(deviceType);
    }

    public com.ca.sec.aa.mobileAuth.lib.Account provisionAuthenticatorAccount(String xml, String provURL, String deviceToken) throws CAMobileAuthenticatorException {
        return mobileAuthenticator.provisionAccount(xml, provURL, deviceToken);
    }

    public void saveAuthenticatorAccount(com.ca.sec.aa.mobileAuth.lib.Account account) throws CAMobileAuthenticatorException {
        mobileAuthenticator.saveAccount(account);
    }

    public void deleteAuthenticatorAccount(String id) throws CAMobileAuthenticatorException {
        mobileAuthenticator.deleteAccount(id);
    }

    public void deletePushAuthAccount(AuthenticatorAccount authenticatorAccount) throws MASPushNotificationException {
        com.ca.sec.aa.mobileAuth.lib.Account account = authenticatorAccount.account;
        try {
            mobileAuthenticator.deletePushAuthAccount(account);
        } catch (CAMobileAuthenticatorException e) {
            e.printStackTrace();
        }
    }

    public void createAuthenticator(Context context, AuthentcatorDelegate authentcatorDelegate) throws CAMobileAuthenticatorCommException {
        mobileAuthenticator.createAuthenticator(context, authentcatorDelegate);
    }

    public AuthenticatorAccount getAuthenticatorAccount(String id) throws MASPushNotificationException {
        try {
            com.ca.sec.aa.mobileAuth.lib.Account account = mobileAuthenticator.getAccount(id);
            AuthenticatorAccount authenticatorAccount = null;
            if (account != null) {
                authenticatorAccount = new AuthenticatorAccount(account);
            }
            return authenticatorAccount;
        } catch (CAMobileAuthenticatorException e) {
            throw new MASPushNotificationException(MASPushNotificationConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASPushNotificationConsts.REASON_CODE_0, mapErrorCode(e.getCode()));
        }
    }

    public AuthenticatorAccount[] getAllAuthenticatorAccounts() throws MASPushNotificationException {

        try {
            com.ca.sec.aa.mobileAuth.lib.Account[] accounts = mobileAuthenticator.getAllAccounts();

            ArrayList<AuthenticatorAccount> authenticatorAccountsList = new ArrayList<AuthenticatorAccount>();

            AuthenticatorAccount[] authenticatorAccounts = null;
            if (accounts != null && accounts.length > 0) {
                for (int i = 0; i < accounts.length; i++) {
                    if(accounts[i] != null)
                    authenticatorAccountsList.add(new AuthenticatorAccount(accounts[i]));
                }

                authenticatorAccounts = authenticatorAccountsList.toArray(new AuthenticatorAccount[0]);
            }

            return authenticatorAccounts;
        } catch (CAMobileAuthenticatorException e) {
            throw new MASPushNotificationException(MASPushNotificationConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASPushNotificationConsts.REASON_CODE_0, mapErrorCode(e.getCode()));
        }
    }

    public com.ca.sec.aa.mobileAuth.lib.Account[] updateDevice() throws CAMobileAuthenticatorException {
        return mobileAuthenticator.updateDevice();
    }

    public AuthenticatorAccount[] getAllAuthenticatorAccounts(String ns) throws MASPushNotificationException {
        try {
            com.ca.sec.aa.mobileAuth.lib.Account[] accounts = mobileAuthenticator.getAllAccounts(ns);

            AuthenticatorAccount[] authenticatorAccounts = null;
            if (accounts != null && accounts.length > 0) {
                authenticatorAccounts = new AuthenticatorAccount[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    authenticatorAccounts[i] = new AuthenticatorAccount(accounts[i]);
                }
            }

            return authenticatorAccounts;
        } catch (CAMobileAuthenticatorException e) {
            throw new MASPushNotificationException(MASPushNotificationConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASPushNotificationConsts.REASON_CODE_0, mapErrorCode(e.getCode()));
        }
    }
    static String mapErrorCode(int errorCode) {
        return String.valueOf(MASPushNotificationConsts.MAS_AA_AOTP_ECODE_BASE + errorCode);
    }

}
