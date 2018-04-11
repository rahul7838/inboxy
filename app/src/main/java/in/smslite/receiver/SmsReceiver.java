package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.threads.BroadcastMessageAsyncTask;
import in.smslite.utils.ContactUtils;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class SmsReceiver extends BroadcastReceiver{

  @Override
  public void onReceive(Context context, Intent intent) {
//    this.abortBroadcast();
//    this.setResultData(null);
    Log.i("SmsReceiver", "Executed");
    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
    StringBuilder bodyText = new StringBuilder();
    String number = "0";
    String body = "";
    PhoneContact.init(context);
    Contact contact = ContactUtils.getContact(messages[0].getDisplayOriginatingAddress(),context,true);
    Message message = new Message();
    Boolean customNotification = false;
    for (SmsMessage sms : messages) {
      bodyText.append(sms.getMessageBody());
      body = bodyText.toString();
      number = sms.getDisplayOriginatingAddress();
      message.body = body;
      message.address = number;
      message.read = false;
      message.seen = false;
      message.timestamp = sms.getTimestampMillis();
      message.threadId = 123;
      message.type = Message.MessageType.INBOX;
      message.category = contact.getCategory();
//      message.widget = isWidgetMessage(context, message.body);
    }

    new BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
  }
}
