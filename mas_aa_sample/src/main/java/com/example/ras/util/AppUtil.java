package com.example.ras.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.example.ras.LoginActivity;
import com.example.ras.R;
import com.example.ras.SelectRegistrationCredActivity;
import com.example.ras.error.ErrorObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by mujmo02 on 8/25/2017.
 */

public class AppUtil {

    private static final String AA_ORGNAME = "AA_ORGNAME";
    private static final String AA_NAMESPACE = "AA_NAMESPACE";
    private static final String AA_AUTHID_PROFILE = "AA_AUTHID_PROFILE";
    private static final String AA_AUTHID_POLICY = "AA_AUTHID_POLICY";
    private static final String AA_AUTHID_ADDITIONAL_PARAMS = "AA_AUTHID_ADDITIONAL_PARAMS";
    private static final String STEP_UP_AUTH_MECHANISM = "STEP_UP_AUTH_MECHANISM";
    private static final String LOCATION_STEP_UP_AUTH_MECHANISM = "LOCATION_STEP_UP_AUTH_MECHANISM";


    public static final String FIDO_LOGIN = "FIDO_LOGIN";
    private static final String LSD_LOGIN = "LSD_LOGIN";
    private static final String ENABLE_BIOMETRIC_LOGIN = "ENABLE_BIOMETRIC_LOGIN";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private static final String CUSTOM_MSSO_CONFIG = "CUSTOM_MSSO_CONFIG";
    private static final String LOCATION = "LOCATION";
    private static final String DEVICE_REG_STEP_UP_ENABLED = "DEVICE_REG_STEP_UP_ENABLED";




    public static void initAppConstants (Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        ApplicationConstants.ORGNAME = sharedPref.getString(AA_ORGNAME, activity.getResources().getString(R.string.aa_orgname));
        ApplicationConstants.NAMESPACE = sharedPref.getString(AA_NAMESPACE, activity.getResources().getString(R.string.aa_namespace));
        ApplicationConstants.AUTHID_PROFILE = sharedPref.getString(AA_AUTHID_PROFILE, null);
        ApplicationConstants.AUTHID_POLICY = sharedPref.getString(AA_AUTHID_POLICY, null);
        ApplicationConstants.AUTHID_DEFAULT_ADDITIONAL_PARAMS = AppUtil.parseAdditionalParams(sharedPref.getString(AA_AUTHID_ADDITIONAL_PARAMS, null));

        ApplicationConstants.FIDO_LOGIN = sharedPref.getBoolean(FIDO_LOGIN, true);
        ApplicationConstants.CONTACT_US_URL = activity.getResources().getString(R.string.contactus_url);
        ApplicationConstants.ATM_NEARBY_URL= activity.getResources().getString(R.string.atm_url);
        ApplicationConstants.BRANCHES_NEARBY_URL = activity.getResources().getString(R.string.branches_url);
        ApplicationConstants.LSD_LOGIN = sharedPref.getBoolean(LSD_LOGIN, false);
        ApplicationConstants.STEP_UP_AUTH_MECHANISM = sharedPref.getString(STEP_UP_AUTH_MECHANISM, null);
        ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM = sharedPref.getString(LOCATION_STEP_UP_AUTH_MECHANISM, null);

        ApplicationConstants.ENABLE_BIOMETRIC_LOGIN = sharedPref.getBoolean(ENABLE_BIOMETRIC_LOGIN, true);
        ApplicationConstants.LOCATION = sharedPref.getString(LOCATION, "United States (ALLOW)");
        ApplicationConstants.DEVICE_REG_STEP_UP_ENABLED = sharedPref.getBoolean(DEVICE_REG_STEP_UP_ENABLED, true);


        ApplicationConstants.CUSTOM_MSSO_CONFIG = convertToJSONObject (sharedPref.getString(CUSTOM_MSSO_CONFIG, null));


    }

    public static void updateAppConstants (Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AA_ORGNAME, ApplicationConstants.ORGNAME);
        editor.putString(AA_NAMESPACE, ApplicationConstants.NAMESPACE);
        editor.putString(AA_AUTHID_PROFILE, ApplicationConstants.AUTHID_PROFILE);
        editor.putString(AA_AUTHID_POLICY, ApplicationConstants.AUTHID_POLICY);
        editor.putString(AA_AUTHID_ADDITIONAL_PARAMS, AppUtil.convertAdditionalParamsToString(ApplicationConstants.AUTHID_DEFAULT_ADDITIONAL_PARAMS));

        editor.putBoolean(FIDO_LOGIN, ApplicationConstants.FIDO_LOGIN);
        editor.putBoolean(LSD_LOGIN, ApplicationConstants.LSD_LOGIN);
        editor.putString(STEP_UP_AUTH_MECHANISM, ApplicationConstants.STEP_UP_AUTH_MECHANISM );
        editor.putString(LOCATION_STEP_UP_AUTH_MECHANISM, ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM );

