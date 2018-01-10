package in.inboxy.threads;

import android.content.Context;
import android.os.AsyncTask;

import in.inboxy.contacts.Contact;
import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;
import in.inboxy.utils.NotificationUtils;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class BroadcastMessageAsyncTask extends AsyncTask<Context, Void, Void> {
//  private Context context;
  private Message message;
  private Contact contact;
  public BroadcastMessageAsyncTask(Message message, Contact contact){
    this.message = message;
    this.contact = contact;
  }

  @Override
  protected Void doInBackground(Context... contexts) {
    MessageDatabase db = MessageDatabase.getInMemoryDatabase(contexts[0]);
    db.messageDao().insertMessage(message);
    NotificationUtils.sendGroupedNotification(contexts[0], contact, message.body);
    return null;
  }
}
