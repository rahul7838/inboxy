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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.widget.RemoteViews;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.services.OTPService;
import in.smslite.services.SwipeToDismissNoti;

import static in.smslite.activity.MainActivity.MAINACTIVTY_CATEGORY_TASKSTACK_KEY;
import static in.smslite.activity.MainActivity.db;

import static in.smslite.services.OTPService.NOTIFICATION_ID;
import static in.smslite.services.SwipeToDismissNoti.SWIPE_TO_DISMISS_CATEGORY_KEY;


public class NotificationUtils {

  private static Boolean vibrate = true;
  private static Boolean led = true;
  private static Boolean sound = true;
  private static Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  public static final String BUNDLE_OTP_KEY = "notification id key";
  public static final String BROADCAST_SMS_CATEGORY_KEY = "category";
  public static final String NOTIFICATION_BUNDLE_CATEGORY_KEY = "category";
  private static final String GROUP_KEY = "key";
  private static final String TAG = NotificationUtils.class.getSimpleName();
  public static Bundle notificationBundle = new Bundle();

  public static void sendGroupedNotification(Context context, Contact contact, Message message) {

    String displayName = contact.getDisplayName();
    RoundedBitmapDrawable drawable = contact.getAvatar(context);
    int category = contact.getCategory();
    int timeStamp = (int) message.timestamp;

//    ArrayList<Integer> listCategoryIntValue = new ArrayList<Integer>(Arrays.asList(Contact.UNCATEGORIZED, Contact.PRIMARY, Contact.FINANCE, Contact.PROMOTIONS, Contact.UPDATES));
//    ArrayList<Integer> listId = new ArrayList<>(Arrays.asList(0, priID, finID, proID, updID));
    List<String> listCategoryStringValue = new ArrayList<String>(Arrays.asList("0", "Primary", "Finance", "Promotion", "Updates"));

    Cursor cursor = db.messageDao().getUnseenSmsCount(contact.getCategory());
    int countUnseenSMS = cursor.getCount();
    Log.i(TAG, Integer.toString(countUnseenSMS) + " countUnseenSMS");

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    vibrate = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_vibration), true);
    led = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_LED), true);

    sound = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_sound), true);
    Set<String> defValues = new HashSet<String>();
    defValues.add("Primary");
    defValues.add("Finance");
    defValues.add("Promotion");
    defValues.add("Updates");
    Set<String> set = sharedPreferences.getStringSet(context.getString(R.string.pref_key_category), defValues);
    Log.i(TAG, Integer.toString(set.size())+ "setSize");
    String categoryStringValue = listCategoryStringValue.get(category);

    // swipe to dismiss notification makes seen=1(true)
    Intent swipeToDismissNotiIntent = new Intent(context, SwipeToDismissNoti.class);
    swipeToDismissNotiIntent.putExtra(SWIPE_TO_DISMISS_CATEGORY_KEY, category);
    PendingIntent swipeToDismissNotiPendingIntent = PendingIntent.getService(context, 12, swipeToDismissNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    if (set.contains(categoryStringValue)) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder
          .setAutoCancel(true)
          .setCategory(Notification.CATEGORY_MESSAGE)
          .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
          .setSmallIcon(R.drawable.ic_stat_name)
          .setLargeIcon(drawable.getBitmap())
          .setPriority(Notification.PRIORITY_HIGH)
          .setDeleteIntent(swipeToDismissNotiPendingIntent);
      if (vibrate) {
        builder.setVibrate(new long[]{300, 300, 300, 300});
      }

      if (led) {
        builder.setLights(Color.WHITE, 300, 1000);
        Log.i(TAG, Boolean.toString(led) + " ledPreferenceValue");
      }

      if (sound) {
        builder.setSound(uri);
      }

      final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      //Mainactivity Pending Intent
      TaskStackBuilder taskStackBuilderMainActivity = TaskStackBuilder.create(context);
      Intent mainActivityintent = new Intent(context, MainActivity.class)
          .putExtra(BROADCAST_SMS_CATEGORY_KEY, category);
      taskStackBuilderMainActivity.addNextIntent(mainActivityintent);
      taskStackBuilderMainActivity.addParentStack(MainActivity.class);
//      PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 12, mainActivityintent, PendingIntent.FLAG_UPDATE_CURRENT);
      PendingIntent mainActivityPendingIntent = taskStackBuilderMainActivity.getPendingIntent(12, PendingIntent.FLAG_UPDATE_CURRENT);

      //Complete Sms Activity Pending Intent
      TaskStackBuilder taskStackBuilderCompleteSmsActivity = TaskStackBuilder.create(context);
      Intent completeSmsActivityintent = new Intent(context, CompleteSmsActivity.class)
          .putExtra(context.getResources().getString(R.string.address_id), contact.getNumber());
      taskStackBuilderCompleteSmsActivity.addNextIntent(mainActivityintent);
      taskStackBuilderCompleteSmsActivity.addNextIntent(completeSmsActivityintent);
      PendingIntent completeSmsActivityPendingIntent = taskStackBuilderCompleteSmsActivity.getPendingIntent(54, PendingIntent.FLAG_UPDATE_CURRENT);

      // Inbox notification style for api 24 and 25 (Nought)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

        builder.setContentTitle(contact.getDisplayName())
            .setContentText(message.body)
            .setContentIntent(completeSmsActivityPendingIntent)
            .setGroup(categoryStringValue)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message.body));

        NotificationCompat.Builder bundleBuilder = new NotificationCompat.Builder(context)
