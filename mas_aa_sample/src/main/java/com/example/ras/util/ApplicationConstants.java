package com.example.ras.util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mujmo02 on 8/25/2017.
 */

public class ApplicationConstants {


    public static String ORGNAME = "apiinc";

    public static String NAMESPACE = "ca";
    public static String AUTHID_PROFILE;
    public static String AUTHID_POLICY;
    public static Map<String, String> AUTHID_DEFAULT_ADDITIONAL_PARAMS;

    public static boolean ENABLE_BIOMETRIC_LOGIN = true;
    public static boolean WAS_BBA_ENABLED_PREVIOUSLY = false;

    public static boolean FIDO_LOGIN;
    public static boolean LSD_LOGIN;
    public static boolean DEVICE_REG_STEP_UP_ENABLED = true;

    public static JSONObject CUSTOM_MSSO_CONFIG;

    //This will completely hide FIDO from the app
    public static boolean HIDE_FIDO_FEATURE = true;

    public static String[] STEP_UP_AUTH_MECHANISMS_LIST  = new String[]{"FIDO", "BIOMETRIC", "OTP"};
    public static String STEP_UP_AUTH_MECHANISM = STEP_UP_AUTH_MECHANISMS_LIST[0];
    public static String LOCATION_STEP_UP_AUTH_MECHANISM = STEP_UP_AUTH_MECHANISMS_LIST[0];


    public static String LOCATION = "United States (ALLOW)";


    public static boolean FROM_SETTINGS_SCREEN = false;

    static {
        if (HIDE_FIDO_FEATURE) {
            STEP_UP_AUTH_MECHANISMS_LIST = new String[]{ "BIOMETRIC", "OTP"};
            STEP_UP_AUTH_MECHANISM = STEP_UP_AUTH_MECHANISMS_LIST[0];

            LOCATION_STEP_UP_AUTH_MECHANISM = STEP_UP_AUTH_MECHANISMS_LIST[0];
        }
    }



    public static String USERNAME;

    public static boolean GOTO_DEVICE_HOME_SCREEN = false;

    public static boolean IS_FUND_TRANSFER = false;


    public enum REGISTERING_FOR { AUTH_ID, AUTH_OTP}
    public static REGISTERING_FOR REG_TYPE;

    public static String CONTACT_US_URL;
    public static String ATM_NEARBY_URL;
    public static String BRANCHES_NEARBY_URL;

    public static String SSO_WEBVIEW_URL;
    public static String SMSESSION;

    public static final String USERNAME_HEADER_KEY = "X-CA-RAS-USERNAME";
    public static final String PASSWORD_HEADER_KEY = "X-CA-RAS-PASSWORD";

    public static float AVAILABLE_BALANCE = 24540;

/*
    public static String getORGNAME() {
        return ORGNAME;
    }

    public static void setORGNAME(String ORGNAME) {
        ApplicationConstants.ORGNAME = ORGNAME;
    }

    public static String getNAMESPACE() {
        return NAMESPACE;
    }

    public static void setNAMESPACE(String NAMESPACE) {
        ApplicationConstants.NAMESPACE = NAMESPACE;
    }
*/


}
