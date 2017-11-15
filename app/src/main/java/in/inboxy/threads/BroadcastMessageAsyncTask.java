package in.inboxy.threads;

import android.content.Context;
import android.os.AsyncTask;

import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;


/**
 * Created by rahul1993 on 11/12/2017.
 */

public class BroadcastMessageAsyncTask extends AsyncTask<Void, Void, Void> {
  Context context;
  Message message;
  public BroadcastMessageAsyncTask(Context context, Message message){
    this.context = context;
    this.message = message;
  }
  @Override
  protected Void doInBackground(Void... voids) {
    MessageDatabase db = MessageDatabase.getInMemoryDatabase(context);
    db.messageDao().insertMessage(message);
    return null;
  }
}
