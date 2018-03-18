package in.smslite.threads;

import android.content.Context;
import android.os.AsyncTask;

import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.NotificationUtils;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class BroadcastMessageAsyncTask extends AsyncTask<Context, Void, Void> {
//  private Context context;
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
    MessageDatabase db = MessageDatabase.getInMemoryDatabase(contexts[0]);
    db.messageDao().insertMessage(message);
    if(!customNotification) {
      NotificationUtils.sendGroupedNotification(contexts[0], contact, message.body);
    }
    return null;
  }
}
