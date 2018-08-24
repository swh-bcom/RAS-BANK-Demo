package com.ca.apim.mas.authotp;

/**
 * <p><b>MASAdvanceAuthConsts</b> contains the base constants used by any classes in the hierarchy. The nature of these constants is
 * intentionally general and is limited to basic timeout, network constants, and character constants that could potentially be
 * used anywhere in the SDK.</p>
 */
class MASAuthOTPConsts {

    enum TokenType {
        smsession, user_jwt, authToken, tokenType
    }

    // MAS Network call End Points
    public static final String CONFIG_FILE_PATH = "msso_config.json";

    public static final String SIGNED_CHALLENGE_KEY = "signedChallenge";
    public static final String TOKEN_TYPE_KEY = "tokenType";
    public static final String ORG_NAME = "orgName";
    public static final String AUTH_ID_CHALLENGE = "authIDChallenge";

    public static final String OTP_USERNAME = "userName";
    public static final String OTP_KEY1 = "otp1";
    public static final String OTP_KEY2 = "otp2";
    public static final String CRED_VERSION = "credentialVersion";

    public static final String RESPONSE_CODE = "response_code";
    public static final String REASON_CODE = "reason_code";
    public static final String RESPONSE_CODE_5702 = "5702";
    public static final String RESPONSE_CODE_5707 = "5707";
    public static final String RESPONSE_CODE_9033 = "9033";
    public static final String RESPONSE_CODE_9133 = "9133";
    public static final int MAS_AA_AID_ECODE_BASE = 9000;
    public static final int MAS_AA_AOTP_ECODE_BASE = 9100;
    public static final int MAS_AA_AOTP_INVALID_USERID = 33;
    public static final int MAS_AA_AOTP_INVALID_ACCOUNT = 34;
    public static final String REASON_CODE_0 = "0";

    //Errors
    public static final String MAS_AA_EXCEPTION_ERROR = "error";
    public static final String MAS_AA_EXCEPTION_ERROR_DESCRIPTION = "error_description";
    public static final String MAS_AA_EXCEPTION_ERROR_DETAILS = "error_details";
    public static final String MAS_AA_EXCEPTION_REASON_CODE = "reason_code";
    public static final String MAS_AA_EXCEPTION_RESPONSE_CODE = "response_code";
    public static final String STRONG_AUTHENTICATION_ERROR = "strong_authentication_error";
    public static final String STRONG_AUTHENTICATION_UNKNOWN_ERROR = "Unknown error has occured";
    public static final String ADVANCED_AUTH_NOT_INITIALIZED = "MAS Advanced Auth has not been initialized.";
    public static final String INVALID_USER_IDENTIFIER = "The user identifier is invalid.";
    public static final String INVALID_ACCOUNT = "The account is invalid.";
    public static final String CREDENTIAL_VERSION_KEY = "CredentialVersion";
    public static final String AOTP_EXCEPTION = "AOTP Internal Exception";
    public static final String AID_EXCEPTION = "AID Internal Exception";
    public static final String AOTP_EXCEPTION_NO_USER = "The user identifier is invalid";

    public static final String MAS_INITIALIZATION_ERROR = "MAS has not been initialized.";

    public static final String REQUEST_PARAM_KEY_ORGNAME = "orgName";
    public static final String REQUEST_PARAM_KEY_PASSWORD = "password";
    public static final String REQUEST_PARAM_KEY_PROFILE = "profile";
    public static final String REQUEST_PARAM_KEY_CLIENTTXNID = "clientTxnId";
    public static final String REQUEST_HEADER_KEY_USERNAME = "X-CA-RAS-USERNAME";
    public static final String REQUEST_HEADER_KEY_PASSWORD= "X-CA-RAS-PASSWORD";
}
