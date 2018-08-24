package com.ca.mas.pushnotification;

import org.json.JSONException;
import org.json.JSONObject;

public class MASPushNotificationException extends Exception {

    JSONObject response;

    public MASPushNotificationException(String error, String error_description, String error_details, String reason_code, String response_code) {
        super(error_details);
        buildResponse(error, error_description, error_details, reason_code, response_code);
    }

    private void buildResponse(String error, String error_description, String error_details, String reason_code, String response_code) {
        response = new JSONObject();
        try {
            response.put(MASPushNotificationConsts.MAS_AA_EXCEPTION_ERROR, error);
            response.put(MASPushNotificationConsts.MAS_AA_EXCEPTION_ERROR_DESCRIPTION, error_description);
            response.put(MASPushNotificationConsts.MAS_AA_EXCEPTION_ERROR_DETAILS, error_details);
            response.put(MASPushNotificationConsts.MAS_AA_EXCEPTION_REASON_CODE, reason_code);
            response.put(MASPushNotificationConsts.MAS_AA_EXCEPTION_RESPONSE_CODE, response_code);
        } catch (JSONException e) {
        }
    }

    public JSONObject getResponse() {
        return response;
    }
}
