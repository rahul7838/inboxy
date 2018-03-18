package in.smslite.utils;

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
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.services.OTPService;
import in.smslite.services.SwipeToDismissNoti;


public class NotificationUtils {

  private static final int NID = 122;
  private static final int priID = 123;
  private static final int finID = 124;
  private static final int proID = 125;
  private static final int updID = 126;
  static SharedPreferences sharedPreferences;
  static Boolean vibrate = false;
  static Boolean led = false;
  static Boolean sound = false;
  static Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  public static final int CUSTOM_NOTIFICATION_ID = 7898;
  public static final String BUNDLE_OTP_KEY = "159";


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

    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    vibrate = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_vibration), false);
    led = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_LED), false);
    sound = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_sound), false);
    Set<String> defValues = new HashSet<String>();
    defValues.add("Promotion");
    defValues.add("Finance");
    defValues.add("Primary");
    defValues.add("Updates");
    Set<String> set = sharedPreferences.getStringSet(context.getString(R.string.pref_key_category), defValues);
    Log.i("Notification", Integer.toString(set.size()));
    String category1 = listCategory.get(category);
    Intent swipeToDismissNotiIntent = new Intent(context, SwipeToDismissNoti.class);
    swipeToDismissNotiIntent.putExtra("Category", category1);
    PendingIntent swipeToDismissNotiPendingIntent = PendingIntent.getService(context,12,swipeToDismissNotiIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    if (set.contains(category1)) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder
              .setAutoCancel(true)
              .setCategory(Notification.CATEGORY_MESSAGE)
              .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
              .setSmallIcon(R.drawable.ic_stat_name)
              .setLargeIcon(drawable.getBitmap())
              .setDeleteIntent(swipeToDismissNotiPendingIntent);
      if (vibrate) {
        builder.setVibrate(new long[]{300, 300, 300, 300});
      }

      if (led) {
        builder.setLights(Color.WHITE,300,1000);
      }

      if (sound) {
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

  public static void sendCustomNotification(Context context, String address, String body, Long timeStamp){
    String textString = body;
    String OTP = null;
    textString = textString.replaceAll("[Rr]{1}[Ss]{1}[.]{1}[\\s]?[0-9]*\\.[0-9]*","");
    textString= textString.replaceAll("[Ii]{1}[Nn]{1}[Rr]{1}[\\s]?[0-9]*\\.[0-9]*", "");
    Pattern p = Pattern.compile("[0-9]{6}|[0-9]{8}|[0-9]{4}");
    Matcher m = p.matcher(textString);
    if(m.find()){
        OTP = m.group();
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    Date date = new Date(timeStamp);
    String prettyTime = TimeUtils.getPrettyElapsedTime(date);


    RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_noti);
    notificationLayout.setTextViewText(R.id.custom_noti_title, address);
    notificationLayout.setTextViewText(R.id.custom_noti_text, OTP);
    Intent intent = new Intent(context, OTPService.class);
    intent.putExtra(BUNDLE_OTP_KEY, OTP);
    PendingIntent pendingIntent = PendingIntent.getService(context,12, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    notificationLayout.setOnClickPendingIntent(R.id.relative_layout_copy, pendingIntent);

    if (OTP != null) {
      OTP =OTP.replaceAll("", "  ").trim();
    }
    RemoteViews bigNotificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_big);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_title, address);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_OTP, OTP);
    bigNotificationLayout.setOnClickPendingIntent(R.id.layout_big_noti_child2, pendingIntent);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_time, prettyTime);

    NotificationCompat.Builder customNotification = new NotificationCompat.Builder(context);
          customNotification
                  .setSmallIcon(R.drawable.ic_stat_name)
                  .setCustomContentView(notificationLayout)
                  .setCustomBigContentView(bigNotificationLayout);
    if (vibrate) {
      customNotification.setVibrate(new long[]{300, 300, 300, 300});
    }

    if (led) {
      customNotification.setLights(Color.WHITE,300,1000);
    }

    if(sound){
      customNotification.setSound(uri);
    }


    notificationManager.notify(CUSTOM_NOTIFICATION_ID,customNotification.build());
  }
}

