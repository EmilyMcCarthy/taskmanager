package com.tutorial.cloudmine;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cloudmine.api.*;
import com.cloudmine.api.rest.AndroidCMWebService;
import com.cloudmine.api.rest.CMWebService;
import com.cloudmine.api.rest.callbacks.CMResponseCallback;
import com.cloudmine.api.rest.callbacks.FileCreationResponseCallback;
import com.cloudmine.api.rest.callbacks.FileLoadCallback;
import com.cloudmine.api.rest.response.CMResponse;
import com.cloudmine.api.rest.response.FileCreationResponse;
import com.cloudmine.api.rest.response.FileLoadResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <br>Copyright CloudMine LLC. All rights reserved<br> See LICENSE file included with SDK for details.
 * CMUser: johnmccarthy
 * Date: 5/29/12, 5:03 PM
 */
public class DetailTaskEditView extends Activity {
    public static final String TAG = "DetailTaskEditView";
    public static final CMGeoPoint CLOUD_MINE_OFFICE = CMGeoPoint.CMGeoPoint(39.958899, -75.15199);
    public static final int LOCATION_DIALOG_ID = 3;
    public static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private final DatePickerDialog.OnDateSetListener DATE_SET_LISTENER = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            updateDate(year, month, day);
        }
    };
    private final TimePickerDialog.OnTimeSetListener TIME_SET_LISTENER = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            updateTime(hour, minute);
        }
    };
    private final CMResponseCallback GO_TO_TASK_VIEW = new CMResponseCallback(){
        public void onCompletion(CMResponse response) {
            goToTaskView();
        }
    };
    private static final DateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");
    private static final DateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final int DATE_DIALOG_ID = 0;
    public static final int TIME_DIALOG_ID = 1;
    private AndroidSimpleCMObject task;
    private CMFile pictureFile;
    private CMWebService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedtask);
        service = AndroidCMWebService.getService().getUserWebService();
        task = getIntent().getParcelableExtra(TaskAdapter.TASK_KEY);
        fillInTaskInformation();
    }

    public void fillInTaskInformation() {
        setIsDone();
        setPriority();
        setTaskName();
        setDueDate();
        setLocation();
        setPicture();
    }

    public void editDueDate(View view) {
        showDialog(DATE_DIALOG_ID);
    }

    public void editDueTime(View view) {
        showDialog(TIME_DIALOG_ID);
    }

    public void editLocation(View view) {
        showDialog(LOCATION_DIALOG_ID);
    }

    public void editImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void setImage(Bitmap photo) {
        ImageView image = (ImageView)findViewById(R.id.image);
        pictureFile = new AndroidCMFile(photo);
        image.setImageBitmap(photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case CAPTURE_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap)data.getExtras().get("data");

                    setImage(photo);
                }
                break;
            default:
                Log.d(TAG, "Unrecognized activity request code: " + requestCode);
        }
    }

    public void delete(View view) {
        service.asyncDeleteObject(task, new CMResponseCallback() {
            @Override
            public void onCompletion(CMResponse response) {
                goToTaskView();
            }

            @Override
            public void onFailure(Throwable error, String message) {
                Log.e(TAG, "Failed deleting detail task");
            }
        });
    }

    public void save(View view) {
        if(pictureFile != null){
            service.asyncUpload(pictureFile, new FileCreationResponseCallback() {
                @Override
                public void onCompletion(FileCreationResponse response) {
                    if (response.was(200, 201)) {
                        String pictureKey = response.getFileName();
                        task.add("picture", pictureKey);
                        service.asyncUpdate(updatedTask(), GO_TO_TASK_VIEW);
                    }
                }
            });
        } else {
            service.asyncUpdate(updatedTask(), GO_TO_TASK_VIEW);
        }
    }

    public void quit(View view) {
        goToTaskView();
    }

    private void goToTaskView() {
        Intent goToTaskListView = new Intent(this, TaskListView.class);
        startActivity(goToTaskListView);
    }

    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, DATE_SET_LISTENER, year(), month(), day());
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, TIME_SET_LISTENER, hour(), minute(), true);
            case LOCATION_DIALOG_ID:
                return locationPicker();
        }
        return null;
    }

    private Dialog locationPicker() {
        AlertDialog.Builder locationPickerBuilder = new AlertDialog.Builder(this);
        locationPickerBuilder.setTitle("Set task location");

        View content = getLayoutInflater().inflate(R.layout.editlocation, (ViewGroup)findViewById(R.id.editLocation));
        locationPickerBuilder.setView(content);

        EditText longitudeText = (EditText)content.findViewById(R.id.longitudeText);
        EditText latitudeText = (EditText)content.findViewById(R.id.latitudeText);

        latitudeText.setText(String.valueOf(location().getLatitude()));
        longitudeText.setText(String.valueOf(location().getLongitude()));

        locationPickerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog locationPicker = (Dialog) dialogInterface;
                EditText longitudeText = (EditText) locationPicker.findViewById(R.id.longitudeText);
                EditText latitudeText = (EditText) locationPicker.findViewById(R.id.latitudeText);
                double longitude = Double.valueOf(longitudeText.getText().toString());
                double latitude = Double.valueOf(latitudeText.getText().toString());
                task.add(TaskAdapter.LOCATION, CMGeoPoint.CMGeoPoint(longitude, latitude));
                setLocation();
            }
        });

        return locationPickerBuilder.create();
    }

    private void updateDate(int year, int month, int day) {
        Date updatedDate = new GregorianCalendar(year, month, day, hour(), minute()).getTime();
        task.add(TaskAdapter.DUE_DATE, updatedDate);
        setDueDate();
    }

    private void updateTime(int hour, int minute) {
        Date updatedDate = new GregorianCalendar(year(), month(), day(), hour, minute).getTime();
        task.add(TaskAdapter.DUE_DATE, updatedDate);
        setDueDate();
    }

    private SimpleCMObject updatedTask() {
        task.setClass(TaskAdapter.TASK_CLASS);
        task.add(TaskAdapter.IS_DONE, isDone());
        task.add(TaskAdapter.TASK_NAME, taskName());
        task.add(TaskAdapter.DUE_DATE, date());
        task.add(TaskAdapter.PRIORITY, priority());
        task.add(TaskAdapter.LOCATION, location());
        return task;
    }

    private int year() {
        return calendar().get(Calendar.YEAR);
    }

    private int month() {
        return calendar().get(Calendar.MONTH);
    }

    private int day() {
        return calendar().get(Calendar.DAY_OF_MONTH);
    }

    private Date date() {
        return task.getDate(TaskAdapter.DUE_DATE, new Date());
    }

    private CMGeoPoint location() {
        return task.getGeoPoint(TaskAdapter.LOCATION, CLOUD_MINE_OFFICE);
    }

    private int hour() {
        return calendar().get(Calendar.HOUR_OF_DAY);
    }

    private int minute() {
        return calendar().get(Calendar.MINUTE);
    }

    private boolean isDone() {
        return getIsDoneComponent().isChecked();
    }

    private String taskName() {
        return getTaskNameComponent().getText().toString();
    }

    private int priority() {
        return getPriorityComponent().getSelectedItemPosition();
    }

    private Calendar calendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date());
        return calendar;
    }

    private void setDueDate() {
        Button dueDate = (Button)findViewById(R.id.dueDate);
        Button dueTime = (Button)findViewById(R.id.dueTime);
        Date dateValue = date();
        dueDate.setText(DISPLAY_DATE_FORMAT.format(dateValue));
        dueTime.setText(DISPLAY_TIME_FORMAT.format(dateValue));
    }

    private void setLocation() {
        Button locationButton = (Button)findViewById(R.id.location);
        CMGeoPoint location = location();

        locationButton.setText(location.getLocationString());
    }

    private void setPicture() {
        String pictureKey = task.getString(TaskAdapter.PICTURE);
        if(pictureKey != null) {
            service.asyncLoadFile(pictureKey, new FileLoadCallback(pictureKey) {
                @Override
                public void onCompletion(FileLoadResponse fileResponse) {
                    if(fileResponse.wasSuccess()) {
                        CMFile file = fileResponse.getFile();
                        byte[] pictureBytes = file.getFileContents();
                        Bitmap asBitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                        setImage(asBitmap);
                    }
                }
            });
        }
    }

    private void setTaskName() {
        EditText taskName = getTaskNameComponent();
        taskName.setText(task.getString(TaskAdapter.TASK_NAME, ""));
    }

    private void setPriority() {
        Spinner priority = getPriorityComponent();
        priority.setSelection(task.getInteger(TaskAdapter.PRIORITY, 0), true);
    }

    private void setIsDone() {
        CheckBox isDone = getIsDoneComponent();
        isDone.setChecked(task.getBoolean(TaskAdapter.IS_DONE, false));
    }

    private EditText getTaskNameComponent() {
        return (EditText)findViewById(R.id.taskName);
    }

    private Spinner getPriorityComponent() {
        return (Spinner)findViewById(R.id.priorityPicker);
    }

    private CheckBox getIsDoneComponent() {
        return (CheckBox)findViewById(R.id.isDone);
    }
}
