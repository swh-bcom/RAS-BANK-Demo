package com.ca.apim.mas.authid.model;


/**
 *   MASAIDIssuanceRequestParams class holds optional parameters (username, orgName, profileName, clientTnxID) of the apis
 *  and MASAuthIDCustomRequestData which contains the custom headers and parameters.
 *   Headers and parameters can be name value pair.
 */
public class MASAIDIssuanceRequestParams {
    private String userName;
    private String orgName;
    private String password;
    private String clientTxnId;
    private MASAuthIDCustomRequestData masAuthIDCustomRequestData;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientTxnId() {
        return clientTxnId;
    }

    public void setClientTxnId(String clientTxnId) {
        this.clientTxnId = clientTxnId;
    }

    public MASAuthIDCustomRequestData getMasAuthIDCustomRequestData() {
        return masAuthIDCustomRequestData;
    }

    public void setMasAuthIDCustomRequestData(MASAuthIDCustomRequestData masAuthIDCustomRequestData) {
        this.masAuthIDCustomRequestData = masAuthIDCustomRequestData;
    }
}
