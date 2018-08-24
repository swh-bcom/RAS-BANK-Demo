package com.example.com.mas_aa_sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.example.ras.Constants;
import com.example.ras.LoginActivity;
import com.example.ras.NotificationDialog;
import com.example.ras.R;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppThemeUtil;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private static final String TAG = FullscreenActivity.class.getSimpleName();
    Activity context;

    ImageView logo;

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_fullscreen);

        logo = (ImageView) findViewById(R.id.fullscreen_content);

        root = (RelativeLayout) findViewById(R.id.rootActivity);
        //MAS.enableBrowserBasedAuthentication();
        //MAS.start(this, true);
        AppUtil.initAppConstants(this);
        AppUtil.masStart(this);
        MAS.debug();
        MAS.setAuthenticationListener(new MASAuthenticationListener() {
            @Override
            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                Class<Activity> loginActivity = getLoginActivity();
                if (loginActivity != null) {
                    if (context != null) {
                        Intent intent = new Intent(context, loginActivity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(MssoIntents.EXTRA_REQUEST_ID, requestId);
                        intent.putExtra(MssoIntents.EXTRA_AUTH_PROVIDERS, providers);
                        context.startActivity(intent);
                    }
                } else {
                    Log.w(TAG, MASAuthenticationListener.class.getSimpleName() + " is required for user authentication.");
                }
            }

            @Override
            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
                Class<Activity> otpActivity = getOtpActivity();
                if (otpActivity != null) {
                    if (context != null) {
                        Intent intent = new Intent(context, otpActivity);
                        intent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, handler);
                        context.startActivity(intent);
                    }
                } else {
                    Log.w(TAG, MASAuthenticationListener.class.getSimpleName() + " is required for otp authentication.");
                }
            }
        });

        AppThemeUtil.initAppConstants(this);

        Log.d(TAG, "Checking intent for extras");
        if(getIntent().getExtras() != null) {


            Log.d(TAG, "Extras not empty");
            Bundle bundle = getIntent().getExtras();
            Log.d(TAG, bundle.toString());

            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    if ("data".equals(key)){
                        String messageBody = value.toString();
                        Log.d(TAG, "Got Data" + messageBody);
                        Intent intent = new Intent(this, NotificationDialog.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.NOTIFICATION_MESSAGE_BODY, messageBody);
                        context.startActivity(intent);
                        context.finish();
                        return;
                    }
                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }
            if (null != getIntent().getExtras().getString(Constants.NOTIFICATION_MESSAGE_BODY) ) {
                String messageBody = getIntent().getExtras().getString(Constants.NOTIFICATION_MESSAGE_BODY);
                Log.d(TAG, "Notification Body present" + messageBody);

                Log.d("Testing", "Send Push");
                Intent intent = new Intent(this, NotificationDialog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.NOTIFICATION_MESSAGE_BODY, messageBody);
                context.startActivity(intent);
                context.finish();
                return;

            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(context, LoginActivity.class);
                context.startActivity(mainIntent);
                context.finish();
            }
        }, 3000);


   /*     final MASRequest request = new MASRequest.MASRequestBuilder(getReturnCookieEndpointURL())
                .setPublic()
                .build();*/

        /*Button withCookie = (Button) findViewById(R.id.withCookieBtn);
        withCookie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                        @Override
                        public void onSuccess(MASResponse<Object> result) {

                            java.net.CookieManager cookieManager = new java.net.CookieManager();
                            CookieHandler.setDefault(cookieManager);
                            String cookieString = result.getHeaders().get("Set-Cookie").toString();
                            cookieString = cookieString.replace("[","");
                            cookieString = cookieString.replace("]","");


                            List<HttpCookie> cookieList =  HttpCookie.parse(cookieString);

                            HttpCookie cookie = cookieList.get(0);//new HttpCookie("mohammed", "mujeeb");
                            *//*cookie.setDomain("ras.ca.com");
                            cookie.setPath("/");
                            cookie.setMaxAge(999999999);
                            cookie.setVersion(0);
                            new URI("http://ras.ca.com/")*//*


                            try {
                                URI uri = new URI("https://ras.ca.com/");
                                cookie.setMaxAge(9999);
                                cookie.setPath("/");
                                cookieManager.getCookieStore().add(uri, cookie);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }


                            MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                                @Override
                                public void onSuccess(MASResponse<Object> result) {
                                    Log.i("TAG", "something");
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
                } catch (Exception e) {

                }

            }
        });

        Button withoutCookie = (Button) findViewById(R.id.withoutCookiebtn);
        withoutCookie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                    @Override
                    public void onSuccess(MASResponse<Object> result) {
                        Log.i("TAG", "something");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
            }
        });*/

        getOrgStaticUrl();

        updateTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
    }

    private void getOrgStaticUrl() {
        try {
            MASRequest request = new MASRequest.MASRequestBuilder(new URI(getContactUsEndpoint()))
                    .setPublic()
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                @Override
                public void onSuccess(MASResponse<Object> result) {
                    ApplicationConstants.CONTACT_US_URL = convertByteArrayToString((byte[]) result.getBody().getContent());
                    Log.i("SplashScreen", "CONTACT_US_URL :" + ApplicationConstants.CONTACT_US_URL);
                }

                @Override
                public void onError(Throwable e) {

                }
            });

            request = new MASRequest.MASRequestBuilder(new URI(getListATMEndpoint()))
                    .setPublic()
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                @Override
                public void onSuccess(MASResponse<Object> result) {
                    ApplicationConstants.ATM_NEARBY_URL = convertByteArrayToString((byte[]) result.getBody().getContent());
                    Log.i("SplashScreen", "ATM_NEARBY_URL :" + ApplicationConstants.ATM_NEARBY_URL);
                    int a = 1;
                }

                @Override
                public void onError(Throwable e) {

                }
            });

            request = new MASRequest.MASRequestBuilder(new URI(getListBranchesEndpoint()))
                    .setPublic()
                    .build();
            MAS.invoke(request, new MASCallback<MASResponse<Object>>() {
                @Override
                public void onSuccess(MASResponse<Object> result) {
                    ApplicationConstants.BRANCHES_NEARBY_URL = convertByteArrayToString((byte[]) result.getBody().getContent());
                    Log.i("SplashScreen", "BRANCHES_NEARBY_URL :" + ApplicationConstants.BRANCHES_NEARBY_URL);
                    int a = 1;
                }

                @Override
                public void onError(Throwable e) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getContactUsEndpoint() {
        return "/bank/contactUs";
    }

    String getListBranchesEndpoint() {
        return "/bank/listBranches";
    }

    String getListATMEndpoint() {
        return "/bank/listAtms";
    }

    URI getReturnCookieEndpointURL() {
        try {
            return new URI("/returnCookie2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertByteArrayToString(byte[] byteArray) {
        String s = null;
        if (byteArray != null) {
            if (byteArray.length > 0) {
                try {
                    s = new String(byteArray, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return s;
    }

    private Class<Activity> getLoginActivity() {

        try {
            return (Class<Activity>) Class.forName("com.example.ras.RASLoginActivity");
        } catch (Exception e) {
            return null;
        }
    }
    private static Class<Activity> getOtpActivity() {
        try {
            return (Class<Activity>) Class.forName("com.ca.mas.ui.otp.MASOtpActivity");
        } catch (Exception e) {
            return null;
        }
    }

    protected void updateTheme() {
        if(AppThemeConstants.LOGO_ICON != ""){
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.LOGO_ICON.getBytes(), Base64.DEFAULT);
            logo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        if(AppThemeConstants.BACKGROUND_IMAGE != "") {
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.BACKGROUND_IMAGE.getBytes(), Base64.DEFAULT);
            BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            root.setBackground(d);
        }
    }

}
