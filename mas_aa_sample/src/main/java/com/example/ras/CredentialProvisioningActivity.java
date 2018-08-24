package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ca.apim.mas.authid.MASAuthID;
import com.ca.apim.mas.authid.MASAuthIDException;
import com.ca.apim.mas.authotp.MASAuthOTP;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASRequestBody;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASResponseBody;
import com.example.ras.error.ErrorObject;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

import java.net.URI;

public class CredentialProvisioningActivity extends BaseActivity {


    EditText usernameet, pwdet, confirmpwet;
    Button register;
    Activity activity;
    TextView credRegHeadertv,userID,pwd,confirm_pwd;

    static String ORGNAME = "orgName";
    static String PASSWORD = "password";
    static String VERSION = "credentialVersion";

    static String AUTHID= "authID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_credential_provisioning);
        activity = this;
        usernameet = findViewById(R.id.usernameet);
        usernameet.setEnabled(false);

        usernameet.setText(ApplicationConstants.USERNAME);
        pwdet = (EditText) findViewById(R.id.pwdet);
        confirmpwet = (EditText) findViewById(R.id.confirmPwdet);
        register = (Button) findViewById(R.id.registerCredbtn);
        credRegHeadertv = (TextView) findViewById(R.id.credRegHeadertv);
        userID = (TextView) findViewById(R.id.credRegHeadertv);
        pwd = (TextView) findViewById(R.id.credRegHeadertv);
        confirm_pwd = (TextView) findViewById(R.id.credRegHeadertv);