        editor.putBoolean(ENABLE_BIOMETRIC_LOGIN, ApplicationConstants.ENABLE_BIOMETRIC_LOGIN);
        editor.putString(CUSTOM_MSSO_CONFIG, convertJSONObjectToString(ApplicationConstants.CUSTOM_MSSO_CONFIG));
        editor.putString(LOCATION, ApplicationConstants.LOCATION);
        editor.putBoolean(DEVICE_REG_STEP_UP_ENABLED, ApplicationConstants.DEVICE_REG_STEP_UP_ENABLED);




        editor.commit();
    }

    public static ErrorObject getMASErrorMessage (Throwable throwable) {
        ErrorObject response = new ErrorObject();

        if (throwable.getCause() instanceof TargetApiException
                &&
                ((TargetApiException) (throwable.getCause())).getResponse().getBody().getContent() instanceof JSONObject) {


            JSONObject errorDataJson = (JSONObject) ((TargetApiException) (throwable.getCause())).getResponse().getBody().getContent();

            try {
                response.setErrorDetail(errorDataJson.getString("error_details"));
                response.setErrorDesc(errorDataJson.getString("error_description"));
                response.setResponseCode(errorDataJson.getString("response_code"));
                response.setReasonCode(errorDataJson.getString("reason_code"));
                response.setError(errorDataJson.getString("error"));
                return response;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(throwable instanceof URISyntaxException) {

            response.setError(((URISyntaxException) throwable).getReason());
            response.setErrorDesc(throwable.getMessage());
            response.setResponseCode("9001");
            response.setReasonCode("0");

        }else{
            response.setErrorDetail(throwable.getCause().toString());
            response.setErrorDesc(throwable.getMessage());
            response.setResponseCode("9001");
            response.setReasonCode("0");
            response.setError(null);
        }
        return response;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            int[] networkTypes = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE};
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo netInfo = cm.getNetworkInfo(networkType);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void showAlertDialog(final Context context, String msg, String title, final boolean doFinish) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        if (context instanceof SelectRegistrationCredActivity  && doFinish) {
                            context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            ((Activity) context).finish();
                        }

                    }
                })
                .show();
    }

    public static Map<String,String> parseAdditionalParams(String additionalParams) {
        Map<String,String> response = new HashMap<String, String>();
        try {
            String[] paramList = additionalParams.split(Pattern.quote("||"));
            for (int i = 0; i < paramList.length; i++) {
                String[] pair = paramList[i].split(":");
                response.put(pair[0], pair[1]);
            }
        } catch (Exception e ) {
            Log.e("AppUtil", e.getMessage());

        }
        return response;
    }



    public static HashMap<String,String> addBasicAuthHeaders(String additionalHeaders) {
        HashMap<String,String> headers = new HashMap<>();
        if(!additionalHeaders.equals("")&& additionalHeaders != null) {
            String[] paramList1 = additionalHeaders.split(Pattern.quote("||"));

                for (int i = 0; i < paramList1.length; i++) {
                    String[] pair = paramList1[i].split(":");
                    if(pair.length == 2) {
                        if (pair[0].equals("username")) {
                            headers.put("username", pair[1]);
                        } else if (pair[0].equals("password")) {
                            headers.put("password", pair[1]);
                        } else {
                            headers.put(pair[0], pair[1]);
                        }
                    }else {
                        Log.v("Please enter","Correct values ");
                    }
                }


        }
       return headers;
    }
    public static HashMap<String, String> addIssuanceAdditionalParams(String additionalParams) {
            HashMap<String, String> params = new HashMap<>();
            if(!additionalParams.equals("")&& additionalParams != null) {
                String[] paramList1 = additionalParams.split(Pattern.quote("||"));
                for (int i = 0; i < paramList1.length; i++) {
                    String[] pair = paramList1[i].split(":");
                    if (pair[0].equals("username")) {
                        params.put("username",pair[1]);
                    }else if(pair[0].equals("password")){
                        params.put("password",pair[1]);
                    }else {
                        params.put(pair[0],pair[1]);
                    }

                }
            }
        return params;
    }

    public  static String convertAdditionalParamsToString (Map<String, String> additionalParams) {
        String response = "";

        if (additionalParams!=null && additionalParams.size()>0) {
            for (Map.Entry<String, String> entry : additionalParams.entrySet())
            {
                //System.out.println(entry.getKey() + "/" + entry.getValue());
                response += entry.getKey()+ ":"+entry.getValue()+"||";
            }
            response = response.substring(0, response.length() -2);
        }

        return response;
    }


    public static void masStart(Context context) {
        if (ApplicationConstants.CUSTOM_MSSO_CONFIG == null) {
            MAS.start(context, true);
        } else {
            MAS.start(context,ApplicationConstants.CUSTOM_MSSO_CONFIG);
        }
    }

    public static String appendQueryParameters(String url, HashMap<String, String> queryParams) {
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


    private static JSONObject convertToJSONObject(String str) {
        JSONObject jsonObject = null;
        try {
            if (str != null && !"".equals(str))
                jsonObject = new JSONObject(str);
        } catch (Exception e) {

        }
        return jsonObject;
    }


    private static String convertJSONObjectToString(JSONObject customMssoConfig) {
        if (customMssoConfig != null ) {
            return customMssoConfig.toString();
        }
        return null;
    }



    public static String prettifyJson(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }
}
