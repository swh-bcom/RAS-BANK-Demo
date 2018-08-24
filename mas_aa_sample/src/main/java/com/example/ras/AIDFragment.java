
package com.example.ras;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arcot.aid.lib.AIDException;
import com.ca.apim.mas.authid.AIDAccount;
import com.ca.apim.mas.authid.MASAuthID;
import com.ca.apim.mas.authid.MASAuthIDException;
import com.ca.apim.mas.authid.model.MASAIDIssuanceRequestParams;
import com.ca.apim.mas.authid.model.MASAuthIDCustomRequestData;
import com.ca.mas.core.MobileSsoConfig;
import com.ca.mas.core.conf.Config;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASDevice;
import com.ca.mas.foundation.MASException;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASResponseBody;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.example.ras.error.ErrorObject;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample to show a TestCases.
 */
public class AIDFragment extends Fragment {

    MASAuthID MASAuthID = null;
    private String authToken_key = "authToken";
    private String error_key = "error_details";
    EditText tokenIDResponse = null;
    EditText protectedAPI_Response = null;
    //static String USERNAME_POSTFIX;// = "cadir1ca";

    CheckBox doMAGLogincb;
    boolean doMAGLogin = true;
    //String base64aid = "";//"MIIDEgIBAAwIVVNFUjAwMDAwggL/MIIC+wIBAQwGQVJDQVJEoIHVMB0GCmCGSAGG+UYJAQEwDwQAAgEBAwEAAgFoAgIFKASBszCBsAIBADAOBgpghkgBhvlGCQIABQAEgZowgZcCgYEAs3pIpaoSwJOjcxQGXC16AzcuO34BhHaZH62j9sjeneDG2uQwFBpDww/MDGZGB0/7z8OrxecQwTKlOD1G+AkaJrc6HXtgBxCcd+lj3YQlYy3oUTuZysiWJfdfWBLSsJ4Yz5yJBO5JTh5Kkm9GDZ+cnozTWL9z8QvFEop5+lyqgQMCEQCpILqDHMvMQ9BEoa4pWsKlooIB2jCCAUOgAwIBAgIQMDafdDqCTxq5AU0Ps4ImpTANBgkqhkiG9w0BAQQFADAgMQ4wDAYDVQQLDAVBcmNvdDEOMAwGA1UECgwFQXJjb3QwHhcNMTcwNDA3MDAwMDAxWhcNMzcwNDAyMjM1OTU5WjA3MREwDwYKCZImiZPyLGQBAQwBMjEPMA0GA1UECgwGQ0FESVIxMREwDwYDVQQDDAhVU0VSMDAwMDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAxaGub8sjonW0TZyv/dl2K0527KiP8HNM4w3cRFxCb9/HaakRniy3l9zhXcpLcQDlYMly7bLx3GjYesVq4FmNcoeQDZasSl9YzpeIv/zL7kAruBSNqF8C5+lWLjLqW+GkysI1zd+uFqgq8+ahs8Zs2ytbavhP1d3N7fD+YUjk8ssCAwEAATANBgkqhkiG9w0BAQQFAAOBgQAI16VCvI8c4RzTsm+LA2clNdDOcXkM5J0yhazQEZzGB3grONZTvCUJoXr0zXAtcephITOUHS57sTvNlegw60emJHWlmHRZ8a4UU+DOpfuSl+gmEiyIZyxdRBe36fUsOQqq3nk40+STiYt8xTsz4QbExB6Ww1NbuRxK5o+cFyYP6KM4MA0WA09yZwQGQ0FESVIxMBIWBnVzZXJpZAQIVVNFUjAwMDAwExYOYWlkYXR0cnZlcnNpb24EATAwAA==";
    //String base64aid = "MIIDCwIBAAwFU0NPVFQwggL7MIIC9wIBAQwGQVJDQVJEoIHVMB0GCmCGSAGG+UYJAQEwDwQAAgEBAwEAAgFoAgIFKASBszCBsAIBADAOBgpghkgBhvlGCQIABQAEgZowgZcCgYEAnzp4hxoQWyux/nncTNeroBpdYtxTNqMjHoXRAcOJIwFTnJ64Jj7PfYy/+iNxv5YZ3+0yqS/dyhtJt5mQQv7/kAFhsvhIlzvWaVgBFmgqRyfSzkpQPBZ/CHvvlGvohsRaDb8NLp+wA06L4kA1Bbmg95Yy6FqknCdm7ZlSjwmMSj8CEQDJB2E5e5OzAyRrOi5M7M6ZooIB2TCCAUKgAwIBAgIQPVMpq8sCShat6RlJG2rsQzANBgkqhkiG9w0BAQQFADAgMQ4wDAYDVQQLDAVBcmNvdDEOMAwGA1UECgwFQXJjb3QwHhcNMTcwNjI4MDAwMDAxWhcNMzcwNjIzMjM1OTU5WjA2MRMwEQYKCZImiZPyLGQBAQwDMTYxMQ8wDQYDVQQKDAZDQURJUjExDjAMBgNVBAMMBVNDT1RUMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFoa5vyyOidbRNnK/92XYrTnbsqI/wc0zjDdxEXEJv38dpqRGeLLeX3OFdyktxAOVgyXLtsvHcaNh6xWrgWY1yh5ANlqxKX1jOl4i//MvuQCu4FI2oXwLn6VYuMupb4aTKwjXN364WqCrz5qGzxmzbK1tq+E/V3c3t8P5hSOTyywIDAQABMA0GCSqGSIb3DQEBBAUAA4GBANWSZSuj0MVkXkRddqrW3xpedcX06RMAWObcxi++gPfaEsVRwErTGktEdYeRErUDtFPYFyINLXi5UTVdOTvUoBgd4UPzXSqLwbf+9+Mvr/2NmmeN05E5ry+de1CN1aAP00tQrjJuPUI1n82qzraLm2hXR4EONJBBl+exoELgF7R5ozUwExYOYWlkYXR0cnZlcnNpb24EATAwDRYDT3JnBAZDQURJUjEwDxYGdXNlcmlkBAVTQ09UVDAA";

