package com.tutorial.cloudmine;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.cloudmine.api.SimpleCMObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 5:42 PM
 */
public class TaskAdapter extends ArrayAdapter<SimpleCMObject> {

    public static final String TASK_NAME = "taskName";
    public static final String IS_DONE = "isDone";
    public static final String TASK_CLASS = "task";
    private final Context context;
    private final List<SimpleCMObject> data;
    private final int layoutResourceId;
    public TaskAdapter(Context context, int textViewResourceId, SimpleCMObject[] data) {
        super(context, textViewResourceId, data);
        this.data = new ArrayList<SimpleCMObject>(Arrays.asList(data));
        this.context = context;
        layoutResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaskHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new TaskHolder();
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.isDone);
            holder.taskName = (TextView)convertView.findViewById(R.id.taskName);

            convertView.setTag(holder);
        } else {
            holder = (TaskHolder)convertView.getTag();
        }

        SimpleCMObject task = data.get(position);
        holder.checkBox.setChecked(task.getBoolean(IS_DONE));
        holder.taskName.setText(task.getString(TASK_NAME));
        return convertView;
    }

    public void addAll(Collection<SimpleCMObject> toAdd) {
        data.clear();
        data.addAll(toAdd);
    }

    @Override
    public void add(SimpleCMObject toAdd) {
        data.add(toAdd);
    }

    static class TaskHolder {
        CheckBox checkBox;
        TextView taskName;
    }
}
