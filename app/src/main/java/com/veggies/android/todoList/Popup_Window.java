package com.veggies.android.todoList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.veggies.android.Widget.WidgetRefresher;
import com.veggies.android.alarm.MyAlarmManager;
import com.veggies.android.backup.GoogleAPIHandler;
import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.testHelper.AppLog;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Popup_Window extends AppCompatActivity {
    private Activity mActivity;
    private static final int SETDATE_DIALOG = 0;
    private Calendar calendar = Calendar.getInstance();
    private DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    private Button add;
    private Button delete;
    private EditText title;
    private EditText description;
    private CheckBox isCompleted;
    private TextView editTime;
    private Spinner spinner;
    private ImageButton img_btn_record;
    private ImageButton img_btn_play;
    private int spinnerPos = 0;
    private int id = -1;

    /*attr for autio*/
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "taski_audio";
    private static final String AUDIO_PATH_INIT = "placeholder";
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};
    private String audioPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup__window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout
                ((int) (dm.widthPixels * 0.8),
                        (int) (dm.heightPixels * 0.8));

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        title = (EditText) findViewById(R.id.todo_title);
        description = (EditText) findViewById(R.id.todo_description);
        isCompleted = (CheckBox) findViewById(R.id.completed);
        editTime = (TextView) findViewById(R.id.todo_date);
        calendar.set(Calendar.SECOND, 0);
        editTime.setText(dateFormat.format(calendar.getTime()));
        add = (Button) findViewById(R.id.add_list);
        delete = (Button) findViewById(R.id.delete_list);
        spinner = (Spinner) findViewById(R.id.tasklist_spinner);
        img_btn_record = (ImageButton) findViewById(R.id.img_btn_audio_record);
        img_btn_play = (ImageButton) findViewById(R.id.img_btn_audio_play);
        audioPath = new String(AUDIO_PATH_INIT);

        if (bundle != null) {
            title.setText(bundle.getString(ToDoItem.TODO_TITLE));
            description.setText(bundle.getString(ToDoItem.TODO_DESCRIPTION));
            editTime.setText(bundle.getString(ToDoItem.TODO_DATE));
            int comTmp = bundle.getInt(ToDoItem.TODO_COMPLETE);
            if (comTmp == 1) {
                isCompleted.setChecked(true);
            } else {
                isCompleted.setChecked(false);
            }
            id = bundle.getInt(ToDoItem.TODO_ID);
            spinnerPos = bundle.getInt(ToDoItem.TODO_TYPE);
            audioPath = bundle.getString(ToDoItem.TODO_AUDIO);
        }

        //populate the spinner with options
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tasklists_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerPos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Spinner spinner = (Spinner) parent;
                //Toast.makeText(getApplicationContext(), "xxxx" + spinner.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                spinnerPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String list_title = title.getText().toString();
                String list_desc = description.getText().toString();
                String editTime = dateFormat.format(calendar.getTime());
                int complete = isCompleted.isChecked() ? 1 : 0;
                long timeMillis = calendar.getTimeInMillis();
                Intent intent = getIntent();

                //add or update database
                if (id != -1) {       //update existing todolist
                    ToDoItem toDoItem = new ToDoItem(id, list_title, list_desc, editTime, timeMillis, complete, spinnerPos, audioPath);
                    DBManager.updateToDoList(Popup_Window.this, toDoItem);
                } else {                //add new toDoItem to the database
                    ToDoItem toDoItem = new ToDoItem(0, list_title, list_desc, editTime, timeMillis, complete, spinnerPos, audioPath);
                    DBManager.addToDoList(Popup_Window.this, toDoItem);
                }

                GoogleAPIHandler.backupDB();
                WidgetRefresher.updateWidget(mActivity);
                MyAlarmManager.getAlarmReceiver().setAlarm(v.getRootView().getContext(), calendar.getTimeInMillis());
                Popup_Window.this.setResult(Activity.RESULT_OK, intent);
                Popup_Window.this.finish();
            }
        });

        delete.setOnClickListener( new View.OnClickListener() {
            Intent intent = getIntent();
            @Override
            public void onClick(View v) {
                if (id != -1) {
                    DBManager.deleteToDoList(Popup_Window.this, id);
                }

                GoogleAPIHandler.backupDB();
                WidgetRefresher.updateWidget(mActivity);
                Popup_Window.this.setResult(Activity.RESULT_OK, intent);
                Popup_Window.this.finish();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setDate(v.getRootView());
            }
        });

        img_btn_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        img_btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
    }

    private void setDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                        view.getContext(),
                        myDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void setTime(View view){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                        view.getContext(),
                        myTimeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                        );
        timePickerDialog.show();
    }

    //callback function for time picker
    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker picker, int hh, int mm){
            calendar.set(Calendar.HOUR_OF_DAY, hh);
            calendar.set(Calendar.MINUTE, mm);
            calendar.set(Calendar.SECOND, 00);
            editTime.setText(dateFormat.format(calendar.getTime()));
        }
    };

    //callback function for date picker
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int yy, int mm, int dd) {
            calendar.set(Calendar.DATE, dd);
            calendar.set(Calendar.MONTH, mm);
            calendar.set(Calendar.YEAR, yy);
            setTime(arg0.getRootView());
        }
    };

    //get the file for storing audio file
    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
    }

    private boolean recordingPermission() {
        String permission = "android.permission.RECORD_AUDIO";
        int result  = mActivity.checkCallingOrSelfPermission(permission);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * function for start audio recording
     */
    private void startRecording() {
        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch (RuntimeException e) {
            Toast.makeText(this, "Please set MIC right", Toast.LENGTH_SHORT).show();
        }

        try {
            recorder.setOutputFormat(output_formats[currentFormat]);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            audioPath = getFilename();
            recorder.setOutputFile(audioPath);
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Hold to record", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            audioPath = AUDIO_PATH_INIT;
            Toast.makeText(this, "Record failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            audioPath = AUDIO_PATH_INIT;
            Toast.makeText(this, "Record failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            audioPath = AUDIO_PATH_INIT;
            Toast.makeText(this, "Record failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("audio startRecording()", "Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("audio startRecording()", "Warning: " + what + ", " + extra);
        }
    };

    /**
     * function for stopping the audio recording
     */
    private void stopRecording(){
        if(null != recorder){
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
            catch(IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * function for playing the audio record
     */
    private void play() {
        if (audioPath.equals(AUDIO_PATH_INIT)) {
            Toast.makeText(this, "No audio file available", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(this, "Audio file paht is: " + audioPath, Toast.LENGTH_SHORT).show();
            player = new MediaPlayer();
            try {
                player.setDataSource(audioPath);
                player.prepare();
                player.start();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "play failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Toast.makeText(this, "play failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "play failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this, "play failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.release();
                }
            });
        }
    }
}
