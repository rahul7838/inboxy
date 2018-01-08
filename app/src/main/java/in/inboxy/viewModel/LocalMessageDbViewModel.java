package in.inboxy.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import in.inboxy.db.Message;
import in.inboxy.db.MessageDatabase;
import me.everything.providers.android.contacts.ContactsProvider;


/**
 * Created by rahul1993 on 11/11/2017.
 */

public class LocalMessageDbViewModel extends AndroidViewModel {
  public LiveData<List<Message>> categoryMsgLiveData;
  public MessageDatabase mDB;

  public LocalMessageDbViewModel(Application application) {
    super(application);
    mDB = MessageDatabase.getInMemoryDatabase(this.getApplication());
//    CDB();
//    messageLiveData = mDB.messageDao().getMessage();
  }

  /*public void CDB(){
//    SharedPreferences sharedPreferences = PreferenceManager
//            .getDefaultSharedPreferences(this.getApplication());
//    if(!sharedPreferences.getBoolean("firstTime", false)){
      new MyAyncTask(mDB, this.getApplication()).execute();

    }
//    SharedPreferences.Editor editor = sharedPreferences.edit();
//    editor.putBoolean("firstTime", true);
//    editor.commit();
//  }*/

  public LiveData<List<Message>> getMessageListByCategory(int category) {
    return categoryMsgLiveData = mDB.messageDao().getMessageListByCategory(category);
  }

  public List<me.everything.providers.android.contacts.Contact> getAllContact(){
    ContactsProvider provider = new ContactsProvider(this.getApplication());
    return provider.getContacts().getList();
  }
}
