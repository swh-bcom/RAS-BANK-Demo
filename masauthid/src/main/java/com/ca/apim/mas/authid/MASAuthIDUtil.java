package com.ca.apim.mas.authid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.arcot.aid.lib.AIDException;
import com.ca.mas.core.context.DeviceIdentifier;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mujmo02 on 04/10/17.
 */

public class MASAuthIDUtil {
    private static final String PREFS_NAME = "DEVICE_ID_AUTHID";

    final static private String TAG = "MASAuthIDUtil";

    static void persistData(String key, String value) {
        key = key.toLowerCase(Locale.getDefault());
        Log.d(TAG, "Persisting data  Key :" + key + "  value :" + value);


        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);

        // Changing to apply. Commit() returns the value, but apply don't
        editor.apply();
    }

    static String getPersistedData(String key) {
        key = key.toLowerCase(Locale.getDefault());
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        Log.d(TAG, "Get Persisting data  Key :" + key + "  value :" + settings.getString(key, null));
        return settings.getString(key, null);

    }

    static void deleteAllEntriesInPersistedData() {
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();

    }

    static void deleteEntryInPersistedData(String key) {
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();

        Log.d(TAG, "deleteEntryInPersistedData  Key :" + key);

    }


    private static JSONObject jsonObject;
    public static void setJsonObject(JSONObject jsonObject) {
        MASAuthIDUtil.jsonObject = jsonObject;
    }


    static String getVerifySignChallengeEndpoint() throws JSONException {
        String challengeUrl = null;
        try {
            challengeUrl = jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("verifychallenge_endpoint_path");
        } catch (Exception e) {
            Log.w(TAG,"Your msso config looks old, please consider updating to the new one.");
        }
        if (challengeUrl == null || "".equals(challengeUrl)) {
            challengeUrl = jsonObject.getJSONObject("custom").getJSONObject("masaa_auth_endpoints").getString("verifychallenge_endpoint_path");
        }
        return challengeUrl;
    }

    static String  getChallengeDownloadEndpoint() throws JSONException {
//        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("getchallenge_endpoint_path");
        String challengeUrl = null;
        try {
            challengeUrl = jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("getchallenge_endpoint_path");
        } catch (Exception e ) {
            Log.w(TAG,"Your msso config looks old, please consider updating to the new one.");
        }
        if (challengeUrl == null || "".equals(challengeUrl) ) {
            challengeUrl = jsonObject.getJSONObject("custom").getJSONObject("masaa_auth_endpoints").getString("getchallenge_endpoint_path");
        }
        return challengeUrl;
    }

    static String getCreateAuthIdEndpoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("create_authid_endpoint_path");
    }


    static String getDeleteAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("delete_authid_endpoint_path");
    }


    static String getDisableAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("disable_authid_endpoint_path");
    }

    static String getDownloadAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("download_authid_endpoint_path");
    }

    static String getEnableAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("enable_authid_endpoint_path");
    }

    static String getFetchAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("fetch_authid_endpoint_path");
    }


    static String getReIssueAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("reissue_authid_endpoint_path");
    }


    static String getResetAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("reset_authid_endpoint_path");
    }

    static String getResetNotesAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("reset_notes_authid_endpoint_path");
    }

    static String getResetValidityAuthIDEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authid_endpoints").getString("reset_validity_authid_endpoint_path");
    }



    static String appendQueryParamsToURL (String url, HashMap<String, String> queryParams) {
        if (queryParams == null || queryParams.size() == 0) {
            return url;
        }
        url +="?";
        for (Map.Entry<String, String> entry : queryParams.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            url += (entry.getKey()+"="+entry.getValue()+"&");
        }
        url = url.substring(0, url.length()-1);
        return url;
    }


    static String appendQueryParamsListPairToURL (String url, List<Pair<String, String>> queryParams) {
        if (queryParams == null || queryParams.size() == 0) {
            return url;
        }
        url +="?";
        Iterator<Pair<String,String>> iterator = queryParams.iterator();
        while (iterator.hasNext()) {
            Pair<String,String> entry = (Pair<String,String>) iterator.next();
            url += (entry.first+"="+entry.second+"&");
        }

        url = url.substring(0, url.length()-1);
        return url;
    }


    public static MASRequest.MASRequestBuilder addHeaders(String url, Map<String, List<String>> headers) {
        MASRequest.MASRequestBuilder builder = null;
        try {
            builder = new MASRequest.MASRequestBuilder(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                builder.header(key,value);
            }
        }
        return builder;
    }


    public static MASRequest.MASRequestBuilder appendHeaders(MASRequest.MASRequestBuilder requestBuilder, HashMap<String, String> headers) {
        if(headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                requestBuilder.header(key, entry.getValue());
            }
        }
        return requestBuilder;
    }



    static String appendParameters(String url, HashMap<String, String> queryParams) {
        if (queryParams == null || queryParams.size() == 0) {
            return url;
        }
        if(url.contains("?")){
            url +="&";
        }else {
            url +="?";
        }

        for (Map.Entry<String, String> entry : queryParams.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            url += (entry.getKey()+"="+entry.getValue()+"&");
        }
        url = url.substring(0, url.length()-1);
        return url;
    }


    static boolean checkForSpecialCharacters(String str){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        if(m.find()){
            return true;
        }else{
        return false;
        }
    }

    static MASAuthIDException parseThrowable(Throwable throwable) {

        if (throwable instanceof MASAuthIDException) {
            return  (MASAuthIDException) throwable;
        }
        if (throwable.getCause() instanceof  MASAuthIDException) {
            return (MASAuthIDException)(throwable.getCause());
        }
        String error = MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR;
        String errorDetails = throwable.getMessage();
        String errorDescription = throwable.getMessage();
        String responseCode = MASAuthIDConsts.RESPONSE_CODE_9011;
        String reasonCode = MASAuthIDConsts.REASON_CODE_0;


        if (throwable.getCause() instanceof TargetApiException
                && ((TargetApiException) (throwable.getCause())).getResponse() != null
                && ((TargetApiException) (throwable.getCause())).getResponse().getBody() != null
                && ((TargetApiException) (throwable.getCause())).getResponse().getBody().getContent() != null
                && ((TargetApiException) (throwable.getCause())).getResponse().getBody().getContent() instanceof JSONObject
                )
             {
                 JSONObject errorDataJson = (JSONObject)((TargetApiException) (throwable.getCause())).getResponse().getBody().getContent();
                 try {
                     errorDetails = errorDataJson.getString("error_details");
                     errorDescription = errorDataJson.getString("error_description");
                     responseCode = errorDataJson.getString("response_code");
                     reasonCode= errorDataJson.getString("reason_code");
                     error = errorDataJson.getString("error");
                 } catch (JSONException e) {
                     Log.e(TAG, e.getMessage());
                     return new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, MASAuthIDConsts.AID_EXCEPTION, e.getMessage(), MASAuthIDConsts.REASON_CODE_0, MASAuthIDConsts.RESPONSE_CODE_9011);
                 }

        }
        return new MASAuthIDException(error,  errorDescription, errorDetails, reasonCode, responseCode);
    }

    static MASAuthIDException parseAIDException (AIDException e ) {
        return new MASAuthIDException(MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthIDConsts.REASON_CODE_0, AuthIdOrchestrator.mapErrorCode(e.getCode()));

    }


    static MASAuthIDException parseException(Exception e) {

        String error = MASAuthIDConsts.STRONG_AUTHENTICATION_ERROR;
        String errorDetails = e.getMessage();
        String errorDescription = "";
        String responseCode = MASAuthIDConsts.RESPONSE_CODE_9011;
        String reasonCode = MASAuthIDConsts.REASON_CODE_0;
        return new MASAuthIDException(error,  errorDescription, errorDetails, reasonCode, responseCode);
    }
}
