package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;

/**
 * Created by rahul1993 on 6/1/2018.
 */

public class ArchiveMsgViewModel extends AndroidViewModel{
  private MessageDatabase mDB;
  public ArchiveMsgViewModel(@NonNull Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(application);
  }

  public LiveData<List<Message>> getArchiveMessage(){
    return mDB.messageDao().getArchiveMessage();
  }
}
