package com.example.ras;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.mas.core.auth.otp.OtpAuthenticationHandler;
import com.ca.mas.core.registration.DeviceRegistrationAwaitingActivationException;
import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASException;
import com.ca.mas.foundation.MASSessionUnlockCallback;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.ui.otp.MASOtpActivity;
import com.example.ras.rs.CustomOtpHandler;
import com.example.ras.rs.StepUpOnLocationRiskActivity;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {



    // UI references.
    private AutoCompleteTextView authIdUsername, authOtpUsernameactv;
    private EditText authIdPwd;
    Activity context;
    TextView errMsg;
    TextView registerButton;
    TextView contact;
    TextView branches;
    TextView atm;
    ProgressDialog dialog;

    ImageView logo;

    LinearLayout unlockSessionLL;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);

        //MAS.start(this, true);
        AppUtil.masStart(this);
        MAS.debug();
        context = this;

        logo = (ImageView) findViewById(R.id.logo);

        unlockSessionLL = (LinearLayout)findViewById(R.id.loginWithFingerPrintll) ;
        unlockSessionLL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockDevice();
            }
        });
        btnLogin = (Button)findViewById(R.id.btnlogin);
        registerButton = (TextView) findViewById(R.id.registertv);
        contact = (TextView) findViewById(R.id.contactUstv);
        branches = (TextView) findViewById(R.id.branchestv);
        atm = (TextView) findViewById(R.id.atmtv);

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SelectRegistrationCredActivity.class);
                startActivity(intent);
            }
        });


        TextView contactUstv = (TextView) findViewById(R.id.contactUstv);
        contactUstv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.CONTACT_US_URL));
                startActivity(browserIntent);
            }
        });

        TextView branchestv = (TextView) findViewById(R.id.branchestv);
        branchestv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.BRANCHES_NEARBY_URL));
                startActivity(browserIntent);
            }
        });

        TextView atmtv = (TextView) findViewById(R.id.atmtv);
        atmtv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.ATM_NEARBY_URL));
                startActivity(browserIntent);
            }
        });

        if (isSessionLocked()) {
            unlockSessionLL.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);

        } else {
            unlockSessionLL.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(true);

        updateTheme();

        super.onCreate(savedInstanceState);

    }






    public void onLogin(View view){
        if(!AppUtil.isNetworkAvailable(context)){
            AppUtil.showAlertDialog(context, "Please check the internet connectivity.", "Network Error", false);
            return;
        }
        doMASLogin();
    }


    public void onFingerPrintCancel(View view){
        if(!AppUtil.isNetworkAvailable(context)){
            AppUtil.showAlertDialog(context, "Please check the internet connectivity.", "Network Error", false);
            return;
        }


        if (MASUser.getCurrentUser() != null) {
            MASUser.getCurrentUser().removeSessionLock(new MASCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    doMASLogin();
                }

                @Override
                public void onError(Throwable e) {
                    AppUtil.showAlertDialog(context, e.getMessage(), "Error", false);
                }
            });
            /*MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    *//*Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);*//*
                    doMASLogin();
                }

                @Override
                public void onError(Throwable e) {


                }
            });*/
        } else {
            doMASLogin();
        }
