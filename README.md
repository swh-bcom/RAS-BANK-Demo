# Android-MAS-AdvancedAuth
This section guides you how to integrate MASAuthID and the MASAuthOTP SDKs with your app:

## Overview

To secure online transactions from Man-in-the-Middle (MITM) and other related attacks, CA Strong Authentication provides client applications that are based on CA Mobile PKI, CA Mobile OTP credentials and CA Mobile API Gateway. These software credentials provide two-factor authentication and are based on the patented Cryptographic Camouflage technique for securely storing keys.
You can develop your own client application by using this Software Development Kit (SDK).

## Project Dependencies

### Client-Side Requirements

* Platform: Android 4.4.2 to 8

* Integrated Development Environment (IDE): Latest Android Studio and Android SDK 21 and higher.


### Server-Side Requirements

Ensure that the following softwares are installed and configured on your server:

* CA Mobile API Gateway (MAG)

* CA API Management OAuth Toolkit

* CA Mobile App Services (MAS)

The APIM Gateway server-side toolkit for Advanced Auth (Strong Authentication) is installed on your server.

# How to Use the RAS Sample Application
The module mas_aa_sample is the RAS sample application.  
Update the msso_config.json located here Android-MAS-AdvancedAuth/mas_aa_sample/src/main/assets/msso_config.json 
Deploy the application to a device or an emulator. 

# How to Integrate SDK with your App

You can generate the sdks from this repository using Android Studio. Or get the pre built sdk from the RAS release package
1. Review and ensure all the server-side and client-side prerequisites are met.

2. Obtain the msso_config.json files from your MAG Administrator, and add it to the src/main/assets folder of your Android app. If you are using multiple MAGs, you may have more than one msso_config.json file.

3. Add the following code snippet in the custom tag:
 	```,
        "masaa_auth_endpoints": {
            "getchallenge_endpoint_path": "/auth/strongauth/authid/challenge",
            "verifychallenge_endpoint_path": "/auth/strongauth/authid/verifySignedChallenge",
            "verifyotp_endpoint_path": "auth/strongauth/aotp/verifyAOTP/"
        }
	
	```
	
The complete code must look as follows:

	```"custom": {
        "oauth_demo_protected_api_endpoint_path":"/oauth/v2/protectedapi/foo",
        "mag_demo_products_endpoint_path":"/protected/resource/products",
        "masaa_auth_endpoints": {
            "getchallenge_endpoint_path": "/auth/strongauth/authid/challenge",
            "verifychallenge_endpoint_path": "/auth/strongauth/authid/verifySignedChallenge",
            "verifyotp_endpoint_path": "auth/strongauth/aotp/verifyAOTP/"
        } }
	```
	
4. Add the following dependency framework or libraries to the project:

	* Copy the ‘masauthid_2.5.aar' file to the 'libs' directory of the project module. Add this as reference in Android studio project.
	* Copy the ‘masauthotp_2.5.aar' file to the 'libs' directory of the project module.
	


# How to Enable FIDO in the Sample App
The module mas_aa_sample is the application which you need to deploy on your device. Please note the project with SDS libraries will not run on an emulator. Update the msso_config.json located here Android-MAS-AdvancedAuth/mas_aa_sample/src/main/assets/msso_config.json 
1. Uncomment the section SDS FIDO Dependencies in the build.gradle of the mas_aa_sample module
```
\\SDS FIDO dependencies
    compile files('libs/samsungsds-um-1.5.2.jar')
    compile(name: 'samsungsds-biosdk-1.5.2', ext: 'aar')
    compile(name: 'samsungsds-bm-1.5.2', ext: 'aar')
    compile(name: 'smma-3.1.0-android', ext: 'aar')
    compile(name: 'vvutils-3.1.0', ext: 'aar')
    compile(name: 'model-1.4.3-combiner', ext: 'aar')
    compile(name: 'model-1.4.3-face-lr_nn', ext: 'aar')
    compile(name: 'model-1.4.3-voice-tssv-udp_enUS', ext: 'aar')
    compile(name: 'samsungsds-sm-1.5.2', ext: 'aar')
    compile(name: 'uaf-authenticator-1.5.2', ext: 'aar')
    compile(name: 'uaf-nexsign-client-1.5.2', ext: 'aar')
 ```
    
2. Get the corresponding SDS libraries and place them in the (Internal : You may get it from RAS Product Owner Hitesh Sharma)

3. Run the application 

4. After the splash screen you will see the login screen 

5. Click on the hamburger icon 

6. On the side menu click on settings option 

7. In the settings screen enable the FIDO Login toggle button and update the StrongAuth organization name  

8. Click on update and go back to the login screen  
