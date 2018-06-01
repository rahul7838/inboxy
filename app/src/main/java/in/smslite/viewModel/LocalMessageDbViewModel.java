package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.InjectionUtils;


/**
 * Created by rahul1993 on 11/11/2017.
 */

public class LocalMessageDbViewModel extends AndroidViewModel {
  private static final String TAG = LocalMessageDbViewModel.class.getSimpleName();
//  private LiveData<List<Message>> categoryMsgLiveData;
  private MessageDatabase mDB;

  public LocalMessageDbViewModel(Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
  }

  public LiveData<List<Message>> getMessageListByCategory(int category) {
    return mDB.messageDao().getMessageListByCategory(category);
  }

  public Cursor getSentSmsCount(){
    return mDB.messageDao().getSentSmsCount();
  }

  public void insertMessage(Message message){
    mDB.messageDao().insertMessage(message);
  }

  public void markAllSeen(int category){
    mDB.messageDao().markAllSeen(category);
  }

  public void markAllRead(String address){
    mDB.messageDao().markAllRead(address);
  }

  public void deleteSelectedConversation(String timeStamp){
    mDB.messageDao().deleteSelectedConversation(timeStamp);
  }

  public String pickContactSelected(Intent data) {
    Log.d(TAG, "contect picked");
    Uri uri = data.getData();
    Log.i(TAG, uri.toString());
    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
    Cursor cursor = getApplication().getContentResolver().query(uri, projection,
        null, null, null);
    cursor.moveToFirst();
    int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
    String number = cursor.getString(columnIndex);
    number = ContactUtils.formatAddress(number);
    Log.i(TAG, number);
    return number;
    }

}
