package com.example.ras;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASDevice;
import com.ca.mas.foundation.MASUser;
import com.example.ras.util.AppThemeConstants;
import com.example.ras.util.AppThemeUtil;
import com.example.ras.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private String[] mScreens;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static final String TAG = "BaseActivity";
    public static final String ACTIVITYMESSAGE = "ACTIVITYMESSAGE";
    MenuItem logouti, lockApp, fundTransferi, billPaymenti, cardsi, logini, homeScreeni;
    TextView errorMessageTextView;
    Context context;


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (ApplicationConstants.GOTO_DEVICE_HOME_SCREEN) {
            ApplicationConstants.GOTO_DEVICE_HOME_SCREEN = false;
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        Bundle extras = getIntent().getExtras();
        if(extras !=null && extras.getString(ACTIVITYMESSAGE) != null) {
            showSuccessMessages(extras.getString(ACTIVITYMESSAGE));
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundResource(R.mipmap.background);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        try {
            Menu menu = navigationView.getMenu();
            logouti = menu.findItem(R.id.logouti);
            lockApp = menu.findItem(R.id.lockAppi);
            fundTransferi = menu.findItem(R.id.fundTransferi);
            billPaymenti = menu.findItem(R.id.billPaymenti);
            cardsi = menu.findItem(R.id.cardsi);
            logini = menu.findItem(R.id.loginScreeni);
            homeScreeni = menu.findItem(R.id.home_screen1);

            logini.setEnabled(true);
            lockApp.setEnabled(false);
            logouti.setEnabled(false);
            fundTransferi.setEnabled(false);
            billPaymenti.setEnabled(false);
            cardsi.setEnabled(false);
            homeScreeni.setEnabled(false);


            MASUser user = MASUser.getCurrentUser();
            if (user != null && user.isAuthenticated()) {
                logouti.setEnabled(true);
                logouti.setEnabled(true);
                fundTransferi.setEnabled(true);
                billPaymenti.setEnabled(true);
                cardsi.setEnabled(true);
                logini.setEnabled(false);
                homeScreeni.setEnabled(true);
            }
            if (user != null && !user.isSessionLocked()) {
                lockApp.setEnabled(true);
            }


        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        lockApp.setVisible(false);

        setTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTheme();
    }

    public void setTheme() {

        //toolbar.setBackgroundColor(Color.parseColor("#ffffff"));//AppThemeConstants.ACTION_BAR_COLOR);
        if(AppThemeConstants.ACTION_BAR_COLOR != 0)
            toolbar.setBackgroundColor(AppThemeConstants.ACTION_BAR_COLOR);
        if (AppThemeConstants.ACTION_BAR_TITLE_COLOR != 0)
            toolbar.setTitleTextColor(AppThemeConstants.ACTION_BAR_TITLE_COLOR);
        if (AppThemeConstants.APP_NAME != "")
            toolbar.setTitle(AppThemeConstants.APP_NAME);
        if (AppThemeConstants.BACKGROUND_IMAGE != "") {
            View root = (View) findViewById(R.id.rootActivity);
            byte[] imageAsBytes = Base64.decode(AppThemeConstants.BACKGROUND_IMAGE.getBytes(), Base64.DEFAULT);
            BitmapDrawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            if (root != null)
                root.setBackground(d);


        }
    }

    public void resetTheme() {
        AppThemeConstants.ACTION_BAR_COLOR = getResources().getColor(R.color.colorPrimary);
        AppThemeConstants.ACTION_BAR_TITLE_COLOR = getResources().getColor(R.color.white);
        AppThemeConstants.ACTION_BAR_COLOR_PICKER = getResources().getColor(R.color.colorPrimary);
        AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER = getResources().getColor(R.color.white);
        AppThemeConstants.TEXT_COLOR = getResources().getColor(R.color.white);
        AppThemeConstants.BUTTON_COLOR = getResources().getColor(R.color.sr_button);
        AppThemeConstants.BUTTON_TEXT_COLOR = getResources().getColor(R.color.white);
        AppThemeConstants.APP_NAME = getResources().getString(R.string.app_name);

        AppThemeConstants.LOGO_ICON = encodeTobase64(BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo));
        AppThemeConstants.BACKGROUND_IMAGE = encodeTobase64(BitmapFactory.decodeResource(context.getResources(),R.mipmap.bg_sr));

        AppThemeUtil.updateAppConstants(this);


    }

    public void showToastMessage(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initialiseTextView(msg,R.color.deny_highlight);
            }
        });
    }

    public void showSuccessMessages(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initialiseTextView(msg,R.color.approve_end);
            }
        });
    }

    void initialiseTextView(String msg, int color){
        errorMessageTextView = (TextView) findViewById(R.id.errorMessageTextView);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setTextColor(ContextCompat.getColor(getApplication(), color));
        errorMessageTextView.setText(msg);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(errorMessageTextView != null) {
            errorMessageTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logouti) {

            if (ApplicationConstants.ENABLE_BIOMETRIC_LOGIN) {
                if (MASUser.getCurrentUser() != null) {
                    MASUser.getCurrentUser().lockSession(new MASCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {

                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);

                        }

                        @Override
                        public void onError(Throwable e) {

                            showToastMessage(e.getMessage());
                        }
                    });
                }



            } else {
                if (MASUser.getCurrentUser() != null) {
                    MASUser.getCurrentUser().logout(new MASCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showToastMessage(e.getMessage());
                        }
                    });
                }
            }

        } else if (id == R.id.lockAppi) {
            if (MASUser.getCurrentUser() != null) {
                MASUser.getCurrentUser().lockSession(new MASCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
            }

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.fundTransferi) {
            ApplicationConstants.IS_FUND_TRANSFER = true;
            Intent intent = new Intent(this, FundTransferActivity.class);
            startActivity(intent);
        } else if (id == R.id.loginScreeni) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.home_screen1) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.home_screen2) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
        }*/ else if (id == R.id.credentials) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else if (id == R.id.appTheme) {
            Intent intent = new Intent(this, AppThemeConfigActivity.class);
            startActivity(intent);
        } else if (id == R.id.pushnotification) {
            //showToastMessage("This feature is disabled");
            MASDevice.getCurrentDevice().deregister(new MASCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    showToastMessage("De-registered");
                }
                @Override
                public void onError(Throwable e) {
                    showToastMessage("De-registeration failed");
                }
            });
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.registerForStrongAuthi) {
            Intent intent = new Intent(this, SelectRegistrationCredActivity.class);
            startActivity(intent);
        } else if (id == R.id.billPaymenti) {
            ApplicationConstants.IS_FUND_TRANSFER = false;
            Intent intent = new Intent(this, FundTransferActivity.class);
            startActivity(intent);
        } else if (id == R.id.cardsi) {
            Intent intent = new Intent(this, CardsActivity.class);
            startActivity(intent);
        }
//        } else if (id == R.id.branchesi) {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.BRANCHES_NEARBY_URL));
//            startActivity(browserIntent);
//        } else if (id == R.id.atmi) {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationConstants.ATM_NEARBY_URL));
//            startActivity(browserIntent);
//        }

        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public void putDataInIntentBundle(Intent intent, String message) {
        intent.putExtra(ACTIVITYMESSAGE,message);
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

}
