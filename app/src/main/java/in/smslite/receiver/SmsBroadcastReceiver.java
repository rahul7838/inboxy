package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.threads.BroadcastMessageAsyncTask;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.ContentProviderUtil;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver{

  @Override
  public void onReceive(Context context, Intent intent) {
//    this.abortBroadcast();
//    this.setResultData(null);
    Log.i("SmsBroadcastReceiver", "Executed");
    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
    StringBuilder bodyText = new StringBuilder();
    String number = "0";
    String body = "";
    PhoneContact.init(context);
    Contact contact = ContactUtils.getContact(messages[0].getDisplayOriginatingAddress(),context,true);
    Message message = new Message();
    Boolean customNotification = false;
    String serviceCenterAddress=null;
    for (SmsMessage sms : messages) {
      bodyText.append(sms.getMessageBody());
      body = bodyText.toString();
      if(contact.getCategory()== Contact.PRIMARY){
        number = ContactUtils.normalizeNumber(sms.getDisplayOriginatingAddress());
      } else {
        number = sms.getDisplayOriginatingAddress();
      }
      message.body = body;
      message.address = number;
      message.read = false;
      message.seen = false;
      message.timestamp = sms.getTimestampMillis();
      message.threadId = 123;
      message.type = Message.MessageType.INBOX;
      message.category = contact.getCategory();
      serviceCenterAddress = sms.getServiceCenterAddress();
//      message.widget = isWidgetMessage(context, message.body);
    }

    new BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);

    //write sms to content provider
    ContentProviderUtil.writeReceivedSms(message, serviceCenterAddress, context);

  }
}
