package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASResponseBody;
import com.ca.mas.foundation.MASUser;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    Activity context;
    Button fundTransferBtn, billPaymentBtn, cardsBtn, transHistoryBtn;
    TextView acc_no_value_tv, acc_name_value_tv, acc_branch_value_tv, acc_balance_tv;

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);

        context = this;
        setTitle("Account Summary");
        fundTransferBtn = (Button) findViewById(R.id.fundTransferBtn);
        fundTransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.IS_FUND_TRANSFER = true;
                Intent intent = new Intent(context, FundTransferActivity.class);
                startActivity(intent);
            }
        });

        billPaymentBtn = (Button) findViewById(R.id.billPaymentBtn);
        billPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ApplicationConstants.IS_FUND_TRANSFER = false;
                Intent intent = new Intent(context, FundTransferActivity.class);
                startActivity(intent);*/
                mortgageFlow();
            }
        });

       /* cardsBtn = (Button) findViewById(R.id.cardsBtn);
        cardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CardsActivity.class);
                startActivity(intent);
            }
        });

        transHistoryBtn = (Button) findViewById(R.id.transHistoryBtn);
        transHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
*/

        final MASRequest request = new MASRequest.MASRequestBuilder(getProductListDownloadUri()).build();

        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {

                acc_no_value_tv = (TextView) findViewById(R.id.acc_no_value_tv);
                acc_no_value_tv.setText("xxxx xxxx xxxx 7890");
                acc_no_value_tv.setTextSize(14);

                acc_name_value_tv = (TextView) findViewById(R.id.acc_name_value_tv);

                try {
                    if (MASUser.getCurrentUser() != null && MASUser.getCurrentUser().getId() != null) {
//                        String username = MASUser.getCurrentUser().getId();
                        String username = MASUser.getCurrentUser().getName().getGivenName() + " " + MASUser.getCurrentUser().getName().getFamilyName();
                        username = username.substring(username.indexOf(':') + 1);
                        acc_name_value_tv.setText(username);
                    }
                } catch (Exception e) {

                }
                acc_name_value_tv.setTextSize(14);

                acc_branch_value_tv = (TextView) findViewById(R.id.acc_branch_name_tv);
                acc_branch_value_tv.setText("Islandia");
                acc_branch_value_tv.setTextSize(14);

                acc_balance_tv = (TextView) findViewById(R.id.acc_balance_tv);
                acc_balance_tv.setText("$"+ApplicationConstants.AVAILABLE_BALANCE);
                acc_balance_tv.setTextSize(20);

            }

            @Override
            public void onError(Throwable e) {
                if (e.getCause() instanceof TargetApiException) {
                    showMessage(new String(((TargetApiException) e.getCause()).getResponse()
                            .getBody().getRawContent()), Toast.LENGTH_SHORT);
                } else {
                    showMessage("Error: " + e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });

        super.onCreate(savedInstanceState);

    }

    private void mortgageFlow() {


        final MASRequest request = new MASRequest.MASRequestBuilder(getMortgageUri()).build();

        MAS.invoke(request, new MASCallback<MASResponse<String>>() {
            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<String> result) {
                MASResponseBody<String> body = result.getBody();
                String content = body.getContent();
                String url = content;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getCause() instanceof TargetApiException) {
                    showMessage(new String(((TargetApiException) e.getCause()).getResponse()
                            .getBody().getRawContent()), Toast.LENGTH_SHORT);
                } else {
                    showMessage("Error: " + e.getMessage(), Toast.LENGTH_LONG);
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

    private URI getMortgageUri() {
        try {
            return new URI("/cabank/mortgage");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public void showMessage(final String message, final int toastLength) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HomeActivity.this, message, toastLength).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acc_balance_tv != null)
            acc_balance_tv.setText("$"+ApplicationConstants.AVAILABLE_BALANCE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(MASUser.getCurrentUser()!=null && MASUser.getCurrentUser().getId()!=null){
            return;
        }
        super.onBackPressed();
    }
}
