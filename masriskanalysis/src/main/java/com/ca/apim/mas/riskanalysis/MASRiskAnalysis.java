package com.ca.apim.mas.riskanalysis;

import android.content.Context;

import com.ca.mas.foundation.MAS;
import com.ca.mobile.riskminder.RMDeviceInventoryResponseCallBack;

/**
 * The top level MASAdvancedAuth object represents the Mobile App Services Advanced Auth SDK in its entirety.
 * It is the front facing class where many of the configuration settings for the SDK as a whole
 * can be found and utilized.
 */


public class MASRiskAnalysis {

    //private AID_AID aid_aid;
    private DDNA ddna;
    //private AOTP aOTP;
//    Context context;

    private static MASRiskAnalysis instance = new MASRiskAnalysis();

    private MASRiskAnalysis() {
        if (MAS.getContext() == null) {
            throw new IllegalStateException(MASRiskAnalysisConsts.MAS_INITIALIZATION_ERROR);
        }
        //this.aid_aid = new AID_AID(MAS.getContext());
        this.ddna = new DDNA(MAS.getContext());
        //this.aOTP = new AOTP(MAS.getContext());

    }


    public static MASRiskAnalysis getInstance() {
        return instance;
    }

    public void collectMASDeviceDNA(RMDeviceInventoryResponseCallBack rmDeviceInventoryResponseCallBack) {
        ddna.collectMASDeviceDNA(rmDeviceInventoryResponseCallBack);
    }

    public String getDeviceId() {
        return ddna.getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        ddna.setDeviceId(deviceId);
    }

    static String mapErrorCode(int errorCode) {
        return String.valueOf(MASRiskAnalysisConsts.MAS_AA_AOTP_ECODE_BASE + errorCode);
    }

}