//            .setContentText(notificationSummary.get(0).getAddress() + ": " + notificationSummary.get(0).getBody())
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setGroup(categoryStringValue)
            .setSmallIcon(R.drawable.ic_stat_name)
//            .setSubText(String.valueOf(countUnseenSMS) + " new messages")
            .setContentIntent(mainActivityPendingIntent);
//
        Log.d(TAG, "api 25");
        notificationManager.notify(timeStamp, builder.build());
        notificationManager.notify(category, bundleBuilder.build());

      } else {
        List<Message> notificationSummary = db.messageDao().getNotificationSummary(category);
        int sizeSummary = notificationSummary.size();
        Log.i(TAG, Integer.toString(sizeSummary)+ "sizeSummary");
        if (sizeSummary>1) {
          Log.d(TAG, "inboxStyleSms");
          NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
          for (int i = 0; i < notificationSummary.size() && i < 7; i++) {
            inboxStyle.addLine(notificationSummary.get(i).getAddress() + ":" + notificationSummary.get(i).getBody());
          }
          inboxStyle.setBigContentTitle(notificationSummary.size() + " new " + categoryStringValue + " messages");
          if (notificationSummary.size() > 7) {
            inboxStyle.setSummaryText(Math.abs(notificationSummary.size() - 7) + " more messages");
          }
          Log.i(TAG, "API 22+23");
          builder.setContentText(notificationSummary.get(0).getAddress() + ": " + notificationSummary.get(0).getBody())
              .setContentIntent(mainActivityPendingIntent)
              .setContentTitle(String.valueOf(countUnseenSMS) + " new messages")
              .setStyle(inboxStyle);
        } else {
          Log.d(TAG, "singleSmsDisplay");
          builder.setContentText(message.body)
              .setContentTitle(displayName)
              .setStyle(new NotificationCompat.BigTextStyle()
                  .bigText(message.body))
              .setContentIntent(completeSmsActivityPendingIntent);
        }
        Log.i(TAG, "NotificationExecuted");
        notificationManager.notify(category, builder.build());
      }
    }
  }


  public static void sendCustomNotification(Context context, String address, String body, Long timeStamp, Contact contact) {
    int notiId = (int)System.currentTimeMillis();
    String OTP = getOTPFromString(body);
    String OTPID = OTP;
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    Date date = new Date(timeStamp);
    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
    sdf.setTimeZone(TimeZone.getDefault());
    String formattedDate = sdf.format(date);
    if (OTP != null) {
      OTP = OTP.replaceAll("", "    ").trim();
    }
    RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_noti);
    notificationLayout.setTextViewText(R.id.custom_noti_title, address);
    notificationLayout.setTextViewText(R.id.custom_noti_text, OTP);
    Intent intent = new Intent(context, OTPService.class);
    intent.putExtra(NOTIFICATION_ID, notiId);
    intent.putExtra(BUNDLE_OTP_KEY, OTPID);
    PendingIntent pendingIntent = PendingIntent.getService(context, 12, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    notificationLayout.setOnClickPendingIntent(R.id.relative_layout_copy, pendingIntent);


    RemoteViews bigNotificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_big);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_title, address);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_OTP, OTP);
    bigNotificationLayout.setOnClickPendingIntent(R.id.layout_big_noti_child2, pendingIntent);
    bigNotificationLayout.setTextViewText(R.id.custom_big_noti_time, formattedDate);

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
    Intent intent1 = new Intent(context, CompleteSmsActivity.class)
        .putExtra(context.getResources().getString(R.string.address_id), contact.getNumber());
    Intent mainactivityIntent = new Intent(context, MainActivity.class);
    mainactivityIntent.putExtra(BROADCAST_SMS_CATEGORY_KEY,contact.getCategory());
//        .setAction(Long.toString(System.currentTimeMillis()));
//            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
    taskStackBuilder.addNextIntent(mainactivityIntent);
    taskStackBuilder.addNextIntent(intent1);
    PendingIntent contentPendingIntent = taskStackBuilder.getPendingIntent(15, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder customNotification = new NotificationCompat.Builder(context);
    customNotification
        .setSmallIcon(R.drawable.ic_stat_name)
        .setCustomContentView(notificationLayout)
        .setCustomBigContentView(bigNotificationLayout)
        .setContentIntent(contentPendingIntent)
        .setPriority(Notification.PRIORITY_HIGH)
        .setAutoCancel(true);
    if (vibrate) {
      customNotification.setVibrate(new long[]{300, 300, 300, 300});
    }

    if (led) {
      customNotification.setLights(Color.WHITE, 300, 1000);
    }

    if (sound) {
      customNotification.setSound(uri);
    }
    notificationManager.notify(notiId, customNotification.build());
  }

  public static String  getOTPFromString(String body){
    String textString = body;
    String OTP = null;
    textString = textString.replaceAll("[Rr]{1}[Ss]{1}[.]{1}[\\s]?[0-9]*\\.[0-9]*", "");
    textString = textString.replaceAll("[Ii]{1}[Nn]{1}[Rr]{1}[\\s]?[0-9]*\\.[0-9]*", "");
    textString = textString.replaceAll("[a-zA-Z]{1}[0-9]{4}", "");
    Pattern p = Pattern.compile("[0-9]{6}|[0-9]{8}|[0-9]{4}|[0-9]{3}|[0-9]{5}");
    Matcher m = p.matcher(textString);
    if (m.find()) {
      OTP = m.group();
    }
    return OTP;
  }
}

