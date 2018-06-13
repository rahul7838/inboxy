package in.smslite.viewModel;

import android.annotation.TargetApi;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;

import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.ContactUtils;

import static android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class CompleteSmsActivityViewModel extends AndroidViewModel {
  private MessageDatabase mDB;
  private boolean isDualSim;


  public CompleteSmsActivityViewModel(@NonNull Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
  }

  public LiveData<List<Message>> getMessageListByAddress(String address, int category) {
    return mDB.messageDao().getMessageListByAddress(address, category);
  }

  public void markAllRead(String address) {
    mDB.messageDao().markAllReadByAddress(address);
  }

  public void deleteFailedMsg(Long time) {
    mDB.messageDao().deleteFailedMsg(time);
  }

  public int findFutureCategory(String address){
    int futureCategory = mDB.messageDao().findCategory(address);
    return futureCategory;
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

//  @TargetApi(22)
//  public List<SubscriptionInfo> getTelephonyInfo(){
//    List<SubscriptionInfo> list = subscriptionManager.getActiveSubscriptionInfoList();
//    int dualSim = subscriptionManager.getActiveSubscriptionInfoCountMax();
//    if(dualSim == 2){
//      isDualSim = true;
//    }
//    return list;
////    SubscriptionInfo sub1 = list.get(0);
//////          CharSequence charSequence = SubscriptionInfo.getDisplayName();
////    SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(sub1.getSubscriptionId());
//  }

//  public boolean getIsDualSim(){
//    return isDualSim;
//  }
//
//  public List<String> getNetworkOperatorName(){
//
//  }
}