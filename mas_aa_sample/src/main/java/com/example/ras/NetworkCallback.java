package com.example.ras;

public class NetworkCallback {}/*implements IArcotAppCallback {

    private final String TAG = getClass().getSimpleName();

    Handler uiHook;

    private AuthenticatorActivity authenticatorActivity;

    public NetworkCallback(Handler handler, AuthenticatorActivity authenticatorActivity) {
        uiHook = handler;
        this.authenticatorActivity = authenticatorActivity;
    }

    public void callback(String status, Hashtable returnParams) {

        if (returnParams == null) {
            Log.e(TAG, "callback params is null! aborting");
            return;
        }

        // Log inputs

        Log.i(TAG, "status: " + status);
        if (returnParams != null) {
            Iterator<?> it = returnParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Log.i("" + getClass(), pairs.getKey() + " = " + pairs.getValue());
            }
        }

        //END log

        String requestType = (String) returnParams.get(IArcotOTPComm.REQUESTTYPE);
        String state = (String) returnParams.get(IArcotOTPComm.STATE);
        Log.i(TAG, "requestType: " + requestType + ", state: " + state);

        if (IArcotOTPComm.STATUS_SUCCESS.equals(status)) {

            Log.d(TAG, "status == STATUS_SUCCESS");

            if (IArcotOTPComm.REQTYPE_REGISTER_DEVICE.equals(requestType)) {


                if (state.equals(IArcotOTPComm.DONE)) {
                    authenticatorActivity.registerDeviceResult(IArcotOTPComm.REQTYPE_REGISTER_DEVICE, authenticatorActivity.getResources().getString(R.string.success));
                }
            }

            if (IArcotOTPComm.REQTYPE_AUTH_USER.equals(requestType)) {
                if (state.equals(IArcotOTPComm.DONE)) {
                }
            }
            if (IArcotOTPComm.REQTYPE_DELETE_DEVICE.equals(requestType)) {
                if (state.equals(IArcotOTPComm.DONE)) {
                    authenticatorActivity.registerDeviceResult(IArcotOTPComm.REQTYPE_DELETE_DEVICE, authenticatorActivity.getString(R.string.success));
                }
            }
        } else if (IArcotOTPComm.STATUS_FAILURE.equals(status)) {
            //Failure case(s)
            Log.d(TAG, "callback 7: status == STATUS_FAILURE");

            final String errorMsg = (String) returnParams.get(IArcotOTPComm.ERR_MSG);
            final String errorCode = (String) returnParams.get(IArcotOTPComm.ERR_CODE);
            final String rawErrorMsg = (String) returnParams.get(IArcotOTPComm.PLATFORM_MSG);

            if (IArcotOTPComm.REQTYPE_REGISTER_DEVICE.equals(requestType)) {
                authenticatorActivity.registerDeviceResult(IArcotOTPComm.REQTYPE_REGISTER_DEVICE, errorMsg);
            } else if (IArcotOTPComm.REQTYPE_DELETE_DEVICE.equals(requestType)) {
                authenticatorActivity.registerDeviceResult(IArcotOTPComm.REQTYPE_REGISTER_DEVICE, errorMsg);
            }
        }
    }


    //@Override
    public void callback(String arg0, Map arg1) {
        // TODO Auto-generated method stub
    }

}*/