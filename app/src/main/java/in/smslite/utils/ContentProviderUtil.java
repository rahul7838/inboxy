package in.smslite.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import java.util.List;

import in.smslite.db.Message;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.AbstractProvider;

/**
 * Created by rahul1993 on 4/17/2018.
 */

public class ContentProviderUtil {
  private static final String TAG = ContentProviderUtil.class.getSimpleName();

  public static void writeReceivedSms(Message message, String serviceCenterAddress, Context context) {
    ContentValues contentValue = new ContentValues();
    contentValue.put(android.provider.Telephony.TextBasedSmsColumns.ADDRESS, message.getAddress());
    contentValue.put(Telephony.TextBasedSmsColumns.BODY, message.getBody());
    contentValue.put(Telephony.TextBasedSmsColumns.READ, false);
    contentValue.put(Telephony.TextBasedSmsColumns.SEEN, false);
    contentValue.put(Telephony.TextBasedSmsColumns.SERVICE_CENTER, serviceCenterAddress);
    contentValue.put(Telephony.TextBasedSmsColumns.DATE, message.getTimestamp());
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
    contentValue.put(Telephony.TextBasedSmsColumns.DATE, message.getTimestamp());
    contentValue.put(Telephony.TextBasedSmsColumns.TYPE, Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT);
    context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, contentValue);
  }




  }