package com.ca.apim.mas.authid;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.arcot.aid.lib.AIDException;
import com.arcot.aid.lib.Account;
import com.arcot.aid.lib.store.DeviceLock;
import com.ca.mas.core.context.DeviceIdentifier;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASIdToken;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASRequestBody;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.notify.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
class AID_AID {

    private static com.arcot.aid.lib.AID aid;
    private static JSONObject jsonObject;
    private boolean challengeRepeated = false;

//     Context context;

    final static private String TAG = AID_AID.class.getSimpleName();

    static {
        aid = null;
    }

    AID_AID(Context context) {
//        this.context = context;
        aid = new com.arcot.aid.lib.AID(context);
        jsonObject = getConfig(MASAuthIDConsts.CONFIG_FILE_PATH, context);
    }

    Account provisionAccount(String base64aid, String namespace, String deviceID) throws Exception {
        Log.d(TAG, "Provisioning Account");
        if (deviceID == null ) {
            Log.d(TAG, "deviceID is null");
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(MAS.getCurrentActivity());
            deviceID = deviceIdentifier.toString();
        }
        setDeviceLockKey(deviceID);

        Account account =  aid.provisionAccount(base64aid, namespace);
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

    void removeAIDAccount(String var) throws AIDException, MASAuthIDException {
        Log.d(TAG, "removing Account");
        Account account = getAIDAccount(var);
        if (account != null) {
            MASAuthIDUtil.deleteEntryInPersistedData(account.getId());
            aid.deleteAccount(var);
        }

        Log.d(TAG, "Account Removed");
    }

    void loginWithAIDGetSMSession(String userID, final String pin, final boolean doLogin, final MASCallback<JSONObject> callback) {
        loginWithAIDGetSMSession(userID, pin, doLogin, false, callback);
    }

    @Deprecated
    void loginWithAIDSMSession(String userID, final String pin, final MASCallback<JSONObject> callback) {
        loginWithAIDGetSMSession(userID, pin, false, false, callback);
    }

    private void loginWithAIDGetSMSession(final String userID, final String pin, final boolean doLogin, final boolean challengeRepeatedParam, final MASCallback<JSONObject> callback) {
        Log.d(TAG, "loginWithAIDGetSMSession");
        challengeRepeated = challengeRepeatedParam;
        Log.d(TAG, "Getting AuthID challenge");
        getAuthIdChallenge(new MASCallback<String>() {
            @Override
            public void onSuccess(String challenge) {
                try {
                    Log.d(TAG, "Challenge received");
                    verifyAuthIdSignChallenge(userID, pin, challenge, doLogin, callback);
                } catch (AIDException | MASAuthIDException | InterruptedException e) {
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

    private void verifyAuthIdSignChallenge(final String userID, final String pin, final String challenge, final boolean doLogin, final MASCallback<JSONObject> callback) throws InterruptedException, AIDException, MASAuthIDException {
       Log.d(TAG, "Verifying AuthID Sign Challenge");

        String signChallenge = signWithAccount(challenge, userID, pin);
        Account account = getAIDAccount(userID);

        verifyAuthIdSignChallenge(signChallenge, MASAuthIDConsts.TokenType.user_jwt_smsession, account.org, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                Log.d(TAG, "Sign challenge verified successfully");
                validateAuthIdSignChallengeSuccessResponse(response, callback, doLogin);
            }
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("Error verifying Sign challenge: %s", throwable.toString()));
                validateAuthIdSignChallengeErrorResponse(userID, pin, doLogin, throwable, callback);
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

            if (responseContent.has(String.valueOf(MASAuthIDConsts.TokenType.authToken))) {
                if (doLogin) {
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
                            Callback.onError(callback, e);
                        }
                    });
                } else {
                    Log.d(TAG, "Login with IDToken success");
                    Callback.onSuccess(callback, smSessionJSONObj);
                }
            } else {
                Log.d(TAG, "Response to verify AuthID sign challenge does not have authToken");
                Callback.onError(callback, new Exception("Response to verify AuthID sign challenge does not have authToken"));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception while parsing AuthID sign challenge response");
            Callback.onError(callback, e);
        }
    }

    private void validateAuthIdSignChallengeErrorResponse(final String userID, final String pin, final boolean doLogin, Throwable throwable, MASCallback<JSONObject> callback){
        Log.d(TAG, "validating AuthIdSignChallengeErrorResponse");

        if (throwable.getCause() instanceof TargetApiException) {
            Log.d(TAG, "AuthId signchallenge error is instance of TargetApiException");

            try {
                JSONObject jsonObject;
                if (((TargetApiException) throwable.getCause()).getResponse().getResponseCode() >= HttpsURLConnection.HTTP_UNAUTHORIZED && ((TargetApiException) throwable.getCause()).getResponse().getResponseCode() <= HttpsURLConnection.HTTP_VERSION) {
                    jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                    if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE) && jsonObject.get(MASAuthIDConsts.RESPONSE_CODE).equals(MASAuthIDConsts.RESPONSE_CODE_5702) && !challengeRepeated) {
                        Log.d(TAG, "Retrying loginWithAIDGetSMSession");
                        loginWithAIDGetSMSession(userID, pin, doLogin, true, callback);
                    } else if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE)) {
                        Log.d(TAG, String.format("Error Description : %s", jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION)));
                        challengeRepeated = false;
                        Callback.onError(callback, getMASAdvancedAuthException(jsonObject));
                    } else {
                        Log.d(TAG, String.format("Error Description : %s", throwable.toString()));
                        challengeRepeated = false;
                        Callback.onError(callback, throwable);
                    }
                } else {
                    Log.d(TAG, String.format("Error Description : %s", throwable.toString()));
                    Callback.onError(callback, throwable);
                }
            } catch (JSONException e) {
                Callback.onError(callback, getMASAdvancedAuthFatalException());
            }
        } else {
            Callback.onError(callback, throwable);
        }
    }

    void loginWithAID(final String userID, final String pin, final MASCallback<MASUser> callback) {
        loginWithAID(userID, pin, false, callback);
    }

    private void loginWithAID(final String userID, final String pin, boolean challengeRepeatedParam, final MASCallback<MASUser> callback) {
        Log.d(TAG, "loginWithAID");
        challengeRepeated = challengeRepeatedParam;
        Log.d(TAG, "getting AuthID challenge");
        getAuthIdChallenge(new MASCallback<String>() {
            @Override
            public void onSuccess(String challenge) {
                Log.d(TAG, "Received challenge from server");
                verifyAuthIdSignChallenge(userID, pin, challenge, callback);
            }
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("Error Receiving challenge from server: %s", throwable.toString()));
                Callback.onError(callback, throwable);
            }
        });
    }

    private void verifyAuthIdSignChallenge(final String userID, final String pin, final String challenge, final MASCallback<MASUser> callback){
        Log.d(TAG, "verifyAuthIdSignChallenge");
        try {
            String signChallenge = signWithAccount(challenge, userID, pin);
            Account account = getAIDAccount(userID);
            verifyAuthIdSignChallenge(signChallenge, MASAuthIDConsts.TokenType.user_jwt, account.org, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> response) {
                    Log.d(TAG, "Verifying AuthID sign challenge success");
                    if (response.getBody().getContent().has(String.valueOf(MASAuthIDConsts.TokenType.authToken))) {
                        try {
                            Log.d(TAG, "Log in With IDToken");
                            loginWithIDToken(response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.authToken)), response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.tokenType)), callback);
                        } catch (JSONException e) {
                            Callback.onError(callback, e);
                        }
                    } else {
                        Log.d(TAG, "Response to verify AuthID sign challenge does not have authToken");
                        Callback.onError(callback, new Exception("Response to verify AuthID sign challenge does not have authToken"));
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable.getCause() instanceof TargetApiException) {
                        try {
                            JSONObject jsonObject;

                            if (((TargetApiException) throwable.getCause()).getResponse().getResponseCode() >= HttpsURLConnection.HTTP_UNAUTHORIZED && ((TargetApiException) throwable.getCause()).getResponse().getResponseCode() <= HttpsURLConnection.HTTP_VERSION) {
                                jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                                if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE) && jsonObject.get(MASAuthIDConsts.RESPONSE_CODE).equals(MASAuthIDConsts.RESPONSE_CODE_5702) && !challengeRepeated) {
                                    //challengeRepeated = true;
                                    loginWithAID(userID, pin, true, callback);
                                } else if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE)) {
                                    challengeRepeated = false;
                                    Callback.onError(callback, getMASAdvancedAuthException(jsonObject));
                                } else {
                                    challengeRepeated = false;
                                    Callback.onError(callback, throwable);
                                }
                            } else {
                                Callback.onError(callback, throwable);
                            }
                        } catch (JSONException e) {
                            Callback.onError(callback, getMASAdvancedAuthFatalException());
                        }
                    } else {
                        Callback.onError(callback, throwable);
                    }
                }
            });
        } catch (AIDException | MASAuthIDException | InterruptedException e) {
            Callback.onError(callback, e);
        }
    }

    @Deprecated
    private <T> void getChallenge(final String userID, final String pin, final Enum tokenType, final MASCallback<T> callback) {

        MASRequest request = null;
        try {
            request = new MASRequest.MASRequestBuilder(new URI(MASAuthIDUtil.getChallengeDownloadEndpoint()))
                    .setPublic()
                    .build();
        } catch (URISyntaxException | JSONException e) {
            e.printStackTrace();
        }

        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                if (HttpURLConnection.HTTP_OK == response.getResponseCode()) {
                    JSONObject j = response.getBody().getContent();

                    try {
                        if (j.has(MASAuthIDConsts.AUTH_ID_CHALLENGE)) {
                            String signChallenge = signWithAccount(j.getString(MASAuthIDConsts.AUTH_ID_CHALLENGE), userID, pin);
                            Account account = getAIDAccount(userID);
                            verifySignChallenge(signChallenge, pin, tokenType, callback, userID, account.org);
                        }
                    } catch (JSONException | InterruptedException | MASAuthIDException throwable) {
                        Callback.onError(callback, throwable);
                    } catch (AIDException throwable) {
                        Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), MASAuthIDConsts.REASON_CODE_0, mapErrorCode(throwable.getCode())));
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable.getCause() instanceof TargetApiException) {
                    try {
                        JSONObject jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());
                        Callback.onError(callback, new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), String.valueOf(jsonObject.get(MASAuthIDConsts.REASON_CODE)), String.valueOf(jsonObject.get(MASAuthIDConsts.RESPONSE_CODE))));
                    } catch (JSONException e) {
                        Callback.onError(callback, getMASAdvancedAuthFatalException());
                    }
                } else {
                    Callback.onError(callback, throwable);
                }
            }
        });
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
                            Callback.onError(callback, new Exception("authIDChallenge not found in response"));
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
                    Callback.onError(callback, throwable);
                }
            }
        });
    }

    @Deprecated
    private void verifySignChallenge(String signedChallenge, final String pin, final Enum tokenType, final MASCallback callback, final String userID, String orgName) throws InterruptedException {
        JSONObject jsonRequest = new JSONObject();

        try {
            jsonRequest.put(MASAuthIDConsts.SIGNED_CHALLENGE_KEY, signedChallenge);
            jsonRequest.put(MASAuthIDConsts.TOKEN_TYPE_KEY, tokenType.toString());
            jsonRequest.put(MASAuthIDConsts.ORG_NAME, orgName);
        } catch (JSONException e) {
            callback.onError(e);
        }

        MASRequest request = null;
        try {
            request = new MASRequest.MASRequestBuilder(new URI(MASAuthIDUtil.getVerifySignChallengeEndpoint()))
                    .setPublic()
                    .post(MASRequestBody.jsonBody(jsonRequest))
                    .build();
        } catch (URISyntaxException | JSONException e) {
            e.printStackTrace();
        }

        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {

            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                int statusCode = response.getResponseCode();
                if (statusCode == response.getResponseCode()) {
                    switch ((MASAuthIDConsts.TokenType) tokenType) {
                        case user_jwt_smsession:
                            try {
                                byte[] decode = Base64.decode(((String) (response.getBody().getContent().get("authToken"))).getBytes(), Base64.DEFAULT);
                                final String smsessionString = new String(decode);

                                loginWithIDToken(response.getBody().getContent().toString(), ((MASAuthIDConsts.TokenType) tokenType).user_jwt_smsession.name(), new MASCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        Callback.onSuccess(callback, smsessionString);
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Callback.onError(callback, e);
                                    }
                                });
                            } catch (Exception e) {
                                Callback.onError(callback, e);
                                break;
                            }
                            //Callback.onSuccess(callback, response.getBody().getContent());
                            break;
                        case user_jwt:
                            if (response.getBody().getContent().has(String.valueOf(MASAuthIDConsts.TokenType.authToken))) {
                                try {
                                    loginWithIDToken(response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.authToken)), response.getBody().getContent().getString(String.valueOf(MASAuthIDConsts.TokenType.tokenType)), callback);
                                } catch (JSONException e) {
                                    Callback.onError(callback, e);
                                }
                            }
                            //
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable.getCause() instanceof TargetApiException) {
                    try {
                        JSONObject jsonObject;

                        if (((TargetApiException) throwable.getCause()).getResponse().getResponseCode() >= HttpsURLConnection.HTTP_UNAUTHORIZED && ((TargetApiException) throwable.getCause()).getResponse().getResponseCode() <= HttpsURLConnection.HTTP_VERSION) {
                            jsonObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                            if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE) && jsonObject.get(MASAuthIDConsts.RESPONSE_CODE).equals(MASAuthIDConsts.RESPONSE_CODE_5702) && !challengeRepeated) {
                                challengeRepeated = true;
                                getChallenge(userID, pin, tokenType, callback);
                            } else if (jsonObject.has(MASAuthIDConsts.RESPONSE_CODE)) {
                                challengeRepeated = false;
                                Callback.onError(callback, getMASAdvancedAuthException(jsonObject));
                            } else {
                                challengeRepeated = false;
                                Callback.onError(callback, throwable);
                            }
                        } else {
                            Callback.onError(callback, throwable);
                        }
                    } catch (JSONException e) {
                        Callback.onError(callback, getMASAdvancedAuthFatalException());
                    }
                } else {
                    Callback.onError(callback, throwable);
                }
            }
        });
    }

    private void verifyAuthIdSignChallenge(String signedChallenge, final Enum tokenType, String orgName, final MASCallback callback) throws InterruptedException {
        Log.d(TAG, "verifyAuthIdSignChallenge");
        MASRequest request = null;
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(MASAuthIDConsts.SIGNED_CHALLENGE_KEY, signedChallenge);
            jsonRequest.put(MASAuthIDConsts.TOKEN_TYPE_KEY, tokenType.toString());
            jsonRequest.put(MASAuthIDConsts.ORG_NAME, orgName);
        } catch (JSONException e) {
            callback.onError(e);
        }

        try {
            Log.d(TAG, "Constructing request");
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
                Callback.onError(callback, throwable);
            }
        });
    }

    private MASAuthIDException getMASAdvancedAuthFatalException() {
        return new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.STRONG_AUTHENTICATION_UNKNOWN_ERROR, MASAuthIDConsts.REASON_CODE_0, String.valueOf(MASAuthIDConsts.MAS_AA_AID_ECODE_BASE));
    }

    private MASAuthIDException getMASAdvancedAuthException(JSONObject jsonObject) throws JSONException {
        return new MASAuthIDException(jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR), jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION), jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_ERROR_DETAILS), jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_REASON_CODE), jsonObject.getString(MASAuthIDConsts.MAS_AA_EXCEPTION_RESPONSE_CODE));
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
                Callback.onError(callback, throwable);
            }
        });
    }

    private String getVerifySignChallengeEndpoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("masaa_auth_endpoints").getString("verifychallenge_endpoint_path");
    }

    private String getChallengeDownloadEndpoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("masaa_auth_endpoints").getString("getchallenge_endpoint_path");
    }

    Account getAIDAccount(String var) throws AIDException, MASAuthIDException {

        Account account = aid.getAccount(var);

        if (account == null) {
            throw new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthIDConsts.AID_EXCEPTION, MASAuthIDConsts.REASON_CODE_0, String.valueOf(MASAuthIDConsts.MAS_AA_AID_ECODE_BASE));
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

    String signWithAccount(String challenge, String userId, String pin) throws AIDException {
        Log.d(TAG, "signWithAccount");
        final String deviceId = MASAuthIDUtil.getPersistedData(userId);
        aid.setDeviceLock(new DeviceLock() {
            @Override
            public String getKey() {
                return deviceId;
            }
        });
        Log.d(TAG, "Device ID user is :" + deviceId);
        return aid.signWithAccount(challenge, userId, pin);
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
}
