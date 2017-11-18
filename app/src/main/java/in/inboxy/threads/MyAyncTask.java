package in.inboxy.threads;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;
import in.inboxy.utils.MessageUtils;
import me.everything.providers.android.telephony.Sms;

import static in.inboxy.db.Message.MessageType.INBOX;
import static in.inboxy.db.Message.MessageType.SENT;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class MyAyncTask extends AsyncTask<Void,Void,Void> {
    MessageDatabase mDB;
    Application application;
    public MyAyncTask(MessageDatabase mDB, Application application){
      this.mDB = mDB;
      this.application = application;
    }

  @Override
  protected Void doInBackground(Void... voids) {
    List<Sms> list = MessageUtils.getAllMessages(application);
    int listsize =list.size();
//    Log.i("myAsyncTask", Integer.toString(listsize));
    int count = 0;
    Message message = new Message();
    for (int i = 0; i < list.size(); i++) {
      Sms sms = list.get(i);
      Message.MessageType type = Sms.MessageType.SENT.compareTo(sms.type) == 0 ? SENT : INBOX;
      final Long timeStamp = MessageUtils.getTimeStamp(sms.receivedDate,sms.sentDate, type);
      message.body = sms.body;
      message.address = sms.address;
      message.read = sms.read;
      message.seen = sms.seen;
      message.threadId = sms.threadId;
      message.type = type;
      message.timestamp = timeStamp;

      mDB.messageDao().insertMessage(message);
    }

//    Log.i("myAsyncTask", Integer.toString(totalCount));

    return null;
  }
}
