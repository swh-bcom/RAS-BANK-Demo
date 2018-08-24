package com.example.ras.error;

import com.google.gson.Gson;

/**
 * Created by mujmo02 on 9/8/2017.
 */

public class ErrorObject {

    String error;
    String errorDesc;
    String errorDetail;
    String responseCode;
    String reasonCode;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }






    public String toString( ) {
//        Gson gson = new Gson();
//        return gson.toJson(this);
        String s = "{ \n error = "+ getError() + ";" + "\n error_description = "+ getErrorDesc() + ";" + "\n error_details = " + getErrorDetail() +
                "\n reason_code = "+ getReasonCode() + "\n response_code = "+ getResponseCode() + "}" ;
        return s;
    }

    //        return Response.ok(gson.toJson(yourClass)).build();
//
//    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//    String json = ow.writeValueAsString(object);
}
