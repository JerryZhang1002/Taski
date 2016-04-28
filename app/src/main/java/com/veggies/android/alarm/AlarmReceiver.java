package com.veggies.android.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Xiaobin Lin on 3/18/2016.
 *
 * The AlarmReceiver class
 *
 */
public class AlarmReceiver extends BroadcastReceiver{
    public static final String ALARM_TIME = "alarm time";
    public static final String ALARM_TEXT = "alarm text";

    public AlarmReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        //what to do when alarm timer is up
        long triggerTime = intent.getLongExtra(AlarmReceiver.ALARM_TIME, 0);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(triggerTime);

        String notificationText = "Todo reminder: ";
        notificationText += dateFormat.format(calendar.getTime());

        sendNotification(context, "TO-DO List Reminder", notificationText);
    }

    /**
    * Display notification when alarm timer event is triggered.
    */
    private void sendNotification(Context context, String title, String text){
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.alarm_clock)
                .setContentTitle(title)
                .setContentText(text);

        Intent resultIntent = new Intent(context, AlarmReceiver.class);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * setup a new alarm which will trigger at triggerTime(milliseconds)
     */
    public void setAlarm(Context context, long triggerTime){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        //pass data to display when alarm is triggered
        //these 2 lines need to be placed before creating the pending intent object
        intent.putExtra(AlarmReceiver.ALARM_TIME, triggerTime);
        intent.putExtra(AlarmReceiver.ALARM_TEXT, "test text");

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) triggerTime, intent, PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(triggerTime);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        //Toast.makeText(context, "Alarm Scheduled :" + dateFormat.format(calendar.getTime()),Toast.LENGTH_SHORT).show();
    }
}
