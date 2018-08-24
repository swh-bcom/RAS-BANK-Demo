package com.example.ras;

public class NotificationDialog {}/*extends AppCompatActivity {

    private Button allowAuthBtn, denyAuthBtn;
    protected String notificationMessageBody = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NotificationDialog", "NotificationDialog onCreate");
        setContentView(R.layout.activity_notification_dialog);
        notificationMessageBody = getIntent().getExtras().getString(Constants.NOTIFICATION_MESSAGE_BODY);
        allowAuthBtn = (Button)findViewById(R.id.button_allow_auth);
        denyAuthBtn = (Button)findViewById(R.id.button_deny_auth);

        addListeners();
    }

    private void addListeners() {
        allowAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                authenticateUserRequest(notificationMessageBody,"APPROVE");
            }
        });

        denyAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                authenticateUserRequest(notificationMessageBody,"DENY");
            }
        });
    }

    private void authenticateUserRequest(String notificationMessageBody, String userResponse){
        try{

            MASPushNotification.getInstance().createAuthenticator(this, new PushProvider());
            MASPushNotification.getInstance().authenticateUser(notificationMessageBody, userResponse);
            finish();
        }  catch (Exception e) {
            Toast.makeText(this, R.string.ERROR_PROVISIONING, Toast.LENGTH_LONG).show();
        }
    }
}
*/