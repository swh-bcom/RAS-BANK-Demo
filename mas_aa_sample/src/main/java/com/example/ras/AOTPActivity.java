package com.example.ras;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.arcot.aotp.lib.OTP;
import com.ca.apim.mas.authotp.AOTPAccount;
import com.ca.apim.mas.authotp.MASAuthOTP;
import com.ca.apim.mas.authotp.MASAuthOTPException;
import com.ca.apim.mas.authotp.model.MASAOTPIssuanceRequestParams;
import com.ca.apim.mas.authotp.model.MASAuthOTPCustomRequestData;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASDevice;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.example.ras.error.ErrorObject;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;


/**
 * Sample to show a TestCases.
 */
public class AOTPActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     */

    //String xmlReq = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><response xmlns=\"http://xs.arcot.com/ArcotOTPProtocolSvc/2.0\" > <status>success</status><aid>USER0000</aid><displayName>USER0000</displayName><logoUrl>https://sample.com </logoUrl><expiry>1675593951</expiry><roam>false</roam><algo><algoType>HOTP</algoType><cs>::CTR_=0000000000000000::DIGS=06::ORG_=CADIR1::TYPE=HOTP::UDK_=8619C0FE4F3BF56C24A183649CA5AB54251F1648::UIDS=0001::UID_=USER0000::USER=USER0000::VER_=0.0.0::CredentialVersion=2022-05-22T12:37:03.000Z::</cs></algo></response>";
    //String xmlReq = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><response xmlns=\"http://xs.arcot.com/ArcotOTPProtocolSvc/2.0\" > <status>success</status><aid>SCOTT</aid><displayName>SCOTT</displayName><logoUrl>https://sample.com </logoUrl><expiry>1675593951</expiry><roam>false</roam><algo><algoType>HOTP</algoType><cs>::CTR_=0000000000000002::DIGS=06::ORG_=CADIR1::TYPE=HOTP::UDK_=90C8A5984EEA35A622AC602A114184E503EED5D8::UIDS=0001::UID_=SCOTT::USER=SCOTT::VER_=0.0.0::CredentialVersion=2017-06-28T10:08:09.107Z::</cs></algo></response>";
    String xmlReq =   "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><response xmlns=\"http://xs.arcot.com/ArcotOTPProtocolSvc/2.0\" > <status>success</status><aid>SCOTT</aid><displayName>SCOTT</displayName><logoUrl>https://sample.com </logoUrl><expiry>1675593951</expiry><roam>false</roam><algo><algoType>HOTP</algoType><cs>::CTR_=0000000000000000::DIGS=06::ORG_=CADIR1::TYPE=HOTP::UDK_=0E1BC80E10FA8B732089D5489FF1B5854FBFB615::UIDS=0001::UID_=SCOTT::USER=SCOTT::VER_=0.0.0::CredentialVersion=2017-06-28T10:08:09.107Z::</cs></algo></response>";
    EditText protectedAPICall = null;
    Button protectedAPIButton = null;

    Activity context;
    public static String[] otp_types_array = {"Default", "Sign"};
    static String USERNAME_POSTFIX; // = "::cadir1::ca"
    public String REQUEST_PARAM_KEY_ORGNAME = "orgName";
    public static final String REQUEST_PARAM_KEY_PASSWORD = "password";
    public static final String REQUEST_PARAM_KEY_PROFILE = "profile";
    public static final String REQUEST_PARAM_KEY_CLIENTTXNID = "clientTxnId";
    @Override
    public void onCreate(Bundle savedInstanceState) {
         //USERNAME_POSTFIX = "::"+getResources().getString(R.string.aa_orgname)+"::"+getResources().getString(R.string.aa_namespace);
        USERNAME_POSTFIX = "::"+ ApplicationConstants.ORGNAME+"::"+ApplicationConstants.NAMESPACE;

        setContentView(R.layout.aotp_activity);
        context = this;
        SwitchCompat provision = (SwitchCompat) findViewById(R.id.provisioning_enable_Tv);
        provision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.provision).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.provision).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat otp = (SwitchCompat) findViewById(R.id.otp);
        otp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.genOTP).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.genOTP).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat access_api = (SwitchCompat) findViewById(R.id.access_api);
        access_api.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.accessAPI).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.accessAPI).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat aotp = (SwitchCompat) findViewById(R.id.login_aotp);
        aotp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.aotp).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.aotp).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat get_account = (SwitchCompat) findViewById(R.id.get_account);
        get_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.getAccount).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.getAccount).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat get_all_account = (SwitchCompat) findViewById(R.id.get_all_account);
        get_all_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.getAllAccount).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.getAllAccount).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat roaming_keys = (SwitchCompat) findViewById(R.id.get_roaming_keys);
        roaming_keys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.roamingKeys).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.roamingKeys).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat remove_account = (SwitchCompat) findViewById(R.id.remove_account);
        remove_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.removeAccount).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.removeAccount).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat resync = (SwitchCompat) findViewById(R.id.resync);
        resync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.reSYNC).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.reSYNC).setVisibility(View.GONE);
                }
            }
        });
        SwitchCompat logout = (SwitchCompat) findViewById(R.id.logout);
        logout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.logOut).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.logOut).setVisibility(View.GONE);
                }
            }
        });


        // Issuance AOTP Apis switches

        SwitchCompat createAOTPSwitch = (SwitchCompat) findViewById(R.id.createAOTPSwitch);
        createAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.createAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.createAOTPLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat deleteAOTPSwitch = (SwitchCompat) findViewById(R.id.deleteAOTPSwitch);
        deleteAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.deleteAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.deleteAOTPLayout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat disableAOTPSwitch = (SwitchCompat) findViewById(R.id.disableAOTPSwitch);
        disableAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.disableAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.disableAOTPLayout).setVisibility(View.GONE);
                }
            }
        });

        SwitchCompat downloadAOTPSwitch = (SwitchCompat) findViewById(R.id.downloadAOTPSwitch);
        downloadAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.downloadAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.downloadAOTPLayout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat enableAOTPSwitch = (SwitchCompat) findViewById(R.id.enableAOTPSwitch);
        enableAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.enableAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.enableAOTPLayout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat fetchAOTPSwitch = (SwitchCompat) findViewById(R.id.fetchAOTPSwitch);
        fetchAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.fetchAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.fetchAOTPLayout).setVisibility(View.GONE);
                }
            }
        });



        SwitchCompat reIssueAOTPSwitch = (SwitchCompat) findViewById(R.id.reIssueAOTPSwitch);
        reIssueAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.reIssueAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.reIssueAOTPLayout).setVisibility(View.GONE);
                }
            }
        });


        SwitchCompat resetAOTPSwitch = (SwitchCompat) findViewById(R.id.resetAOTPSwitch);
        resetAOTPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    findViewById(R.id.resetAOTPLayout).setVisibility(View.VISIBLE);
                } else {
                    // The toggle is disabled
                    findViewById(R.id.resetAOTPLayout).setVisibility(View.GONE);
                }
            }
        });



        protectedAPICall = (EditText) findViewById(R.id.protectedAPI_AOTP_response);
        protectedAPIButton = (Button) findViewById(R.id.protectedAPI_AOTP_button);

        protectedAPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                protectedAPICall.setText("");
                invokeGetProducts();
            }
        });

        final EditText aotp_data = (EditText) findViewById(R.id.otp_xml_data);
        final EditText namespace = (EditText) findViewById(R.id.otp_namespace);
        final EditText activationCode = (EditText) findViewById(R.id.otp_activation_code);
        final EditText pin = (EditText) findViewById(R.id.otp_pin);

        aotp_data.setText(xmlReq);

        final EditText provisionResponse = (EditText) findViewById(R.id.otp_provision_response);

        MAS.setAuthenticationListener(new MASAuthenticationListener() {

            @Override
            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        protectedAPICall.setText("Authentication Required");
                    }
                });
            }

            @Override
            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
            }
        });

        final EditText deviceIdet = (EditText) findViewById(R.id.device_id_aotp_et) ;


        Button provisionButton = (Button) findViewById(R.id.otp_provision_button);
        provisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId;
                if ("".equals(deviceIdet.getEditableText().toString())) {
                    deviceId = null;
                } else {
                    deviceId = deviceIdet.getEditableText().toString();
                }
                try {

                    AOTPAccount aotpAccount = MASAuthOTP.getInstance().provisionAOTPAccount(aotp_data.getEditableText().toString(), namespace.getEditableText().toString(), activationCode.getEditableText().toString(), pin.getEditableText().toString(), deviceId);

                    if (aotpAccount != null) {
                        provisionResponse.setText(getResources().getString(R.string.success) + ",\n ID : " + aotpAccount.getId() + " \n User Name " + aotpAccount.accountId + "\n version : " + aotpAccount.getAttribute("VER_"));
                    } else {
                        provisionResponse.setText(getResources().getString(R.string.failed));
                    }
                } catch (MASAuthOTPException e) {
                    provisionResponse.setText(e.getResponse().toString());
                } catch (Exception e) {
                    provisionResponse.setText(e.getMessage());
                }
            }
        });

        final EditText generateOTP_userID = (EditText) findViewById(R.id.generate_otp_userid);
        final EditText generateOTP_PIN = (EditText) findViewById(R.id.generate_otp_pin);
        final EditText generateOTP_response = (EditText) findViewById(R.id.generate_otp_response);
        final EditText challenge_edit_text = (EditText) findViewById(R.id.challenge_edit_text);
        final EditText generateOTP_orgname = (EditText) findViewById(R.id.generate_otp_orgname);
        final EditText generateOTP_namespace = (EditText) findViewById(R.id.generate_otp_namespace);
        final boolean[] challenge = {false};
        Spinner otp_type_spinner = (Spinner) findViewById(R.id.otp_type_spinner);
        final ArrayAdapter<String> mainCategoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, otp_types_array);
        otp_type_spinner.setAdapter(mainCategoryList);

        otp_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                challenge_edit_text.setText("");
                if (position == 1) {
                    challenge[0] = true;
                    challenge_edit_text.setVisibility(View.VISIBLE);
                } else if (position == 0) {
                    challenge[0] = false;
                    challenge_edit_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button generateOTP = (Button) findViewById(R.id.generate_otp_button);
        generateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hashtable hashtable = null;
                try {
                    if (challenge[0] == true) {
                        hashtable = new Hashtable();
                        hashtable.put(OTP.P_UN, challenge_edit_text.getEditableText().toString());
                        hashtable.put(OTP.P_MODE, OTP.M_1);
                    }
                    String otp = MASAuthOTP.getInstance().generateAOTP(generateOTP_userID.getEditableText().toString() + "::" + generateOTP_orgname.getEditableText().toString() + "::"+ generateOTP_namespace.getEditableText().toString(), generateOTP_PIN.getEditableText().toString(), hashtable);
                    generateOTP_response.setText(otp);
                } catch (MASAuthOTPException e) {
                    generateOTP_response.setText(e.getResponse().toString());
                } catch (Exception e) {
                    generateOTP_response.setText(e.getMessage());
                }
            }
        });

        final EditText loginWithAOTP_userID = (EditText) findViewById(R.id.loginWithAOTP_userID);
        final EditText loginWithAOTP_pin = (EditText) findViewById(R.id.loginWithAOTP_pin);
        final EditText loginWithAOTP_response = (EditText) findViewById(R.id.loginWithAOTP_response);
        final EditText loginWithAOTP_orgname = (EditText) findViewById(R.id.loginWithAOTP_orgname);
        final EditText loginWithAOTP_namespace = (EditText) findViewById(R.id.loginWithAOTP_namespace);
        Button loginWithAOTP_button = (Button) findViewById(R.id.loginWithAOTP_button);

        loginWithAOTP_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginWithAOTP_response.setText("");
                    }
                });


                MASAuthOTP.getInstance().loginWithAOTP(loginWithAOTP_userID.getEditableText().toString()+"::"+ loginWithAOTP_orgname.getEditableText().toString()+"::"+loginWithAOTP_namespace.getEditableText().toString(), loginWithAOTP_pin.getEditableText().toString(), new MASCallback<MASUser>() {
                    @Override
                    public void onSuccess(MASUser result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginWithAOTP_response.setText(getResources().getString(R.string.success));
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (throwable.getCause() instanceof TargetApiException) {
                                    String aa = ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString();
                                } else if (throwable.getCause() instanceof MASAuthOTPException) {
                                    loginWithAOTP_response.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    loginWithAOTP_response.setText("" + throwable.getMessage());
                                }
                            }
                        });
                    }
                });

            }
        });
        final EditText get_otp_userID = (EditText) findViewById(R.id.get_otp_account_userID);
        final EditText get_otp_response = (EditText) findViewById(R.id.get_otp_account_response);
        final EditText get_otp_orgname = (EditText) findViewById(R.id.get_otp_orgname);
        final EditText get_otp_namespace = (EditText) findViewById(R.id.get_otp_namespace);
        Button getOTPAccount = (Button) findViewById(R.id.get_otp_account_button);

        getOTPAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    AOTPAccount aotpAccount = MASAuthOTP.getInstance().getAOTPAccount(get_otp_userID.getEditableText().toString()+ "::"+ get_otp_orgname.getEditableText().toString()+ "::"+ get_otp_namespace.getEditableText().toString());
                    if (aotpAccount != null)
                        get_otp_response.setText(aotpAccount.getId());
                    else
                        get_otp_response.setText(getResources().getString(R.string.failed));
                } catch (MASAuthOTPException e) {
                    get_otp_response.setText(e.getResponse().toString());
                }
            }
        });

        final EditText getAllOTPAccoutsResponse = (EditText) findViewById(R.id.getAllOTPAccounts_response);

        Button getAllOTPAccounts = (Button) findViewById(R.id.getAllOTPAccounts_button);
        getAllOTPAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    AOTPAccount[] aotpAccounts = MASAuthOTP.getInstance().getAllAOTPAccounts();

                    if (aotpAccounts != null && aotpAccounts.length > 0) {

                        JSONArray jsonArray = new JSONArray();

                        for (AOTPAccount account : aotpAccounts) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("User ID", account.getId());
                                jsonObject.put("UserName", account.name);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        getAllOTPAccoutsResponse.setText(jsonArray.toString());
                    } else {
                        getAllOTPAccoutsResponse.setText(getResources().getString(R.string.no_accounts));
                    }
                } catch (MASAuthOTPException e) {
                    getAllOTPAccoutsResponse.setText(e.getResponse().toString());
                }
            }
        });

        final EditText getRoamingKeysUserID = (EditText) findViewById(R.id.get_roamingkeys_userID);
        final EditText getRoamingKeysOrgName = (EditText) findViewById(R.id.get_roamingkeys_orgname);
        final EditText getRoamingKeysNamespace = (EditText) findViewById(R.id.get_roamingkeys_namespace);
        final EditText getRoamingKeysResponse = (EditText) findViewById(R.id.get_roamingkeys_response);
        Button getRoamingKeysButton = (Button) findViewById(R.id.get_roamingkeys__button);

        getRoamingKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRoamingKeysResponse.setText("");
                AOTPAccount aotpAccount = null;

                try {
                    aotpAccount = MASAuthOTP.getInstance().getAOTPAccount(getRoamingKeysUserID.getEditableText().toString()+ "::" + getRoamingKeysOrgName.getEditableText().toString() + "::" + getRoamingKeysNamespace.getEditableText().toString());
                    if (aotpAccount != null) {
                        String roamingKeys = MASAuthOTP.getInstance().getRoamingKeys(aotpAccount);
                        getRoamingKeysResponse.setText(roamingKeys);
                    } else {
                        getRoamingKeysResponse.setText("Enter Valid UserID");
                    }

                } catch (MASAuthOTPException e) {
                    getRoamingKeysResponse.setText(e.getResponse().toString());
                    e.printStackTrace();
                }
            }
        });

        final EditText delete_otp_userID = (EditText) findViewById(R.id.remove_otp_account_userID);
        final EditText delete_otp_orgname = (EditText) findViewById(R.id.remove_otp_orgname);
        final EditText delete_otp_namespace = (EditText) findViewById(R.id.remove_otp_namespace);
        final EditText delete_otp_response = (EditText) findViewById(R.id.remove_otp_account_response);
        Button deleteOTPAccount = (Button) findViewById(R.id.remove_otp_account_button);
        deleteOTPAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    delete_otp_response.setText("");

                    MASAuthOTP.getInstance().removeAOTPAccount(delete_otp_userID.getEditableText().toString()+ "::"+delete_otp_orgname.getEditableText().toString()+"::"+delete_otp_namespace.getEditableText().toString());
                    delete_otp_response.setText(getResources().getString(R.string.success));

                } catch (MASAuthOTPException e) {
                    delete_otp_response.setText(e.getResponse().toString());
                }
            }
        });

        final EditText resync_userID = (EditText) findViewById(R.id.resync_userID);
        final EditText resync_time = (EditText) findViewById(R.id.resync_time);
        final EditText resync_orgname = (EditText) findViewById(R.id.resync_orgname);
        final EditText resync_namespace = (EditText) findViewById(R.id.resync_namespace);
        final EditText resync_response = (EditText) findViewById(R.id.resync_response);
        Button resync_Button = (Button) findViewById(R.id.resync_button);

        resync_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AOTPAccount aotpAccount = null;

                try {
                    resync_response.setText("");
                    aotpAccount = MASAuthOTP.getInstance().getAOTPAccount(resync_userID.getEditableText().toString()+ "::"+ resync_orgname.getEditableText().toString()+"::"+resync_namespace.getEditableText().toString());

                    if (aotpAccount != null) {
                        MASAuthOTP.getInstance().resync(aotpAccount, resync_time.getEditableText().toString());
                        resync_response.setText("Resync Successfull");
                    } else {
                        resync_response.setText("Enter valid aotp_data");
                    }

                } catch (MASAuthOTPException e) {
                    resync_response.setText(e.getResponse().toString());
                    e.printStackTrace();
                }
            }
        });


        final EditText logout_response = (EditText) findViewById(R.id.logout_response);
        Button unregisterButton = (Button)findViewById(R.id.aid_unregister_device_button);


        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_response.setText("");
                MASDevice.getCurrentDevice().deregister(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        //MASDevice.getCurrentDevice().resetLocally();
                        //showMessage("Server Registration Removed for This Device", Toast.LENGTH_SHORT);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logout_response.setText(getResources().getString(R.string.success) +"::"+" De register successful");
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

        Button deleteTokenStoreButton = (Button) findViewById(R.id.aid_delete_token_store_btn);


        deleteTokenStoreButton.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          MASDevice.getCurrentDevice().resetLocally();
                                                          context.runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  logout_response.setText("Reset successful");
                                                              }
                                                          });
                                                      }
                                                  }
        );



        Button logoutButton = (Button) findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_response.setText("");

                if (MASUser.getCurrentUser() != null) {
                    MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logout_response.setText(getResources().getString(R.string.success));
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            runOnUiThread(new Runnable() {
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



        // Issuance apis

        //Create AOTP
        final EditText createAOTPUserName = (EditText) findViewById(R.id.aotp_username);
        final EditText createAOTPOrgName = (EditText) findViewById(R.id.aotp_org_name);
        final EditText createAOTPPassword = (EditText) findViewById(R.id.aotp_pass_word);
        final EditText createAOTPClientTnxID = (EditText) findViewById(R.id.aotp_client_txn_id);
        final EditText createAOTPResponse = (EditText) findViewById(R.id.aotp_response);
        final EditText createAdditionalHeaders = (EditText) findViewById(R.id.aotp_additional_header);
        final EditText createAOTPAdditionalParam = (EditText) findViewById(R.id.aotp_additional_param);
        final CheckBox createCheckBox = (CheckBox) findViewById(R.id.aotp_checkbox);
        Button aotpCreateButton = (Button) findViewById(R.id.aotp_create_id);
        aotpCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = createAOTPUserName.getText().toString();
                String orgName = createAOTPOrgName.getText().toString();
                String profileName = createAOTPPassword.getText().toString();
                String clientTnxId = createAOTPClientTnxID.getText().toString();
                String addParamStr = createAOTPAdditionalParam.getText().toString();
                String addHeaderStr = createAdditionalHeaders.getText().toString();
                createAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);
                if(createCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }

                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);


                MASAuthOTP.getInstance().createAOTP(masAotpIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    createAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        createAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });
            }
        });


        // Delete  AOTP
        final EditText deleteAOTPUserName = (EditText) findViewById(R.id.delete_aotp_username);
        final EditText deleteAOTPOrgName = (EditText) findViewById(R.id.delete_aotp_org_name);
        final EditText deleteAOTPPassword = (EditText) findViewById(R.id.delete_aotp_pass_word);
        final EditText deleteAOTPClientTnxID = (EditText) findViewById(R.id.delete_aotp_client_txn_id);
        final EditText deleteAOTPResponse = (EditText) findViewById(R.id.delete_aotp_response);
        final EditText deleteAOTPAdditionalHeaders = (EditText) findViewById(R.id.delete_aotp_additional_header);
        final EditText deleteAOTPAdditionalParam = (EditText) findViewById(R.id.delete_aotp_additional_param);
        final CheckBox deleteAOTPCheckBox = (CheckBox) findViewById(R.id.delete_aotp_checkbox);
        Button aotpDeleteButton = (Button) findViewById(R.id.aotp_delete);
        aotpDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = deleteAOTPUserName.getText().toString();
                String orgName = deleteAOTPOrgName.getText().toString();
                String profileName = deleteAOTPPassword.getText().toString();
                String clientTnxId = deleteAOTPClientTnxID.getText().toString();
                String addHeaderStr = deleteAOTPAdditionalHeaders.getText().toString();
                String addParamStr = deleteAOTPAdditionalParam.getText().toString();
                deleteAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);

                if(deleteAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }
                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);

                MASAuthOTP.getInstance().deleteAOTP(masAotpIssuanceRequestParams, new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    deleteAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        deleteAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });


            }
        });

        // Disable  AOTP
        final EditText disableAOTPUserName = (EditText) findViewById(R.id.disable_aotp_username);
        final EditText disableAOTPOrgName = (EditText) findViewById(R.id.disable_aotp_org_name);
        final EditText disableAOTPPassword = (EditText) findViewById(R.id.disable_aotp_pass_word);
        final EditText disableAOTPClientTnxID = (EditText) findViewById(R.id.disable_aotp_client_txn_id);
        final EditText disableAOTPResponse = (EditText) findViewById(R.id.disable_aotp_response);
        final EditText disableAOTPAdditionalHeaders = (EditText) findViewById(R.id.disable_aotp_additional_header);
        final EditText disableAOTPAdditionalParam = (EditText) findViewById(R.id.disable_aotp_additional_param);
        final CheckBox disableAOTPCheckBox = (CheckBox) findViewById(R.id.disable_aotp_checkbox);
        Button aotpDisableButton = (Button) findViewById(R.id.aotp_disable);
        aotpDisableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = disableAOTPUserName.getText().toString();
                String orgName = disableAOTPOrgName.getText().toString();
                String profileName = disableAOTPPassword.getText().toString();
                String clientTnxId = disableAOTPClientTnxID.getText().toString();
               // need to add
                String addHeaderStr = disableAOTPAdditionalHeaders.getText().toString();
                String addParamStr = disableAOTPAdditionalParam.getText().toString();
                disableAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);
                if(disableAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }

                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);

                MASAuthOTP.getInstance().disableAOTP(masAotpIssuanceRequestParams, new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disableAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    disableAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        disableAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });


            }
        });

        // Download  AOTP
        final EditText downloadAOTPUserName = (EditText) findViewById(R.id.download_aotp_username);
        final EditText downloadAOTPOrgName = (EditText) findViewById(R.id.download_aotp_org_name);
        final EditText downloadAOTPPassword = (EditText) findViewById(R.id.download_aotp_pass_word);
        final EditText downloadAOTPClientTnxID = (EditText) findViewById(R.id.download_aotp_client_txn_id);
        final EditText downloadAOTPResponse = (EditText) findViewById(R.id.download_aotp_response);
        final EditText downloadAOTPAdditionalHeaders = (EditText) findViewById(R.id.download_aotp_additional_header);
        final EditText downloadAOTPAdditionalParam = (EditText)findViewById(R.id.download_aotp_additional_param);
        final CheckBox downloadAOTPCheckBox = (CheckBox)findViewById(R.id.download_aotp_checkbox);
        Button aotpDownloadButton = (Button) findViewById(R.id.aotp_download);
        aotpDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = downloadAOTPUserName.getText().toString();
                String orgName = downloadAOTPOrgName.getText().toString();
                String profileName = downloadAOTPPassword.getText().toString();
                String clientTnxId = downloadAOTPClientTnxID.getText().toString();
                String addParamStr = downloadAOTPAdditionalParam.getText().toString();
                String addHeaderStr = downloadAOTPAdditionalHeaders.getText().toString();
                downloadAOTPResponse.setText("");


                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);

                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);

                if(downloadAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }
                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);


                MASAuthOTP.getInstance().downloadAOTP(masAotpIssuanceRequestParams , new MASCallback<MASResponse<JSONObject>>() {
                   @Override
                   public void onSuccess(final MASResponse<JSONObject> result) {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               downloadAOTPResponse.setText(result.getBody().getContent().toString());
                           }
                       });
                   }

                   @Override
                   public void onError(final Throwable throwable) {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                               if (throwable.getCause() instanceof MASAuthOTPException) {
                                   downloadAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                               } else {
                                   if (throwable.getCause() instanceof TargetApiException)
                                       downloadAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                               }
                           }
                       });
                   }
               });

            }
        });


        // Enable  AOTP
        final EditText enableAOTPUserName = (EditText) findViewById(R.id.enable_aotp_username);
        final EditText enableAOTPOrgName = (EditText) findViewById(R.id.enable_aotp_org_name);
        final EditText enableAOTPPassword = (EditText) findViewById(R.id.enable_aotp_pass_word);
        final EditText enableAOTPClientTnxID = (EditText) findViewById(R.id.enable_aotp_client_txn_id);
        final EditText enableAOTPResponse = (EditText) findViewById(R.id.enable_aotp_response);
        final EditText enableAOTPAdditionalHeaders = (EditText) findViewById(R.id.enable_aotp_additional_header);
        final EditText enableAOTPAdditionalParam = (EditText) findViewById(R.id.enable_aotp_additional_param);
        final CheckBox enableAOTPCheckBox = (CheckBox) findViewById(R.id.enable_aotp_checkbox);
        Button aotpEnableButton = (Button) findViewById(R.id.aotp_enable);
        aotpEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = enableAOTPUserName.getText().toString();
                String orgName = enableAOTPOrgName.getText().toString();
                String profileName = enableAOTPPassword.getText().toString();
                String clientTnxId = enableAOTPClientTnxID.getText().toString();
                String addHeaderStr = enableAOTPAdditionalHeaders.getText().toString();
                String addParamStr = enableAOTPAdditionalParam.getText().toString();
                enableAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);

                if(enableAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }
                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);

                MASAuthOTP.getInstance().enableAOTP(masAotpIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    enableAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        enableAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });

            }
        });


        // Fetch  AOTP
        final EditText fetchAOTPUserName = (EditText) findViewById(R.id.fetch_aotp_username);
        final EditText fetchAOTPOrgName = (EditText) findViewById(R.id.fetch_aotp_org_name);
        final EditText fetchAOTPPassword = (EditText) findViewById(R.id.fetch_aotp_pass_word);
        final EditText fetchAOTPClientTnxID = (EditText) findViewById(R.id.fetch_aotp_client_txn_id);
        final EditText fetchAOTPResponse = (EditText) findViewById(R.id.fetch_aotp_response);
        final EditText fetchAOTPAdditionalHeaders = (EditText) findViewById(R.id.fetch_aotp_additional_header);
        final EditText fetchAOTPAdditionalParam = (EditText) findViewById(R.id.fetch_aotp_additional_param);
        final CheckBox fetchAOTPCheckBox = (CheckBox) findViewById(R.id.fetch_aotp_checkbox);
        Button aotpFetchButton = (Button) findViewById(R.id.aotp_fetch);
        aotpFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = fetchAOTPUserName.getText().toString();
                String orgName = fetchAOTPOrgName.getText().toString();
                String password = fetchAOTPPassword.getText().toString();
                String clientTnxId = fetchAOTPClientTnxID.getText().toString();
                String addHeaderStr = fetchAOTPAdditionalHeaders.getText().toString();
                String addParamStr = fetchAOTPAdditionalParam.getText().toString();
                fetchAOTPResponse.setText("");
                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);
                if(fetchAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }

                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(password);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);


                MASAuthOTP.getInstance().fetchAOTP(masAotpIssuanceRequestParams ,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fetchAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    fetchAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        fetchAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });


            }
        });


        // Re-Issue  AOTP
        final EditText reIssueAOTPUserName = (EditText) findViewById(R.id.reIssue_aotp_username);
        final EditText reIssueAOTPOrgName = (EditText) findViewById(R.id.reIssue_aotp_org_name);
        final EditText reIssueAOTPPassword = (EditText) findViewById(R.id.reIssue_aotp_pass_word);
        final EditText reIssueAOTPClientTnxID = (EditText) findViewById(R.id.reIssue_aotp_client_txn_id);
        final EditText reIssueAOTPResponse = (EditText) findViewById(R.id.reIssue_aotp_response);
        final EditText reIssueAOTPAdditionalHeaders = (EditText) findViewById(R.id.reIssue_aotp_additional_header);
        final EditText reIssueAOTPAdditionalParam = (EditText) findViewById(R.id.reIssue_aotp_additional_param);
        final CheckBox reIssueAOTPCheckBox = (CheckBox) findViewById(R.id.reIssue_aotp_checkbox);
        Button aotpReIssueButton = (Button) findViewById(R.id.aotp_reIssue);
        aotpReIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = reIssueAOTPUserName.getText().toString();
                String orgName = reIssueAOTPOrgName.getText().toString();
                String profileName = reIssueAOTPPassword.getText().toString();
                String clientTnxId = reIssueAOTPClientTnxID.getText().toString();
                String addHeaderStr = reIssueAOTPAdditionalHeaders.getText().toString();
                String addParamStr = reIssueAOTPAdditionalParam.getText().toString();
                reIssueAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);
                if(reIssueAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }

                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);

                MASAuthOTP.getInstance().reIssueAOTP(masAotpIssuanceRequestParams,new MASCallback<MASResponse<JSONObject>>() {
                    @Override
                    public void onSuccess(final MASResponse<JSONObject> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reIssueAOTPResponse.setText(result.getBody().getContent().toString());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                                if (throwable.getCause() instanceof MASAuthOTPException) {
                                    reIssueAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                                } else {
                                    if (throwable.getCause() instanceof TargetApiException)
                                        reIssueAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                                }
                            }
                        });
                    }
                });



            }
        });

        // Reset  AOTP
        final EditText resetAOTPUserName = (EditText) findViewById(R.id.reset_aotp_username);
        final EditText resetAOTPOrgName = (EditText) findViewById(R.id.reset_aotp_org_name);
        final EditText resetAOTPPassword = (EditText) findViewById(R.id.reset_aotp_pass_word);
        final EditText resetAOTPClientTnxID = (EditText) findViewById(R.id.reset_aotp_client_txn_id);
        final EditText resetAOTPAdditionalHeaders = (EditText) findViewById(R.id.reset_aotp_additional_header);
        final EditText resetAOTPAdditionalParam = (EditText) findViewById(R.id.reset_aotp_additional_param);
        final CheckBox resetAOTPCheckBox = (CheckBox) findViewById(R.id.reset_aotp_checkbox);
        final EditText resetAOTPResponse = (EditText) findViewById(R.id.reset_aotp_response);
        Button aotpResetButton = (Button) findViewById(R.id.aotp_reset);
        aotpResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = resetAOTPUserName.getText().toString();
                String orgName = resetAOTPOrgName.getText().toString();
                String profileName = resetAOTPPassword.getText().toString();
                String clientTnxId = resetAOTPClientTnxID.getText().toString();
                String addHeaderStr = resetAOTPAdditionalHeaders.getText().toString();
                String addParamStr = resetAOTPAdditionalParam.getText().toString();
                resetAOTPResponse.setText("");

                HashMap<String,String> addHeaders = AppUtil.addBasicAuthHeaders(addHeaderStr);
                HashMap<String,String> addParams = AppUtil.addIssuanceAdditionalParams(addParamStr);
                MASAuthOTPCustomRequestData authOTPCustomRequestData = new MASAuthOTPCustomRequestData();
                authOTPCustomRequestData.setHeaders(addHeaders);
                authOTPCustomRequestData.setQueryParams(addParams);
                if(resetAOTPCheckBox.isChecked()){
                    authOTPCustomRequestData.setPublic(true);
                }
                MASAOTPIssuanceRequestParams masAotpIssuanceRequestParams = new MASAOTPIssuanceRequestParams();
                masAotpIssuanceRequestParams.setUserName(userName);
                masAotpIssuanceRequestParams.setOrgName(orgName);
                masAotpIssuanceRequestParams.setProfileName(profileName);
                masAotpIssuanceRequestParams.setClientTxnId(clientTnxId);
                masAotpIssuanceRequestParams.setMasAuthOTPCustomRequestData(authOTPCustomRequestData);

            MASAuthOTP.getInstance().resetAOTP(masAotpIssuanceRequestParams, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(final MASResponse<JSONObject> result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetAOTPResponse.setText(result.getBody().getContent().toString());
                        }
                    });
                }

                @Override
                public void onError(final Throwable throwable) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                            if (throwable.getCause() instanceof MASAuthOTPException) {
                                resetAOTPResponse.setText(((MASAuthOTPException) throwable.getCause()).getResponse().toString());
                            } else {
                                if (throwable.getCause() instanceof TargetApiException)
                                    resetAOTPResponse.setText(((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString());

                            }
                        }
                    });
                }
            });
            }
        });
        super.onCreate(savedInstanceState);

    }

    private URI getProductListDownloadUri() {
        try {
            return new URI("/protected/resource/products?operation=listProducts");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void invokeGetProducts() {
        final MASRequest request = new MASRequest.MASRequestBuilder(getProductListDownloadUri()).build();
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {

            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {
                protectedAPICall.setText(result.getBody().getContent().toString());
            }

            @Override
            public void onError(Throwable e) {
                if (e.getCause() instanceof TargetApiException) {
                    protectedAPICall.setText(new String(((TargetApiException) e.getCause()).getResponse()
                            .getBody().getRawContent()));
                } else {
                    protectedAPICall.setText(getResources().getString(R.string.failed));
                }
            }
        });
    }
}
