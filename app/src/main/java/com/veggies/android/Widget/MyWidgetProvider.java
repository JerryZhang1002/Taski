package com.veggies.android.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.veggies.android.todoList.R;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "EXTRA ITEM";

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_listview);

        Intent remoteViewIntent = new Intent(context, WidgetService.class);
        remoteViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //need this....
        remoteViewIntent.setData(Uri.parse(remoteViewIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //set listview adapter
        remoteViews.setRemoteAdapter(appWidgetId, R.id.lvWidget, remoteViewIntent);

        //default empty view
        remoteViews.setEmptyView(R.id.lvWidget, R.id.empty_view);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

