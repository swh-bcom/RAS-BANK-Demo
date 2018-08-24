
package com.example.ras;

/**
 * Sample to show a TestCases.
 */
public class MASAATestsFragment /*extends Fragment*/ {/*

    MASAdvanceAuth masAdvanceAuth = null;
    private String authToken_key = "authToken";
    private String error_key = "error_details";

    public static MASAATestsFragment newInstance() {
        MASAATestsFragment fragment = new MASAATestsFragment();
        return fragment;
    }

    public MASAATestsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.testcases, container, false);

        Config HOSTNAME = new Config(true, MobileSsoConfig.PROP_TOKEN_HOSTNAME, "server.hostname", String.class);

        // Provision Request Test
        final EditText base64aid = (EditText) view.findViewById(R.id.base64aid);
        final EditText provisionURL = (EditText) view.findViewById(R.id.provision_url);
        final EditText provisionResponse = (EditText) view.findViewById(R.id.provisionresponse);

        setScrollToEditText(base64aid);
        setScrollToEditText(provisionResponse);
        ((Button) view.findViewById(R.id.provision_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    provisionResponse.setText("");
                    AIDAccount account = MASAdvanceAuth.getInstance().provisionAIDAccount(base64aid.getEditableText().toString(), provisionURL.getEditableText().toString());

                    if (account != null) {
                        String att = "";
                        try {
                            att = account.getAttribute("AID_PROFILE");
                        } catch (AIDException e) {
                            e.printStackTrace();
                        }
                        provisionResponse.setText(getResources().getString(R.string.success) + ",\n ID : " + account.getId() + " \n User Name " + account.accountId + "\n profile : " + att);
                    } else {
                        provisionResponse.setText(getResources().getString(R.string.failed));
                    }
                } catch (MASAdvanceAuthException e) {
                    provisionResponse.setText(e.getResponse().toString());
                }
            }
        });

        // LoginWithSmSession Test
        final EditText smsessionUserID = (EditText) view.findViewById(R.id.smsession_userid);
        final EditText smsessionPassword = (EditText) view.findViewById(R.id.smsession_password);
        final EditText smsessionResponse = (EditText) view.findViewById(R.id.smsession_response);

        setScrollToEditText(smsessionResponse);

        String smSessionResponse = "";

        Button verifySmSession = (Button) view.findViewById(R.id.verify_smsession_button);
        verifySmSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smSessionRespose = smsessionResponse.getEditableText().toString();
                try {
                    JSONObject jsonObject = new JSONObject(smSessionRespose);
                    if (jsonObject.has("smCookie")) {
                        String smCookie = jsonObject.getString("smCookie");
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra("COOKIE", smCookie);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                }
            }
        });
        Button getSMSession = (Button) view.findViewById(R.id.smsession_button);

        getSMSession.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        smsessionResponse.setText("");
                                                    }
                                                });

                                                MASAdvanceAuth.getInstance().loginWithAIDSMSession(smsessionUserID.getEditableText().toString(), smsessionPassword.getEditableText().toString(), new MASCallback<JSONObject>() {
                                                    @Override
                                                    public void onSuccess(final JSONObject jsonObject) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            smsessionResponse.setText(jsonObject.toString());
                                                                                        }
                                                                                    }
                                                        );
                                                    }

                                                    @Override
                                                    public void onError(final Throwable throwable) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (throwable.getCause() instanceof MASAdvanceAuthException) {
                                                                    smsessionResponse.setText(((MASAdvanceAuthException) throwable.getCause()).getResponse().toString());
                                                                } else {
                                                                    Log.d("Testing", "-- " + (throwable.getCause()));
                                                                    smsessionResponse.setText(throwable.getMessage());
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
        );

        // GetAccount Request Test
        final EditText getAccountUserID = (EditText) view.findViewById(R.id.getAccount_userID);
        final EditText getAccountResponse = (EditText) view.findViewById(R.id.getaccount_response);

        setScrollToEditText(getAccountResponse);
        final Button getAccountButton = (Button) view.findViewById(R.id.getAccount_button);
        getAccountButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        getAccountResponse.setText("");
                                                        AIDAccount account = MASAdvanceAuth.getInstance().getAIDAccount(getAccountUserID.getEditableText().toString());
                                                        if (account != null) {
                                                            getAccountResponse.setText(account.getId());
                                                        } else {
                                                            getAccountResponse.setText(getResources().getString(R.string.failed));
                                                        }
                                                    } catch (MASAdvanceAuthException e) {
                                                        getAccountResponse.setText(e.getResponse().toString());
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
        );

        // GetAllAccounts Request Test
        final EditText getAllAccountsResponse = (EditText) view.findViewById(R.id.getAllaccounts_response);
        setScrollToEditText(getAllAccountsResponse);
        Button getAllAccountsButton = (Button) view.findViewById(R.id.getAllAccounts_button);
        getAllAccountsButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            getAllAccountsResponse.setText("");
                                                            AIDAccount[] accounts = MASAdvanceAuth.getInstance().getAllAIDAccounts();

                                                            if (accounts != null && accounts.length > 0) {

                                                                JSONArray jsonArray = new JSONArray();

                                                                for (AIDAccount account : accounts) {
                                                                    try {
                                                                        JSONObject jsonObject = new JSONObject();
                                                                        jsonObject.put("User ID", account.getId());
                                                                        jsonObject.put("UserName", account.name);
                                                                        jsonArray.put(jsonObject);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                getAllAccountsResponse.setText(jsonArray.toString());
                                                            } else {
                                                                getAllAccountsResponse.setText(getResources().getString(R.string.no_accounts));
                                                            }
                                                        } catch (MASAdvanceAuthException e) {
                                                            getAllAccountsResponse.setText(e.getResponse().toString());
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

        );

        // Remove Account Request Test
        final EditText removeAccountUserID = (EditText) view.findViewById(R.id.removeAccount_userID);
        final EditText removeAccountResponse = (EditText) view.findViewById(R.id.removeAccount_response);
        Button removeAccountButton = (Button) view.findViewById(R.id.removeAccount_button);
        removeAccountButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       try {
                                                           AIDAccount account = MASAdvanceAuth.getInstance().getAIDAccount(removeAccountUserID.getEditableText().toString());
                                                           if (account != null) {
                                                               MASAdvanceAuth.getInstance().removeAIDAccount(removeAccountUserID.getEditableText().toString());
                                                               account = MASAdvanceAuth.getInstance().getAIDAccount(removeAccountUserID.getEditableText().toString());
                                                               if (account == null) {
                                                                   removeAccountResponse.setText(getResources().getString(R.string.success));
                                                               } else {
                                                                   removeAccountResponse.setText(getResources().getString(R.string.failed));
                                                               }
                                                           } else
                                                               removeAccountResponse.setText(getResources().getString(R.string.invalid_user_id));
                                                       } catch (MASAdvanceAuthException e) {
                                                           removeAccountResponse.setText(e.getResponse().toString());
                                                       }
                                                   }
                                               }

        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static void setScrollToEditText(final EditText editText) {
        editText.setMovementMethod(new ScrollingMovementMethod());
        ScrollingMovementMethod.getInstance();

        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editText.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }


        });
    }*/

}
