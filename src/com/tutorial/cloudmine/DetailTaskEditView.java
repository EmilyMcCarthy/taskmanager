package com.tutorial.cloudmine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.cloudmine.api.SimpleCMObject;

import java.util.Date;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/29/12, 5:03 PM
 */
public class DetailTaskEditView extends Activity {

    private SimpleCMObject task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedtask);

        task = (SimpleCMObject)savedInstanceState.get(TaskAdapter.TASK_KEY);
        fillInTaskInformation();
    }

    public void fillInTaskInformation() {
        setIsDone();
        setPriority();
        setTaskName();
        Button dueDate = (Button)findViewById(R.id.dueDateTime);
        Date dateValue = task.getDate(TaskAdapter.DUE_DATE, new Date());
//        dueDate.setText();
    }

    private void setTaskName() {
        EditText taskName = (EditText)findViewById(R.id.taskName);
        taskName.setText(task.getString(TaskAdapter.TASK_NAME, ""));
    }

    private void setPriority() {
        Spinner priority = (Spinner)findViewById(R.id.priorityPicker);
        priority.setSelection(task.getInteger(TaskAdapter.PRIORITY, 0), true);
    }

    private void setIsDone() {
        CheckBox isDone = (CheckBox)findViewById(R.id.isDone);
        isDone.setChecked(task.getBoolean(TaskAdapter.IS_DONE, false));
    }
}
