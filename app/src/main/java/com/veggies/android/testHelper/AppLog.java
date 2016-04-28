package com.veggies.android.testHelper;

import android.util.Log;

/**
 * Created by JerryCheung on 4/22/16.
 */
public class AppLog {

    public static int logString(String tag, String message) {
        return Log.i(tag, message);
    }
}
