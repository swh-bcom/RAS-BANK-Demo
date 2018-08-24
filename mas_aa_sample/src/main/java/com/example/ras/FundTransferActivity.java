package com.example.ras;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ca.apim.mas.riskanalysis.MASRiskAnalysis;
import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.fido.MASFido;
import com.ca.mas.fido.exception.MASFidoException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASResponseBody;
import com.ca.mas.foundation.MASUser;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;
import com.marcoscg.fingerauth.FingerAuth;
import com.marcoscg.fingerauth.FingerAuthDialog;
import com.nimbusds.jose.util.Base64URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class FundTransferActivity extends BaseActivity {

    Spinner beneficiary, payTos;
    EditText amount;
    Button transfer;
    Activity context;
    boolean stepUpDone = false;
    //Session Unlocking feature
    public final static int SESSION_UNLOCK_CODE = 0x1000;

    TextView headerText, paytoText, beneficiaryText, transferText;

    static int amountValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fund_transfer);
        context = this;

        headerText = (TextView) findViewById(R.id.fundTransferHeadertv);
        paytoText = (TextView) findViewById(R.id.payTo);
        beneficiaryText = (TextView) findViewById(R.id.beneficiary);
        transferText = (TextView) findViewById(R.id.transfer);

        beneficiary = (Spinner) findViewById(R.id.beneficiarys);
        payTos = (Spinner) findViewById(R.id.payTos);
        amount = (EditText) findViewById(R.id.amountet);
        transfer = (Button) findViewById(R.id.transferBtn);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beneficiaryStr = beneficiary.getSelectedItem().toString();
                String payTo = payTos.getSelectedItem().toString();
                String amountStr = amount.getText().toString();


                String regexStr = "^[0-9]*$";

                if (amountStr == null || "".equals(amountStr) || !amountStr.matches(regexStr)) {
                    showToastMessage("Please enter a valid transfer amount");
                    return;
                }

                if (ApplicationConstants.IS_FUND_TRANSFER && "--Choose--".equals(beneficiaryStr) ) {
                        showToastMessage("Please select a beneficiary");
                          return;
                }
                if (!ApplicationConstants.IS_FUND_TRANSFER &&"--Choose--".equals(payTo)) {
                        showToastMessage("Please select who you want to pay");
                    return;
                }

                makeAPICall(amountStr);

            }
        });
        LinearLayout beneficiarysll = (LinearLayout) findViewById(R.id.beneficiarysll);
        LinearLayout paytoll = (LinearLayout) findViewById(R.id.paytoll);
        TextView fundTransferHeadertv = (TextView) findViewById(R.id.fundTransferHeadertv);

        if (ApplicationConstants.IS_FUND_TRANSFER) {
            fundTransferHeadertv.setText("FUND TRANSFER");
            beneficiarysll.setVisibility(View.VISIBLE);
            paytoll.setVisibility(View.GONE);
        } else {
            fundTransferHeadertv.setText("PAY BILLS");
            beneficiarysll.setVisibility(View.GONE);
            paytoll.setVisibility(View.VISIBLE);
        }
        updateTheme();
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
    }


    private void makeAPICall( String amountStr) {

        amountValue = Integer.parseInt(amountStr);
        JSONObject json = new JSONObject();
        try {

            String userId = MASUser.getCurrentUser().getId();

            if (userId.contains(":")) {
                String[] splitUserName = userId.split(":");
                userId = splitUserName[1];
            }

            json.put("userName", userId);
            json.put("orgName", ApplicationConstants.ORGNAME);
            json.put("txnAmt", amountStr);
            json.put("deviceIDValue", MASRiskAnalysis.getInstance().getDeviceId());


        } catch (JSONException e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
        }

        Base64URL jsonEncoded = Base64URL.encode(json.toString());

        final MASRequest request = new MASRequest.MASRequestBuilder(getRiskAPIUrl()).header("X-CA-RAS-RiskDetails", jsonEncoded.toString()).build();

        MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
            @Override
            public void onSuccess(MASResponse<Object> result) {



                Log.d(TAG, "Success");
                Map headers = result.getHeaders();
                List cookiesList = (List)headers.get("Set-Cookie");
                String cookiesString  = (String) cookiesList.get(0);

                String cookies[] = cookiesString.split(";");
                String deviceId = "";
                for (int i=0; i<cookies.length; i++) {
                    if (cookies[i].startsWith("deviceId=")) {
                        deviceId = cookies[i].replace("deviceId=", "");
                        MASRiskAnalysis.getInstance().setDeviceId(deviceId);
                        break;
                    }
                }

                MASResponseBody<Object> body = result.getBody();
                Object content = body.getContent();
                if (body.getContent().toString().contains("INCREASEAUTH")) {
                //if (true) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (ApplicationConstants.STEP_UP_AUTH_MECHANISM == null || ApplicationConstants.STEP_UP_AUTH_MECHANISM.equals("BIOMETRIC")) {
                                    doBiometricLogin();
                                } else if (ApplicationConstants.STEP_UP_AUTH_MECHANISM.equals("FIDO")) {
                                    doFidoLogin();
                                }  else {
                                    doOTPLogin();
                                }
                                                                }
                        });

                } else {
                    showSuccessMessage();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "failure");

                if (e.getCause() instanceof TargetApiException) {
                    int responseCode = ((TargetApiException) e.getCause()).getResponse().getResponseCode();
                    if (403 == responseCode) {
                        showToastMessage("Transaction Denied");
                        return;
                    }
                }

                    showToastMessage(e.getMessage());

            }
        });
    }

    private void doFidoLogin() {
        try {
            if (!ApplicationConstants.FIDO_LOGIN) {
                showToastMessage("FIDO is disabled. Unable to perform step up authentication. Transaction failed. ");
                return;
            }
            MASFido.loginWithFido(MASUser.getCurrentUser().getId(), new MASCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    stepUpDone = true;
                    showSuccessMessage();
                }

                @Override
                public void onError(Throwable e) {

                    showToastMessage("Biometric Authentication Error");
                    Log.e(TAG, e.getMessage());
                }
            });

        } catch (MASFidoException e) {
            showToastMessage(e.getMessage());
            Log.e(TAG, e.getMessage());
        }
    }


    private void doOTPLogin() {
        final MASRequest request = new MASRequest.MASRequestBuilder(getOTPProtectedUrl()).build();
        MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
            @Override
            public void onSuccess(MASResponse<Object> result) {
                stepUpDone = true;
                showSuccessMessage();
            }

            @Override
            public void onError(Throwable e) {
                showToastMessage("OTP Authentication Error");
                Log.e(TAG, e.getMessage());
            }
        });

    }

    private void doBiometricLogin() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Confirm your credentials",
                "Use a fingerprint or enter the current PIN");
        if (intent != null) {
            startActivityForResult(intent, SESSION_UNLOCK_CODE);
        }

    }


    private URI getRiskAPIUrl() {
        try {
            return new URI("/riskDemo/resource");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getOTPProtectedUrl() {
        try {
            return new URI("/otpProtected");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    void showSuccessMessage () {
        String message;
        final String msg;
        if (ApplicationConstants.IS_FUND_TRANSFER) {
            message = "Fund Transfer Successful";
        } else {
            message = "Bill Payment Successful";
        }

        if (stepUpDone) {
            msg  = "Step Up Auth Successful And " + message;
        } else {
            msg = message;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppUtil.showAlertDialog(context,msg, "Success", false);
            }
        });


        ApplicationConstants.AVAILABLE_BALANCE-=amountValue;

    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SESSION_UNLOCK_CODE ) {
            if (resultCode == RESULT_OK) {
                onAuthenticationSuccess();
            } else if (resultCode == RESULT_CANCELED) {
                onAuthenticationCancelled();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Called when a user Authenticates himself successfully.
     */
    protected void onAuthenticationSuccess() {
        stepUpDone = true;
        showSuccessMessage();
    }


    /**
     * Called when a user cancels the authentication screen.
     */
    protected void onAuthenticationCancelled() {
        showToastMessage("Biometric Authentication Failure");
    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0 && AppThemeConstants.TEXT_COLOR != getResources().getColor(R.color.white)) {
            headerText.setTextColor(AppThemeConstants.TEXT_COLOR);
            paytoText.setTextColor(AppThemeConstants.TEXT_COLOR);
            beneficiaryText.setTextColor(AppThemeConstants.TEXT_COLOR);
            transferText.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
        if(AppThemeConstants.BUTTON_COLOR !=0) {
            GradientDrawable verifyShape = (GradientDrawable)transfer.getBackground();
            verifyShape.setColor(AppThemeConstants.BUTTON_COLOR);
        }

        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0)
            transfer.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
    }
}
