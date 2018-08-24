package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

import java.net.URI;

public class UserDLVerificationActivity extends BaseActivity {

    EditText usernameet, dlet;
    TextView registrationHeader, userid, driverLicenceNo;
    Button verifyBtn;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_dlverification);
        usernameet = (EditText) findViewById(R.id.usernameet);
        dlet = (EditText) findViewById(R.id.dlet);
        registrationHeader = (TextView) findViewById(R.id.credRegHeadertv);
        userid = (TextView) findViewById(R.id.userid);
        driverLicenceNo = (TextView) findViewById(R.id.driverLicenceNo);
        activity = this;
        verifyBtn = (Button) findViewById(R.id.verifyDlbtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameet.getText().toString();
                String dl = dlet.getText().toString();
                validateDL(username, dl);

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

    void validateDL(final String username, String dl) {

        if ("".equals(username) || "".equals(dl) || null == username || null == dl) {
            showToastMessage("Info in some fields is missing");
            return;
        }

        try {
            MASRequest request = new MASRequest.MASRequestBuilder(new URI(getDLVerificationEndpoint(username, dl)))
                    .setPublic()
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
                @Override
                public void onSuccess(MASResponse<JSONObject> result) {
                    ApplicationConstants.USERNAME = username;
                    Intent intent = new Intent(activity, CredentialProvisioningActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    showToastMessage("User verification failed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
        }

    }

    private String getDLVerificationEndpoint(String username, String dl) {
        return "/bank/verifyUser/" + username + "/driverlicense/" + dl;
    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0 && AppThemeConstants.TEXT_COLOR != getResources().getColor(R.color.white)) {
            registrationHeader.setTextColor(AppThemeConstants.TEXT_COLOR);
            userid.setTextColor(AppThemeConstants.TEXT_COLOR);
            driverLicenceNo.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
        if(AppThemeConstants.BUTTON_COLOR !=0) {
            GradientDrawable verifyShape = (GradientDrawable)verifyBtn.getBackground();
            verifyShape.setColor(AppThemeConstants.BUTTON_COLOR);
        }

        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0)
            verifyBtn.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);
    }
}
