package com.ca.apim.mas.authid;

import com.arcot.aid.lib.AIDException;
import com.arcot.aid.lib.Account;

import java.util.Vector;

/**
 * Model Class to hold AID Account data
 */

public class AIDAccount {

    public String accountId;
    public String org;
    public String ns;
    public String logoUrl;
    public String provisioningURL;
    public String name;
    public long creationTime;
    public long lastUsed;
    public int uses;

    private Account account;

    public AIDAccount(Account account) throws AIDException {
        this.account = account;
        if (account != null) {
            this.accountId = account.accountId;
            this.org = account.org;
            this.ns = account.ns;
            this.logoUrl = account.logoUrl;
            this.provisioningURL = account.provisioningURL;
            this.name = account.name;
            this.creationTime = account.creationTime;
            this.lastUsed = account.lastUsed;
            this.uses = account.uses;
        }
    }

    public String getId() {
        return account.getId();
    }

    public String getBase64Aid() throws AIDException {
        return account.getBase64Aid();
    }

    public String getAttribute(String var1) throws AIDException {
        return account.getAttribute(var1);
    }

    public void setAttribute(String var1, String var2) throws AIDException {
        account.setAttribute(var1, var2);
    }
}
