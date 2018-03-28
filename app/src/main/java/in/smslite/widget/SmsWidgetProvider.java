package in.smslite.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.services.ListWidgetService;

/**
 * Created by rahul1993 on 3/22/2018.
 */

public class SmsWidgetProvider extends AppWidgetProvider {
  public static final String TOAST_ACTION = "com.example.android.smsWidget.TOAST_ACTION";
  private static final String TAG = SmsWidgetProvider.class.getSimpleName();
  public HashMap temporaryDb;
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//    super.onUpdate(context, appWidgetManager, appWidgetIds);
//    int[] ids = appWidgetManager.getAppWidgetIds();

    // when you dont have app widget with collection and u want to perform long operation simply perform it in intentService
   /* Intent intent = new Intent(context, WidgetService.class);
    intent.putExtra("ids", appWidgetIds);
    context.startService(intent);*/
      int appWidgetIdsSize = appWidgetIds.length;
      for(int i=0; i<appWidgetIdsSize; i++){

      Intent intent = new Intent(context, ListWidgetService.class);
//      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
//      remoteViews.setTextColor(R.id.widget_list_id, context.getResources().getColor(R.color.));

      remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_id,intent);
      remoteViews.setEmptyView(R.id.widget_list_id,R.id.widget_empty_view);

      //open Mainactivity when clicked on widget label
        Intent mainactivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainactivityPendingIntent = PendingIntent.getActivity(context, 701, mainactivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_label, mainactivityPendingIntent);


      // set the pending intent template for the list view.
      Intent activityIntent = new Intent(context, CompleteSmsActivity.class);
      PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setPendingIntentTemplate(R.id.widget_list_id, activityPendingIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_list_id);
        Log.i(TAG, " onUpdate");
      appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
    }

//    super.onUpdate(context,appWidgetManager,appWidgetIds);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    Log.i(TAG, " Widget received broadcast of sms");
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    ComponentName provider = new ComponentName(context.getPackageName(), SmsWidgetProvider.class.getName());
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
    onUpdate(context,appWidgetManager, appWidgetIds);
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);

  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);

  }

  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
    Log.i(TAG,"OnDisabled");
  }
}
