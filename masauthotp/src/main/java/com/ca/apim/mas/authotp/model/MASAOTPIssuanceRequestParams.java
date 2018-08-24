package com.ca.apim.mas.authotp.model;

/**
 *   MASAOTPIssuanceRequestParams class holds optional parameters (username, orgName, profileName, clientTnxID) of the apis
 *  and MASAuthOTPCustomRequestData which contains the custom headers and parameters.
 *   Headers and parameters can be name value pair.
 */

public class MASAOTPIssuanceRequestParams {
    private String userName;
    private String orgName;
    private String profileName;
    private String clientTxnId;
    private MASAuthOTPCustomRequestData masAuthOTPCustomRequestData;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getClientTxnId() {
        return clientTxnId;
    }

    public void setClientTxnId(String clientTxnId) {
        this.clientTxnId = clientTxnId;
    }

    public MASAuthOTPCustomRequestData getMasAuthOTPCustomRequestData() {
        return masAuthOTPCustomRequestData;
    }

    public void setMasAuthOTPCustomRequestData(MASAuthOTPCustomRequestData masAuthOTPCustomRequestData) {
        this.masAuthOTPCustomRequestData = masAuthOTPCustomRequestData;
    }
}
