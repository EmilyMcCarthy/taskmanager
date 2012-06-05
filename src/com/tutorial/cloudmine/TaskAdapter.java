package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.CloudMineWebService;
import org.joda.time.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    public static final String LOCATION = "location";
    public static final String PICTURE = "picture";
    public static final String TASK_CLASS = "task";
    public static final String TASK_KEY = "TASK";
    private static final long TASK_COMPLETION_TIME = 24 * 60 * 60 * 1000; //one day
    private final Activity context;
    private final int layoutResourceId;

    public static Date defaultDueDate() {
        return new Date(DateTimeUtils.currentTimeMillis() + TASK_COMPLETION_TIME);
    }

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

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SimpleCMObject checkedItem = getItem(position);
                    checkedItem.add(IS_DONE, b);
                    CloudMineWebService.defaultService().asyncUpdate(checkedItem);
                }
            });

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

    public void setListContents(Collection<SimpleCMObject> toAdd) {
        clear();
        for(SimpleCMObject simpleObject : toAdd) {
            add(simpleObject);
        }
        notifyUpdated();
    }

    private void notifyUpdated() {
        context.runOnUiThread(updated);
    }

    public Collection<SimpleCMObject> getCompletedTasks() {
        List<SimpleCMObject> completedTasks = new ArrayList<SimpleCMObject>();
        for(int i = 0; i < getCount(); i++) {
            SimpleCMObject task = getItem(i);
            if(task.getBoolean(IS_DONE, Boolean.FALSE)) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    static class TaskHolder {
        CheckBox checkBox;
        TextView taskName;
    }
}
