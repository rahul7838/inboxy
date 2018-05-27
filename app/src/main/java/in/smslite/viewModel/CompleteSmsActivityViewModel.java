package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.common.ArrayListAccumulator;

import java.util.ArrayList;
import java.util.List;

import in.smslite.activity.CompleteSmsActivity;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.ContactUtils;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class CompleteSmsActivityViewModel extends AndroidViewModel {
  private MessageDatabase mDB;

  public CompleteSmsActivityViewModel(@NonNull Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
  }

  public LiveData<List<Message>> getMessageListByAddress(String address) {
    return mDB.messageDao().getMessageListByAddress(address);
  }

  public void markAllRead(String address) {
    mDB.messageDao().markAllRead(address);
  }

  public void deleteFailedMsg(Long time) {
    mDB.messageDao().deleteFailedMsg(time);
  }


  public List<String> queryDataToFindConatact(Intent data) {
    List<String> list = new ArrayList<>();
    Uri uri = data.getData();
    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

    try (Cursor cursor = getApplication().getContentResolver().query(uri, projection,
        null, null, null)) {
      int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
      cursor.moveToFirst();
      String number = cursor.getString(numberIndex);
      String name = cursor.getString(nameIndex);
      number = ContactUtils.formatAddress(number);
      list.add(name);
      list.add(number);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
//    Log.i(TAG, number);
    return list;
  }
}