package com.example.ras;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends BaseActivity {

    ListView itemList;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transaction_history);
        super.onCreate(savedInstanceState);
        activity = this;
        itemList = (ListView) findViewById(R.id.itemList);
        ArrayAdapter arrayAdapter = new ArrayAdapter<Object>(TransactionHistoryActivity.this, R.layout.listitem, getTxnList());
        itemList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

    }


    private List<Object> getTxnList()  {
        List<Object> objects = new ArrayList<Object>();
        try {
            String[] array = activity.getResources().getStringArray(R.array.transaction_history_array);
            for (int i = 0; i < array.length; i++) {
                objects.add(new Pair<Integer, String>(i-1, array[i]) {
                    @Override
                    public String toString() {
                        if (first.equals(-1) || first.equals(0)){
                            return "  " + second;
                        } else
                            return first + "  " + second;
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
        return objects;
            /*JSONArray items = json.getJSONArray("products");
            for (int i = 0; i < items.length(); ++i) {
                JSONObject item = (JSONObject) items.get(i);
                Integer id = (Integer) item.get("id");
                String name = (String) item.get("name");
                objects.add(new Pair<Integer, String>(id, name) {
                    @Override
                    public String toString() {
                        return first + "  " + second;
                    }
                });
            }
            return objects;
        } */
    }
}
