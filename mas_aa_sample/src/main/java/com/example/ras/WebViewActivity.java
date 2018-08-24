package com.example.ras;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASDevice;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASProximityLoginQRCode;
import com.example.ras.util.ApplicationConstants;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebViewActivity extends BaseActivity {
    private static final String TAG = "WebViewActivity";

    private WebView webView;

    Activity context;




    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);

        context = this;
        webView = (WebView)findViewById(R.id.webview) ;
/*        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.clearCache(true);
        Map<String, String> headers  = new HashMap<String, String>();
        headers.put("Cookie", ApplicationConstants.SMSESSION);
        webView.loadUrl(ApplicationConstants.SSO_WEBVIEW_URL, headers);*/




        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        webView.setWebChromeClient(new WebChromeClient());

        CookieSyncManager.createInstance(WebViewActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = ApplicationConstants.SMSESSION;//"param=value";
        cookieManager.setCookie(".ca.com", cookieString);
        CookieSyncManager.getInstance().sync();

        Map<String, String> abc = new HashMap<String, String>();
        abc.put("Cookie", cookieString);
        webView.loadUrl(ApplicationConstants.SSO_WEBVIEW_URL,
                abc);
        super.onCreate(savedInstanceState);

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearCache(true);
                Map<String, String> headers  = new HashMap<>();
                headers.put("Cookie", ApplicationConstants.SMSESSION);
                webView.loadUrl(ApplicationConstants.SSO_WEBVIEW_URL, headers);

            }
        });
    }

}
