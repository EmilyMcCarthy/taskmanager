package com.tutorial.cloudmine;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.UserCloudMineWebService;
import com.cloudmine.api.rest.SimpleObjectResponse;
import com.cloudmine.api.rest.callbacks.SimpleObjectResponseCallback;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 11:13 AM
 */
public class TaskListView extends ListActivity {
    private static final String TAG = "TaskListView";
    private UserCloudMineWebService service;
    private TaskAdapter dataAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataAdapter = new TaskAdapter(this, R.layout.task, new SimpleCMObject[0]);
        setListAdapter(dataAdapter);

        Intent intent = getIntent();
        service = intent.getParcelableExtra(LoginView.STORE);
        service.allObjectsOfClass("task", new SimpleObjectResponseCallback() {

            @Override
            public void onCompletion(SimpleObjectResponse response) {
                dataAdapter.addAll(response.objects());

            }

            @Override
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "Problem loading tasks", error);
            }
        });
    }

}
