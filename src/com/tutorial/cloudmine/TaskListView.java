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
import com.cloudmine.api.rest.SimpleObjectResponse;
import com.cloudmine.api.rest.callbacks.SimpleObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.WebServiceCallback;
import org.apache.http.HttpResponse;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 11:13 AM
 */
public class TaskListView extends ListActivity {
    private static final String TAG = "TaskListView";
    private static final int ADD_ITEM = 1;

    private UserCloudMineWebService service;
    private TaskAdapter dataAdapter;
    private Dialog editorDialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleCMObject someTask = new SimpleCMObject();
        dataAdapter = new TaskAdapter(this, R.layout.task);
        setListAdapter(dataAdapter);

        Intent intent = getIntent();
        service = intent.getParcelableExtra(LoginView.STORE);
        loadAllTasks();
    }

    private void loadAllTasks() {
        service.allObjectsOfClass(TaskAdapter.TASK_CLASS, new SimpleObjectResponseCallback() {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ADD_ITEM, ADD_ITEM, "Add");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case ADD_ITEM:
                showDialog(0);
                break;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(editorDialog == null) {
            editorDialog = createEditorDialog();
        }
        return editorDialog;
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
                service.create(taskObject, new WebServiceCallback() {
                    @Override
                    public void onCompletion(HttpResponse response) {
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
