package in.inboxy.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import in.inboxy.activity.CompleteSmsActivity;
import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class CompleteSmsActivityViewModel extends AndroidViewModel {
    public LiveData<List<Message>> messageListByAddress;
    MessageDatabase mDB;

  public CompleteSmsActivityViewModel(@NonNull Application application) {
    super(application);
    CDB();
    messageListByAddress = mDB.messageDao().getMessageListByAddress(CompleteSmsActivity.phoneNumber);
  }
  public void CDB(){
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
  }
}
