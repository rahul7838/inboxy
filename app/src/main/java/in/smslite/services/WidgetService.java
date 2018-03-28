package in.smslite.services;

/*
 * Created by rahul1993 on 3/22/2018.
 */

/*public class WidgetService extends IntentService {


  /* * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */

/*  public WidgetService() {
    super("WidgetService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    int[] appWidgetids = new int[5];
    if (intent != null) {
      appWidgetids = intent.getExtras().getIntArray("ids");
    }
    int N = 0;
    if (appWidgetids != null) {
      N = appWidgetids.length;
    }
    for (int i = 0; i < N; i++) {
      int appWidgetId = appWidgetids[i];
      String address = "VM-IRSMSA";
      Intent intentActivity = new Intent(getApplicationContext(), CompleteSmsActivity.class);
      intentActivity.putExtra(getApplication().getResources().getString(R.string.address_id), address);
      PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
      RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.initial_app_widget);
      remoteViews.setOnClickPendingIntent(R.id.app_widget_view, pendingIntent);
      AppWidgetManager appWidgetManager = (AppWidgetManager) getSystemService(getApplication().APPWIDGET_SERVICE);
      assert appWidgetManager != null;
      appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
  }
}*/