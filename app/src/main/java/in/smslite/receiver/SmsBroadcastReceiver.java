package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import in.smslite.SMSApplication;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.threads.BroadcastMessageAsyncTask;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.ContentProviderUtil;


/**
 *
 * When the app is default sms app (@link SmsBroadCastReceiver) is called.
 *
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
      message.threadId = 0;
      message.type = Message.MessageType.INBOX;
      int value = MessageDatabase.getInMemoryDatabase(SMSApplication.getApplication()).messageDao().askSendFutureMessage(number);
      if(value == 1){
        message.category = MessageDatabase.getInMemoryDatabase(SMSApplication.getApplication()).messageDao().findCategory(number);
      } else {
        message.category = contact.getCategory();
      }
      serviceCenterAddress = sms.getServiceCenterAddress();
//      message.widget = isWidgetMessage(context, message.body);
    }

    new BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);

    //write sms to content provider
    ContentProviderUtil.writeReceivedSms(message, serviceCenterAddress, context);

  }
}
