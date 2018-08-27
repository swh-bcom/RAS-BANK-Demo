package com.example.ras.rs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.widget.Toast;

import com.ca.mas.core.auth.otp.OtpAuthenticationHandler;
import com.ca.mas.core.store.StorageProvider;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.example.ras.HomeActivity;
import com.example.ras.LoginActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomOtpHandler extends MASOtpAuthenticationHandler {

    public CustomOtpHandler(OtpAuthenticationHandler handler) {
        super(handler);
    }

    @Override
    public void proceed(final Context context, String otp) {

        MASRequest request = new MASRequest.MASRequestBuilder(
                new Uri.Builder().encodedAuthority("mag.rasdemo.apim.ca.com:8443")
                        .scheme("https")
                        //.path("register/OTP")
                        .path("device/registration/otphandler")
                        .build())

                .setPublic()
                .header("mag-identifier", StorageProvider.getInstance().getTokenManager().getMagIdentifier())
                .header("X-OTP", otp)
                .build();
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {
            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {
                if (MASUser.getCurrentUser() == null ) {

                    MASUser.login(new MASCallback<MASUser>() {
                        @Override
                        public void onSuccess(MASUser result) {
                            gotoHomeActivity();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("", e.getMessage());
                        }
                    });
                } else {
                    gotoHomeActivity();
                }


            }

            private void gotoHomeActivity() {
                //Toast.makeText(context, "Register Success!", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(context, StepUpOnLocationRiskActivity.class);

                Intent intent = new Intent(context, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(intent);

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, "Register Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void deliver(String channel, final MASCallback<Void> callback) {
        MASRequest request = new MASRequest.MASRequestBuilder(
                new Uri.Builder().encodedAuthority("mag.rasdemo.apim.ca.com:8443")
                        .scheme("https")
                        //.path("register/OTP")
                        .path("device/registration/otphandler")
                        .build())
                .setPublic()
                .header("mag-identifier", StorageProvider.getInstance().getTokenManager().getMagIdentifier())
                //.header("X-OTP", "")
                .build();
        MAS.invoke(request, new MASCallback<MASResponse<JSONObject>>() {

            @Override
            public Handler getHandler() {
                return new Handler(Looper.getMainLooper());
            }

            @Override
            public void onSuccess(MASResponse<JSONObject> result) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        });

    }

    @Override
    public List<String> getChannels() {
        List<String> channels = new ArrayList<>();
//        channels.add("Email");
        channels.add("Phone");
        return channels;
    }

    @Override
    public boolean isInvalidOtp() {
        return false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected CustomOtpHandler(Parcel in) {
        super((OtpAuthenticationHandler) null);
    }

    public static final Creator<CustomOtpHandler> CREATOR = new Creator<CustomOtpHandler>() {
        @Override
        public CustomOtpHandler createFromParcel(Parcel source) {
            return new CustomOtpHandler(source);
        }

        @Override
        public CustomOtpHandler[] newArray(int size) {
            return new CustomOtpHandler[size];
        }
    };
}