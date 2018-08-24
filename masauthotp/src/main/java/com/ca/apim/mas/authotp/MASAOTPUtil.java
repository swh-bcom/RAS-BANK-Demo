package com.ca.apim.mas.authotp;

import android.content.SharedPreferences;
import android.util.Log;

import com.arcot.aotp.lib.OTP;
import com.arcot.aotp.lib.store.DeviceLock;
import com.ca.mas.core.context.DeviceIdentifier;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Created by mujmo02 on 9/15/2017.
 */

public class MASAOTPUtil {

    private static final String TAG = "MASAOTPUtil";
    static public String getPrintableErrorMessage (Throwable e) {
        if (e instanceof MASAuthOTPException) {
            try {
                return ((MASAuthOTPException) e).getResponse().getString("error_details");
            } catch (JSONException e1) {
                e.printStackTrace();
            }
        }
        return e.getMessage();
    }

    private static final String PREFS_NAME = "DEVICE_ID_AUTHOTP";

    static void persistData(String key, String value) {
        key = key.toLowerCase(Locale.getDefault());
        Log.d(TAG, "Persisting data  Key :" + key + "  value :"+value);

        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        // Apply the edits!
        editor.apply();
    }

    static String getPersistedData(String key) {
        key = key.toLowerCase(Locale.getDefault());
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        Log.d(TAG, "Get Persisting data  Key :" + key + "  value :"+settings.getString(key, null));
        return settings.getString(key, null);

    }

    static void deleteAllEntriesInPersistedData () {
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "deleteAllEntriesInPersistedData" );

    }

    static void deleteEntryInPersistedData (String key) {
        key = key.toLowerCase(Locale.getDefault());
        SharedPreferences settings = MAS.getCurrentActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
        Log.d(TAG, "deleteEntryInPersistedData  Key :" + key );
    }

    static void setDeviceLocking (final String deviceId, OTP otp) {

        DeviceLock dl = new DeviceLock() {
            @Override
            public String getKey() {

                return deviceId;
            }
        };
        otp.setDeviceLock(dl);
    }

    private static JSONObject jsonObject;
    public static void setJsonObject(JSONObject jsonObject) {
        MASAOTPUtil.jsonObject = jsonObject;
    }


    static String getCreateAOTPEndpoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("create_aotp_endpoint_path");
    }


    static String getDeleteAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("delete_aotp_endpoint_path");
    }


    static String getDisableAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("disable_aotp_endpoint_path");
    }

    static String getDownloadAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("download_aotp_endpoint_path");
    }

    static String getEnableAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("enable_aotp_endpoint_path");
    }

    static String getFetchAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("fetch_aotp_endpoint_path");
    }


    static String getReIssueAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("reissue_aotp_endpoint_path");
    }


    static String getResetAOTPEndPoint() throws JSONException {
        return jsonObject.getJSONObject("custom").getJSONObject("mas_authotp_endpoints").getString("reset_aotp_endpoint_path");
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
}
