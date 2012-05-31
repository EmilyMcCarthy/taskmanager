package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.cloudmine.api.SimpleCMObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 5:42 PM
 */
public class TaskAdapter extends ArrayAdapter<SimpleCMObject> {

    public static final String TASK_NAME = "taskName";
    public static final String IS_DONE = "isDone";
    public static final String PRIORITY = "priority";
    public static final String DUE_DATE = "dueDate";
    public static final String TASK_CLASS = "task";
    public static final String TASK_KEY = "TASK";
    private final Activity context;
    private final int layoutResourceId;
    private final Runnable updated = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public TaskAdapter(Activity context, int textViewResourceId) {
        super(context, textViewResourceId, new ArrayList<SimpleCMObject>());
        this.context = context;
        layoutResourceId = textViewResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TaskHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new TaskHolder();
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.isDone);
            holder.taskName = (TextView)convertView.findViewById(R.id.taskName);

            convertView.setTag(holder);
            ((Button)convertView.findViewById(R.id.edit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent goToDetailTaskEdit = new Intent(context, DetailTaskEditView.class);
                    goToDetailTaskEdit.putExtra(TASK_KEY, getItem(position)); //TODO does this get messed up if the ordering changes? probably
                    context.startActivity(goToDetailTaskEdit);
                }
            });
        } else {
            holder = (TaskHolder)convertView.getTag();
        }

        SimpleCMObject task = getItem(position);
        holder.checkBox.setChecked(task.getBoolean(IS_DONE));
        holder.taskName.setText(task.getString(TASK_NAME));
        return convertView;
    }

    public void addAll(Collection<SimpleCMObject> toAdd) {
        clear();
        for(SimpleCMObject simpleObject : toAdd) {
            add(simpleObject);
        }
        context.runOnUiThread(updated);
    }

    static class TaskHolder {
        CheckBox checkBox;
        TextView taskName;
    }
}
