///**
// * Copyright 2016 Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.example.vreeni.firebaseauthentiction;
//
//import android.app.Activity;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.os.Bundle;
//import android.support.annotation.StringRes;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Base64;
//import android.util.Log;
//import android.widget.TextView;
//
//import com.example.vreeni.firebaseauthentication.R;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//
///**
// * Demonstrate Firebase Authentication using a Facebook access token.
// */
//public class FacebookLoginActivity extends AppCompatActivity {
//    private LoginButton loginButton;
//    private CallbackManager callbackManager;
//    private TextView txtStatus;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_facebook);
//
//        printKeyHash(this);
//
//
//        initializeControls();
//        loginWithFacebook();
//    }
//
//
//
//    private void initializeControls(){
//        callbackManager = CallbackManager.Factory.create();
//        txtStatus = (TextView) findViewById(R.id.txtStatus);
//        loginButton = (LoginButton) findViewById(R.id.login_button);
//    }
//
//    private void
//    loginWithFacebook() {
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                String token = loginResult.getAccessToken().toString();
//                txtStatus.setText(getString(R.string.login_successful, token));
//            }
//
//            @Override
//            public void onCancel() {
//                txtStatus.setText(getString(R.string.login_cancelled));
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                txtStatus.setText(getString(R.string.login_error, error.getMessage()));
//            }
//        });
//    }
//
//    public static String printKeyHash(Activity context) {
//        PackageInfo packageInfo;
//        String key = null;
//        try {
//            //getting application package name, as defined in manifest
//            String packageName = context.getApplicationContext().getPackageName();
//
//            //Retriving package info
//            packageInfo = context.getPackageManager().getPackageInfo(packageName,
//                    PackageManager.GET_SIGNATURES);
//
//            Log.e("Package Name=", context.getApplicationContext().getPackageName());
//
//            for (Signature signature : packageInfo.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                key = new String(Base64.encode(md.digest(), 0));
//
//                // String key = new String(Base64.encodeBytes(md.digest()));
//                Log.e("Key Hash=", key);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("Name not found", e1.toString());
//        }
//        catch (NoSuchAlgorithmException e) {
//            Log.e("No such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("Exception", e.toString());
//        }
//
//        return key;
//    }
//
//}
