package com.ca.mas.pushnotification;

import android.content.Context;

import com.ca.sec.aa.mobileAuth.lib.Account;
import com.ca.sec.aa.mobileAuth.lib.AuthentcatorDelegate;
import com.ca.sec.aa.mobileAuth.lib.CAMobileAuthenticator;
import com.ca.sec.aa.mobileAuth.lib.CAMobileAuthenticatorException;
import com.ca.sec.aa.mobileAuth.lib.network.CAMobileAuthenticatorCommException;
import com.ca.sec.aa.mobileAuth.lib.network.IArcotAppCallback;
import com.ca.sec.aa.mobileAuth.lib.store.DbStore;
import com.ca.sec.aa.mobileAuth.lib.store.Store;

import java.util.Hashtable;

class MobileAuthenticator {

    CAMobileAuthenticator caMobileAuthenticator = null;

    MobileAuthenticator(Context context) throws CAMobileAuthenticatorCommException {
    }

    public void createAuthenticator(Context context, AuthentcatorDelegate authentcatorDelegate) throws CAMobileAuthenticatorCommException
    {
        caMobileAuthenticator = new CAMobileAuthenticator(context, authentcatorDelegate);
    }

    public void setStore(Store store) {
        caMobileAuthenticator.setStore(store);
    }

    public void registerDevice(String userID, String provisionURL, String activationCode, Hashtable props) throws CAMobileAuthenticatorCommException {
        caMobileAuthenticator.registerDevice(userID, provisionURL, activationCode, props);
    }

    public void authenticateUser(String authData, String userResponse) throws CAMobileAuthenticatorCommException {
        caMobileAuthenticator.authenticateUser(authData, userResponse);
    }

    public void setCallback(IArcotAppCallback callback) throws CAMobileAuthenticatorCommException {
        caMobileAuthenticator.setCallback(callback);
    }

    public static String getVersion() {
        return CAMobileAuthenticator.getVersion();
    }

    public void setDeviceType(String deviceType) {

        caMobileAuthenticator.setDeviceType(deviceType);
    }

    public Account provisionAccount(String xml, String provURL, String deviceToken) throws CAMobileAuthenticatorException {
        return caMobileAuthenticator.provisionAccount(xml, provURL, deviceToken);
    }

    public void saveAccount(Account account) throws CAMobileAuthenticatorException {
        caMobileAuthenticator.saveAccount(account);
    }

    public void deleteAccount(String id) throws CAMobileAuthenticatorException {
        caMobileAuthenticator.deleteAccount(id);
    }

    public void deletePushAuthAccount(Account id) throws CAMobileAuthenticatorException {
        caMobileAuthenticator.deletePushAuthAccount(id);
    }

    public Account getAccount(String id) throws CAMobileAuthenticatorException {
        Account[] accounts = caMobileAuthenticator.getAllAccounts();
        Account account = null;
        if(accounts != null){
            for(Account acc: accounts){
                if(acc != null) {
                    if (acc.name.equals(id)) {
                        account = acc;
                    }
                }
            }
        }

        if(account != null){
           return account;
        }
        else{
            return null;
        }
    }

    public Account[] getAllAccounts() throws CAMobileAuthenticatorException {
        return caMobileAuthenticator.getAllAccounts();
    }

    public Account[] updateDevice() throws CAMobileAuthenticatorException {
        return caMobileAuthenticator.updateDevice();
    }

    public Account[] getAllAccounts(String ns) throws CAMobileAuthenticatorException {
        return caMobileAuthenticator.getAllAccounts(ns);
    }
}