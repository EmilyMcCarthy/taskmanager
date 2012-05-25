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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Copyright CloudMine LLC
 * User: johnmccarthy
 * Date: 5/24/12, 5:42 PM
 */
public class TaskAdapter extends ArrayAdapter<SimpleCMObject> {

    private final Context context;
    private final List<SimpleCMObject> data;
    private final int layoutResourceId;
    public TaskAdapter(Context context, int textViewResourceId, SimpleCMObject[] data) {
        super(context, textViewResourceId, data);
        this.data = Arrays.asList(data);
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
        holder.checkBox.setChecked(task.getBoolean("isDone"));
        holder.taskName.setText(task.getString("taskName"));
        return convertView;
    }

    public void addAll(Collection<SimpleCMObject> toAdd) {
        data.addAll(toAdd);
    }

    static class TaskHolder {
        CheckBox checkBox;
        TextView taskName;
    }
}