    //private static String SMSESSION = "";

    public static AIDFragment newInstance() {
        AIDFragment fragment = new AIDFragment();
        return fragment;
    }

    public AIDFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //USERNAME_POSTFIX = ApplicationConstants.ORGNAME + ApplicationConstants.NAMESPACE;//getResources().getString(R.string.aa_orgname)+getResources().getString(R.string.aa_namespace);
    }

    View viewGlobalObj;
    EditText base64aid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.aid_fragment, container, false);

        viewGlobalObj = view;
        Config HOSTNAME = new Config(true, MobileSsoConfig.PROP_TOKEN_HOSTNAME, "server.hostname", String.class);

        // Provision Request Test
        base64aid = (EditText) view.findViewById(R.id.base64aid);
        final EditText provisionURL = (EditText) view.findViewById(R.id.provision_url);
        final EditText provisionResponse = (EditText) view.findViewById(R.id.provisionresponse);
        final EditText userNameForDownloadB64 = (EditText) view.findViewById(R.id.userIdDownloadet);
        final EditText profileForDownloadB64 = (EditText) view.findViewById(R.id.profileForDownloadB64);
        final EditText orgnameForDownloadB64 = (EditText) view.findViewById(R.id.orgnameForDownloadB64);



        base64aid.setText("");
        setScrollToEditText(base64aid);
        setScrollToEditText(provisionResponse);

        ((Button) view.findViewById(R.id.download_b64_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadAuthIdCredentials(userNameForDownloadB64.getText().toString(), orgnameForDownloadB64.getText().toString(), profileForDownloadB64.getText().toString());
            }
        });


        ((Button) view.findViewById(R.id.provision_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    provisionResponse.setText("");
                    EditText deviceIdet = (EditText) view.findViewById(R.id.device_id_aid_et);
                    String deviceId;
                    if ("".equals(deviceIdet.getEditableText().toString())) {
                        deviceId = null;
                    } else {
                        deviceId = deviceIdet.getEditableText().toString();
                    }
                    AIDAccount account = MASAuthID.getInstance().provisionAIDAccount(base64aid.getEditableText().toString(),
                            provisionURL.getEditableText().toString(), deviceId);

                    if (account != null) {
                        String att = "";
                        try {
                            att = account.getAttribute("AID_PROFILE");
                        } catch (AIDException e) {
                            e.printStackTrace();
                        }

                        provisionResponse.setText(getResources().getString(R.string.success) + ",\n ID : " + account.getId() + " \n User Name " + account.accountId);
                    } else {
                        provisionResponse.setText(getResources().getString(R.string.failed));
                    }
                } catch (MASAuthIDException e) {
                    provisionResponse.setText(e.getResponse().toString());
                } catch (Exception e) {
                    provisionResponse.setText(e.getMessage());
                }
            }
        });

        // LoginWithSmSession Test
        final EditText smsessionUserID = (EditText) view.findViewById(R.id.smsession_userid);
        final EditText smsessionOrgName = (EditText) view.findViewById(R.id.smsession_orgname);
        final EditText smsessionNameSpace = (EditText) view.findViewById(R.id.smsession_namespace);

        final EditText smsessionPassword = (EditText) view.findViewById(R.id.smsession_password);
        final EditText smsessionPolicy = (EditText) view.findViewById(R.id.smsession_policy);
        final EditText smsessionAdditionalParam = (EditText) view.findViewById(R.id.smsession_additionalparam);
        final EditText smsessionResponse = (EditText) view.findViewById(R.id.smsession_response);

        setScrollToEditText(smsessionResponse);

        Button verifySmSession = (Button) view.findViewById(R.id.verify_smsession_button);
        verifySmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smSessionRespose = smsessionResponse.getEditableText().toString();
                try {
                    JSONObject jsonObject = new JSONObject(smSessionRespose);
                    if (jsonObject.has("smCookie")) {
                        String smCookie = jsonObject.getString("smCookie");
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra("COOKIE", smCookie);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                }
            }
        });
        doMAGLogincb = (CheckBox) view.findViewById(R.id.smsession_do_mag_login_cb);
        doMAGLogincb.setText("Do MAG Login");
        Button loginwithAIDSMSession = (Button) view.findViewById(R.id.smsession_button);

        loginwithAIDSMSession.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        smsessionResponse.setText("");
                                                    }
                                                });
                                                doMAGLogin = true;
                                                if (doMAGLogincb.isChecked()) {
                                                    doMAGLogin = true;
                                                } else {
                                                    doMAGLogin = false;
                                                }
                                                String policy = smsessionPolicy.getText().toString();
                                                String additionalInputStr = smsessionAdditionalParam.getText().toString();
                                                Map<String, String> additionaParam = AppUtil.parseAdditionalParams(additionalInputStr);

                                                MASAuthID.getInstance().loginWithAIDGetSMSession(smsessionUserID.getEditableText().toString() + smsessionOrgName.getEditableText().toString() + smsessionNameSpace.getEditableText().toString(),
                                                        smsessionPassword.getEditableText().toString(),
                                                        policy, additionaParam, doMAGLogin, new MASCallback<JSONObject>() {
                                                            @Override
                                                            public void onSuccess(final JSONObject jsonObject) {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {


                                                                                                    Log.d("Testing", "loginWIthIDToken succes");
                                                                                                    smsessionResponse.setText(jsonObject.toString());

                                                                                                    try {
                                                                                                        ApplicationConstants.SMSESSION = (String) jsonObject.get("cookie");
                                                                                                    } catch (JSONException e) {
                                                                                                        e.printStackTrace();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                );
                                                            }

                                                            @Override
                                                            public void onError(final Throwable throwable) {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (throwable.getCause() instanceof MASAuthIDException) {
                                                                            smsessionResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                                                        } else {
                                                                            Log.d("Testing", "-- " + (throwable.getCause()));
                                                                            smsessionResponse.setText(throwable.getMessage());
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                        });
                                            }
                                        }
        );










        // GetSMSession Test
        final EditText getsmsessionUserID = (EditText) view.findViewById(R.id.getsmsession_userid);
        final EditText getsmsessionOrgName = (EditText) view.findViewById(R.id.getsmsession_orgname);
        final EditText getsmsessionNameSpace = (EditText) view.findViewById(R.id.getsmsession_namespace);

        final EditText getsmsessionPassword = (EditText) view.findViewById(R.id.getsmsession_password);
        final EditText getsmsessionPolicy = (EditText) view.findViewById(R.id.getsmsession_policy);
        final EditText getsmsessionAdditionalParam = (EditText) view.findViewById(R.id.getsmsession_additionalparam);
        final EditText getsmsessionResponse = (EditText) view.findViewById(R.id.getsmsession_response);

        setScrollToEditText(getsmsessionResponse);

        Button getverifySmSession = (Button) view.findViewById(R.id.getverify_smsession_button);
        getverifySmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smSessionRespose = getsmsessionResponse.getEditableText().toString();
                try {
                    JSONObject jsonObject = new JSONObject(smSessionRespose);
                    if (jsonObject.has("smCookie")) {
                        String smCookie = jsonObject.getString("smCookie");
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra("COOKIE", smCookie);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                }
            }
        });

        Button getSMSession = (Button) view.findViewById(R.id.getsmsession_button);

        getSMSession.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         getActivity().runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 getsmsessionResponse.setText("");
                                                             }
                                                         });

                                                         String policy = getsmsessionPolicy.getText().toString();
                                                         String additionalInputStr = getsmsessionAdditionalParam.getText().toString();
                                                         Map<String, String> additionaParam = AppUtil.parseAdditionalParams(additionalInputStr);

                                                         MASAuthID.getInstance().getSMSession(getsmsessionUserID.getEditableText().toString() + getsmsessionOrgName.getEditableText().toString() + getsmsessionNameSpace.getEditableText().toString(),
                                                                 getsmsessionPassword.getEditableText().toString(),
                                                                 policy, additionaParam, new MASCallback<JSONObject>() {
                                                                     @Override
                                                                     public void onSuccess(final JSONObject jsonObject) {
                                                                         getActivity().runOnUiThread(new Runnable() {
                                                                                                         @Override
                                                                                                         public void run() {


                                                                                                             Log.d("Testing", "loginWIthIDToken succes");
                                                                                                             smsessionResponse.setText(jsonObject.toString());

                                                                                                             try {
                                                                                                                 ApplicationConstants.SMSESSION = (String) jsonObject.get("cookie");
                                                                                                             } catch (JSONException e) {
                                                                                                                 e.printStackTrace();
                                                                                                             }
                                                                                                         }
                                                                                                     }
                                                                         );
                                                                     }

                                                                     @Override
                                                                     public void onError(final Throwable throwable) {
                                                                         getActivity().runOnUiThread(new Runnable() {
                                                                             @Override
                                                                             public void run() {
                                                                                 if (throwable.getCause() instanceof MASAuthIDException) {
                                                                                     getsmsessionResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                                                                 } else {
                                                                                     Log.d("Testing", "-- " + (throwable.getCause()));
                                                                                     getsmsessionResponse.setText(throwable.getMessage());
                                                                                 }
                                                                             }
                                                                         });
                                                                     }

                                                                 });
                                                     }
                                                 }
        );








        // LoginWithTokenID Test
        final EditText tokenIDUserID = (EditText) view.findViewById(R.id.tokenID_userid);
        final EditText tokenIDOrgname = (EditText) view.findViewById(R.id.tokenID_orgname);
        final EditText tokenIDNamespace = (EditText) view.findViewById(R.id.tokenID_namespace);
        final EditText tokenIDPassword = (EditText) view.findViewById(R.id.tokenID_password);
        final EditText tokenIDPolicy = (EditText) view.findViewById(R.id.tokenID_policy);
        final EditText tokenIDadditionalparam = (EditText) view.findViewById(R.id.tokenID_additionalparam);
        tokenIDResponse = (EditText) view.findViewById(R.id.tokenID_response);

        setScrollToEditText(tokenIDResponse);

