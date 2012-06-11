package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.cloudmine.api.AndroidCMUser;
import com.cloudmine.api.UserCMWebService;
import com.cloudmine.api.rest.AndroidCMWebService;
import com.cloudmine.api.rest.CMWebService;
import com.cloudmine.api.rest.callbacks.CMResponseCallback;
import com.cloudmine.api.rest.response.CMResponse;

/**
 * Copyright CloudMine LLC
 * CMUser: johnmccarthy
 * Date: 5/22/12, 3:35 PM
 */
public class LoginView extends Activity {
    private static final String TAG = "LoginView";
    public static final String STORE = "STORE";
    private CMWebService webService = new AndroidCMWebService("c1a562ee1e6f4a478803e7b51babe287");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        setDefaultLogin();
    }


    public void create(View view) {
        webService.asyncCreateUser(getUser(), new CMResponseCallback() {
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
        webService.asyncLogin(getUser(), new CMResponseCallback() {
            @Override
            public void onCompletion(CMResponse response) {
                goToTaskListView(webService.userWebService(response));
            }

            @Override
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "LogIn failed: ", error);
            }
        });
    }

    private void goToTaskListView(UserCMWebService userWebService) {
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