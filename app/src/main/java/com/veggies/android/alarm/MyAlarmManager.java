package com.veggies.android.alarm;

public class MyAlarmManager {
    private static AlarmReceiver mAlarmReceiver = new AlarmReceiver();

    public static void setAlarmReceiver(AlarmReceiver alarmReceiver){
        mAlarmReceiver = alarmReceiver;
    }

    public static AlarmReceiver getAlarmReceiver(){
        return mAlarmReceiver;
    }
}