//        final String tokenIDResponse = "";
        Button tokenIDButton = (Button) view.findViewById(R.id.tokenID_button);

        tokenIDButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 getActivity().runOnUiThread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         AIDFragment.this.tokenIDResponse.setText("");
                                                     }
                                                 });
                                                 String policy = tokenIDPolicy.getText().toString();
                                                 String addParamStr = tokenIDadditionalparam.getText().toString();
                                                 Map<String, String> addParam = AppUtil.parseAdditionalParams(addParamStr);
                                                 MASAuthID.getInstance().loginWithAID(tokenIDUserID.getEditableText().toString() + tokenIDOrgname.getEditableText().toString() + tokenIDNamespace.getEditableText().toString(),
                                                         tokenIDPassword.getEditableText().toString(),
                                                         policy, addParam, new MASCallback<MASUser>() {
                                                             @Override
                                                             public void onSuccess(final MASUser masUser) {
                                                                 getActivity().runOnUiThread(new Runnable() {
                                                                                                 @Override
                                                                                                 public void run() {
                                                                                                     AIDFragment.this.tokenIDResponse.setText("Success");
                                                                                                 }
                                                                                             }
                                                                 );
                                                             }

                                                             @Override
                                                             public void onError(final Throwable throwable) {
                                                                 getActivity().runOnUiThread(new Runnable() {
                                                                     @Override
                                                                     public void run() {
                                                                         if (throwable.getCause() instanceof MASAuthIDException) {
                                                                             AIDFragment.this.tokenIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                                                         } else {
                                                                             AIDFragment.this.tokenIDResponse.setText(throwable.getMessage());
                                                                         }
                                                                     }
                                                                 });
                                                             }


                                                         });
                                             }
                                         }
        );


        // GetAccount Request Test
        final EditText getAccountUserID = (EditText) view.findViewById(R.id.get_pki_account_userID);
        final EditText getAccountOrgName = (EditText) view.findViewById(R.id.get_pki_account_orgname);
        final EditText getAccountNamespace = (EditText) view.findViewById(R.id.get_pki_account_namespace);
        final EditText getAccountResponse = (EditText) view.findViewById(R.id.get_pki_account_response);

        setScrollToEditText(getAccountResponse);
        final Button getAccountButton = (Button) view.findViewById(R.id.get_pki_account_button);
        getAccountButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        getAccountResponse.setText("");
                                                        AIDAccount account = MASAuthID.getInstance().getAIDAccount(getAccountUserID.getEditableText().toString() + getAccountOrgName.getEditableText().toString() + getAccountNamespace.getEditableText().toString());
                                                        if (account != null) {
                                                            getAccountResponse.setText(account.getId());
                                                        } else {
                                                            getAccountResponse.setText(getResources().getString(R.string.failed));
                                                        }
                                                    } catch (MASAuthIDException e) {
                                                        getAccountResponse.setText(e.getResponse().toString());
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
        );

        // GetAllAccounts Request Test
        final EditText getAllAccountsResponse = (EditText) view.findViewById(R.id.getAllaccounts_response);
        setScrollToEditText(getAllAccountsResponse);
        Button getAllAccountsButton = (Button) view.findViewById(R.id.getAllAccounts_button);
        getAllAccountsButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            getAllAccountsResponse.setText("");
                                                            AIDAccount[] accounts = MASAuthID.getInstance().getAllAIDAccounts();

                                                            if (accounts != null && accounts.length > 0) {

                                                                JSONArray jsonArray = new JSONArray();

                                                                for (AIDAccount account : accounts) {
                                                                    try {
                                                                        JSONObject jsonObject = new JSONObject();
                                                                        jsonObject.put("User ID", account.getId());
                                                                        jsonObject.put("UserName", account.name);
                                                                        jsonArray.put(jsonObject);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                getAllAccountsResponse.setText(jsonArray.toString());
                                                            } else {
                                                                getAllAccountsResponse.setText(getResources().getString(R.string.no_accounts));
                                                            }
                                                        } catch (MASAuthIDException e) {
                                                            getAllAccountsResponse.setText(e.getResponse().toString());
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

        );


        // Remove Account Request Test
        final EditText removeAccountUserID = (EditText) view.findViewById(R.id.removeAccount_userID);
        final EditText removeAccountOrgname = (EditText) view.findViewById(R.id.removeAccount_orgname);
        final EditText removeAccountNamespace = (EditText) view.findViewById(R.id.removeAccount_namespace);
        final EditText removeAccountResponse = (EditText) view.findViewById(R.id.removeAccount_response);
        Button removeAccountButton = (Button) view.findViewById(R.id.removeAccount_button);
        removeAccountButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       try {
                                                           MASAuthID.getInstance().removeAIDAccount(removeAccountUserID.getEditableText().toString() + removeAccountOrgname.getEditableText().toString() + removeAccountNamespace.getEditableText().toString());
                                                           removeAccountResponse.setText(getResources().getString(R.string.success));
                                                       } catch (MASAuthIDException e) {
                                                           removeAccountResponse.setText(e.getResponse().toString());
                                                       }
                                                   }
                                               }

        );

        final EditText logout_response = (EditText) view.findViewById(R.id.aid_logout_response);


        Button unregisterButton = (Button) view.findViewById(R.id.aid_unregister_device_button);
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_response.setText("");
                MASDevice.getCurrentDevice().deregister(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        //showMessage("Server Registration Removed for This Device", Toast.LENGTH_SHORT);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logout_response.setText(getResources().getString(R.string.success) + " : Unregistered");
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        logout_response.setText(getResources().getString(R.string.failed) + "::" + e.getMessage());
                    }
                });
            }
        });

        Button deleteTokenStoreButton = (Button) view.findViewById(R.id.aid_delete_token_store_btn);
        deleteTokenStoreButton.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          MASDevice.getCurrentDevice().resetLocally();
                                                          getActivity().runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  logout_response.setText(getResources().getString(R.string.success) + " : Reset complete");
                                                              }
                                                          });
                                                      }


                                                  }
        );
        Button logoutButton = (Button) view.findViewById(R.id.aid_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_response.setText("");

                if (MASUser.getCurrentUser() != null) {
                    MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logout_response.setText(getResources().getString(R.string.success));
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logout_response.setText(getResources().getString(R.string.failed));
                                }
                            });

                        }
                    });
                } else {
                    logout_response.setText("User is not logged in");
                }


            }
        });


        Button accessSMSessionProtectedAPIbtn = (Button) view.findViewById(R.id.accessSMSessionProtectedAPIbtn);
        final EditText accessSMSessionProtectedAPIURLet = (EditText) view.findViewById(R.id.smsession_protected_api_et);
        final TextView accessSMSessionProtectedAPIResulttv = (TextView) view.findViewById(R.id.smsession_protected_api_resp_tv);

        accessSMSessionProtectedAPIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyLog.setTag("VolleyLogs");

                com.android.volley.RequestQueue queue = Volley.newRequestQueue(MAS.getContext());

                String url = accessSMSessionProtectedAPIURLet.getText().toString();
                accessSMSessionProtectedAPIResulttv.setText("");
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.length() > 1000) {
                                    accessSMSessionProtectedAPIResulttv.setText("Response is: " + response.substring(0, 1000));
                                } else {
                                    accessSMSessionProtectedAPIResulttv.setText("Response is: " + response);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        accessSMSessionProtectedAPIResulttv.setText("Response is: " + error.getMessage());
                    }
                });

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);

            }
        });


        protectedAPI_Response = (EditText) view.findViewById(R.id.protectedAPI_response);
        Button access_protectedAPI = (Button) view.findViewById(R.id.protectedAPI_button);
        final EditText access_protectedAPIet = (EditText) view.findViewById(R.id.protectedAPI_URL);

        access_protectedAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Testing", "Before InvokeGet products");
                String url = access_protectedAPIet.getText().toString();
                URI uri;
                if (url == null || "".equals(url)) {
                    uri = getProductListDownloadUri();
                } else {
                    uri = getURI(url);
                }
                invokeAPI(uri);
            }
        });


        Button accessProtectedAPIWebview = (Button) view.findViewById(R.id.accessSMSessionProtectedAPIWebViewbtn);
        final TextView accessProtectedAPIWebviewCookie = (TextView) view.findViewById(R.id.smsession_using_cookie_webview_et);
        final EditText accessProtectedAPIWebviewet = (EditText) view.findViewById(R.id.smsession_protected_api_webview_et);
        Button accessSMSessionProtectedAPIWebViewClearCookiebtn = (Button) view.findViewById(R.id.accessSMSessionProtectedAPIWebViewClearCookiebtn);

        accessSMSessionProtectedAPIWebViewClearCookiebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                CookieManager cookieManager = CookieManager.getInstance();
                //cookieManager.
                cookieManager.removeAllCookie();
                ApplicationConstants.SMSESSION = null;
            }
        });

        accessProtectedAPIWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = accessProtectedAPIWebviewet.getText().toString();


                accessProtectedAPIWebviewCookie.setText(ApplicationConstants.SMSESSION);


                ApplicationConstants.SSO_WEBVIEW_URL = url;
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                startActivity(intent);
            }
        });


    // views initialised for  Create AuthID

        final EditText authIDUserName = (EditText) view.findViewById(R.id.authid_username);
        final EditText authIDOrgName = (EditText) view.findViewById(R.id.authid_org_name);
        final EditText authIDPassword = (EditText) view.findViewById(R.id.authid_pass_word);
        final EditText authIDClientTnxID = (EditText) view.findViewById(R.id.authid_client_txn_id);
        final EditText authIDResponse = (EditText) view.findViewById(R.id.authid_response);
        final EditText authIDAdditionalHeaders = (EditText) view.findViewById(R.id.authid_additional_header);
        final EditText authIDAdditionalParam = (EditText) view.findViewById(R.id.authid_additional_param);
        final CheckBox authIDCheckBox = (CheckBox) view.findViewById(R.id.authid_checkbox);
        Button authIdCreateButton = (Button) view.findViewById(R.id.authid_create_id);
        authIdCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = authIDUserName.getText().toString();
                String orgName = authIDOrgName.getText().toString();
                String password = authIDPassword.getText().toString();
                String clientTnxId = authIDClientTnxID.getText().toString();
                String addParamStr = authIDAdditionalParam.getText().toString();
                String addHeaderStr = authIDAdditionalHeaders.getText().toString();
                authIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);

                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(authIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().createAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                authIDResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }
                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    authIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    authIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException) {
                                        authIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    }
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        authIDResponse.setText(err.toString());
                                    }

                                }
                            }
                        });
                    }

                });

            }
        });



    // views initialised for  Delete AuthID


        final EditText deleteAuthIDUserName = (EditText) view.findViewById(R.id.delete_authid_username);
        final EditText deleteAuthIDOrgName = (EditText) view.findViewById(R.id.delete_authid_org_name);
        final EditText deleteAuthIDPassword = (EditText) view.findViewById(R.id.delete_authid_pass_word);
        final EditText deleteAuthIDClientTnxID = (EditText) view.findViewById(R.id.delete_authid_client_txn_id);
        final EditText deleteAuthIDResponse = (EditText) view.findViewById(R.id.delete_authid_response);
        final EditText deleteAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.delete_authid_additional_header);
        final EditText deleteAuthIDAdditionalParam = (EditText) view.findViewById(R.id.delete_authid_additional_param);
        final CheckBox deleteAuthIDCheckBox = (CheckBox) view.findViewById(R.id.delete_authid_checkbox);
        Button authIdDeleteButton = (Button) view.findViewById(R.id.authid_delete);
        authIdDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = deleteAuthIDUserName.getText().toString();
                String orgName = deleteAuthIDOrgName.getText().toString();
                String password = deleteAuthIDPassword.getText().toString();
                String clientTnxId = deleteAuthIDClientTnxID.getText().toString();
                String addParamStr = deleteAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = deleteAuthIDAdditionalHeaders.getText().toString();
                deleteAuthIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);

                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(deleteAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().deleteAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteAuthIDResponse.setText(result.getBody().getContent().toString());
                            }
                        });

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//
                                if(throwable instanceof  MASAuthIDException){
                                    deleteAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    deleteAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        deleteAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    if(throwable.getCause() instanceof MASException){
                                        deleteAuthIDResponse.setText(((MASException) throwable.getCause()).getMessage());
                                    }else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        deleteAuthIDResponse.setText(err.toString());
                                    }
                                }
                            }
                        });
                    }

                });


            }
        });


        // views initialised for  Disable AuthID

        final EditText disableAuthIDUserName = (EditText) view.findViewById(R.id.disable_authid_username);
        final EditText disableAuthIDResponse = (EditText) view.findViewById(R.id.disable_authid_response);
        final EditText disableAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.disable_authid_additional_header);
        final EditText disableAuthIDAdditionalParam = (EditText) view.findViewById(R.id.disable_authid_additional_param);
        final CheckBox disableAuthIDCheckBox = (CheckBox) view.findViewById(R.id.disable_authid_checkbox);

        Button authIdDisable = (Button) view.findViewById(R.id.authid_disable);
        authIdDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = disableAuthIDUserName.getText().toString();
                String addParamStr = disableAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = disableAuthIDAdditionalHeaders.getText().toString();
                disableAuthIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);

                final MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(disableAuthIDCheckBox.isChecked()) {
                    authIDCustomRequestData.setPublic(true);
                }



                MASAuthID.getInstance().disableAID(userName, authIDCustomRequestData, new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disableAuthIDResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    disableAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    disableAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        disableAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err = AppUtil.getMASErrorMessage(throwable);
                                        disableAuthIDResponse.setText(err.toString());
                                    }
                                }

                            }
                        });
                    }

                });


            }
        });


        // views initialised for  Download AuthID

        final EditText downloadAuthIDUserName = (EditText) view.findViewById(R.id.download_authid_username);
        final EditText downloadAuthIDOrgName = (EditText) view.findViewById(R.id.download_authid_org_name);
        final EditText downloadAuthIDPassword = (EditText) view.findViewById(R.id.download_authid_pass_word);
        final EditText downloadAuthIDClientTnxID = (EditText) view.findViewById(R.id.download_authid_client_txn_id);
        final EditText downloadAuthIDResonse = (EditText) view.findViewById(R.id.download_authid_response);
        final EditText downloadAuthIDAdditionalParam = (EditText) view.findViewById(R.id.download_authid_additional_param);
        final EditText downloadAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.download_authid_additional_header);
        final CheckBox downloadAuthIDCheckBox = (CheckBox) view.findViewById(R.id.download_authid_checkbox);
        Button authIdDownloadButton = (Button) view.findViewById(R.id.authid_download);
        authIdDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = downloadAuthIDUserName.getText().toString();
                String orgName = downloadAuthIDOrgName.getText().toString();
                String password = downloadAuthIDPassword.getText().toString();
                String clientTnxId = downloadAuthIDClientTnxID.getText().toString();
                String addParamStr = downloadAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = downloadAuthIDAdditionalHeaders.getText().toString();
                downloadAuthIDResonse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);


                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(downloadAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().downloadAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadAuthIDResonse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    downloadAuthIDResonse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    downloadAuthIDResonse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        downloadAuthIDResonse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        downloadAuthIDResonse.setText(err.toString());
                                    }

                                }

                            }
                        });
                    }

                });

            }
        });


        //Views initialised for enable AuthID
        final EditText enableAuthIDUserName = (EditText) view.findViewById(R.id.enable_authid_username);
        final EditText enableAuthIDResponse = (EditText) view.findViewById(R.id.enable_authid_response);
        final EditText enableAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.enable_authid_additional_header);
        final EditText enableAuthIDAdditionalParam = (EditText) view.findViewById(R.id.enable_authid_additional_param);
        final CheckBox enableAuthIDCheckBox = (CheckBox) view.findViewById(R.id.enable_authid_checkbox);
        Button authIdEnable = (Button) view.findViewById(R.id.authid_enable);
        authIdEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = enableAuthIDUserName.getText().toString();
                String addParamStr = enableAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = enableAuthIDAdditionalHeaders.getText().toString();
                enableAuthIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(enableAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAuthID.getInstance().enableAID(userName,authIDCustomRequestData, new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableAuthIDResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    enableAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    enableAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        enableAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        enableAuthIDResponse.setText(err.toString());
                                    }

                                }
                            }
                        });
                    }

                });

            }
        });


        // views initialised for  fetch AuthID

        final EditText fetchAuthIDUserName = (EditText) view.findViewById(R.id.fetch_authid_username);
        final EditText fetchAuthIDOrgName = (EditText) view.findViewById(R.id.fetch_authid_org_name);
        final EditText fetchAuthIDPassword = (EditText) view.findViewById(R.id.fetch_authid_pass_word);
        final EditText fetchAuthIDResponse = (EditText) view.findViewById(R.id.fetch_authid_response);
        final EditText fetchAuthIDClientTnxID = (EditText) view.findViewById(R.id.fetch_authid_client_txn_id);
        final EditText fetchAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.fetch_authid_additional_header);
        final EditText fetchAuthIDAdditionalParam = (EditText) view.findViewById(R.id.fetch_authid_additional_param);
        final CheckBox fetchAuthIDCheckBox = (CheckBox) view.findViewById(R.id.fetch_authid_checkbox);
        Button authIdFetchButton = (Button) view.findViewById(R.id.authid_fetch);
        authIdFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = fetchAuthIDUserName.getText().toString();
                String orgName = fetchAuthIDOrgName.getText().toString();
                String password = fetchAuthIDPassword.getText().toString();
                String clientTnxId = fetchAuthIDClientTnxID.getText().toString();
                String addParamStr = fetchAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = fetchAuthIDAdditionalHeaders.getText().toString();
                fetchAuthIDResponse.setText("");


                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);

                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(fetchAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().fetchAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fetchAuthIDResponse.setText(result.getBody().getContent().toString());

                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    fetchAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    fetchAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        fetchAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        fetchAuthIDResponse.setText(err.toString());
                                    }

                                }
                            }
                        });
                    }

                });

            }
        });


         // views initialised for  reissue AuthID
        final EditText reIssueAuthIDUserName = (EditText) view.findViewById(R.id.reIssue_authid_username);
        final EditText reIssueAuthIDOrgName = (EditText) view.findViewById(R.id.reIssue_authid_org_name);
        final EditText reIssueAuthIDPassword = (EditText) view.findViewById(R.id.reIssue_authid_pass_word);
        final EditText reIssueAuthIDClientTnxID = (EditText) view.findViewById(R.id.reIssue_authid_client_txn_id);
        final EditText reIssueAuthIDResponse = (EditText) view.findViewById(R.id.reIssue_authid_response);
        final EditText reIssueAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.reIssue_authid_additional_header);
        final EditText reIssueAuthIDAdditionalParam = (EditText) view.findViewById(R.id.reIssue_authid_additional_param);
        final CheckBox reIssueAuthIDCheckBox = (CheckBox) view.findViewById(R.id.reIssue_authid_checkbox);
        Button authIdReIssueButton = (Button) view.findViewById(R.id.authid_reIssue);
        authIdReIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = reIssueAuthIDUserName.getText().toString();
                String orgName = reIssueAuthIDOrgName.getText().toString();
                String password = reIssueAuthIDPassword.getText().toString();
                String clientTnxId = reIssueAuthIDClientTnxID.getText().toString();
                String addParamStr = reIssueAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = reIssueAuthIDAdditionalHeaders.getText().toString();
                reIssueAuthIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(reIssueAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().reIssueAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reIssueAuthIDResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    reIssueAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    reIssueAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        reIssueAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        reIssueAuthIDResponse.setText(err.toString());
                                    }

                                }

                            }
                        });
                    }
                });

            }
        });


        // views initialised for  reset AuthID

        final EditText resetAuthIDUserName = (EditText) view.findViewById(R.id.reset_authid_username);
        final EditText resetAuthIDOrgName = (EditText) view.findViewById(R.id.reset_authid_org_name);
        final EditText resetAuthIDPassword = (EditText) view.findViewById(R.id.reset_authid_pass_word);
        final EditText resetAuthIDClientTnxID = (EditText) view.findViewById(R.id.reset_authid_client_txn_id);
        final EditText resetAuthIDResponse = (EditText) view.findViewById(R.id.reset_authid_response);
        final EditText resetAuthIDAdditionalHeaders = (EditText) view.findViewById(R.id.reset_authid_additional_header);
        final EditText resetAuthIDAdditionalParam = (EditText) view.findViewById(R.id.reset_authid_additional_param);
        final CheckBox resetAuthIDCheckBox = (CheckBox) view.findViewById(R.id.reset_authid_checkbox);
        Button authIdResetButton = (Button) view.findViewById(R.id.authid_reset);
        authIdResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = resetAuthIDUserName.getText().toString();
                String orgName = resetAuthIDOrgName.getText().toString();
                String password = resetAuthIDPassword.getText().toString();
                String clientTnxId = resetAuthIDClientTnxID.getText().toString();
                String addParamStr = resetAuthIDAdditionalParam.getText().toString();
                String addHeaderStr = resetAuthIDAdditionalHeaders.getText().toString();
                resetAuthIDResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthIDCustomRequestData authIDCustomRequestData = new MASAuthIDCustomRequestData();
                authIDCustomRequestData.setHeaders(addHeaders);
                authIDCustomRequestData.setQueryParams(addParams);
                if(resetAuthIDCheckBox.isChecked()){
                    authIDCustomRequestData.setPublic(true);
                }

                MASAIDIssuanceRequestParams masAidIssuanceRequestParams = new MASAIDIssuanceRequestParams();
                masAidIssuanceRequestParams.setUserName(userName);
                masAidIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAidIssuanceRequestParams.setPassword(password);
                masAidIssuanceRequestParams.setOrgName(orgName);
                masAidIssuanceRequestParams.setMasAuthIDCustomRequestData(authIDCustomRequestData);

                MASAuthID.getInstance().resetAID(masAidIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {


                        getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                         resetAuthIDResponse.setText(result.getBody().getContent().toString());
                                                        }
                        });

                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(throwable instanceof  MASAuthIDException){
                                    resetAuthIDResponse.setText(((MASAuthIDException) throwable).getResponse().toString());
                                }
                                else if (throwable.getCause() instanceof MASAuthIDException) {
                                    resetAuthIDResponse.setText(((MASAuthIDException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        resetAuthIDResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());
                                    else{
                                        ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                        resetAuthIDResponse.setText(err.toString());
                                    }

                                }

                            }
                        });
                    }
                });

            }
        });




        MAS.setAuthenticationListener(new MASAuthenticationListener() {

            @Override
            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            protectedAPI_Response.setText("Authentication Required");
                        }
                    });
                }
            }

            @Override
            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
            }
        });

        setSwitchFunctionality(view);
        return view;

    }


    public void invokeAPI(URI uri) {
        Log.d("Testing", "Inside InvokeGet products");
        final MASRequest request = new MASRequest.MASRequestBuilder(uri).build();
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {

            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {

                protectedAPI_Response.setText(result.getBody().getContent().toString());
            }

            @Override
            public void onError(Throwable e) {
                if (e.getCause() instanceof TargetApiException) {
                    protectedAPI_Response.setText(new String(((TargetApiException) e.getCause()).getResponse()
                            .getBody().getRawContent()));
                } else {
                    protectedAPI_Response.setText(getResources().getString(R.string.failed));
                }
            }
        });
    }

    private URI getProductListDownloadUri() {
        try {
            return new URI("/protected/resource/products?operation=listProducts");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getURI(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static void setScrollToEditText(final EditText editText) {
     /*   editText.setMovementMethod(new ScrollingMovementMethod());
        ScrollingMovementMethod.getInstance();

        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editText.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }


        });*/
    }

    private void setSwitchFunctionality(final View view) {

        SwitchCompat provision = (SwitchCompat) view.findViewById(R.id.provisioningSwitch);
        provision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.base64aidrelativelayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.base64aidrelativelayout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat access_api = (SwitchCompat) view.findViewById(R.id.accessProtectedAPISwitch);
        access_api.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.access_protectedAPI_layout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.access_protectedAPI_layout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat login_aid = (SwitchCompat) view.findViewById(R.id.loginWithAIDSwitch);
        login_aid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.loginWithTokenID).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.loginWithTokenID).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat get_smsession = (SwitchCompat) view.findViewById(R.id.loginWithSMSessionSwitch);
        get_smsession.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.loginWithSmSession).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.loginWithSmSession).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat get_smsession_API = (SwitchCompat) view.findViewById(R.id.getSMSessionSwitch);
        get_smsession_API.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.getSmSession).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.getSmSession).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat get_account = (SwitchCompat) view.findViewById(R.id.getAccountSwitch);
        get_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.getAccountLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.getAccountLayout).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat remove_account = (SwitchCompat) view.findViewById(R.id.removeAccountSwitch);
        remove_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.removeAccountLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.removeAccountLayout).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat get_all_account = (SwitchCompat) view.findViewById(R.id.getAllAccountSwitch);
        get_all_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.getAllAccountsLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.getAllAccountsLayout).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat logout = (SwitchCompat) view.findViewById(R.id.aidLogoutwitch);
        logout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.aid_logoutLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.aid_logoutLayout).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat accessSMSessionAPI = (SwitchCompat) view.findViewById(R.id.aidAccessSMSessionAPItswitch);
        accessSMSessionAPI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.aidAccessSMSessionAPIll).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.aidAccessSMSessionAPIll).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat accessSMSessionWebViewAPI = (SwitchCompat) view.findViewById(R.id.accessSMSessionAPIWebViewswitch);
        accessSMSessionWebViewAPI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.accessSMSessionAPIWebViewll).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.accessSMSessionAPIWebViewll).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat createAuthID = (SwitchCompat) view.findViewById(R.id.createAuthIDSwitch);
        createAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.createAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.createAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat deleteAuthID = (SwitchCompat) view.findViewById(R.id.deleteAuthIDSwitch);
        deleteAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.deleteAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.deleteAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat disableAuthID = (SwitchCompat) view.findViewById(R.id.disableAuthIDSwitch);
        disableAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.disableAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.disableAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat downloadAuthID = (SwitchCompat) view.findViewById(R.id.downloadAuthIDSwitch);
        downloadAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.downloadAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.downloadAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat enableAuthID = (SwitchCompat) view.findViewById(R.id.enableAuthIDSwitch);
        enableAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.enableAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.enableAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat fetchAuthID = (SwitchCompat) view.findViewById(R.id.fetchAuthIDSwitch);
        fetchAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.fetchAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.fetchAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat reIssueAuthID = (SwitchCompat) view.findViewById(R.id.reIssueAuthIDSwitch);
        reIssueAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.reIssueAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.reIssueAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat resetAuthID = (SwitchCompat) view.findViewById(R.id.resetAuthIDSwitch);
        resetAuthID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    view.findViewById(R.id.resetAuthIdLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    view.findViewById(R.id.resetAuthIdLayout).setVisibility(View.GONE);
                }
            }
        });


    }


    static String AUTHID = "authID";

    private void downloadAuthIdCredentials(String username, String orgName, String profile) {
        try {
            HashMap<String, String> queryParams = new HashMap<>();
            if (orgName != null && !"".equals(orgName)) {
                queryParams.put("orgName", orgName);
            }

            if (profile != null && !"".equals(profile)) {
                queryParams.put("profileName", profile);
            }
            String url = AppUtil.appendQueryParameters(downloadAuthIdEndpoint() + username , queryParams);
            MASRequest request = new MASRequest.MASRequestBuilder(new URI(url))
                    .setPublic()
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    try {
                        MASResponseBody<JSONObject> body = result.getBody();
                        String authIdBase64Str = body.getContent().getString(AUTHID);

                        populateBase64(authIdBase64Str);
                    } catch (Exception e) {
                        e.printStackTrace();
                        populateBase64(e.getMessage());
                        //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    populateBase64(AppUtil.getMASErrorMessage(e).getErrorDetail());
                    //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            populateBase64(e.getMessage());
        }
    }

    void populateBase64(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                base64aid.setText(msg);
            }

        });
    }

    private String downloadAuthIdEndpoint() {
        return "/auth/strongauth/authid/download/";
    }

}
