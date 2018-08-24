package com.example.ras;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.ca.mas.foundation.MAS;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppUtil;

public class DashboardActivity extends BaseActivity {

    private static final String TAG = "DashboardActivity";

    public enum next_class {
        authidPKI, ddna, mobileOTP, touchID, authenticator
    }

    TextView headingTv,magCredentials;


    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dashboard);

        //MAS.start(this, true);
        AppUtil.masStart(this);
        MAS.debug();

        findViewById(R.id.authid_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_class.authidPKI);
            }
        });

        findViewById(R.id.ddna_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_class.ddna);
            }
        });

        findViewById(R.id.mobileOTP_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_class.mobileOTP);
            }
        });

        findViewById(R.id.touchId_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_class.touchID);
            }
        });

       /* findViewById(R.id.authenticator_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_class.authenticator);
            }
        });*/

        headingTv = (TextView)findViewById(R.id.headingTV);
        magCredentials = (TextView)findViewById(R.id.magCredentials);
        headingTv.setTypeface(null, Typeface.BOLD);
        headingTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        updateTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
    }

    private void setStatusText(String s) {
        showToastMessage(s);
        //Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void startActivity(Enum<next_class> next_classEnum) {
        Intent intent = null;

        if (next_classEnum == next_class.authidPKI) {
            intent = new Intent(this, AIDActivity.class);
        } else if (next_classEnum == next_class.ddna) {
            intent = new Intent(this, DDNAActivity.class);
        } else if (next_classEnum == next_class.mobileOTP) {
            intent = new Intent(this, AOTPActivity.class);
        }
        /*else if (next_classEnum == next_class.authenticator) {
            intent = new Intent(this, AuthenticatorActivity.class);
        }*/ else if (next_classEnum == next_class.touchID) {
            intent = new Intent(this, TouchIDActivity.class);
        }
        startActivity(intent);

    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0 && AppThemeConstants.TEXT_COLOR != getResources().getColor(R.color.white)) {
            headingTv.setTextColor(AppThemeConstants.TEXT_COLOR);
            magCredentials.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
    }
}