//package in.smslite.services;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.RemoteViews;
//import android.widget.RemoteViewsService;
//
//import java.util.List;
//
//import in.smslite.R;
//import in.smslite.db.Message;
//import in.smslite.db.MessageDatabase;
//
///**
// * Created by rahul1993 on 3/22/2018.
// */
//
//public class ListWidgetService extends RemoteViewsService {
//  private static final String TAG = ListWidgetService.class.getName();
//
//  @Override
//  public RemoteViewsFactory onGetViewFactory(Intent intent) {
//    return new ListRemoteViewsFactory();
//  }
//
//  class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
//    List<Message> widgetMessage;
//    int widgetMessageSize;
//
//    @Override
//    public void onCreate() {
//    }
//
//      @Override
//      public void onDataSetChanged() {
////          Log.d(TAG, "onDataSetChanged");
////          widgetMessageAsyncTask.execute();
//          MessageDatabase db = MessageDatabase.getInMemoryDatabase(getApplicationContext());
//          widgetMessage = db.messageDao().getWidgetMessage();
//          Log.d(TAG, " onDataSetChangedDone");
//          widgetMessageSize = widgetMessage.size();
//          }
//
//
//    @Override
//    public void onDestroy() {
//    }
//
//    @Override
//    public int getCount() {
//      return widgetMessageSize;
//    }
//
//    @Override
//    public RemoteViews getViewAt(int position) {
//      RemoteViews remoteViews = null;
////        Log.d(TAG, "getViewAtdone");
//        remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_list_item);
//        String body = null;
//        String address = null;
//
//        Message message = widgetMessage.get(position);
//        body = message.getBody();
//        address = message.address;
//
//        remoteViews.setTextViewText(R.id.widget_item_textview_id, body);
//
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtra(getString(R.string.address_id), address);
//        remoteViews.setOnClickFillInIntent(R.id.widget_item_linearlayout, fillInIntent);
//      return remoteViews;
//    }
//
//    @Override
//    public RemoteViews getLoadingView() {
//      RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_loading);
//      return remoteViews;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//
//      return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//      return 0;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//      return false;
//    }
//  }
//}
//
//
