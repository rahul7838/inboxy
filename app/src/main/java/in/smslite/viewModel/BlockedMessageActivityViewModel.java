package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;

/**
 * Created by rahul1993 on 5/31/2018.
 */

public class BlockedMessageActivityViewModel extends AndroidViewModel {
  private MessageDatabase mDB;
  public BlockedMessageActivityViewModel(@NonNull Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(application);
  }

  public void updateCategoryOfMessage(String address, int category, int presentCategory){
    mDB.messageDao().moveToCategory(address, category, presentCategory);
  }

  public LiveData<List<Message>> getBlockedMessage(){
    return mDB.messageDao().getBlockedMessage();
  }
}
