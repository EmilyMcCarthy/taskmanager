package com.tutorial.cloudmine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.UserCloudMineWebService;
import com.cloudmine.api.rest.CloudMineResponse;
import com.cloudmine.api.rest.CloudMineWebService;
import com.cloudmine.api.rest.SimpleObjectResponse;
import com.cloudmine.api.rest.callbacks.CloudMineResponseCallback;
import com.cloudmine.api.rest.callbacks.SimpleObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.WebServiceCallback;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 11:13 AM
 */
public class TaskListView extends ListActivity {
    private static final String TAG = "TaskListView";
    private static final int ADD_ITEM = 1;
    private static final int LOG_OUT = 2;
    private static final int DELETE_COMPLETED = 3;

    private final WebServiceCallback updateListContentsCallback = new SimpleObjectResponseCallback() {

        @Override
        public void onCompletion(SimpleObjectResponse response) {
            dataAdapter.setListContents(response.objects());
        }

        @Override
        public void onFailure(Throwable error, String message) {
            Log.e(TAG, "Problem loading tasks", error);
        }
    };

    private final WebServiceCallback loadAllTasksCallback = new CloudMineResponseCallback() {
        public void onCompletion(CloudMineResponse response) {
            loadAllTasks();
        }
    };

    private UserCloudMineWebService service;
    private TaskAdapter dataAdapter;
    private Dialog editorDialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataAdapter = new TaskAdapter(this, R.layout.task);
        setListAdapter(dataAdapter);

        service = service();
        loadAllTasks();
    }

    private UserCloudMineWebService service() {
        if(service != null) {
            return service;
        } else {
            return (UserCloudMineWebService)CloudMineWebService.defaultService();
        }
    }

    private void loadAllTasks() {
        dataAdapter.setListContents(new ArrayList<SimpleCMObject>());

        service().allObjectsOfClass(TaskAdapter.TASK_CLASS, updateListContentsCallback);
    }

    private void deleteCompletedTasks() {
        Collection<SimpleCMObject> completedTasks = dataAdapter.getCompletedTasks();
        service().asyncDelete(completedTasks, loadAllTasksCallback);
    }

    private void logout() {
        service().asyncLogout();
        //No need to wait for the logout to go through to go back to the login screen
        Intent goToLoginView = new Intent(this, LoginView.class);
        startActivity(goToLoginView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ADD_ITEM, ADD_ITEM, "Add");
        menu.add(Menu.NONE, LOG_OUT, LOG_OUT, "Log Out");
        menu.add(Menu.NONE, DELETE_COMPLETED, DELETE_COMPLETED, "Delete Completed Tasks");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case ADD_ITEM:
                showDialog(ADD_ITEM);
                break;
            case DELETE_COMPLETED:
                showDialog(DELETE_COMPLETED);
                break;
            case LOG_OUT:
                logout();
                break;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case ADD_ITEM:
                //Could cache this but then the task name doesn't get cleared between adds and this is just  a sample app
                return createEditorDialog();
            case DELETE_COMPLETED:
                return createConfirmDeleteDialog();
        }
        return null;
    }

    private Dialog createConfirmDeleteDialog() {
        DialogInterface.OnClickListener confirmDeleteListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int clickedButtonId) {
                switch(clickedButtonId) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteCompletedTasks();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //don't care
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder.setMessage("Delete all completed tasks?").setPositiveButton("Yes", confirmDeleteListener)
                                                                .setNegativeButton("No", confirmDeleteListener)
                                                                .create();
    }

    private Dialog createEditorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a task");

        View content = getLayoutInflater().inflate(R.layout.addtask, (ViewGroup)findViewById(R.id.addTask));
        builder.setView(content);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog dialog = (Dialog)dialogInterface;
                EditText taskNameField = (EditText)dialog.findViewById(R.id.taskName);
                String taskName = taskNameField.getText().toString();

                SimpleCMObject taskObject = new SimpleCMObject();
                taskObject.setClass(TaskAdapter.TASK_CLASS);
                taskObject.add(TaskAdapter.IS_DONE, Boolean.FALSE);
                taskObject.add(TaskAdapter.TASK_NAME, taskName);
                taskObject.add(TaskAdapter.DUE_DATE, TaskAdapter.defaultDueDate());
                service().create(taskObject, new CloudMineResponseCallback() {
                    @Override
                    public void onCompletion(CloudMineResponse response) {
                        Log.e(TAG, "Got a response!");
                    }

                    @Override
                    public void onFailure(Throwable error, String message) {
                        Log.e(TAG, "failed!", error);
                    }
                });
                dataAdapter.add(taskObject);
            }
        });

        return builder.create();
    }

}
