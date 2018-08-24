package com.example.ras.rs;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ca.mas.fido.MASFido;
import com.ca.mas.fido.exception.MASFidoException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.example.ras.BaseActivity;
import com.example.ras.HomeActivity;
import com.example.ras.LoginActivity;
import com.example.ras.R;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class StepUpOnLocationRiskActivity extends AppCompatActivity {

    public static final String ACTIVITYMESSAGE = "ACTIVITYMESSAGE";
    private static final String TAG = "StpUpOnLocRisk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_up_on_location_risk);




    }

    public void putDataInIntentBundle(Intent intent, String message) {
        intent.putExtra(ACTIVITYMESSAGE,message);
    }

    public final static int SESSION_UNLOCK_CODE = 0x1000;
    private void doBiometricLogin() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Confirm your credentials",
                "Use a fingerprint or enter the current PIN");
        if (intent != null) {
            startActivityForResult(intent, SESSION_UNLOCK_CODE);
        }

    }



    private void doFidoLogin() {
        try {
            if (!ApplicationConstants.FIDO_LOGIN) {
                //showToastMessage("FIDO is disabled. Unable to perform step up authentication. Transaction failed. ");
                return;
            }
            MASFido.loginWithFido(MASUser.getCurrentUser().getId(), new MASCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    gotoHomeScreen();
                }

                @Override
                public void onError(Throwable e) {
                    //showToastMessage("Biometric Authentication Error");
                    doUserLogout();
                    Log.e(TAG, e.getMessage());
                    gotoLoginScreen("Biometric Authentication Error");
                }
            });

        } catch (MASFidoException e) {
            //showToastMessage(e.getMessage());
            Log.e(TAG, e.getMessage());
        }
    }

    private void doUserLogout() {
        MASUser.getCurrentUser().logout(new MASCallback<Void>() {
            @Override
            public void onSuccess(Void result) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private void gotoHomeScreen() {
        Intent intent = new Intent(StepUpOnLocationRiskActivity.this, HomeActivity.class);
        putDataInIntentBundle(intent, "Login Successful");
        startActivity(intent);
    }


    private void doOTPLogin() {
        final MASRequest request = new MASRequest.MASRequestBuilder(getOTPProtectedUrl()).build();
        MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
            @Override
            public void onSuccess(MASResponse<Object> result) {
                gotoHomeScreen();
            }

            @Override
            public void onError(Throwable e) {

                Log.e(TAG, e.getMessage());
                MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                });

                gotoLoginScreen("OTP Authentication Error");
            }
        });

    }

    private void gotoLoginScreen(String s) {
        /*try {
            doUserLogout();
        } catch ( Exception e) {
        }*/
        Intent intent = new Intent(StepUpOnLocationRiskActivity.this, LoginActivity.class);
        putDataInIntentBundle(intent, s);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }


    private URI getOTPProtectedUrl() {
        try {
            return new URI("/otpProtected");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isOnResume = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isOnResume = true;
        if (requestCode == SESSION_UNLOCK_CODE ) {
            if (resultCode == RESULT_OK) {
                gotoHomeScreen();
            } else if (resultCode == RESULT_CANCELED) {
                gotoLoginScreen("Login cancelled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isOnResume) {
            isOnResume = false;
            return;
        }
        if(
            //(MASUser.getCurrentUser() != null && MASUser.getCurrentUser().isAuthenticated()) ||
                ApplicationConstants.LOCATION.startsWith("DISABLED") || ApplicationConstants.LOCATION.startsWith("United")
                        || ApplicationConstants.LOCATION.startsWith("Canada")) {

            gotoHomeScreen();
        } else if (ApplicationConstants.LOCATION.startsWith("North") ) {
            MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                @Override
                public void onSuccess(Void result) {

                    gotoLoginScreen("Login Failed. Risk too high.");
                }

                @Override
                public void onError(Throwable e){

                }

            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Step-up triggered due to geo location");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM== null || ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM.equals("BIOMETRIC")) {
                        doBiometricLogin();
                    } else if (ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM.equals("FIDO")) {
                        doFidoLogin();
                    }  else {
                        doOTPLogin();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        }

    }

