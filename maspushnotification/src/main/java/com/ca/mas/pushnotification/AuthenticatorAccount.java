package com.ca.mas.pushnotification;


import com.ca.sec.aa.mobileAuth.lib.Account;
import com.ca.sec.aa.mobileAuth.lib.CAMobileAuthenticatorException;
import java.util.Hashtable;

/**
 * Model class to hold Authentication Account data
 */

public class AuthenticatorAccount {

    Account account;

    public static final String A_PROTOCOLVER = "PVER";
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
    public String authUrl;
    public String deviceToken;
    public String deviceId;
    public String deviceType;
    public String deviceOS;
    public String deviceName;
    public boolean isAccountDeleted;
    protected boolean isResetSupported;
    protected String protocolVersion;
    protected String version;
    protected String storeId;
    private static final String A_USER = "USER";
    private static final String A_ORG = "ORG_";
    private static final String A_TYPE = "TYPE";
    private static final String A_CROSS_DOMAIN = "crossdomain";
    private static final String A_DOMAIN = "Domain";
    private static final String A_NAME = "Name";
    private static final String A_CREATION = "Creation";
    private static final String A_EXPIRY = "Expiry";
    private static final String A_LASTUSED = "Last";
    private static final String A_USES = "Uses";
    private static final String A_LOGOURL = "LogoUrl";
    private static final String A_PROVURL = "ProvUrl";
    public static final String A_PINTYPE = "PTYP";
    protected Hashtable att;


    public AuthenticatorAccount(Account account) {

        this.account = account;

        this.accountId = account.accountId;
        this.algo = account.algo;
        this.authUrl = account.authUrl;
        this.creationTime = account.creationTime;
        this.deviceId = account.deviceId;
        this.deviceName = account.deviceName;
        this.deviceOS = account.deviceOS;
        this.deviceToken = account.deviceToken;
        this.deviceType = account.deviceType;
        this.expiryTime = account.expiryTime;
        this.isAccountDeleted = account.isAccountDeleted;
        this.lastUsed = account.lastUsed;
        this.logoUrl = account.logoUrl;
        this.name = account.name;
        this.ns = account.ns;
        this.org = account.org;
        this.provUrl = account.provUrl;
        this.uses = account.uses;
    }

    public String getId() {
       return account.getId();
    }

//    public String getDeviceId() {
//        return account.getDeviceId();
//    }
//
//    public String getDeviceType() {
//        return account.getDeviceType();
//    }
//
//    public String getDeviceOS() {
//        return account.getDeviceOS();
//    }
//
//    public String getDeviceName() {
//        return account.getDeviceName();
//    }
//
//    public void setDeviceId(String deviceId) {
//        account.setDeviceId(deviceId);
//    }
//
//    public void setDeviceType(String deviceType) {
//        account.setDeviceType(deviceType);
//    }

//    public void setDeviceOS(String deviceOS) {
//        account.setDeviceOS(deviceOS);
//    }
//
//    public void setDeviceName(String deviceName) {
//        account.setDeviceName(deviceName);
//    }
//
//    public String getAuthUrl() {
//        return account.getAuthUrl();
//    }
//
//    public String getDeviceToken() {
//        return account.getDeviceToken();
//    }
//
//    public void setAuthUrl(String authUrl) {
//        account.setAuthUrl(authUrl);
//    }
//
//    public void setDeviceToken(String deviceToken) {
//        account.setDeviceToken(deviceToken);
//    }
//
//    public String getAccountId() {
//        return account.getAccountId();
//    }

    public String getOrg() {
        return account.getOrg();
    }

    public String getNs() {
        return account.getNs();
    }

    public String getName() {
        return account.getName();
    }

//    public String getAlgo() {
//        return account.getAlgo();
//    }
//
//    public long getCreationTime() {
//        return account.getCreationTime();
//    }
//
//    public long getExpiryTime() {
//        return account.getExpiryTime();
//    }
//
//    public long getLastUsed() {
//        return account.getLastUsed();
//    }
//
//    public int getUses() {
//        return account.getUses();
//    }
//
//    public String getLogoUrl() {
//        return account.getLogoUrl();
//    }

//    public String getProvUrl() {
//        return account.getProvUrl();
//    }
//
//    public boolean isResetSupported() {
//        return account.isResetSupported();
//    }
//
//    public String getProtocolVersion() {
//        return account.getProtocolVersion();
//    }

    public String getVersion() {
        return account.getVersion();
    }

//    public String getStoreId() {
//        return account.getStoreId();
//    }
//
//    public Hashtable getAtt() {
//        return account.getAtt();
//    }

//    public void setAccountId(String accountId) {
//        account.setAccountId(accountId);
//    }

    public void setOrg(String org) {
        account.setOrg(org);
    }

//    public void setNs(String ns) {
//        account.setNs(ns);
//    }

    public void setName(String name) {
        account.setName(name);
    }

//    public void setAlgo(String algo) {
//        account.setAlgo(algo);
//    }

//    public void setCreationTime(long creationTime) {
//        account.setCreationTime(creationTime);
//    }
//
//    public void setExpiryTime(long expiryTime) {
//        account.setExpiryTime(expiryTime);
//    }
//
//    public void setLastUsed(long lastUsed) {
//        account.setLastUsed(lastUsed);
//    }
//
//    public void setUses(int uses) {
//        account.setUses(uses);
//    }
//
//    public void setLogoUrl(String logoUrl) {
//        account.setLogoUrl(logoUrl);
//    }
//
//    public void setProvUrl(String provUrl) {
//        account.setProvUrl(provUrl);
//    }
//
//    public void setResetSupported(boolean isResetSupported) {
//        account.setResetSupported(isResetSupported);
//    }
//
//    public void setProtocolVersion(String protocolVersion) {
//        account.setProtocolVersion(protocolVersion);
//    }

    public void setVersion(String version) {
        account.setVersion(version);
    }

//    public void setStoreId(String storeId) {
//        account.setStoreId(storeId);
//    }
//
//    public void setAtt(Hashtable att) {
//        account.setAtt(att);
//    }
//
//    public boolean getIsAccountDeleted() {
//        return account.getIsAccountDeleted();
//    }
//
//    public void setIsAccountDeleted(boolean isAccountDeleted) {
//        account.setIsAccountDeleted(isAccountDeleted);
//    }
//
//    public void addDomain(String domain) throws CAMobileAuthenticatorException {
//        account.addDomain(domain);
//    }
}
