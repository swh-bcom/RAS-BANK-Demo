package com.example.ras;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class AIDActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.aid_activity);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.testcases_fragment, AIDFragment.newInstance());
        transaction.commit();
        super.onCreate(savedInstanceState);
    }

}