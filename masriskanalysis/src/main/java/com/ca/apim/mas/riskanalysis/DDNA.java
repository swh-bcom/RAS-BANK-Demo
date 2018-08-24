package com.ca.apim.mas.riskanalysis;

import android.content.Context;
import com.ca.mobile.riskminder.RMDeviceInventory;
import com.ca.mobile.riskminder.RMDeviceInventoryImpl;
import com.ca.mobile.riskminder.RMDeviceInventoryResponseCallBack;
import com.ca.mobile.riskminder.RMError;

/**
 * Created by ganpo01 on 05/15/2017.
 */
class DDNA {

    RMDeviceInventory rm = null;

    DDNA(Context context) {
        rm = RMDeviceInventoryImpl.getDeviceInventoryInstance(context, RMDeviceInventory.DDNA_Mode.SDK);
    }

    public void collectMASDeviceDNA(final RMDeviceInventoryResponseCallBack rmDeviceInventoryResponseCallBack) {

        rm.collectDeviceDNA(new RMDeviceInventoryResponseCallBack() {
            @Override
            public void onResponse(String s, RMError rmError) {
                rmDeviceInventoryResponseCallBack.onResponse(s, rmError);
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
        });
    }

    public String  getDeviceId () {
       return  rm.getRMDeviceId();
    }

    public void setDeviceId (String deviceId) {
        rm.setRMDeviceId(deviceId);
    }
}