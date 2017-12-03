package in.inboxy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import in.inboxy.contacts.Contact;
import in.inboxy.db.Message;
import in.inboxy.threads.BroadcastMessageAsyncTask;
import in.inboxy.utils.ContactUtils;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class SmsReceiver extends BroadcastReceiver{

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i("SmsReceiver", "Executed");
    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
    StringBuilder bodyText = new StringBuilder();
    String number = "0";
    Contact contact = ContactUtils.getContact(messages[0].getDisplayOriginatingAddress(),context,true);
    Message message = new Message();

    for (SmsMessage sms : messages) {
      bodyText.append(sms.getMessageBody());
      String body = bodyText.toString();
      number = sms.getDisplayOriginatingAddress();
      message.body = body;
      message.address = number;
      message.read = false;
      message.seen = false;
      message.timestamp = sms.getTimestampMillis();
      message.threadId = 123;
      message.type = Message.MessageType.INBOX;
      message.category = contact.getCategory();
    }
    new BroadcastMessageAsyncTask(context, message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}