//
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
//    }


    /**
     * Layout ID of the screen to be shown before the system authentication screen.
     *
     * @return
     */
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.activity_session_unlock_login;
    }




    @Override
    protected void onResume() {
        super.onResume();
        if(ApplicationConstants.FROM_SETTINGS_SCREEN && (MASUser.getCurrentUser() != null && MASUser.getCurrentUser().isAuthenticated())){
            //startHomeActivity(true);

            //Intent intent = new Intent(context, StepUpOnLocationRiskActivity.class);
            ApplicationConstants.FROM_SETTINGS_SCREEN = false;
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            return;
        }
        updateTheme();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private URI getProductListDownloadUri() {
        try {
            return new URI("/protected/resource/products?operation=listProducts");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void doMASLogin() {
        MASUser.login(new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser result) {
                //dialog.dismiss();
                //finish();
                startHomeActivity(false);

            }
            
            @Override
            public void onError(final Throwable e) {
                //dialog.dismiss();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppUtil.showAlertDialog(context,e.getMessage(),"Login Error", false);
                    }
                });


                MAS.cancelAllRequests();

                if (((MASException)e).getRootCause() instanceof DeviceRegistrationAwaitingActivationException) {

                    Intent intent = new Intent(MAS.getContext(), MASOtpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, new CustomOtpHandler((OtpAuthenticationHandler) null));
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Bundle data = new Bundle();
                    data.putString("LOGIN_STATUS", "FAILED");
                }
            }
        });
    }


    private void startHomeActivity(boolean isResumed){
        Intent intent = new Intent(context, StepUpOnLocationRiskActivity.class);

        //Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(!isResumed)
         putDataInIntentBundle(intent, "Login Successful");
        startActivity(intent);
    }

    //Session Unlocking feature
    public final static int SESSION_UNLOCK_CODE = 0x1000;

    private boolean isSessionLocked() {
        try {
            MASUser user = MASUser.getCurrentUser();
            if (user != null) {
                return user.isSessionLocked();
            }
        } catch (Exception e) {

        }
        return false;
    }

    private void unlockDevice() {
        MASUser user = MASUser.getCurrentUser();
        if (user != null) {
            user.unlockSession(getUnlockCallback());
        } else {
            Toast.makeText(LoginActivity.this, "User object is null. Press cancel" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getFingerprintRequestCode()) {
            if (resultCode == RESULT_OK) {
                onAuthenticationSuccess();
            } else if (resultCode == RESULT_CANCELED) {
                onAuthenticationCancelled();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public MASSessionUnlockCallback<Void> getUnlockCallback() {
        return new MASSessionUnlockCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Intent intent = new Intent(context, HomeActivity.class);
                putDataInIntentBundle(intent, "App Unlocked");
                startActivity(intent);

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            @TargetApi(23)
            public void onUserAuthenticationRequired() {
                launchKeyguardIntent();
            }
        };
    }

    private void launchKeyguardIntent() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(getAuthenticationTitle(),
                getAuthenticationDescription());
        if (intent != null) {
            startActivityForResult(intent, getFingerprintRequestCode());
        }
    }

    /**
     * Value for the authentication screen title.
     *
     * @return
     */
    protected String getAuthenticationTitle() {
        return "Confirm your pattern";
    }

    /**
     * Value for the authentication screen description.
     *
     * @return
     */
    protected String getAuthenticationDescription() {
        return "Use a fingerprint or enter the current PIN";
    }

    /**
     * Called when a user successfully authenticates.
     */
    protected void onAuthenticationSuccess() {
        MASUser.getCurrentUser().unlockSession(getUnlockCallback());
    }

    /**
     * Called when a user cancels the authentication screen.
     */
    protected void onAuthenticationCancelled() {
    }

    /**
     * The delay between showing the splash image before the authentication screen appears.
     *
     * @return
     */
    protected int getAuthenticationScreenDelay() {
        return 1000;
    }

    /**
     * The ID for retrieving fingerprint results.
     *
     * @return
     */
    protected int getFingerprintRequestCode() {
        return SESSION_UNLOCK_CODE;
    }

    /**
     * The ID for startActivityForResult() results.
     *
     * @return
     */
    protected int getResultCode() {
        return SESSION_UNLOCK_CODE;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void updateTheme() {
        if(AppThemeConstants.TEXT_COLOR !=0) {
            registerButton.setTextColor(AppThemeConstants.TEXT_COLOR);
            contact.setTextColor(AppThemeConstants.TEXT_COLOR);
            branches.setTextColor(AppThemeConstants.TEXT_COLOR);
            atm.setTextColor(AppThemeConstants.TEXT_COLOR);
        }
        if(AppThemeConstants.BUTTON_COLOR !=0) {
            GradientDrawable bgShape = (GradientDrawable)btnLogin.getBackground();
            bgShape.setColor(AppThemeConstants.BUTTON_COLOR);
        }
        if(AppThemeConstants.BUTTON_TEXT_COLOR !=0)
            btnLogin.setTextColor(AppThemeConstants.BUTTON_TEXT_COLOR);

        if(AppThemeConstants.LOGO_ICON != ""){
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.LOGO_ICON.getBytes(), Base64.DEFAULT);
            logo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }

}