        if (ApplicationConstants.REG_TYPE.equals(ApplicationConstants.REGISTERING_FOR.AUTH_ID)) {
            credRegHeadertv.setText("CA AUTH ID REGISTRATION");
        } else {
            credRegHeadertv.setText("CA MOBILE OTP REGISTRATION");
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwdet.getText().toString().equals(confirmpwet.getText().toString())) {
                    showToastMessage("Passwords don't match");
                    //Toast.makeText(activity, "Passwords don't match", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ApplicationConstants.REG_TYPE.equals(ApplicationConstants.REGISTERING_FOR.AUTH_ID)) {
                    createAuthIdCredentials(usernameet.getText().toString(), pwdet.getText().toString());
                } else {
                    createAuthOTPCredentials(usernameet.getText().toString(), pwdet.getText().toString());
                }
            }
        });

        updateTheme();

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
    }

    private void createAuthIdCredentials(final String username, final String password) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(ORGNAME, ApplicationConstants.ORGNAME);
            jsonRequest.put(PASSWORD, password);
            jsonRequest.put("profileName", ApplicationConstants.AUTHID_PROFILE);

            //jsonRequest.put(VERSION, "2017-05-03T07:20:17.922Z");

            MASRequest request = new MASRequest.MASRequestBuilder(new URI(createAuthIdEndpoint(username)))
                    .setPublic().post(MASRequestBody.jsonBody(jsonRequest)).header(ApplicationConstants.USERNAME_HEADER_KEY,username)
                    .build();

            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> response) {
                    downloadAuthIdCredentials(username);
                }

                @Override
                public void onError(Throwable throwable) {
                    ErrorObject err= AppUtil.getMASErrorMessage(throwable);
                    if ("1102".equals(err.getResponseCode())) {
                        showToastMessage(err.getErrorDetail());
                        return;
                    }
                    reissueAuthIdCredential(username, password);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void reissueAuthIdCredential(final String username, final String password) {
        try {

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(ORGNAME, ApplicationConstants.ORGNAME);
            jsonRequest.put(PASSWORD, password);
            jsonRequest.put("profileName", ApplicationConstants.AUTHID_PROFILE);
            //jsonRequest.put(VERSION, "2017-05-03T07:20:17.922Z");

            MASRequest request = new MASRequest.MASRequestBuilder(new URI(reissueAuthIdEndpoint(username)))
                    .setPublic().post(MASRequestBody.jsonBody(jsonRequest)).header(ApplicationConstants.USERNAME_HEADER_KEY,username)
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    downloadAuthIdCredentials(username);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    showToastMessage(AppUtil.getMASErrorMessage(e).getErrorDetail());
                    //showToastMessage(e.getMessage());
                    //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void downloadAuthIdCredentials(String username) {

        try {
            String profileQueryString;
            if (null == ApplicationConstants.AUTHID_PROFILE || "".equals(ApplicationConstants.AUTHID_PROFILE) ) {
                profileQueryString = "";
            } else {
                profileQueryString = "profileName="+ApplicationConstants.AUTHID_PROFILE+"&";
            }

            String orgNameQueryString;
            if (null == ApplicationConstants.ORGNAME || "".equals(ApplicationConstants.ORGNAME)) {
                orgNameQueryString = "";
            } else {
                orgNameQueryString = "orgName="+ApplicationConstants.ORGNAME;
            }

            MASRequest request = new MASRequest.MASRequestBuilder(new URI(downloadAuthIdEndpoint(username)  + "?"+profileQueryString+orgNameQueryString))
                    .setPublic().header(ApplicationConstants.USERNAME_HEADER_KEY,username)
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    try {
                        MASResponseBody<JSONObject> body = result.getBody();
                        String authIdBase64Str = body.getContent().getString(AUTHID);
                        MASAuthID.getInstance().provisionAIDAccount(authIdBase64Str, ApplicationConstants.NAMESPACE, null);
                        redirectToLoginScreen("AuthId Registration Complete");
                    } catch (Throwable e) {
                        e.printStackTrace();
                        showToastMessage(e.getMessage());
                        //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    showToastMessage(AppUtil.getMASErrorMessage(e).getErrorDetail());
                    //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            showToastMessage(e.getMessage());
        }
    }


    private void createAuthOTPCredentials(final String username, final String password) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(ORGNAME, ApplicationConstants.ORGNAME);
            //jsonRequest.put(PASSWORD, password);
            //jsonRequest.put(PASSWORD, password);

            MASRequest request = new MASRequest.MASRequestBuilder(new URI(createAuthOTPEndpoint(username) ))
                    .setPublic().post(MASRequestBody.jsonBody(jsonRequest)).header(ApplicationConstants.USERNAME_HEADER_KEY,username)
                    .build();

            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    try {
                        JSONObject response = result.getBody().getContent();
                        String aotpCardStr = response.getString("aotp_card");
                        String credentialVersion = response.getString("credentialVersion");
                        String card = createAotpCardString(username, aotpCardStr, credentialVersion);
                        MASAuthOTP.getInstance().provisionAOTPAccount(card, ApplicationConstants.NAMESPACE, password, password, null);
                        redirectToLoginScreen("AuthOTP registration complete");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToastMessage(e.getMessage());
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    reissueAuthOTPCredential(username, password);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void reissueAuthOTPCredential(final String username, final String password) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(ORGNAME, ApplicationConstants.ORGNAME);
            //jsonRequest.put(PASSWORD, password);


            MASRequest request = new MASRequest.MASRequestBuilder(new URI(reissueAuthOTPEndpoint(username)))
                    .setPublic().post(MASRequestBody.jsonBody(jsonRequest)).header(ApplicationConstants.USERNAME_HEADER_KEY, username)
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    try {
                        JSONObject response = result.getBody().getContent();
                        String aotpCardStr = response.getString("aotp_card");
                        String credentialVersion = response.getString("credentialVersion");
                        String card = createAotpCardString(username, aotpCardStr, credentialVersion);

                        MASAuthOTP.getInstance().provisionAOTPAccount(card, ApplicationConstants.NAMESPACE, null, password, null);
                        redirectToLoginScreen("AuthOTP registration complete");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    showToastMessage(AppUtil.getMASErrorMessage(e).getErrorDetail());
                    //showToastMessage(e.getMessage());
                    //Toast.makeText(getApplicationContext()," junk", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
            //Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String createAotpCardString(String username, String aotpCardStr, String credentialVersion) {
        String cardTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><response xmlns=\"http://xs.arcot.com/ArcotOTPProtocolSvc/2.0\" > " +
                "<status>success</status><aid>##USERNAME##</aid><displayName>##USERNAME##</displayName><logoUrl>https://sample.com </logoUrl><expiry>1675593951</expiry>" +
                "<roam>false</roam><algo><algoType>HOTP</algoType>" +
                "<cs>##CARDSTR##" +
                "CredentialVersion=##CREDVER##::</cs></algo></response>";




        cardTemplate = cardTemplate.replace("##CARDSTR##", aotpCardStr);
        cardTemplate = cardTemplate.replace("##CREDVER##", credentialVersion);
        //username = username.toUpperCase();
        cardTemplate = cardTemplate.replace("##USERNAME##", username );

        return cardTemplate;
    }

    void redirectToLoginScreen(String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        putDataInIntentBundle(intent, message);
        startActivity(intent);
    }

    private String createAuthIdEndpoint(String username) {
        return "/auth/strongauth/authid/create/"+username;
    }

    private String reissueAuthIdEndpoint(String username) {
        return "/auth/strongauth/authid/reissue/"+username;
    }

    private String downloadAuthIdEndpoint(String username) {
        return "/auth/strongauth/authid/download/"+username;
    }

    private String createAuthOTPEndpoint(String username) {
        return "/auth/strongauth/aotp/create/"+username;
    }

    private String reissueAuthOTPEndpoint(String username) {
        return "/auth/strongauth/aotp/reissue/"+username;
    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0) {
            credRegHeadertv.setTextColor(AppThemeConstants.TEXT_COLOR);
            userID.setTextColor(AppThemeConstants.TEXT_COLOR);
            pwd.setTextColor(AppThemeConstants.TEXT_COLOR);
            confirm_pwd.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
        if(AppThemeConstants.BUTTON_COLOR !=0) {
            GradientDrawable bgShape = (GradientDrawable)register.getBackground();
            bgShape.setColor(AppThemeConstants.BUTTON_COLOR);
        }
        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0)
            register.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);

    }

}
