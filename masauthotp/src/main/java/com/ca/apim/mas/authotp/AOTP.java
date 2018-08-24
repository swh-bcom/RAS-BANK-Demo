package com.ca.apim.mas.authotp;

import android.content.Context;
import android.util.Log;

import com.arcot.aotp.lib.Account;
import com.arcot.aotp.lib.OTP;
import com.arcot.aotp.lib.OTPException;
import com.ca.apim.mas.authotp.model.MASAOTPIssuanceRequestParams;
import com.ca.apim.mas.authotp.model.MASAuthOTPCustomRequestData;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */
class AOTP {

    private OTP otp = null;
    private static JSONObject jsonObject;
//    private Context context;

    private static final String TAG = AOTP.class.getSimpleName();

    AOTP(Context context) throws Exception{
//        this.context = context;
        otp = new OTP(context);

        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(MAS.getCurrentActivity());
            String deviceID = deviceIdentifier.toString();
            MASAOTPUtil.setDeviceLocking(deviceID, otp);
        } catch (Exception e) {
            Log.e(TAG, "Error while getting new instance of DeviceIdentifier");
            Log.e(TAG, e.getMessage());
            throw e;
        }
        jsonObject = getConfig(MASAuthOTPConsts.CONFIG_FILE_PATH, context);
        MASAOTPUtil.setJsonObject(getConfig(MASAuthOTPConsts.CONFIG_FILE_PATH, context));
    }

    Account provisionAccount(String userID, String provisionURL, String activationCode, String pin, String deviceID) throws Exception {
        Log.d(TAG, "Provisioning Account");
        try {
            if (deviceID == null) {
                Log.d(TAG, "deviceID is null");
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier(MAS.getCurrentActivity());
                deviceID = deviceIdentifier.toString();
            }


            Log.d(TAG, "Setting deviceLock key as :" + deviceID);

            MASAOTPUtil.setDeviceLocking(deviceID, otp);

            Account account = otp.provisionAccount(userID, provisionURL, activationCode, pin);
            String id = account.getId();
            MASAOTPUtil.persistData(id, deviceID);
            Log.d(TAG, "Account added successfully");
            return account;
        }  catch (Exception e) {
            Log.e(TAG, "Error while getting new instance of DeviceIdentifier");
            Log.e(TAG, e.getMessage());
            throw e;
        }
    }

    String generateAOTP(String userId, String pin, Hashtable otpParams) throws OTPException {
        Log.d(TAG, "generating OTP");
        String deviceId = MASAOTPUtil.getPersistedData(userId);
        MASAOTPUtil.setDeviceLocking(deviceId, otp);
        return otp.generateOTP(userId, pin, otpParams);
    }

    Account getAOTPAccount(String userId) throws OTPException, MASAuthOTPException {
        Log.d(TAG, "getting OTP Account");
        String deviceId = MASAOTPUtil.getPersistedData(userId);
        MASAOTPUtil.setDeviceLocking(deviceId, otp);

        Account account = otp.getAccount(userId);
        if (account == null) {
            Log.d(TAG, "OTP Account is null");
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", MASAuthOTPConsts.AOTP_EXCEPTION_NO_USER, MASAuthOTPConsts.REASON_CODE_0, MASAuthOTPConsts.RESPONSE_CODE_9133);
        }

        Log.d(TAG, "returning OTP account");
        return account;
    }

    Account[] getAllAccounts() throws OTPException {
        Log.d(TAG, "getting all OTP accounts");
        return otp.getAllAccounts();
    }

    Account[] getAllAccounts(String namespace) throws OTPException {
        return otp.getAllAccounts(namespace);
    }

    void removeAccount(String userID) throws OTPException {
        Log.d(TAG, "removing OTP account");
        otp.deleteAccount(userID);
        MASAOTPUtil.deleteEntryInPersistedData(userID);
        Log.d(TAG, "Account Removed");
    }

    String getRoamingKeys(Account account) throws OTPException {
        Log.d(TAG, "getting roaming keys");
        return otp.getRomingKeys(account);
    }

    void resync(Account account, String resyncValue) throws OTPException {
        Log.d(TAG, "resyncing account");
        String userId = MASAOTPUtil.getPersistedData(account.accountId);
        MASAOTPUtil.setDeviceLocking(userId, otp);
        otp.resync(account, resyncValue);
        Log.d(TAG, "resync success");
    }

    static String mapErrorCode(int errorCode) {
        Log.d(TAG, "mapping error");
        return String.valueOf(MASAuthOTPConsts.MAS_AA_AOTP_ECODE_BASE + errorCode);
    }

    void loginWithAOTP(String userID, String pin, final MASCallback<MASUser> callback) throws InterruptedException, MASAuthOTPException {
        Log.d(TAG, "Login with AOTP");
        try {
            String passcode;
            String userName;
            String credentialVersion = null;

            Account account = getAOTPAccount(userID);

            passcode = generateAOTP(userID, pin, null);
            credentialVersion = account.getAttribute(MASAuthOTPConsts.CREDENTIAL_VERSION_KEY);
            userName = account.name;
            JSONObject jsonRequest = constructJson(userName, account.org, passcode, credentialVersion, callback);
            MASRequest request = constructMASRequest(jsonRequest, userName);
            invokeMASRequest(request, callback);
        } catch (OTPException e) {
            Callback.onError(callback, new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, mapErrorCode(e.getCode())));
        }
    }

    private void validateSuccessResponseAndLogin(MASResponse<JSONObject> response, MASCallback<MASUser> callback){
        Log.d(TAG,"validating MAS success response");
        try {
            String idToken = response.getBody().getContent().getString(String.valueOf(MASAuthOTPConsts.TokenType.authToken));
            String tokeType = response.getBody().getContent().getString(String.valueOf(MASAuthOTPConsts.TokenType.tokenType));
            loginWithIDToken(idToken, tokeType, callback);
        } catch (JSONException exception) {
            Log.d(TAG, String.format("Error parsing json response: %s", exception.toString()));
            exception.printStackTrace();
        }
    }

    private void invokeMASRequest(MASRequest request,final MASCallback<MASUser> callback){
        Log.d(TAG, "invoking MAS Request to get idToken");
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public void onSuccess(MASResponse<JSONObject> response) {
                Log.d(TAG, "MASRequest success");
                validateSuccessResponseAndLogin(response, callback);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, String.format("error invoking MASRequest to get idToken: %s", throwable.toString()));
                validateErrorResponse(throwable, callback);
            }
        });
    }

    private void validateErrorResponse(Throwable throwable, MASCallback<MASUser> callback){
        Log.d(TAG, "validating MAS invoke error response");
        if (throwable.getCause() instanceof TargetApiException) {
            Log.d(TAG, "Error is instance of TargetApiException");
            String reasonCode = "";
            String responseCode = "";
            String errorDescription;
            try {
                JSONObject jsonResponseObject = ((JSONObject) ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent());

                if (jsonResponseObject.has(MASAuthOTPConsts.REASON_CODE))
                    reasonCode = String.valueOf(jsonResponseObject.get(MASAuthOTPConsts.REASON_CODE));

                if (jsonResponseObject.has(MASAuthOTPConsts.RESPONSE_CODE))
                    responseCode = String.valueOf(jsonResponseObject.get(MASAuthOTPConsts.RESPONSE_CODE));

                if (jsonResponseObject.has(MASAuthOTPConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION)) {
                    errorDescription = String.valueOf(jsonResponseObject.get(MASAuthOTPConsts.MAS_AA_EXCEPTION_ERROR_DETAILS));
                } else {
                    errorDescription = throwable.getMessage();
                }

                Callback.onError(callback, new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", errorDescription, reasonCode, responseCode));
            } catch (JSONException exception) {
                Log.d(TAG, "exception parsing json error response: %s");
                Callback.onError(callback, new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", throwable.getMessage(), reasonCode, responseCode));
            }
        } else {
            Log.d(TAG, String.format("MAS invoke error details: %s", throwable.toString()));
            Callback.onError(callback, throwable);
        }
    }

    private MASRequest constructMASRequest(JSONObject jsonRequest, String userName) {
        Log.d(TAG, "Constructing MAS request");
        MASRequest request = null;
        try {
            request = new MASRequest.MASRequestBuilder(new URI(getVerifyOTPEndpoint(userName)))
                    .setPublic()
                    .post(MASRequestBody.jsonBody(jsonRequest))
                    .build();
        } catch (URISyntaxException | JSONException exception) {
            Log.d(TAG, String.format("Exception constructing MAS Request: %s", exception.toString()));
        }
        Log.d(TAG, "MAS request construct");
        return request;
    }

    private JSONObject constructJson(String userName, String org, String passcode, String credentialVersion, MASCallback<MASUser> callback) {
        Log.d(TAG, "constructing json request object");
        JSONObject jsonRequestObject = new JSONObject();
        try {
            jsonRequestObject.put(MASAuthOTPConsts.OTP_USERNAME, userName);
            jsonRequestObject.put(MASAuthOTPConsts.ORG_NAME, org);
            jsonRequestObject.put(MASAuthOTPConsts.OTP_KEY1, passcode);
            jsonRequestObject.put(MASAuthOTPConsts.OTP_KEY2, "");
            jsonRequestObject.put(MASAuthOTPConsts.CRED_VERSION, credentialVersion);
            jsonRequestObject.put(MASAuthOTPConsts.TOKEN_TYPE_KEY, MASAuthOTPConsts.TokenType.user_jwt);
        } catch (JSONException e) {
            Callback.onError(callback, e);
            return null;
        }
        return jsonRequestObject;
    }

    private String getVerifyOTPEndpoint(String userName) throws JSONException {
        Log.d(TAG,"getting verify OTP end point");
//        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("verifyotp_endpoint_path") + userName;
        String otpUrl = null;
        try {
            otpUrl = jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("verifyotp_endpoint_path")  + userName;
        } catch (Exception e ) {
            Log.w(TAG,"Your msso config looks old, please consider updating to the new one.");
        }
        if (otpUrl == null || "".equals(otpUrl) ) {
            otpUrl = jsonObject.getJSONObject("custom").getJSONObject("masaa_auth_endpoints").getString("verifyotp_endpoint_path") + userName;
        }
        return otpUrl;
    }

    private void loginWithIDToken(String idToken, String tokenType, final MASCallback callback) {
        Log.d(TAG, "login with IDToken");

        MASIdToken masIdToken = new MASIdToken.Builder().value(idToken).type(tokenType).build();
        MASUser.login(masIdToken, new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser masUser) {
                Log.d(TAG, "login with IDToken success");
                Callback.onSuccess(callback, masUser);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "login with IDToken failed");
                Callback.onError(callback, throwable);
            }
        });
    }

    private static JSONObject getConfig(String filename, Context context) {
        Log.d(TAG, "getting config file details");
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
            Log.d(TAG, "returning config json object");
            return new JSONObject(jsonConfig.toString());
        } catch (IOException | JSONException exception) {
            Log.d(TAG, String.format("error reading config file: %s", exception.toString()));
            throw new IllegalArgumentException("Unable to read Json Configuration file: " + filename, exception);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG,"error closing stream");
                    //Ignore
                }
            }
        }
    }

    /**
     * Api request: Issuance create AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void createAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE,profileName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAOTPUtil.getCreateAOTPEndpoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    createRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    /**
     * Api request: Issuance delete AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void deleteAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {
        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            String url = MASAOTPUtil.getDeleteAOTPEndPoint();
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);

            if (profileName != null && !"".equals(profileName))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE, profileName);

            if(clientTxnId != null && !"".equals(clientTxnId))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAOTPUtil.appendQueryParamsToURL(url, queryParam);
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    deleteRequest = request
                    .delete(MASRequestBody.jsonBody(jsonRequest))
                    .header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(deleteRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    /**
     * Api request: Issuance disable AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */

    public void disableAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {
        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE,profileName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);
            String url = MASAOTPUtil.getDisableAOTPEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    createRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    private MASRequest.MASRequestBuilder getMasRequestBuilder(MASAuthOTPCustomRequestData authOTPCustomRequestData, String url) {
        url = MASAOTPUtil.appendParameters(url,authOTPCustomRequestData.getQueryParams());
        MASRequest.MASRequestBuilder request = null;
        try {
            request = new MASRequest.MASRequestBuilder(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        MASAOTPUtil.appendHeaders(request, authOTPCustomRequestData.getHeaders());
        if (authOTPCustomRequestData != null && authOTPCustomRequestData.isPublic()) {
            request.setPublic();
        }
        return request;
    }

    /**
     * Api request: Issuance download AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void downloadAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {

        try {

            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            String url = MASAOTPUtil.getDownloadAOTPEndPoint();
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);

            if (profileName != null && !"".equals(profileName))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE, profileName);

            if(clientTxnId != null && !"".equals(clientTxnId))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAOTPUtil.appendQueryParamsToURL(url, queryParam);
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);
            MASRequest
                    deleteRequest = request
                    .setPublic()
                    .header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(deleteRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    /**
     * Api request: Issuance enable AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void enableAOTP( MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {

        try {

            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE,profileName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAOTPUtil.getEnableAOTPEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    createRequest = request
                    .setPublic()
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    /**
     * Api request: Issuance fetch AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void fetchAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams, final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String password = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            String url = MASAOTPUtil.getFetchAOTPEndPoint();
            HashMap<String, String> queryParam = new HashMap<String, String>();
            if (orgName != null && !"".equals(orgName))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME, orgName);

            if (password != null && !"".equals(password))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PASSWORD, password);

            if(clientTxnId != null && !"".equals(clientTxnId))
                queryParam.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID, clientTxnId);

            url = MASAOTPUtil.appendQueryParamsToURL(url, queryParam);
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    deleteRequest = request
                    .header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(deleteRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    /**
     * Api request: Issuance re-issue AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void reIssueAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {

        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE,profileName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAOTPUtil.getReIssueAOTPEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    createRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    /**
     * Api request: Issuance reset AOTP
     * @param masAotpIssuanceRequestParams The masAidIssuanceRequestParams contains the optional parameters(userName, orgName ,password, clientTnxID) and MASAuthIDCustomRequestData contains list of headers and parameters
     * @param callback The callback function to return the response message back to the application
     */
    public void resetAOTP(MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams,final MASCallback<MASResponse<JSONObject>> callback) {
        try {
            String username = masAotpIssuanceRequestParams.getUserName();
            String orgName = masAotpIssuanceRequestParams.getOrgName();
            String profileName = masAotpIssuanceRequestParams.getProfileName();
            String clientTxnId = masAotpIssuanceRequestParams.getClientTxnId();
            MASAuthOTPCustomRequestData authOTPCustomRequestData = masAotpIssuanceRequestParams.getMasAuthOTPCustomRequestData();

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_ORGNAME,orgName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_PROFILE,profileName);
            jsonRequest.put(MASAuthOTPConsts.REQUEST_PARAM_KEY_CLIENTTXNID,clientTxnId);

            String url = MASAOTPUtil.getResetAOTPEndPoint();
            MASRequest.MASRequestBuilder request = getMasRequestBuilder(authOTPCustomRequestData, url);

            MASRequest
                    createRequest = request
                    .post(MASRequestBody.jsonBody(jsonRequest)).header(MASAuthOTPConsts.REQUEST_HEADER_KEY_USERNAME,username)
                    .build();


            MAS.invoke(createRequest, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {

                    callback.onSuccess(result);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(e);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }
}