package com.ca.apim.mas.authid;

/**
 * <p><b>MASAdvanceAuthConsts</b> contains the base constants used by any classes in the hierarchy. The nature of these constants is
 * intentionally general and is limited to basic timeout, network constants, and character constants that could potentially be
 * used anywhere in the SDK.</p>
 */
class MASAuthIDConsts {




    public enum TokenType {
        smsession("smsession"), user_jwt("user_jwt"), authToken("authToken"), tokenType("tokenType"), user_jwt_smsession("user_jwt-smsession");

        private final String name;

        private TokenType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }

    }

    //TODO This needs to come from MAS. This name may change
    public static final String CONFIG_FILE_PATH = "msso_config.json";

    public static final String SIGNED_CHALLENGE_KEY = "signedChallenge";
    public static final String TOKEN_TYPE_KEY = "tokenType";
    public static final String ORG_NAME = "orgName";
    public static final String AUTH_ID_CHALLENGE = "authIDChallenge";
    public static final String AUTH_ID_POLICY  = "authenticationPolicy";
    public static final String AUTH_ID_ADDITIONAL_PARAM  = "additionalParams";


    public static final String RESPONSE_CODE = "response_code";
    public static final String REASON_CODE = "reason_code";
    public static final String RESPONSE_CODE_5702 = "5702";
    public static final String RESPONSE_CODE_5707 = "5707";
    public static final String RESPONSE_CODE_9033 = "9033";
    public static final int MAS_AA_AID_ECODE_BASE = 9000;
    public static final int MAS_AA_AOTP_ECODE_BASE = 9100;
    public static final int MAS_AA_AOTP_INVALID_USERID = 33;
    public static final int MAS_AA_AOTP_INVALID_ACCOUNT = 34;
    public static final String REASON_CODE_0 = "0";
    public static final String RESPONSE_CODE_9031 = "9031";
    public static final String RESPONSE_CODE_9001 = "9001";
    public static final String RESPONSE_CODE_9011 = "9011";
    //Sign challenge authid does not exists.
    public static final String RESPONSE_CODE_9051 = "9051";
    //Error in getting authID challenge
    public static final String RESPONSE_CODE_9052 = "9052";
    //Error in login with ID token
    public static final String RESPONSE_CODE_9053 = "9053";
    //Not a target api exception
    public static final String RESPONSE_CODE_9054 = "9054";

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

    public static final String MAS_INITIALIZATION_ERROR = "MAS has not been initialized.";

    public static final String REQUEST_PARAM_KEY_ORGNAME = "orgName";
    public static final String REQUEST_PARAM_KEY_PASSWORD = "password";
    public static final String REQUEST_PARAM_KEY_CLIENTTXNID = "clientTxnId";
    public static final String REQUEST_PARAM_KEY_SIGNEDCHALL = "signedChallenge";
    public static final String REQUEST_PARAM_KEY_TOKENTYPE = "tokenType";

    public static final String REQUEST_HEADER_KEY_USERNAME = "X-CA-RAS-USERNAME";
    public static final String REQUEST_HEADER_KEY_PASSWORD= "X-CA-RAS-PASSWORD";

    public static final String NAMESPACE_INVALID_ERROR = "The namespace is invalid.";

    public static final String NAMESPACE_ERROR_DESCRIPTION = "Please validate the namespace, special characters are not allowed";

    public static final String USER_ALREADY_AUTHENTICATED = "A user is already authenticated";

    public static final String SIGN_CHALLENGE_AUTHID_DOESNOT_EXISTS = "Response to verify AuthID sign challenge does not have authToken";



}
