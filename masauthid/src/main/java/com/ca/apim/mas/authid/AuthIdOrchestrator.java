package com.ca.apim.mas.authid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import com.arcot.aid.lib.AIDException;
import com.arcot.aid.lib.Account;
import com.arcot.aid.lib.store.DeviceLock;
import com.ca.apim.mas.authid.model.MASAIDIssuanceRequestParams;
import com.ca.apim.mas.authid.model.MASAuthIDCustomRequestData;
import com.ca.mas.core.context.DeviceIdentifier;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthCredentials;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASException;
import com.ca.mas.foundation.MASIdToken;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASRequestBody;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASResponseBody;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.notify.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */

class AuthIdOrchestrator {

    private static com.arcot.aid.lib.AID aid;

    private boolean challengeRepeated = false;

    Context context;

    final static private String TAG = AuthIdOrchestrator.class.getSimpleName();

    static {
        aid = null;
    }

    AuthIdOrchestrator(Context context) {
        this.context = context;
        aid = new com.arcot.aid.lib.AID(context);
        MASAuthIDUtil.setJsonObject( getConfig(MASAuthIDConsts.CONFIG_FILE_PATH, this.context));
    }

    Account provisionAccount(String base64aid, String namespace, String deviceID) throws MASAuthIDException {
        Log.d(TAG, "Provisioning Account");
        if (deviceID == null ) {
            Log.d(TAG, "deviceID is null");
            try {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier(MAS.getCurrentActivity());
                deviceID = deviceIdentifier.toString();
            } catch (Exception e) {
                throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9001);
            }
        }
        setDeviceLockKey(deviceID);
        Account account;
        try{
             account =  aid.provisionAccount(base64aid, namespace);
        }catch(AIDException e){
            throw MASAuthIDUtil.parseAIDException(e);
            //throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));
        }

        MASAuthIDUtil.persistData(account.getId(), deviceID);
        Log.d(TAG, "Account added successfully");
        return account;
    }

    private void setDeviceLockKey(final String deviceLockKey){
        Log.d(TAG, "Setting deviceLock key as :" + deviceLockKey);
        aid.setDeviceLock(new DeviceLock() {
            @Override
            public String getKey() {
                return deviceLockKey;
            }
        });
    }

    void removeAIDAccount(String var) throws  MASAuthIDException {
        Log.d(TAG, "removing Account");
        Account account = getAIDAccount(var);
        if (account != null) {
            MASAuthIDUtil.deleteEntryInPersistedData(account.getId());
            try {
                aid.deleteAccount(var);
            } catch (AIDException e) {
                throw MASAuthIDUtil.parseAIDException(e);
            }
        }

        Log.d(TAG, "Account Removed");
    }

