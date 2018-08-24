package com.ca.apim.mas.authotp.model;

import java.util.HashMap;

/**
 *   MASAuthOTPCustomRequestData class which contains the custom headers and parameters.
 *   Headers and query parameters can be name value pair.
 */

public class MASAuthOTPCustomRequestData {

    private HashMap<String, String> headers;

    private HashMap<String, String> queryParams;

    private boolean isPublic = false;

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(HashMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
