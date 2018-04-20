package in.smslite.utils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDao;
import in.smslite.db.MessageDatabase;
import in.smslite.receiver.SmsReceiver;
import in.smslite.threads.BroadcastMessageAsyncTask;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 4/19/2018.
 */

public class TestUtil {

  private static final String TAG = TestUtil.class.getSimpleName();

//  SmsReceiver smsReceiver = new SmsReceiver();
//  smsReceiver.onReceive();
  public static void TestOTP(Context context) {
    List<Message> msg = db.messageDao().getOTPFOrTest();
    int size = msg.size();
    Message message = new Message();
    for (int i = 0; i < size; i++) {
      Message sms = msg.get(i);
      message.body = sms.body;
      message.address = sms.address;
      message.timestamp = sms.timestamp;
      Intent intent = new Intent();
      intent.setAction("in.smslite.utils.TEST_NOTIFICATION");
      Bundle bundle = new Bundle();
      bundle.putSerializable("sms", (Serializable) message );
      intent.putExtra("bundle", bundle);
      LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
      context.sendBroadcast(intent);
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static BroadcastReceiver testNotiBroadCast = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "testNotiExecuted");
      Bundle bundle =  intent.getBundleExtra("bundle");
      Message sms = (Message) bundle.getSerializable("sms");
      Contact contact = ContactUtils.getContact(sms.getAddress(), context, true);
      Message message = new Message();
      Boolean customNotification = false;
      String number = "";
//        bodyText.append(sms.getMessageBody());
//        body = bodyText.toString();
        if (contact.getCategory() == Contact.PRIMARY) {
          number = ContactUtils.normalizeNumber(sms.getAddress());
        } else {
          number = sms.getAddress();
        }
        message.body = sms.getBody();
        message.address = number;
        message.read = false;
        message.seen = false;
        message.timestamp = 123454;
        message.threadId = 123;
        message.type = Message.MessageType.INBOX;
        message.category = contact.getCategory();


      new BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);

    }
  };
}