/*    void loginWithAIDGetSMSession(String userID, final String pin, final boolean doLogin, final MASCallback<JSONObject> callback) {
        loginWithAIDGetSMSession(userID, pin, doLogin, false, callback);
    }*/


    void loginWithAIDGetSMSession(String userID, final String pin, final String policyName, final Map<String, String> additionParams, final boolean doLogin, final MASCallback<JSONObject> callback) {
        loginWithAIDGetSMSession(userID, pin, policyName, additionParams, doLogin, false, callback);
    }

    private void loginWithAIDGetSMSession(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final boolean doLogin, final boolean challengeRepeatedParam, final MASCallback<JSONObject> callback) {
        Log.d(TAG, "loginWithAIDGetSMSession");
        challengeRepeated = challengeRepeatedParam;
        Log.d(TAG, "Getting AuthID challenge");
        getAuthIdChallenge(new MASCallback<String>() {
            @Override
            public void onSuccess(String challenge) {
                try {
                    Log.d(TAG, "Challenge received");
                    verifyAuthIdSignChallengeSMSession(userID, pin, challenge, policyName, additionParams, doLogin, callback);
                } catch (MASAuthIDException  e) {
                    Log.d(TAG, "Error verifying AuthID Sign challenge");
                    Callback.onError(callback, e);
                }
            }
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "Error getting AuthID challenge");
                Callback.onError(callback, throwable);
            }
        });
    }

    private void verifyAuthIdSignChallengeSMSession(final String userID, final String pin, final String challenge, final String policyName, final Map<String, String> additionParams, final boolean doMAGLogin, final MASCallback<JSONObject> callback) throws  MASAuthIDException {
       Log.d(TAG, "Verifying AuthID Sign Challenge");

        String signChallenge = null;
        Account account = null;
       try {
            signChallenge = signWithAccount(challenge, userID, pin);
            account = getAIDAccount(userID);
       } catch (MASAuthIDException e ) {
           throw e;//MASAuthIDUtil.parseAIDException(e);
           //throw new  MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR,  e.getMessage(), e.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9001);
       }


        verifyAuthIdSignChallenge(signChallenge, policyName, additionParams, MASAuthIDConsts.TokenType.user_jwt_smsession, account.org, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                Log.d(TAG, "Sign challenge verified successfully");
                validateAuthIdSignChallengeSuccessResponse(response, callback, doMAGLogin);
            }
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("Error verifying Sign challenge: %s", throwable.toString()));
                validateAuthIdSignChallengeErrorResponse(userID, pin, policyName, additionParams, doMAGLogin, throwable, callback);
            }
        });
    }

    private void validateAuthIdSignChallengeSuccessResponse(MASResponse<JSONObject> response, final MASCallback<JSONObject> callback, boolean doLogin){
        Log.d(TAG, "validating AuthIdSignChallengeSuccessResponse");
        try {
            final JSONObject responseContent = response.getBody().getContent();
            String authToken = responseContent.getString(String.valueOf(MASAuthIDConsts.TokenType.authToken));
            String tokenType = responseContent.getString(String.valueOf(MASAuthIDConsts.TokenType.tokenType));
            String smSessionBase64 = authToken.substring(0, authToken.indexOf("."));
            byte[] smSessionBytes = Base64.decode(smSessionBase64,  Base64.DEFAULT);
            String smSession = new String (smSessionBytes);
            final JSONObject smSessionJSONObj = new JSONObject(smSession);
            final String cookie = persistCookieIfExists(smSessionJSONObj);
            if (cookie != null && !"".equals(cookie)) {
                smSessionJSONObj.put("cookie", cookie);
            }

            if (responseContent.has(String.valueOf(MASAuthIDConsts.TokenType.authToken))) {
                if (doLogin && !isUserLoggedIn()) {
                    Log.d(TAG, "log in in with IDToken");
                    loginWithIDToken(authToken, tokenType, new MASCallback<MASUser>() {
                        @Override
                        public void onSuccess(MASUser result) {
                            Log.d(TAG, "Login with IDToken success");
                            Callback.onSuccess(callback, smSessionJSONObj);
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, String.format("Error Login with IDToken : %s", e.toString()));
                            Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9033));
                        }
                    });
                } else {
                    Log.d(TAG, "Login with IDToken success");
                    Callback.onSuccess(callback, smSessionJSONObj);
                }
            } else {
                Log.d(TAG, "Response to verify AuthID sign challenge does not have authToken");
                Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.SIGN_CHALLENGE_AUTHID_DOESNOT_EXISTS, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9052));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception while parsing AuthID sign challenge response");
            Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, e.getMessage(),MASAuthIDConsts.AID_EXCEPTION, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9001 ));
        }
    }

    private boolean isUserLoggedIn() {
        if (MASUser.getCurrentUser() != null) {
            if (MASUser.getCurrentUser().isAuthenticated()) {
                return true;
            }
        }
        return false;
    }

    private final static String COOKIE_KEY_NAME = "cookie";

    private String persistCookieIfExists(JSONObject smSessionJSONObj) {
        String response  = null;
        try {
            if (smSessionJSONObj.get(COOKIE_KEY_NAME) == null ) {
                Log.d(TAG, "Did not find cookie in smSession JSON Object");
                return null;
            }
            java.net.CookieManager cookieManager = new java.net.CookieManager();

            CookieHandler.setDefault(cookieManager);

            String cookieStr = (String) smSessionJSONObj.get(COOKIE_KEY_NAME);
            response = cookieStr;

            List<HttpCookie> cookie = HttpCookie.parse(cookieStr);

            if (cookie != null && cookie.size() > 0) {
                cookieManager.getCookieStore().add(null, cookie.get(0));
            }


        } catch (Exception e) {
            Log.d(TAG, "Error adding cookie to cookiestore");
            Log.d(TAG, e.getMessage());
        }
        return response;
    }

    private void validateAuthIdSignChallengeErrorResponse(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final boolean doLogin, Throwable throwable, MASCallback<JSONObject> callback){
        Log.d(TAG, "validating AuthIdSignChallengeErrorResponse");

        if (throwable.getCause() instanceof TargetApiException) {
            Log.d(TAG, "AuthId signchallenge error is instance of TargetApiException");

            try {
                JSONObject jsonObject;
                if (((TargetApiException) throwable.getCause()).getResponse().getResponseCode() >= HttpsURLConnection.HTTP_UNAUTHORIZED && ((TargetApiException) throwable.getCause()).getResponse().getResponseCode() <= HttpsURLConnection.HTTP_VERSION) {
                    jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                    if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE) && jsonObject.get(MASAuthIDConsts.RESPONSE_CODE).equals(MASAuthIDConsts.RESPONSE_CODE_5702) && !challengeRepeated) {
                        Log.d(TAG, "Retrying loginWithAIDGetSMSession");
                        loginWithAIDGetSMSession(userID, pin, policyName, additionParams, doLogin, true, callback);
                    } else if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE)) {
                        Log.d(TAG, String.format("Error Description : %s", jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION)));
                        challengeRepeated = false;
                        Callback.onError(callback, getMASAdvancedAuthException(jsonObject));
                    } else if (jsonObject.has("error") && jsonObject.getString("error").equals("sso_error")){
                        challengeRepeated = false;
                        Callback.onError(callback,  new MASAuthIDException(jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR),
                                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION),
                                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DETAILS),
                                MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9011
                                ));
                        
                    }else {
                        Log.d(TAG, String.format("Error Description : %s", throwable.toString()));
                        challengeRepeated = false;
                        Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9052));
                    }
                } else {
                    Log.d(TAG, String.format("Error Description : %s", throwable.toString()));
                    Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9052));
                }
            } catch (JSONException e) {
                Callback.onError(callback, getMASAdvancedAuthFatalException());
            }
        } else {
            Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9054));
        }
    }

    void loginWithAID(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final MASCallback<MASUser> callback) {
        loginWithAID(userID, pin, policyName, additionParams, false, callback);
    }

    private void loginWithAID(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, boolean challengeRepeatedParam, final MASCallback<MASUser> callback) {
        Log.d(TAG, "loginWithAID");
        challengeRepeated = challengeRepeatedParam;
        Log.d(TAG, "getting AuthID challenge");
        getAuthIdChallenge(new MASCallback<String>() {
            @Override
            public void onSuccess(String challenge) {
                Log.d(TAG, "Received challenge from server");
                verifyAuthIdSignChallenge(userID, pin, policyName, additionParams, challenge, callback);
            }
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("Error Receiving challenge from server: %s", throwable.toString()));

                Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, MASAuthIDConsts.AUTH_ID_CHALLENGE +" : Error Receiving challenge from server", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9052));
            }
        });
    }

    /**
     * Used by LoginWithAID API
     * @param userID
     * @param pin
     * @param challenge
     * @param callback
     */
    private void verifyAuthIdSignChallenge(final String userID, final String pin, final String policyName, final Map<String, String> additionParams, final String challenge, final MASCallback<MASUser> callback){
        Log.d(TAG, "verifyAuthIdSignChallenge");
        String signChallenge = null;
        Account account = null;
        try {
             signChallenge = signWithAccount(challenge, userID, pin);
             account = getAIDAccount(userID);
             verifyAuthIdSignChallenge(signChallenge, policyName, additionParams, MASAuthIDConsts.TokenType.user_jwt, account.org, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> response) {
                    Log.d(TAG, "Verifying AuthID sign challenge success");
                    if (response.getBody().getContent().has(String.valueOf(MASAuthIDConsts.TokenType.authToken))) {
                        try {
                            Log.d(TAG, "Log in With IDToken");
                            loginWithIDToken(response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.authToken)), response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.tokenType)), callback);
                        } catch (JSONException e) {
                            Callback.onError(callback, getMASAdvancedAuthFatalException());
                        }
                    } else {
                        Log.d(TAG, "Response to verify AuthID sign challenge does not have authToken");
                        Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.SIGN_CHALLENGE_AUTHID_DOESNOT_EXISTS, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9051));
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable.getCause() instanceof TargetApiException) {
                        try {
                            JSONObject jsonObject;

                            if (((TargetApiException) throwable.getCause()).getResponse().getResponseCode() >= HttpsURLConnection.HTTP_UNAUTHORIZED
                                    && ((TargetApiException) throwable.getCause()).getResponse().getResponseCode() <= HttpsURLConnection.HTTP_VERSION) {
                                jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                                if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE) && jsonObject.get(MASAuthIDConsts.RESPONSE_CODE).equals(MASAuthIDConsts.RESPONSE_CODE_5702) && !challengeRepeated) {
                                    //challengeRepeated = true;
                                    loginWithAID(userID, pin, policyName, additionParams, true, callback);
                                } else if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE)) {
                                    challengeRepeated = false;
                                    Callback.onError(callback, getMASAdvancedAuthException(jsonObject));
                                } else {
                                    challengeRepeated = false;
                                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(throwable);
                                    Callback.onError(callback, masAuthIDException);
                                }
                            } else {
                                //TODO Make this generic internal server error.
                                //Callback.onError(callback, throwable);
                                MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(throwable);
                                Callback.onError(callback, masAuthIDException);
                            }
                        } catch (JSONException e) {
                            Callback.onError(callback, getMASAdvancedAuthFatalException());
                        }
                    } else {
                        //Callback.onError(callback, throwable);
                        MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(throwable);
                        Callback.onError(callback, masAuthIDException);
                    }
                }
            });
        } catch ( MASAuthIDException  e) {
            Callback.onError(callback, e);
        }
    }

    private <T> void getAuthIdChallenge(final MASCallback<String> callback) {
        Log.d(TAG, "getAuthIdChallenge");

        MASRequest request = null;
        try {
            Log.d(TAG, "Constructing MAS request");
            request = new MASRequest.MASRequestBuilder(new URI(MASAuthIDUtil.getChallengeDownloadEndpoint()))
                    .setPublic()
                    .build();
        } catch (URISyntaxException | JSONException e) {
            Log.d(TAG, String.format("Error constructing request : %s", e.toString()));
            Callback.onError(callback, e);
        }

        Log.d(TAG, "Calling MAS invoke");
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                if (HttpURLConnection.HTTP_OK == response.getResponseCode()) {
                    JSONObject jsonResponse = response.getBody().getContent();
                    try {
                        if (jsonResponse.has(MASAuthIDConsts.AUTH_ID_CHALLENGE)) {
                            Log.d(TAG, "Received AuthID challenge");
                            Callback.onSuccess(callback, jsonResponse.getString(MASAuthIDConsts.AUTH_ID_CHALLENGE));
                        } else {
                            Log.d(TAG,"authIDChallenge not found in response");
                            Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.SIGN_CHALLENGE_AUTHID_DOESNOT_EXISTS, MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9052));
                        }
                    } catch (JSONException throwable) {
                        Log.d(TAG, String.format("Error parsing response: %s", throwable.toString()));
                        Callback.onError(callback, throwable);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

                if (throwable.getCause() instanceof TargetApiException) {
                    Log.d(TAG, "MAS invoke throws TargetApiException");
                    try {
                        JSONObject jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());
                        Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), String.valueOf(jsonObject.get(MASAuthIDConsts.REASON_CODE)), String.valueOf(jsonObject.get(MASAuthIDConsts.RESPONSE_CODE))));
                    } catch (JSONException e) {
                        Callback.onError(callback, getMASAdvancedAuthFatalException());
                    }
                } else {
                    Log.d(TAG, String.format("MAS invoke throws: %s", throwable.toString()));
                    Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9054));
                }
            }
        });
    }


    /**
     * Used by both LoginWithAID and LoginWithAIDGetSMSession
     * @param signedChallenge
     * @param policyName
     * @param additionParams
     * @param tokenType
     * @param orgName
     * @param callback
     * @throws InterruptedException
     */
    private void verifyAuthIdSignChallenge(String signedChallenge, final String policyName, final Map<String, String> additionParams, final Enum<MASAuthIDConsts.TokenType> tokenType, String orgName, final MASCallback<MASResponse<JSONObject>> callback)  {
        Log.d(TAG, "verifyAuthIdSignChallenge");
        MASRequest request = null;
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(MASAuthIDConsts.SIGNED_CHALLENGE_KEY, signedChallenge);
            jsonRequest.put(MASAuthIDConsts.TOKEN_TYPE_KEY, tokenType.toString());
            jsonRequest.put(MASAuthIDConsts.ORG_NAME, orgName);
            if (policyName != null && !"".equals(policyName)) {
                Log.d(TAG, "Appending policy name to request");
                jsonRequest.put(MASAuthIDConsts.AUTH_ID_POLICY, policyName);
            }
            if (additionParams != null && additionParams.size() != 0) {
                Log.d(TAG, "Appending additional params to request");
                JSONObject additionalParams = new JSONObject();
                for (Map.Entry<String,String> entry : additionParams.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();
                    additionalParams.put(name, value);
                    Log.d(TAG, "Appending additional param to request : " + name + "  " +value);
                }
                jsonRequest.put(MASAuthIDConsts.AUTH_ID_ADDITIONAL_PARAM, additionalParams);

            }

        } catch (JSONException e) {
            callback.onError(e);
        }

        try {
            Log.d(TAG, "JSON request : "+ jsonRequest.toString());
            Log.d(TAG, "Constructing MASRequest object");
            request = new MASRequest.MASRequestBuilder(new URI(MASAuthIDUtil.getVerifySignChallengeEndpoint()))
                    .setPublic()
                    .post(MASRequestBody.jsonBody(jsonRequest))
                    .build();
        } catch (URISyntaxException | JSONException e) {
            Log.d(TAG, String.format("Error constructing request : %s", e.toString()));
            Callback.onError(callback, e);
        }

        Log.d(TAG, "Calling MAS Invoke");
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                Log.d(TAG, "MAS invoke call success");
                Callback.onSuccess(callback, response);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("MAS invoke throws : %s", throwable.toString()));
                //MASAuthIDException e = MASAuthIDUtil.parseThrowable(throwable);
                Callback.onError(callback, throwable);
                //Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9001));
            }
        });
    }

    private MASAuthIDException getMASAdvancedAuthFatalException() {
        return new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.STRONG_AUTHENTICATION_UNKNOWN_ERROR, MASAuthIDConsts.REASON_CODE_0, String.valueOf(MASAuthIDConsts.MAS_AA_AID_ECODE_BASE));
    }

    private MASAuthIDException getMASAdvancedAuthException(JSONObject jsonObject) throws JSONException {
        return new MASAuthIDException(jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR),
                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION),
                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DETAILS),
                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_REASON_CODE),
                jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_RESPONSE_CODE));
    }

    private void loginWithIDToken(String idToken, String tokenType, final MASCallback callback) {
        Log.d(TAG, "loginWithIDToken");

        MASIdToken masIdToken = new MASIdToken.Builder().value(idToken).type(tokenType).build();
        MASUser.login(masIdToken, new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser masUser) {
                Log.d(TAG, "Login with IDToken success");
                Callback.onSuccess(callback, masUser);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("Error Login with IDToken: %s", throwable.toString()));
                Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9053));
            }
        });
    }



    Account getAIDAccount(String var) throws MASAuthIDException {
        Account account = null;
        try {
            account = aid.getAccount(var);
        } catch (AIDException e) {
            throw MASAuthIDUtil.parseAIDException(e);
        }

        if (account == null) {
//            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.AID_EXCEPTION, MASAuthIDConsts.REASON_CODE_0, String.valueOf(MASAuthIDConsts.MAS_AA_AID_ECODE_BASE));
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.INVALID_ACCOUNT, MASAuthIDConsts.REASON_CODE_0, String.valueOf(MASAuthIDConsts.RESPONSE_CODE_9033));
        }

        return account;
    }

    Account[] getAllAIDAccounts(String var) throws AIDException {
        return aid.getAllAccounts(var);
    }

    private void getAllAIDAccounts(String username, String namespace) throws AIDException {
        Account[] accounts = getAllAIDAccounts();
        ArrayList<Account> myAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (account.ns.equalsIgnoreCase(namespace)) {
                myAccounts.add(account);
            }
        }
    }

    Account[] getAllAIDAccounts() throws AIDException {
        return aid.getAllAccounts();
    }

    String signWithAccount(String challenge, String userId, String pin) throws MASAuthIDException {
        Log.d(TAG, "signWithAccount");
        final String deviceId = MASAuthIDUtil.getPersistedData(userId);
        aid.setDeviceLock(new DeviceLock() {
            @Override
            public String getKey() {
                return deviceId;
            }
        });
        Log.d(TAG, "Device ID user is :" + deviceId);
        String s = null;
        try {
             s = aid.signWithAccount(challenge, userId, pin);
        }catch (AIDException e) {
            throw MASAuthIDUtil.parseAIDException(e);
        }
        return s;
    }


    void close() {
        aid.close();
    }

   private static JSONObject getConfig(String filename, Context context) {
        Log.d(TAG, "getConfig");
        InputStream is = null;
        StringBuilder jsonConfig = new StringBuilder();

        try {
            is = context.getAssets().open(filename);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                jsonConfig.append(str);
            }
            return new JSONObject(jsonConfig.toString());
        } catch (IOException | JSONException e) {
            Log.d(TAG, String.format("Unable to read Json Configuration file: %s", e.toString()) );
            throw new IllegalArgumentException("Unable to read Json Configuration file: " + filename, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //Ignore
                }
            }
        }
    }

    static String mapErrorCode(int errorCode) {
        return String.valueOf(MASAuthIDConsts.MAS_AA_AID_ECODE_BASE + errorCode);
    }


    /**
     * Api request: Issuance create AuthID
     * @param masAidIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void createAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        try {

            String orgName = masAidIssuanceRequestParams.getOrgName();
            String password = masAidIssuanceRequestParams.getPassword();
            String clientTxnId = masAidIssuanceRequestParams.getClientTxnId();
            String username = masAidIssuanceRequestParams.getUserName();
            MASAuthIDCustomRequestData authIDCustomRequestData = masAidIssuanceRequestParams.getMasAuthIDCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD,password);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAuthIDUtil.getCreateAuthIdEndpoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);

            MASRequest
            createRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


        MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable e) {

                callback.onError(MASAuthIDUtil.parseThrowable(e));
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

    /**
     * Api request: Issuance delete AuthID
     * @param masAidIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void deleteAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {

        try {

            String orgName = masAidIssuanceRequestParams.getOrgName();
            String password = masAidIssuanceRequestParams.getPassword();
            String clientTxnId = masAidIssuanceRequestParams.getClientTxnId();
            String username = masAidIssuanceRequestParams.getUserName();
            MASAuthIDCustomRequestData authIDCustomRequestData = masAidIssuanceRequestParams.getMasAuthIDCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            String url = MASAuthIDUtil.getDeleteAuthIDEndPoint();
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);

            if (password != null && !"".equals(password))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD, password);

            if(clientTxnId != null && !"".equals(clientTxnId))
            queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAuthIDUtil.appendQueryParamsToURL(url, queryParam);

            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);
            MASRequest
                    deleteRequest = request
                    .delete(MASRequestBody.jsonBody(jsonRequest))
                    .header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(deleteRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

    /**
     * Api request: Issuance disable AuthID
     * @param username  The username of the userAccount
     * @param authIDCustomRequestData The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void disableAID(String username, MASAuthIDCustomRequestData authIDCustomRequestData, final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            JSONObject jsonRequest = new JSONObject();
            String url = MASAuthIDUtil.getDisableAuthIDEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);

            MASRequest
                    disableRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest))
                    .header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();

            MAS.invoke(disableRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {

                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

    /**
     * Api request: Issuance download AuthID
     * @param masaidIssuanceRequestParams The masaidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void downloadAID(MASAIDIssuanceRequestParams masaidIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {

        String orgName = masaidIssuanceRequestParams.getOrgName();
        String password = masaidIssuanceRequestParams.getPassword();
        String clientTxnId = masaidIssuanceRequestParams.getClientTxnId();
        String username = masaidIssuanceRequestParams.getUserName();
        MASAuthIDCustomRequestData authIDCustomRequestData = masaidIssuanceRequestParams.getMasAuthIDCustomRequestData();
        try {

            String url = MASAuthIDUtil.getDownloadAuthIDEndPoint() ;
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);


            if (password != null && !"".equals(password))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD, password);

            if(clientTxnId != null && !"".equals(clientTxnId))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAuthIDUtil.appendQueryParamsToURL(url,queryParam);

            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);

            MASRequest
                    downloadRequest = request
                    .header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(downloadRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

    /**
     * Api request: Issuance enable AuthID
     * @param username  The username of the userAccount
     * @param authIDCustomRequestData The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void enableAID(String username, MASAuthIDCustomRequestData authIDCustomRequestData, final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            JSONObject jsonRequest = new JSONObject();
            String url = MASAuthIDUtil.getEnableAuthIDEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);

            MASRequest
                    enableRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest))
                    .header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();

            MAS.invoke(enableRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

    @NonNull
    public static MASRequest.MASRequestBuilder getMasRequestBuilder(MASAuthIDCustomRequestData authIDCustomRequestData, String url) throws URISyntaxException {
        url = MASAuthIDUtil.appendParameters(url,authIDCustomRequestData.getQueryParams());
        MASRequest.MASRequestBuilder request = new MASRequest.MASRequestBuilder(new URI(url));
        MASAuthIDUtil.appendHeaders(request, authIDCustomRequestData.getHeaders());
        if (authIDCustomRequestData != null && authIDCustomRequestData.isPublic()) {
            request.setPublic();
        }
        return request;
    }


    /**
     * Api request: Issuance Fetch AuthID
     * @param masAidIssuanceRequestParams The masaidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    // Api request for the fetch and retrieving AuthID
    public void fetchAID( MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            String orgName = masAidIssuanceRequestParams.getOrgName();
            String password = masAidIssuanceRequestParams.getPassword();
            String clientTxnId = masAidIssuanceRequestParams.getClientTxnId();
            String username = masAidIssuanceRequestParams.getUserName();
            MASAuthIDCustomRequestData authIDCustomRequestData = masAidIssuanceRequestParams.getMasAuthIDCustomRequestData();

            String url = MASAuthIDUtil.getFetchAuthIDEndPoint() ;
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);

            if (password != null && !"".equals(password))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD, password);

            if(clientTxnId != null && !"".equals(clientTxnId))
                queryParam.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAuthIDUtil.appendQueryParamsToURL(url,queryParam);
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);

            MASRequest
                    fetchRequest =request
                    .header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(fetchRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }


    /**
     * Api request: Issuance Re-Issue AuthID
     * @param masAidIssuanceRequestParams The masaidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void reIssueAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            String orgName = masAidIssuanceRequestParams.getOrgName();
            String password = masAidIssuanceRequestParams.getPassword();
            String clientTxnId = masAidIssuanceRequestParams.getClientTxnId();
            String username = masAidIssuanceRequestParams.getUserName();
            MASAuthIDCustomRequestData authIDCustomRequestData = masAidIssuanceRequestParams.getMasAuthIDCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD,password);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);
            String url = MASAuthIDUtil.getReIssueAuthIDEndPoint() ;
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);
            MASRequest
                    reIssueRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(reIssueRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {

                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }


    /**
     * Api request: Issuance Reset AuthID
     * @param masAidIssuanceRequestParams The masaidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void resetAID(MASAIDIssuanceRequestParams masAidIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {

        try {

            String orgName = masAidIssuanceRequestParams.getOrgName();
            String password = masAidIssuanceRequestParams.getPassword();
            String clientTxnId = masAidIssuanceRequestParams.getClientTxnId();
            String username = masAidIssuanceRequestParams.getUserName();
            MASAuthIDCustomRequestData authIDCustomRequestData = masAidIssuanceRequestParams.getMasAuthIDCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_PASSWORD,password);
            jsonRequest.put(MASAuthIDConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAuthIDUtil.getResetAuthIDEndPoint() ;
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authIDCustomRequestData, url);
            MASRequest
                    resetRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthIDConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(resetRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    MASAuthIDException masAuthIDException = MASAuthIDUtil.parseThrowable(e);
                    callback.onError(masAuthIDException);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(MASAuthIDUtil.parseException(e));
        }
    }

}
