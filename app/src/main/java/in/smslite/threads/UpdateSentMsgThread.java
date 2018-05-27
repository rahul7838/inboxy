package in.smslite.threads;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.Telephony;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.MessageUtils;
import in.smslite.activity.MainActivity;

import static in.smslite.activity.MainActivity.localMessageDbViewModel;

/**
 * Thread to update the sent message when inboxy was not default sms app
 * <p>
 * Created by rahul1993 on 5/4/2018.
 */

public class UpdateSentMsgThread extends Thread {
  private static final String TAG = UpdateSentMsgThread.class.getSimpleName();
  private Context mContext;
//  int cursorLastCountValue;

  public UpdateSentMsgThread(Context context) {
    this.mContext = context;
  }

  @Override
  public void run() {
    super.run();
    if (MessageUtils.checkIfDefaultSms(mContext)) {
      Answers.getInstance().logCustom(new CustomEvent("Default SMS app")
          .putCustomAttribute("Manufacturer", Build.MANUFACTURER)
          .putCustomAttribute("Version", Build.VERSION.CODENAME));
    } else {
      Answers.getInstance().logCustom(new CustomEvent("Not Default SMS app")
          .putCustomAttribute("Manufacturer", Build.MANUFACTURER)
          .putCustomAttribute("Version", Build.VERSION.CODENAME));
    }

    String timeStampLocalDb = null;

    try (Cursor localDbcur = localMessageDbViewModel.getSentSmsCount()) {
      int localDbCount = localDbcur.getCount(); //localDbcur can be null
      if (localDbCount != 0) {
        Log.d(TAG, Integer.toString(localDbCount) + "localDb");
        localDbcur.moveToFirst();
        timeStampLocalDb = localDbcur.getString(localDbcur.getColumnIndex("timestamp"));
        Log.d(TAG, timeStampLocalDb + "localDbTimeStamp");
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      Crashlytics.logException(e);
      Thread.currentThread().interrupt();
    }


    String[] projection = new String[]{Telephony.TextBasedSmsColumns.ADDRESS,
        Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.DATE};
    //      cursor.setNotificationUri(mContext.getContentResolver(), Telephony.Sms.Sent.CONTENT_URI);
    Message message = new Message();

    try (Cursor cursor = mContext.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, projection,
        null, null, Telephony.TextBasedSmsColumns.DATE + " DESC")) {
      int size = cursor.getCount();
      int dateIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE);
      int addressIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS);
      int bodyIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY);
      if (size != 0) {
        Log.d(TAG, Integer.toString(size) + "contentProvider");
//          int newRow = size - localDbCount;
        cursor.moveToFirst();
        do {
          String time = cursor.getString(dateIndex);
          if (Long.parseLong(time) > Long.parseLong(timeStampLocalDb)) {
            String address = cursor.getString(addressIndex);
            String body = cursor.getString(bodyIndex);
            Contact contact = ContactUtils.getContact(address, mContext, false);
            address = ContactUtils.normalizeNumber(address);
            message.address = address;
            message.seen = true;
            message.read = true;
            message.body = body;
            message.timestamp = Long.parseLong(time);
            message.threadId = 0; // TODO retrive the correct threadId
            message.type = Message.MessageType.SENT;
            message.category = contact.getCategory();
            localMessageDbViewModel.insertMessage(message);
            Log.d(TAG, "time>timeStampLocal");
            Log.d(TAG, address + body + time);
          } else {
            break;
          }
        } while (cursor.moveToNext());
      }
    } catch (NullPointerException | NumberFormatException e) {
      e.printStackTrace();

    }
  }
}


