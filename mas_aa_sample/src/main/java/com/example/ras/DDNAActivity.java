package com.example.ras;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ca.apim.mas.riskanalysis.MASRiskAnalysis;
import com.ca.mobile.riskminder.RMDeviceInventoryResponseCallBack;
import com.ca.mobile.riskminder.RMError;
import com.example.ras.util.AppUtil;

import java.util.ArrayList;

public class DDNAActivity extends BaseActivity implements RMDeviceInventoryResponseCallBack {

    /**
     * Called when the activity is first created.
     */

    EditText ddnadetails = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ddna_activity);

        Button collectDNA = (Button) findViewById(R.id.get_dna_details);
        collectDNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MASRiskAnalysis.getInstance().collectMASDeviceDNA(DDNAActivity.this);
            }
        });
        ddnadetails = (EditText) findViewById(R.id.ddna_details);

        AIDFragment.setScrollToEditText(ddnadetails);
        registerForContextMenu(ddnadetails);
        checkRunTimePermissions();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResponse(String s, RMError rmError) {
        EditText ddnadetails = (EditText) findViewById(R.id.ddna_details);
        ddnadetails.setText(AppUtil.prettifyJson(s));
    }

    @Override
    public void storeRMDeviceId(String s) {
    }

    @Override
    public void deleteRMDeviceId() {
    }

    @Override
    public String getRMDeviceId() {
        return null;
    }


    private void checkRunTimePermissions() {

        ArrayList<String> perms = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (perms != null && perms.size() > 0) {
            String[] stockArr = new String[perms.size()];
            stockArr = perms.toArray(stockArr);
            ActivityCompat.requestPermissions(this, stockArr, 0);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Copy");
        ddnadetails = (EditText) v;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(ddnadetails.getText());
    }
}