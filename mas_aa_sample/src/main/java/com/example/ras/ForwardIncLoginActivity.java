package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ca.apim.mas.authotp.MASAuthOTP;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASUser;

/**
 * A login screen that offers login via email/password.
 */
@Deprecated
public class ForwardIncLoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    Activity context;
    // UI references.
    private EditText userIdet;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_inc_login);
        context = this;
        // Set up the login form.
        userIdet = (EditText) findViewById(R.id.userIdet);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        Button goto_main_screen_button = (Button) findViewById(R.id.goto_main_screen_button);
        goto_main_screen_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(context, LoginActivity.class);
                context.startActivity(mainIntent);
                context.finish();
            }
        });


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userIdet.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userId = userIdet.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuthTask = new UserLoginTask(userId, password);
        mAuthTask.execute((Void) null);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mPassword;

        UserLoginTask(String userId, String password) {
            mUserId = userId;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Login with Auth ID
            /*MASAdvanceAuth.getInstance().loginWithAID( mUserId, mPassword, new MASCallback<MASUser>() {
                @Override
                public void onSuccess(MASUser result) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                }
            });*/


            //Login with Auth OTP

            MASAuthOTP.getInstance().loginWithAOTP(mUserId, mPassword, new MASCallback<MASUser>() {
                @Override
                public void onSuccess(MASUser result) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }


    }
}

