package com.example.ras;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASSessionUnlockCallback;
import com.ca.mas.foundation.MASUser;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class TouchIDActivity extends BaseActivity{

    /**
     * Called when the activity is first created.
     */

    TextView msgTv = null;
    Button lockBtn, unlockBtn, isLockedBtn, accessProtectedAPIBtn;
    boolean LOCK_ON_PAUSE = false;

static String NO_LOGGED_IN_USER = "There is no user currently logged in";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.touchid_activity);

        lockBtn = (Button)findViewById(R.id.lockSessionBtn);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MASUser currentUser = MASUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.lockSession(null);
                    setMessage("Session is locked");
                } else {
                    setMessage(NO_LOGGED_IN_USER);
                }
            }
        });


        unlockBtn = (Button)findViewById(R.id.unlockSessionBtn);
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MASUser currentUser = MASUser.getCurrentUser();
                if (currentUser != null) {
                    currentUser.unlockSession(getUnlockCallback());
                } else {
                    setMessage(NO_LOGGED_IN_USER);
                }

            }
        });


        isLockedBtn = (Button)findViewById(R.id.islockedBtn);
        isLockedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MASUser currentUser = MASUser.getCurrentUser();
                if (currentUser != null) {
                    if (currentUser.isSessionLocked()) {
                        setMessage("Session is locked");
                    } else {
                        setMessage("Session is not locked");
                    }
                } else {
                    setMessage(NO_LOGGED_IN_USER);
                }
            }
        });

        accessProtectedAPIBtn = (Button)findViewById(R.id.accessProtectedAPIBtn);
        accessProtectedAPIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeGetProducts();
            }
        });


        msgTv = (TextView) findViewById(R.id.msgTv);

        SwitchCompat lockOnPauseSwitch = (SwitchCompat)findViewById(R.id.lockOnPauseSwitch);
        lockOnPauseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                         @Override
                                                         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                             LOCK_ON_PAUSE = isChecked;
                                                         }
                                                     }
        );

        super.onCreate(savedInstanceState);

    }


    void setMessage (String msg) {
        msgTv.setText(msg);
    }


    public MASSessionUnlockCallback<Void> getUnlockCallback() {
        return new MASSessionUnlockCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // The session was successfully unlocked
                setMessage("The session was successfully unlocked");
            }

            @Override
            public void onError(Throwable e) {
                // Handle errors
                setMessage(e.getMessage());
            }

            @Override
            @TargetApi(23)
            public void onUserAuthenticationRequired() {
                // Handle user authentication
                launchKeyguardIntent();
            }
        };
    }

    protected int REQUEST_CODE = 0x1000;

    // Launch the default Android lockscreen to authenticate the user
    @TargetApi(23)
    private void launchKeyguardIntent() {


        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Confirm your pattern",
                "Please provide your credentials.");
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE);
        }

        /*Intent i = new Intent("MASUI.intent.action.SessionUnlock");
        startActivityForResult(i, REQUEST_CODE);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setMessage("The session was successfully unlocked");
                // The session was successfully unlocked
                MASUser.getCurrentUser().unlockSession(new MASSessionUnlockCallback<Void>() {
                    @Override
                    public void onUserAuthenticationRequired() {
                        //TODO : Add the code to handle this scenario
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        String result = "Session unlock complete";
                        setMessage("The session was successfully unlocked");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        setMessage(throwable.getMessage());
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                // The Android lockscreen Activity was cancelled
                setMessage("The Android lockscreen Activity was cancelled");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void invokeGetProducts() {
        final MASRequest request = new MASRequest.MASRequestBuilder(getProductListDownloadUri()).build();
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {

            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {
                setMessage(result.getBody().getContent().toString());
            }

            @Override
            public void onError(Throwable e) {
                if (e.getCause() instanceof TargetApiException) {
                    setMessage(new String(((TargetApiException) e.getCause()).getResponse()
                            .getBody().getRawContent()));
                } else {
                    setMessage(getResources().getString(R.string.failed));
                }
            }
        });
    }

    private URI getProductListDownloadUri() {
        try {
            return new URI("/protected/resource/products?operation=listProducts");
        } catch (URISyntaxException e) {
            setMessage(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOCK_ON_PAUSE) {
            MASUser currentUser = MASUser.getCurrentUser();
            if (currentUser != null) {
                currentUser.lockSession(null);
                setMessage("Session is locked");
            } else {
                setMessage(NO_LOGGED_IN_USER);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MASUser currentUser = MASUser.getCurrentUser();
        if (currentUser != null) {
            currentUser.unlockSession(getUnlockCallback());
        } else {
            setMessage(NO_LOGGED_IN_USER);
        }
    }


}