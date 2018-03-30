package in.smslite.threads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import in.smslite.R;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.NotificationUtils;

import static in.smslite.activity.MainActivity.db;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class BroadcastMessageAsyncTask extends AsyncTask<Context, Void, Void> {
//  private Context context;
  private static final String TAG = BroadcastMessageAsyncTask.class.getName();
  private Message message;
  private Contact contact;
  Boolean customNotification;
  public BroadcastMessageAsyncTask(Message message, Contact contact, Boolean customNotification){
    this.message = message;
    this.contact = contact;
    this.customNotification = customNotification;
  }

  @Override
  protected Void doInBackground(Context... contexts) {
    List<String> OTPKeywords = Arrays.asList(contexts[0].getResources().getStringArray(R.array.OTP_keyword));
    int OTPKeywordsSize = OTPKeywords.size();
    db = MessageDatabase.getInMemoryDatabase(contexts[0]);
    if(message.category != Contact.PRIMARY) {
      for (int i = 0; i < OTPKeywordsSize; i++) {
        if (message.body.toLowerCase().contains(OTPKeywords.get(i))) {
          NotificationUtils.sendCustomNotification(contexts[0], message.address, message.body, message.timestamp, contact);
          customNotification = true;
          db.messageDao().markAllSeen(contact.getCategory());
          break;
        }
      }
    }

    db.messageDao().insertMessage(message);
    Log.i(TAG, "insertMessageDone");
    if(!customNotification) {
      NotificationUtils.sendGroupedNotification(contexts[0], contact, message.body);
    }
    return null;
  }
}
