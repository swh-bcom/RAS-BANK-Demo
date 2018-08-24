package com.example.ras;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

@Deprecated
public class ExampleActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcasesactivity);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        /*transaction.replace(R.id.testcases_fragment, MASAATestsFragment.newInstance());*/
        transaction.commit();
    }
}