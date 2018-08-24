package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.mas.fido.MASFido;
import com.ca.mas.fido.exception.MASFidoException;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASUser;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

public class SelectRegistrationCredActivity extends BaseActivity {
    TextView authid;
    RadioButton regForAIDrb, regForAOTPrb, regForBIOrb, deregBIOrb;
    Activity activity;
    String msg, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_registration_cred);

        activity = this;
        authid = (TextView) findViewById(R.id.authid);
        regForAIDrb = (RadioButton) findViewById(R.id.regForAIDrb);
        regForAIDrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.REG_TYPE = ApplicationConstants.REGISTERING_FOR.AUTH_ID;
                Intent intent = new Intent(activity, UserDLVerificationActivity.class);
                startActivity(intent);
            }
        });

        regForAOTPrb = (RadioButton) findViewById(R.id.regForAOTPrb);
        regForAOTPrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.REG_TYPE = ApplicationConstants.REGISTERING_FOR.AUTH_OTP;
                Intent intent = new Intent(activity, UserDLVerificationActivity.class);
                startActivity(intent);
            }
        });

        regForBIOrb = (RadioButton) findViewById(R.id.regForBiometrics);
        deregBIOrb = (RadioButton) findViewById(R.id.deregForBiometric);
        if (!ApplicationConstants.FIDO_LOGIN) {
            regForBIOrb.setVisibility(View.GONE);
            deregBIOrb.setVisibility(View.GONE);

        } else {

            regForBIOrb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppUtil.isNetworkAvailable(activity)) {
                        regForBIOrb.setChecked(false);
                        msg = "Please check the internet connectivity";
                        title = "Network Error";
                        showAlertDialog();
                        return;
                    }
                    if (MASUser.getCurrentUser() == null || !MASUser.getCurrentUser().isAuthenticated()) {

                        msg = "Select OK to login before you register biometrics.";
                        title = "Login Required";
                        showAlertDialog();
                    } else {
                        fidoRegister();
                    }


                }
            });


            deregBIOrb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppUtil.isNetworkAvailable(activity)) {
                        deregBIOrb.setChecked(false);
                        msg = "Please check the internet connectivity";
                        title = "Network Error";
                        showAlertDialog();
                        return;
                    }
                    if (MASUser.getCurrentUser() == null || !MASUser.getCurrentUser().isAuthenticated()) {

                        msg = "Select OK to login before you deregister biometrics.";
                        title = "Login Required";
                        showAlertDialog();
                    } else {

                        fidoDeregistration();
                    }
                }
            });
        }
        updateTheme();
        super.onCreate(savedInstanceState);

    }

    private void fidoRegister() {
        if(ApplicationConstants.FIDO_LOGIN) {
            try {
                MASFido.registerWithFido(MASUser.getCurrentUser().getId(), new MASCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        AppUtil.showAlertDialog(activity, "Biometrics registered.", "Success", false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!(e.getMessage().contains("cancel")))
                            AppUtil.showAlertDialog(activity, "Biometric registration failed.", "Failure", false);
                    }
                });
            } catch (MASFidoException e) {
                AppUtil.showAlertDialog(activity, "Biometric registration failed.", "Failure", false);
                Log.i("error", e.getErrorDetails().toString());
            }
        }
        else{
            regForBIOrb.setChecked(false);
           showFidoDisbaleToast();
        }
    }

    private void showFidoDisbaleToast(){
        Toast toast = Toast.makeText(this, getResources().getString(R.string.fidologindisabled), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void fidoDeregistration(){
        if(ApplicationConstants.FIDO_LOGIN){
        try {
            MASFido.deregisterFido(MASUser.getCurrentUser().getId(), new MASCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    AppUtil.showAlertDialog(activity, "Biometrics deregistered.", "Success", false);
                }

                @Override
                public void onError(Throwable e) {
                    String message = "Biometric deregistration failed.";
                    if(!e.getMessage().contains("cancel")) {
                        if(e.getMessage().contains("911105"))
                            message = "No Biometrics found.";
                        AppUtil.showAlertDialog(activity, message, "Failure", false);
                    }


                }
            });
        } catch (MASFidoException e) {
            AppUtil.showAlertDialog(activity, "Biometric deregistration failed.", "Failure", false);
            Log.i("error", e.getErrorDetails().toString());
        }}
        else{
            deregBIOrb.setChecked(false);
            showFidoDisbaleToast();
        }
    }

    private void showAlertDialog() {
       AppUtil.showAlertDialog(activity, msg, title, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        regForAIDrb.setChecked(false);
        regForAOTPrb.setChecked(false);
        regForBIOrb.setChecked(false);
        deregBIOrb.setChecked(false);
        updateTheme();

    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0) {
            authid.setTextColor(AppThemeConstants.TEXT_COLOR);
            regForAIDrb.setTextColor(AppThemeConstants.TEXT_COLOR);
            regForAOTPrb.setTextColor(AppThemeConstants.TEXT_COLOR);
            regForBIOrb.setTextColor(AppThemeConstants.TEXT_COLOR);
            deregBIOrb.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
    }
}