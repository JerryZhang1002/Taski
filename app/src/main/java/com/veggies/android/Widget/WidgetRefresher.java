package com.veggies.android.Widget;


import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.todoList.R;

import java.util.List;

public class WidgetRefresher {
    public static void updateWidget(Activity activity){
        Log.d("Widget", "widget update");

        int[] ids = AppWidgetManager.
                getInstance(activity.getApplication()).
                getAppWidgetIds(new ComponentName(activity.getPackageName(), MyWidgetProvider.class.getName()));
        AppWidgetManager.getInstance(activity.getApplication()).notifyAppWidgetViewDataChanged(ids, R.id.lvWidget);

        /*
        Intent intent = new Intent(activity,MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.
                getInstance(activity.getApplication()).
                getAppWidgetIds(new ComponentName(activity, MyWidgetProvider.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        activity.sendBroadcast(intent);*/

    }
}
