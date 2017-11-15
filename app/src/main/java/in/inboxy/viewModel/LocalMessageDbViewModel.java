package in.inboxy.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;
import in.inboxy.threads.MyAyncTask;


/**
 * Created by rahul1993 on 11/11/2017.
 */

public class LocalMessageDbViewModel extends AndroidViewModel {

  public LiveData<List<Message>> messageLiveData;
  public MessageDatabase mDB;
  public LocalMessageDbViewModel(Application application){
    super(application);
    CDB();
    messageLiveData = mDB.messageDao().getMessage();
  }

  public void CDB(){
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
    new MyAyncTask(mDB, this.getApplication()).execute();
  }

}
