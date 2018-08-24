package com.ca.apim.mas.authotp;

import com.arcot.aotp.lib.Account;
import com.arcot.aotp.lib.OTPException;

import java.util.Hashtable;

/**
 * Model class to hold AOTP Account
 */

public class AOTPAccount {


    public String accountId;
    public String org;
    public String ns;
    public String name;
    public String algo;
    public long creationTime;
    public long expiryTime;
    public long lastUsed;
    public int uses;
    public String logoUrl;
    public String provUrl;
    public String provisioningURL;
    protected Hashtable att;
    Account account = null;

    public AOTPAccount(Account account) throws OTPException {

        if (account == null)
            return;

        this.account = account;

        this.accountId = account.accountId;
        this.org = account.org;
        this.creationTime = account.creationTime;
        this.expiryTime = account.expiryTime;
        this.algo = account.algo;
        this.lastUsed = account.lastUsed;
        this.logoUrl = account.logoUrl;
        this.uses = account.uses;
        this.name = account.name;
        this.ns = account.ns;
        this.provisioningURL = account.provisioningURL;
        this.provUrl = account.provUrl;

        this.att = new Hashtable();
    }

    public String getId() {
        return account.getId();
    }

    public String getAttribute(String var1) throws MASAuthOTPException {
        try {
            return account.getAttribute(var1);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    public void setAttribute(String var1, String var2) throws MASAuthOTPException {
        try {
            account.setAttribute(var1, var2);
        } catch (OTPException e) {
            throw new MASAuthOTPException(MASAuthOTPConsts.STRONG_AUTHENTICATION_ERROR, "-", e.getMessage(), MASAuthOTPConsts.REASON_CODE_0, AOTP.mapErrorCode(e.getCode()));
        }
    }

    public int getMinPINLength() {
        return account.getMinPINLength();
    }

    public int getPINType() {
        return account.getPINType();
    }

}
