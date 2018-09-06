package com.example.ras;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASAuthorizationRequest;
import com.ca.mas.foundation.MASAuthorizationRequestHandler;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.example.ras.util.AppUtil;
import com.example.ras.util.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public static String TAG = "MAS";
    public ArrayAdapter<String> adapter,  locationadapter;
    EditText strongAuthOrgNameet, strongAuthNamespaceet, strongAuthAuthIDProfileet, strongAuthAuthIDPolicyet, strongAuthAuthIDDefaultAdditionalParamset, customMssoConfiget, customMssoConfigUrlet;
    Activity activity;
    TextView versionName;
    Button updateSettingsBtn;
    SwitchCompat fidoLogin, enableLsd, touchIdswitch, enableFido, deviceRegStepUpswitch;
    Spinner stepupSpinner, locationStepupSpinner;
    Spinner locationSpinner;
    List<String> stepUpAuthMechanismList = new ArrayList<>();
    List<String> locationStepUpAuthMechanismList = new ArrayList<>();
    List<String> locationList = new ArrayList<>();

    static String SETTINGS_UPDATED = "Settings Updated";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if((MASUser.getCurrentUser() != null && MASUser.getCurrentUser().isAuthenticated()))
            ApplicationConstants.FROM_SETTINGS_SCREEN = true;
    }

    private static MASAuthorizationRequestHandler getAuthorizationRequestHandler(Context context) {

        try {
            Class<MASAuthorizationRequestHandler> c = (Class<MASAuthorizationRequestHandler>) Class.forName("com.ca.mas.ui.MASAppAuthAuthorizationRequestHandler");
            Constructor constructor = c.getConstructor(Context.class/*, Intent.class, Intent.class*/);

            return (MASAuthorizationRequestHandler) constructor.newInstance(context
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static Class<Activity> getLoginActivity() {

        try {
            //return (Class<Activity>) Class.forName("com.ca.mas.ui.MASLoginActivity");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        activity = this;
        updateSettingsBtn = (Button) findViewById(R.id.updateSettingsBtn);
        strongAuthOrgNameet = (EditText) findViewById(R.id.strongAuthOrgNameet);
        strongAuthNamespaceet = (EditText) findViewById(R.id.strongAuthNamespaceet);
//        strongAuthAuthIDProfileet = (EditText) findViewById(R.id.strongAuthAuthIDProfileet);
//        strongAuthAuthIDPolicyet = (EditText) findViewById(R.id.strongAuthAuthIDPolicyet);
        strongAuthAuthIDDefaultAdditionalParamset = (EditText) findViewById(R.id.strongAuthAuthIDDefaultAdditionalParamset);
        versionName = (TextView) findViewById(R.id.version_name);

        enableFido = findViewById(R.id.enable_fido_switch);
        SharedPreferences sharedPref = activity.getSharedPreferences(AppUtil.MY_PREFS_NAME, Context.MODE_PRIVATE);

        enableFido.setChecked(sharedPref.getBoolean(AppUtil.FIDO_LOGIN, false));
        if (enableFido.isChecked()) {
            ApplicationConstants.FIDO_LOGIN = true;
            stepUpAuthMechanismList.add("FIDO");
            stepUpAuthMechanismList.add("BIOMETRIC");
            stepUpAuthMechanismList.add("OTP");
            locationStepUpAuthMechanismList.add("FIDO");
            locationStepUpAuthMechanismList.add("BIOMETRIC");
            locationStepUpAuthMechanismList.add("OTP");
        } else {
            ApplicationConstants.FIDO_LOGIN = false;
            stepUpAuthMechanismList.add("BIOMETRIC");
            stepUpAuthMechanismList.add("OTP");
            locationStepUpAuthMechanismList.add("BIOMETRIC");
            locationStepUpAuthMechanismList.add("OTP");
        }


        AppUtil.updateAppConstants(activity);


        stepupSpinner = findViewById(R.id.stepupSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stepUpAuthMechanismList);
        stepupSpinner.setAdapter(adapter);
        if (ApplicationConstants.STEP_UP_AUTH_MECHANISM != null
                ) {
            ArrayAdapter myAdap = (ArrayAdapter) stepupSpinner.getAdapter();
            int spinnerPosition = myAdap.getPosition(ApplicationConstants.STEP_UP_AUTH_MECHANISM);
            if (spinnerPosition>0)
                stepupSpinner.setSelection(spinnerPosition);
        }

        locationStepupSpinner = findViewById(R.id.locationStepupSpinner);
        locationadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationStepUpAuthMechanismList);
        locationStepupSpinner.setAdapter(locationadapter);
        if (ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM != null
                ) {
            ArrayAdapter myAdap = (ArrayAdapter) locationStepupSpinner.getAdapter();
            int spinnerPosition = myAdap.getPosition(ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM);
            if (spinnerPosition>0)
                locationStepupSpinner.setSelection(spinnerPosition);
        }



        locationList.add("United States (ALLOW)");
        locationList.add("Canada (ALLOW)");
        locationList.add("North Korea (DENY)");
        locationList.add("Nigeria (INCREASE AUTH)");
        locationList.add("DISABLED");



        locationSpinner = findViewById(R.id.locationSpinner);
        locationadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationList);
        locationSpinner.setAdapter(locationadapter);
        if (ApplicationConstants.LOCATION != null
                ) {
            ArrayAdapter myAdap = (ArrayAdapter) locationSpinner.getAdapter();
            int spinnerPosition = myAdap.getPosition(ApplicationConstants.LOCATION);
            if (spinnerPosition>0)
                locationSpinner.setSelection(spinnerPosition);
        }


        strongAuthOrgNameet.setText(ApplicationConstants.ORGNAME);
        strongAuthNamespaceet.setText(ApplicationConstants.NAMESPACE);
        // strongAuthAuthIDProfileet.setText(ApplicationConstants.AUTHID_PROFILE);
        // strongAuthAuthIDPolicyet.setText(ApplicationConstants.AUTHID_POLICY);
        strongAuthAuthIDDefaultAdditionalParamset.setText(AppUtil.convertAdditionalParamsToString(ApplicationConstants.AUTHID_DEFAULT_ADDITIONAL_PARAMS));
        versionName.setText("Ver:" + BuildConfig.VERSION_NAME);


        enableLsd = (SwitchCompat) findViewById(R.id.lsdLoginswitch);
        enableLsd.setChecked(ApplicationConstants.LSD_LOGIN);
        touchIdswitch = (SwitchCompat) findViewById(R.id.touchIdswitch);
        touchIdswitch.setChecked(ApplicationConstants.ENABLE_BIOMETRIC_LOGIN);
        deviceRegStepUpswitch = (SwitchCompat) findViewById(R.id.deviceRegStepUpswitch);
        deviceRegStepUpswitch.setChecked(ApplicationConstants.DEVICE_REG_STEP_UP_ENABLED);


        enableFido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ApplicationConstants.FIDO_LOGIN = true;
                    if (!stepUpAuthMechanismList.contains("FIDO")) {
                        stepUpAuthMechanismList.add("FIDO");
                    }
                    if (!locationStepUpAuthMechanismList.contains("FIDO")) {
                        locationStepUpAuthMechanismList.add("FIDO");

                    }
                    //make the shared preference variable to true.
                } else {
                    //make the shared preference variable to false.
                    ApplicationConstants.FIDO_LOGIN = false;
                    if (stepUpAuthMechanismList.contains("FIDO")) {
                        stepUpAuthMechanismList.remove("FIDO");
                    }
                    if (locationStepUpAuthMechanismList.contains("FIDO")) {
                        locationStepUpAuthMechanismList.remove("FIDO");
                    }
                }
                AppUtil.updateAppConstants(activity);
                adapter.notifyDataSetChanged();
            }

        });


        //TODO delete fido login feature
        LinearLayout fidoLoginll = (LinearLayout) findViewById(R.id.fidologinll);
        fidoLoginll.setVisibility(View.GONE);


        if (ApplicationConstants.HIDE_FIDO_FEATURE) {


        } else {
            fidoLogin = (SwitchCompat) findViewById(R.id.fidologin);
            fidoLogin.setChecked(ApplicationConstants.FIDO_LOGIN);
        }
        updateSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.ORGNAME = strongAuthOrgNameet.getText().toString();
                ApplicationConstants.NAMESPACE = strongAuthNamespaceet.getText().toString();
               // ApplicationConstants.AUTHID_PROFILE = strongAuthAuthIDProfileet.getText().toString();
               // ApplicationConstants.AUTHID_POLICY = strongAuthAuthIDPolicyet.getText().toString();
                String additionalParams = strongAuthAuthIDDefaultAdditionalParamset.getText().toString();
                ApplicationConstants.AUTHID_DEFAULT_ADDITIONAL_PARAMS = AppUtil.parseAdditionalParams(additionalParams);

                if (fidoLogin != null)
                    ApplicationConstants.FIDO_LOGIN = fidoLogin.isChecked();

                ApplicationConstants.ENABLE_BIOMETRIC_LOGIN = touchIdswitch.isChecked();

                ApplicationConstants.STEP_UP_AUTH_MECHANISM = stepupSpinner.getSelectedItem().toString();
                ApplicationConstants.LOCATION_STEP_UP_AUTH_MECHANISM = locationStepupSpinner.getSelectedItem().toString();
                ApplicationConstants.DEVICE_REG_STEP_UP_ENABLED = deviceRegStepUpswitch.isChecked();

                ApplicationConstants.LOCATION = locationSpinner.getSelectedItem().toString();

                ApplicationConstants.LSD_LOGIN = enableLsd.isChecked();
                if (ApplicationConstants.LSD_LOGIN) {
                    MAS.enableBrowserBasedAuthentication();
                    //MAS.start(activity);
                    AppUtil.masStart(activity);
                    MAS.setAuthenticationListener(new MASAuthenticationListener() {
                        @Override
                        public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                            MASAuthorizationRequest authReq = new MASAuthorizationRequest.MASAuthorizationRequestBuilder().buildDefault();
                            MASAuthorizationRequestHandler handler = getAuthorizationRequestHandler(activity);
                            if (handler != null) {
                                MASUser.login(authReq, handler);
                                return;
                            }
                        }

                        @Override
                        public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
                            Class<Activity> otpActivity = getOtpActivity();
                            if (otpActivity != null) {
                                if (activity != null) {
                                    Intent intent = new Intent(activity, otpActivity);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, handler);
                                    activity.startActivity(intent);
                                }
                            } else {
                                Log.w(TAG, MASAuthenticationListener.class.getSimpleName() + " is required for otp authentication.");
                            }
                        }
                    });

                    ApplicationConstants.WAS_BBA_ENABLED_PREVIOUSLY = true;
                } else {

                    if (ApplicationConstants.WAS_BBA_ENABLED_PREVIOUSLY) {
                        SETTINGS_UPDATED += "\nRestart app to disable Browser Based Login";

                    } else {
                        AppUtil.masStart(activity);

                        MAS.setAuthenticationListener(new MASAuthenticationListener() {
                            @Override
                            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                                Class<Activity> loginActivity = getLoginActivity();
                                if (loginActivity != null) {
                                    if (activity != null) {
                                        Intent intent = new Intent(activity, loginActivity);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(MssoIntents.EXTRA_REQUEST_ID, requestId);
                                        intent.putExtra(MssoIntents.EXTRA_AUTH_PROVIDERS, providers);
                                        activity.startActivity(intent);
                                    }
                                } else {

                                    Log.w(TAG, MASAuthenticationListener.class.getSimpleName() + " is required for user authentication.");
                                }
                            }

                            @Override
                            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {


                                Class<Activity> otpActivity = getOtpActivity();
                                if (otpActivity != null) {
                                    if (activity != null) {
                                        Intent intent = new Intent(activity, otpActivity);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, handler);
                                        activity.startActivity(intent);
                                    }
                                } else {

                                    Log.w(TAG, MASAuthenticationListener.class.getSimpleName() + " is required for otp authentication.");
                                }
                            }
                        });
                    }
                }

                AppUtil.updateAppConstants(activity);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, SETTINGS_UPDATED, Toast.LENGTH_LONG).show();
                        SETTINGS_UPDATED = "Settings Updated";
                    }
                });
            }
        });

        customMssoConfiget = (EditText) findViewById(R.id.customMssoConfiget);
        customMssoConfigUrlet = (EditText) findViewById(R.id.customMssoConfigUrlet);

        Button customMssoConfigbtn = (Button) findViewById(R.id.customMssoConfigbtn);
        customMssoConfigbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (customMssoConfiget.getText() != null && customMssoConfiget.getText().toString().toString() != null && !"".equals(customMssoConfiget.getText().toString())) {

                        JSONObject customMsso = new JSONObject(customMssoConfiget.getText().toString());
                        MAS.start(activity, customMsso);
                        ApplicationConstants.CUSTOM_MSSO_CONFIG = customMsso;
                        Toast.makeText(activity, "Configurations updated with msso text", Toast.LENGTH_LONG).show();
                    } else if (customMssoConfigUrlet.getText() != null && customMssoConfigUrlet.getText().toString().toString() != null && !"".equals(customMssoConfigUrlet.getText().toString())) {
                        getMssoFromUrl(activity, customMssoConfigUrlet.getText().toString().toString());

                    } else {
                        Toast.makeText(activity, "You must fill atleast one of the fields", Toast.LENGTH_LONG).show();
                    }

                    AppUtil.updateAppConstants(activity);
                } catch (Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        super.onCreate(savedInstanceState);

    }

    private void getMssoFromUrl(final Activity activity, String url) {
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject customMsso = new JSONObject(response);
                            MAS.start(activity, customMsso);
                            ApplicationConstants.CUSTOM_MSSO_CONFIG = customMsso;

                            Toast.makeText(activity, "Configurations updated with URL", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {

                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        queue.add(stringRequest);
    }


}
