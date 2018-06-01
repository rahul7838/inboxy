package in.smslite.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.List;

import in.smslite.activity.CompleteSmsActivity;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;

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
      Cursor cursor = mDB.messageDao().getUnseenSmsCount(contact.getCategory());
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

  public static class UpdateDbNotiClickedThread extends Thread {
    String address;
    public UpdateDbNotiClickedThread(String address){
      this.address =  address;
    }
    @Override
    public void run() {
      super.run();
      CompleteSmsActivity.completeSmsActivityViewModel.markAllRead(address);
    }
  }

  public static class UpdateMessageCategoryToBlocked extends Thread{
    Context context;
    List<Message> selectedItem;
    int category;
    public UpdateMessageCategoryToBlocked(Context context, List<Message> selectedItem, int category){
      this.context = context;
      this.selectedItem = selectedItem;
      this.category = category;
    }
    @Override
    public void run() {
      super.run();
      int length = selectedItem.size();
      MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(context);
      for (int i = 0; i < length; i++) {
        mDB.messageDao().moveToCategory(selectedItem.get(i).getAddress(), category);
      }
    }
  }
}
