package com.example.ras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcot.aid.lib.AIDException;
import com.ca.apim.mas.authid.MASAuthID;
import com.ca.apim.mas.authid.MASAuthIDException;
import com.ca.apim.mas.authotp.MASAuthOTP;
import com.ca.apim.mas.authotp.MASAuthOTPException;
import com.ca.mas.core.auth.otp.OtpAuthenticationHandler;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.core.registration.DeviceRegistrationAwaitingActivationException;
import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.fido.ui.MASFidoLoginActivity;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASException;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.ui.otp.MASOtpActivity;
import com.example.ras.rs.CustomOtpHandler;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by nikru01 on 11/3/2017.
 */


public class RASLoginActivity extends MASFidoLoginActivity {

    TextView or, signInWith;
    ScrollView rasLogin;
    ImageView logo;
    Button login, authid, aotp, facebook, google, salesforce, likedin, enterprise, qrcode;
    RASLoginActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        rasLogin = (ScrollView) findViewById(R.id.rasLogin);

        or = (TextView) findViewById(R.id.tvor);
        signInWith = (TextView) findViewById(R.id.signInWith);
        logo = (ImageView) findViewById(R.id.logo);
        login= (Button) findViewById(R.id.activity_mas_login_button_login);
        authid= (Button) findViewById(R.id.login_with_authid);
        aotp= (Button) findViewById(R.id.login_with_otp);
        facebook= (Button) findViewById(R.id.activity_mas_login_facebook);
        google= (Button) findViewById(R.id.activity_mas_login_google);
        salesforce= (Button) findViewById(R.id.activity_mas_login_salesforce);
        likedin= (Button) findViewById(R.id.activity_mas_login_linked_in);
        enterprise= (Button) findViewById(R.id.activity_mas_login_enterprise);
        qrcode= (Button) findViewById(R.id.activity_mas_login_qr_code);

        /*if(ApplicationConstants.FIDO_LOGIN) {
            View fidoButton = findViewById(R.id.activity_mas_fido_login_button_login);
            fidoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        Toast toast = Toast.makeText(RASLoginActivity.this, getResources().getString(R.string.fidologindisabled), Toast.LENGTH_SHORT);
                        toast.show();
                }
            });
        }*/

        if (!ApplicationConstants.FIDO_LOGIN) {
            View fidoButton = findViewById(R.id.activity_mas_fido_login_button_login);
            fidoButton.setVisibility(View.INVISIBLE);

            TextView tvor = findViewById(R.id.tvor);
            tvor.setVisibility(View.INVISIBLE);
        }

