package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.cloudmine.api.AndroidCMUser;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.DeviceIdentifier;
import com.cloudmine.api.rest.AndroidCMWebService;
import com.cloudmine.api.rest.callbacks.CMResponseCallback;
import com.cloudmine.api.rest.callbacks.LoginResponseCallback;
import com.cloudmine.api.rest.response.CMResponse;
import com.cloudmine.api.rest.response.LoginResponse;

/**
 * <br>Copyright CloudMine LLC. All rights reserved<br> See LICENSE file included with SDK for details.
 * CMUser: johnmccarthy
 * Date: 5/22/12, 3:35 PM
 */
public class LoginView extends Activity {
    private static final String TAG = "LoginView";
    public static final String STORE = "STORE";
    private static final String APP_ID = "c1a562ee1e6f4a478803e7b51babe287";
    private static final String API_KEY = "3fc494b36d6d432d9afb051d819bdd72";
    public static final String USER_TOKEN_KEY = "userTokenJson";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceIdentifier.initialize(getApplicationContext());
        CMApiCredentials.initialize(APP_ID, API_KEY);
        setContentView(R.layout.login);
        setDefaultLogin();
    }


    public void create(View view) {
        AndroidCMWebService.getService().asyncCreateUser(getUser(), new CMResponseCallback() {
            public void onCompletion(CMResponse response) {
                login();
            }

            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "Create failed: ", error); //TODO show the user something proper here
            }
        });
    }

    public void login(View view) {
        login();
    }

    private void login() {
        //TODO should we should a processing message here?
        AndroidCMWebService.getService().asyncLogin(getUser(), new LoginResponseCallback() {
            @Override
            public void onCompletion(LoginResponse response) {
                if(response.wasSuccess()) {
                    AndroidCMWebService.getService().setLoggedInUser(response.getSessionToken());
                    goToTaskListView();
                } else {
                    //in a real app we should show a notification that the log in failed
                }
            }

            @Override
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "LogIn failed: ", error);
            }
        });
    }

    private void goToTaskListView() {
        Intent goToTaskListView = new Intent(this, TaskListView.class);
        startActivity(goToTaskListView);
    }

    private void setDefaultLogin() {
        EditText  emailField = (EditText)findViewById(R.id.email);
        EditText passwordField = (EditText)findViewById(R.id.password);
        emailField.setText("q@q.com");
        passwordField.setText("q");
    }

    private AndroidCMUser getUser() {
        return new AndroidCMUser(getEmailFieldText(), getPasswordFieldText());
    }

    private String getEmailFieldText() {
        EditText emailField = (EditText)findViewById(R.id.email);
        return emailField.getText().toString();
    }

    private String getPasswordFieldText() {
        EditText passwordField = (EditText)findViewById(R.id.password);
        return passwordField.getText().toString();
    }
}