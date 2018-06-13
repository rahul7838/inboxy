package in.smslite.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;

import java.util.List;

import in.smslite.SMSApplication;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;

/**
 * Created by rahul1993 on 2/4/2018.
 */

public class ThreadUtils {
  private static final String TAG = ThreadUtils.class.getSimpleName();
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

  public static class UpdateMessageCategory extends Thread{
    Context context;
    List<Message> selectedItem;
    int category;
    int presentCategory;
    boolean checked;
    public UpdateMessageCategory(Context context, List<Message> selectedItem, int category, int presentCategory, boolean checked){
      this.context = context;
      this.selectedItem = selectedItem;
      this.category = category;
      this.checked = checked;
      this.presentCategory = presentCategory;
    }
    @Override
    public void run() {
      super.run();
      int length = selectedItem.size();
      MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(context);
      for (int i = 0; i < length; i++) {
        Log.d(TAG, selectedItem.get(i).getAddress());
        mDB.messageDao().moveToCategory(selectedItem.get(i).getAddress(), category, presentCategory);
        if(checked){
          mDB.messageDao().updateSendFutureMessage(selectedItem.get(i).getAddress(), 1);
          mDB.messageDao().updateFutureCategory(selectedItem.get(i).getAddress(), category);
        } else {
//          mDB.messageDao().updateSendFutureMessage(selectedItem.get(i).getAddress(), 0);
        }
      }
    }
  }

  public static class MarkAllReadThread extends Thread{
    MessageDatabase mDB = MessageDatabase.getInMemoryDatabase(SMSApplication.getApplication());
    public MarkAllReadThread(){

    }
    @Override
    public void run() {
      super.run();
//      mDB.messageDao().markAllRead();
      ContentValues contentValues = new ContentValues();
      contentValues.put(Telephony.TextBasedSmsColumns.READ, 1);
      contentValues.put(Telephony.TextBasedSmsColumns.SEEN, 1);
      int updatedRows = SMSApplication.getApplication().getContentResolver().update(Telephony.Sms.CONTENT_URI, contentValues, null, null);
      Log.d(TAG, Integer.toString(updatedRows) + " updated rows");
    }
  }

  public static class cachePrimaryContactName extends Thread{

    public cachePrimaryContactName() {

    }

    @Override
    public void run() {
      super.run();
      PhoneContact.init(SMSApplication.getApplication());
      List<Message> list = MessageDatabase.getInMemoryDatabase(SMSApplication.getApplication())
          .messageDao().getPrimaryMessage();
      int size = list.size();
      for(int i = 0; i<size; i++){
        Contact contact = PhoneContact.get(list.get(i).getAddress(),true);
      }
    }
  }

  /*public static class sendFutureMessage extends Thread{
    String address;
    public sendFutureMessage(String address) {
      this.address = address;
    }

    @Override
    public void run() {
      super.run();
      MessageDatabase.getInMemoryDatabase(SMSApplication.getApplication()).messageDao().sendFutureMessage(address);
    }
  }*/
}