        View view = findViewById(com.ca.mas.fido.R.id.activity_mas_login_button_login);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithUsernamePassword();
            }
        });

        updateTheme();

    }


    private void loginWithUsernamePassword() {
        EditText mEditTextUsername = (EditText) findViewById(com.ca.mas.fido.R.id.activity_mas_login_edit_text_username);
        EditText mEditTextPassword = (EditText) findViewById(com.ca.mas.fido.R.id.activity_mas_login_edit_text_password);
        String username = mEditTextUsername.getText().toString();
        int passwordLength = mEditTextPassword.length();
        char[] password = new char[passwordLength];
        mEditTextPassword.getText().getChars(0, passwordLength, password, 0);
        if (!isValid(username, passwordLength)) {
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Logging in...");
        progress.setCancelable(false);
        progress.show();


        MASUser.login(username, password, new MASCallback<MASUser>() {
            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASUser result) {
                progress.dismiss();
                finish();
            }

            @Override
            public void onError(Throwable e) {
                progress.dismiss();
                Toast.makeText(RASLoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Bundle data = new Bundle();
                data.putString("LOGIN_STATUS", "FAILED");

                MAS.cancelAllRequests();

                if (((MASException)e).getRootCause() instanceof DeviceRegistrationAwaitingActivationException) {

                    Intent intent = new Intent(MAS.getContext(), MASOtpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, new CustomOtpHandler((OtpAuthenticationHandler) null));
                    RASLoginActivity.this.startActivity(intent);
                    RASLoginActivity.this.finish();
                } else {
                    Toast.makeText(RASLoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    //Bundle data = new Bundle();
                    data.putString("LOGIN_STATUS", "FAILED");
                }
            }
        });

    }

    private boolean isValid(String username, int passwordLength) {
        if (username != null && username.isEmpty() && passwordLength == 0) {
            Toast.makeText(RASLoginActivity.this, "Login failed: Invalid credentials - Please enter username and password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username == null || username.isEmpty()) {
            Toast.makeText(RASLoginActivity.this, "Login failed: Invalid credentials - Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordLength == 0) {
            Toast.makeText(RASLoginActivity.this, "Login failed: Invalid credentials -  Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
    }



    public void logingWithAuthId(View view){
        TextInputEditText usernameEt = (TextInputEditText)findViewById( R.id.activity_mas_login_edit_text_username);
        String username = usernameEt.getText().toString();
        TextInputEditText passwordEt = (TextInputEditText)findViewById( R.id.activity_mas_login_edit_text_password);
        String password = passwordEt.getText().toString();

        if (username == null || "".equals(username)) {
            Toast toast = Toast.makeText(RASLoginActivity.this, "Username is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (password == null || "".equals(password)) {
            Toast toast = Toast.makeText(RASLoginActivity.this, "Password is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        MASAuthID.getInstance().loginWithAIDGetSMSession(username+ ApplicationConstants.ORGNAME + ApplicationConstants.NAMESPACE, password,
                ApplicationConstants.AUTHID_POLICY, ApplicationConstants.AUTHID_DEFAULT_ADDITIONAL_PARAMS, true,
                new MASCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Intent intent = new Intent(activity, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(final Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String msg = throwable.getMessage();
                        if (throwable.getCause() instanceof TargetApiException) {
                            msg = ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString();
                        } else if (throwable.getCause() instanceof MASAuthIDException) {
                            try {
                                msg = (String)((MASAuthIDException) throwable.getCause()).getResponse().get("error_details");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (throwable.getCause() instanceof AIDException) {
                            msg =  throwable.getCause().getMessage();
                        }else {
                            msg = "" + throwable.getMessage();
                        }
                        initializeErrorView(msg);

                    }
                });

            }
        });

    }

    public void logingWithOTP(View view){
        TextInputEditText usernameEt = (TextInputEditText)findViewById( R.id.activity_mas_login_edit_text_username);
        String username = usernameEt.getText().toString();
        TextInputEditText passwordEt = (TextInputEditText)findViewById( R.id.activity_mas_login_edit_text_password);
        String password = passwordEt.getText().toString();

        if (username == null || "".equals(username)) {
            Toast toast = Toast.makeText(RASLoginActivity.this, "Username is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (password == null || "".equals(password)) {
            Toast toast = Toast.makeText(RASLoginActivity.this, "Password is empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        MASAuthOTP.getInstance().loginWithAOTP(username + "::" + ApplicationConstants.ORGNAME+ "::" + ApplicationConstants.NAMESPACE,
                password, new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser result) {
                Intent intent = new Intent(activity, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable throwable) {


                String msg = throwable.getMessage();
                if (throwable.getCause() instanceof TargetApiException) {
                    msg = ((TargetApiException) throwable.getCause()).getResponse().getBody().getContent().toString();
                } else if (throwable.getCause() instanceof MASAuthOTPException) {
                    try {
                        msg = (String)((MASAuthOTPException) throwable.getCause()).getResponse().get("error_details");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    msg = "" + throwable.getMessage();
                }

                initializeErrorView(msg);

            }
        });
    }


    void initializeErrorView(String msg){
        TextView errorMessageTextView = (TextView)findViewById( R.id.errorMessageTextView);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setTextColor(ContextCompat.getColor(getApplication(), R.color.deny_highlight));
        errorMessageTextView.setText(msg);
    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0 && AppThemeConstants.TEXT_COLOR != getResources().getColor(R.color.white)) {
            or.setTextColor(AppThemeConstants.TEXT_COLOR);
            signInWith.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
        if(AppThemeConstants.BUTTON_COLOR !=0) {
            GradientDrawable loginShape = (GradientDrawable)login.getBackground();
            loginShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable authidShape = (GradientDrawable)authid.getBackground();
            authidShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable aotpShape = (GradientDrawable)aotp.getBackground();
            aotpShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable facebookShape = (GradientDrawable)facebook.getBackground();
            facebookShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable googleShape = (GradientDrawable)google.getBackground();
            googleShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable salesforceShape = (GradientDrawable)salesforce.getBackground();
            salesforceShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable linkedinShape = (GradientDrawable)likedin.getBackground();
            linkedinShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable enterpriseShape = (GradientDrawable)enterprise.getBackground();
            enterpriseShape.setColor(AppThemeConstants.BUTTON_COLOR);
            GradientDrawable qrcodeShape = (GradientDrawable)qrcode.getBackground();
            qrcodeShape.setColor(AppThemeConstants.BUTTON_COLOR);
        }
        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0) {
            login.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            authid.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            aotp.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            facebook.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            google.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            salesforce.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            likedin.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            enterprise.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
            qrcode.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
        }
        if(AppThemeConstants.LOGO_ICON != ""){
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.LOGO_ICON.getBytes(), Base64.DEFAULT);
            logo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        if(AppThemeConstants.BACKGROUND_IMAGE != "") {
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.BACKGROUND_IMAGE.getBytes(), Base64.DEFAULT);
            BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            rasLogin.setBackground(d);
        }
    }

}
