package in.inboxy.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.inboxy.R;
import in.inboxy.activity.CompleteSmsActivity;
import in.inboxy.activity.MainActivity;
import in.inboxy.contacts.Contact;
import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;


public class NotificationUtils {

  private static final int NID = 122;
  private static final int priID = 123;
  private static final int finID = 124;
  private static final int proID = 125;
  private static final int updID = 126;


  public static void sendGroupedNotification(Context context, Contact contact, final String body) {
    MessageDatabase mDB;
    String displayName = contact.getDisplayName();
    RoundedBitmapDrawable drawable = contact.getAvatar(context);
    int category = contact.getCategory();

    ArrayList<String> listCategory = new ArrayList<String>(Arrays.asList("0", "Primary", "Finance", "Promotion", "Updates"));
    ArrayList<Integer> listId = new ArrayList<>(Arrays.asList(0, priID, finID, proID, updID));
    mDB = MessageDatabase.getInMemoryDatabase(context);
    Cursor cursor = mDB.messageDao().getUnreadSmsCount(contact.getCategory());
    int countUnreadSMS = cursor.getCount();

    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    Set<String> defValues = new HashSet<String>();
    defValues.add("Promotion");
    defValues.add("Finance");
    Set<String> set = sharedPreferences.getStringSet(context.getString(R.string.pref_key_category), defValues);
    String category1 = listCategory.get(category);
    if (set.contains(category1)) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder
              .setAutoCancel(true)
              .setCategory(Notification.CATEGORY_MESSAGE)
              .setColor(ContextCompat.getColor(context, R.color.colorLogo))
              .setSmallIcon(R.drawable.ic_stat_ic_launcher_1)
              .setLargeIcon(drawable.getBitmap());

      if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_vibration), false)) {
        builder.setVibrate(new long[]{300, 300, 300, 300});
      }

      if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_LED), false)) {
        builder.setLights(Color.WHITE,300,1000);
      }

      if (sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_sound), false)) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
      }

      final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//      builder.setContentText("You have " + String.valueOf(countUnreadSMS) + " unread "+ "message");

      TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
      NotificationCompat.InboxStyle inboxStyle= new NotificationCompat.InboxStyle();
      List<Message> notificationSummary = mDB.messageDao().getNotificationSummary(category);

      for(int i=0; i<notificationSummary.size() && i<=7; i++){
        inboxStyle.addLine(notificationSummary.get(i).getAddress()+":"+notificationSummary.get(i).getBody());
      }
      inboxStyle.setBigContentTitle(notificationSummary.size() + " Unread " + listCategory.get(category) + " messages");

      if(notificationSummary.size() > 7){
        inboxStyle.setSummaryText(Math.abs(notificationSummary.size() - 7) + " more messages");
      }

      if(countUnreadSMS > 1) {
        Intent intent = new Intent(context, MainActivity.class)
                .putExtra("passCategory", category);
        mDB.messageDao().markAllSeen(category);
        taskStackBuilder.addNextIntent(intent);
        taskStackBuilder.addParentStack(MainActivity.class);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(NID, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentText("You have " + String.valueOf(countUnreadSMS) + " unread " + listCategory.get(category) + " message")
                .setContentTitle("Unread Messages")
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);
      } else {
        Intent intent1 = new Intent(context, CompleteSmsActivity.class)
                .putExtra(context.getResources().getString(R.string.address_id), contact.getNumber());
        taskStackBuilder.addParentStack(CompleteSmsActivity.class);
        taskStackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent1 = taskStackBuilder.getPendingIntent(NID, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentText(body)
                .setContentTitle(displayName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setContentIntent(pendingIntent1);
      }

      Log.i("NotificationUtils", "NotificationExecuted");
      notificationManager.notify(listId.get(category), builder.build());
    }
  }

  public void accessDbForNoti(final Context context) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(context);
      }
    };
  }
}


