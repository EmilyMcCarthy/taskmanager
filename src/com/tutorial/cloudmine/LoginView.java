package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.cloudmine.api.User;
import com.cloudmine.api.rest.CloudMineResponse;
import com.cloudmine.api.rest.CloudMineWebService;
import com.cloudmine.api.rest.callbacks.CloudMineResponseCallback;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/22/12, 3:35 PM
 */
public class LoginView extends Activity {
    private static final String TAG = "LoginView";
    public static final String STORE = "STORE";
    private CloudMineWebService webService = new CloudMineWebService("c1a562ee1e6f4a478803e7b51babe287");
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setDefaultLogin();
    }

    public void create(View view) {
        webService.asyncCreateUser(getUser(), new CloudMineResponseCallback() {
            public void onCompletion(CloudMineResponse response) {
                Log.e(TAG, "CREATE SUCCEEDED");
            }
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "Create failed: ", error);
            }
        });
    }

    public void login(View view) {
        webService.asyncLogin(getUser(), new CloudMineResponseCallback() {
            @Override
            public void onCompletion(CloudMineResponse response) {
                Intent goToTaskListView = new Intent(LoginView.this, TaskListView.class);
                goToTaskListView.putExtra(STORE, webService.userWebService(response));
                startActivity(goToTaskListView);
            }

            @Override
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "LogIn failed: ",  error);
            }
        });
    }

    private void setDefaultLogin() {
        EditText  emailField = (EditText)findViewById(R.id.email);
        EditText passwordField = (EditText)findViewById(R.id.password);
        emailField.setText("q@q.com");
        passwordField.setText("q");
    }

    private User getUser() {
        return new User(getEmailFieldText(), getPasswordFieldText());
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