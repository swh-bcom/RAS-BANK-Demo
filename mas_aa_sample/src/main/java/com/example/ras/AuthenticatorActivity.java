package com.example.ras;

public class AuthenticatorActivity{} /*extends AppCompatActivity {

    static Hashtable<String, Integer> reservedTLDs = null;
    String headers = null;
    EditText provisionResponse = null;
    EditText remove_auth_account_response = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticator_activity);
        try {
            MASPushNotification.getInstance().createAuthenticator(this, new PushProvider());
            MASPushNotification.getInstance().setCallback(new NetworkCallback(new Handler(), this));
        } catch (CAMobileAuthenticatorCommException e) {
            e.printStackTrace();
        }

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d("Testing", "Token : " + token);

        final EditText provisionUserID = (EditText) findViewById(R.id.authenticator_provision_userid);
        final EditText provisionURL = (EditText) findViewById(R.id.authenticator_provision_url);
        final EditText provisionActivationCode = (EditText) findViewById(R.id.authenticator_provision_activation_code);
        provisionResponse = (EditText) findViewById(R.id.authenticator_provision_response);

        findViewById(R.id.authenticator_provision_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provisionResponse.setText(String.valueOf(""));
                doRegisterDevice(provisionURL.getEditableText().toString(), provisionUserID.getEditableText().toString(), provisionActivationCode.getEditableText().toString());
//                doRegisterDevice("http://10.7.37.85:8080/arcotafm/controller_push.jsp?profile=masaa", provisionUserID.getEditableText().toString(), provisionActivationCode.getEditableText().toString());
                try {
                    MASPushNotification.getInstance().provisionAuthenticatorAccount("", "", "");
                } catch (CAMobileAuthenticatorException e) {
                }
            }
        });

        final EditText get_auth_account_userid = (EditText) findViewById(R.id.get_authenticator_account_userID);
        final EditText get_auth_account_response = (EditText) findViewById(R.id.get_authenticator_account_response);

        findViewById(R.id.authenticator_get_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_auth_account_response.setText("");
                try {
                    AuthenticatorAccount account = MASPushNotification.getInstance().getAuthenticatorAccount(get_auth_account_userid.getEditableText().toString());

                    if (account != null) {
                        get_auth_account_response.setText(account.getId());
                    } else {
                        get_auth_account_response.setText(getResources().getString(R.string.no_accounts));
                    }

                } catch (MASPushNotificationException e) {

                }
            }
        });

        final EditText all_authenticator_accounts_response = (EditText) findViewById(R.id.getAllauthenticatorAccounts_response);
        findViewById(R.id.getAllauthenticatorAccounts_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_authenticator_accounts_response.setText("");
                AuthenticatorAccount authenticatorAccounts[] = new AuthenticatorAccount[0];
                try {
                    authenticatorAccounts = MASPushNotification.getInstance().getAllAuthenticatorAccounts();

                    if (authenticatorAccounts != null && authenticatorAccounts.length > 0) {

                        JSONArray jsonArray = new JSONArray();

                        for (AuthenticatorAccount account : authenticatorAccounts) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("User ID", account.getId());
                                jsonObject.put("UserName", account.name);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        all_authenticator_accounts_response.setText(jsonArray.toString());
                    } else {
                        all_authenticator_accounts_response.setText(getResources().getString(R.string.no_accounts));
                    }
                } catch (MASPushNotificationException e) {
                    e.printStackTrace();
                }
                Log.d("Testing", "number of accounts " + (authenticatorAccounts == null));
            }
        });

        final EditText get_all_authAccounts_ns = (EditText) findViewById(R.id.get_all_authenticator_accounts_ns);
        final EditText get_all_authAccounts_ns_response = (EditText) findViewById(R.id.get_all_authenticator_accounts_ns_response);

        findViewById(R.id.get_all_authenticator_accounts_ns_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    get_all_authAccounts_ns_response.setText("");
                    AuthenticatorAccount[] authenticatorAccounts = MASPushNotification.getInstance().getAllAuthenticatorAccounts(get_all_authAccounts_ns.getEditableText().toString());

                    if (authenticatorAccounts != null && authenticatorAccounts.length > 0) {

                        JSONArray jsonArray = new JSONArray();

                        for (AuthenticatorAccount account : authenticatorAccounts) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("User ID", account.getId());
                                jsonObject.put("UserName", account.name);
                                jsonObject.put("NS ", account.ns);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        get_all_authAccounts_ns_response.setText(jsonArray.toString());
                    } else {
                        get_all_authAccounts_ns_response.setText(getResources().getString(R.string.no_accounts));
                    }
                } catch (MASPushNotificationException e) {
                    e.printStackTrace();
                }
            }
        });

        final EditText remove_auth_account_userid = (EditText) findViewById(R.id.remove_authenticator_account_userID);
        remove_auth_account_response = (EditText) findViewById(R.id.remove_authenticator_account_response);
        findViewById(R.id.remove_authenticator_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (null == remove_auth_account_userid.getEditableText().toString()
                            || "".equals(remove_auth_account_userid.getEditableText().toString())) {
                        return;
                    }
                    AuthenticatorAccount account = MASPushNotification.getInstance().getAuthenticatorAccount(remove_auth_account_userid.getEditableText().toString());
                    if(account != null)
                    MASPushNotification.getInstance().deletePushAuthAccount(account);
                    else
                        remove_auth_account_response.setText(getResources().getString(R.string.invalid_user_id));

                } catch (MASPushNotificationException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private boolean doRegisterDevice(String passedServerURL, String passedUserID, String passedActivationCode) {
        String theURL = passedServerURL;
        String userID = passedUserID;
        String activationCode = passedActivationCode;

        String android_id = null;
        String deviceName = null;
        String deviceType = null;
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        if(passedServerURL.length() == 0|| passedUserID.length() == 0 || passedActivationCode.length() == 0){
            Toast.makeText(this, getResources().getString(R.string.invalid_details) , Toast.LENGTH_SHORT).show();
            return false;
        }

        android_id = Settings.Secure.getString(AuthenticatorActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceName = android.os.Build.MODEL;
        deviceType = getDeviceType();

        String url = theURL;
        try {
            url = UrlMap(theURL);
        } catch (Exception e) {
        }
        try {
            Map<String, Object> param = new Hashtable<String, Object>();
            param.put(IArcotOTPComm.ACCOUNTID, userID);
            param.put("serverOTP", activationCode);
            if (headers != null && headers.trim().length() != 0) {
                HttpURLConnection connection = null;
                try {
                    URL provURL = new URL(url);
                    connection = (HttpURLConnection) provURL.openConnection();
                    connection.setRequestProperty("Cookie", headers);
                    param.put(IArcotOTPComm.CONNOBJ, connection);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                param.put(IArcotOTPComm.URL, url);
            }
            param.put("deviceId", android_id);
            param.put("DEVICETOKEN", refreshedToken);
            param.put("deviceType", deviceType);
            param.put("deviceOS", Constants.MOBILE_OS);
            param.put("deviceName", deviceName);

            MASPushNotification.getInstance().registerDevice(userID, url, activationCode, (Hashtable) param);
        } catch (CAMobileAuthenticatorCommException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.ERROR_PROVISIONING, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.ERROR_PROVISIONING, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public static String UrlMap(String origUrl) {
        if (reservedTLDs == null) {
            final String[] rTLDs = {"biz", "com", "edu", "gov", "info", "mil",
                    "mobi", "net", "org"};
            reservedTLDs = new Hashtable<String, Integer>();
            Integer one = new Integer(1);
            for (int i = 0; i < rTLDs.length; i++) {
                reservedTLDs.put(rTLDs[i], one);
            }
        }
        // Let's figure out what this URL should map to
        String protocol = "https";
        String host, path, port;
        int index;
        boolean protocolSpecified = false;

        // Look for protocol
        index = origUrl.indexOf("://");
        if (index > 0) {
            protocol = origUrl.substring(0, index);
            origUrl = origUrl.substring(index + 3);
            protocolSpecified = true;
        }

        // now path
        index = origUrl.indexOf('/');
        if (index > 0) {
            host = origUrl.substring(0, index).toLowerCase();
            path = origUrl.substring(index);
            // special case - path of / => default path
            if (path.equals("/"))
                path = "/otp/cprov";
        } else {
            host = origUrl.toLowerCase();
            path = "/otp/cprov";
        }
        if (host.length() == 0)
            host = "otp.arcot.com"; // which hopefully will never happen

        // now port
        index = host.indexOf(':');
        if (index > 0) {
            port = host.substring(index);
            host = host.substring(0, index);
            if (!protocolSpecified && port.endsWith("80"))
                protocol = "http";
        } else
            port = "";

        // now let's figure out if we need to add a .com on the host
        index = host.lastIndexOf('.');
        if (index > 0) {
            String lastPart = host.substring(index + 1);
            int len = lastPart.length();
            boolean allNumeric = true;
            for (int i = 0; i < len; i++) {
                char c = lastPart.charAt(i);
                if (c < '0' || c > '9') {
                    allNumeric = false;
                    break;
                }
            }
            if (!allNumeric && len != 2 && !reservedTLDs.containsKey(lastPart))
                host = host + ".com";
        } else
            host = host + ".com";
        return protocol + "://" + host + port + path;
    }

    private String getDeviceType() {
        return "Android Phone";
    }

    public void registerDeviceResult(final String requestType, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IArcotOTPComm.REQTYPE_REGISTER_DEVICE.equals(requestType)) {
                    provisionResponse.setText(response);
                } else if (IArcotOTPComm.REQTYPE_DELETE_DEVICE.equals(requestType)) {
                    remove_auth_account_response.setText(response);
                }
            }
        });
    }
}*/