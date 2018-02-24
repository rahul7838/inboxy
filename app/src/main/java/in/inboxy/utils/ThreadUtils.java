package in.inboxy.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.List;

import in.inboxy.contacts.Contact;
import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;

/**
 * Created by rahul1993 on 2/4/2018.
 */

public class ThreadUtils {

  public static class getUnreadSmsAysnTask extends AsyncTask<Void, Void, Integer> {
    Contact contact;
    Context context;

    getUnreadSmsAysnTask(Contact contact, Context context) {
      this.contact = contact;
      this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
      MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(context);
      Cursor cursor = mDB.messageDao().getUnreadSmsCount(contact.getCategory());
      int countUnreadSMS = cursor.getCount();
      return countUnreadSMS;
    }
  }

  public static class getNotiSummaryAsyncTask extends AsyncTask<Void, Void, List<Message>> {
    Context context;
    int category;

    getNotiSummaryAsyncTask(int category, Context context) {
      this.category = category;
      this.context = context;
    }

    @Override
    protected List<Message> doInBackground(Void... voids) {
      MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(context);
      List<Message> notificationSummary = mDB.messageDao().getNotificationSummary(category);
      return notificationSummary;
    }
  }
}
