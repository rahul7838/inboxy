package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import in.smslite.activity.CompleteSmsActivity;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class CompleteSmsActivityViewModel extends AndroidViewModel {
    public LiveData<List<Message>> messageListByAddress;
    private MessageDatabase mDB;

  public CompleteSmsActivityViewModel(@NonNull Application application) {
    super(application);
    CDB();
    messageListByAddress = mDB.messageDao().getMessageListByAddress(CompleteSmsActivity.address);
  }
  public void CDB(){
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
  }
}
