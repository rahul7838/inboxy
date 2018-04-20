package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.concurrent.CopyOnWriteArrayList;

import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.threads.BroadcastMessageAsyncTask;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.MessageUtils;

/**
 * Created by rahul1993 on 4/18/2018.
 */
// when Inboxy is not default sms app the message is read by by this receiver

public class SmsReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {

    if (!MessageUtils.checkIfDefaultSms(context)) {
      SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
      StringBuilder bodyText = new StringBuilder();
      String number = "0";
      String body = "";
      PhoneContact.init(context);
      Contact contact = ContactUtils.getContact(messages[0].getDisplayOriginatingAddress(), context, true);
      Message message = new Message();
      Boolean customNotification = false;
      String serviceCenterAddress = null;
      for (SmsMessage sms : messages) {
        bodyText.append(sms.getMessageBody());
        body = bodyText.toString();
        if (contact.getCategory() == Contact.PRIMARY) {
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

    }
  }
}
