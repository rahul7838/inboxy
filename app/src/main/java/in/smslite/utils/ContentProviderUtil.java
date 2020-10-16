package in.smslite.utils;

/**
 * Created by rahul1993 on 4/17/2018.
 */

public class ContentProviderUtil {
  private static final String TAG = ContentProviderUtil.class.getSimpleName();

  /*public static void writeReceivedSms(Message message, String serviceCenterAddress, Context context) {
    ContentValues contentValue = new ContentValues();
    contentValue.put(android.provider.Telephony.TextBasedSmsColumns.ADDRESS, message.getAddress());
    contentValue.put(Telephony.TextBasedSmsColumns.BODY, message.getBody());
    contentValue.put(Telephony.TextBasedSmsColumns.READ, false);
    contentValue.put(Telephony.TextBasedSmsColumns.SEEN, false);
    contentValue.put(Telephony.TextBasedSmsColumns.SERVICE_CENTER, serviceCenterAddress);
    contentValue.put(Telephony.TextBasedSmsColumns.DATE, message.getReceivedDate());
    contentValue.put(Telephony.TextBasedSmsColumns.TYPE, Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX);
    context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValue);
  }

  public static void writeSentSms(Message message, Context context) {
    ContentValues contentValue = new ContentValues();
    contentValue.put(android.provider.Telephony.TextBasedSmsColumns.ADDRESS, message.getAddress());
    contentValue.put(Telephony.TextBasedSmsColumns.BODY, message.getBody());
    contentValue.put(Telephony.TextBasedSmsColumns.READ, true);
    contentValue.put(Telephony.TextBasedSmsColumns.SEEN, true);
//    contentValue.put(Telephony.TextBasedSmsColumns.SERVICE_CENTER, serviceCenterAddress );
    contentValue.put(Telephony.TextBasedSmsColumns.DATE, message.getReceivedDate());
    contentValue.put(Telephony.TextBasedSmsColumns.TYPE, Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT);
    context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, contentValue);
  }

  public static void markAllRead(Context context){
    ContentValues contentValue = new ContentValues();
    contentValue.put(Telephony.TextBasedSmsColumns.READ, 1);
//    contentValue.put(Telephony.TextBasedSmsColumns.SEEN, 1);
    int updatedRows = context.getContentResolver().update(Telephony.Sms.CONTENT_URI, contentValue, null, null);
    Log.d(TAG, updatedRows + " updated rows");
  }*/

  }