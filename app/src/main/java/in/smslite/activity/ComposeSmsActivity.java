package in.smslite.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import in.smslite.R;
import in.smslite.adapter.ComposeAdapter;
import in.smslite.contacts.PhoneContact;
import in.smslite.contacts.PhoneContactList;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Created by rahul1993 on 1/7/2018.
 */


public class ComposeSmsActivity extends AppCompatActivity {
  private static final String TAG = ComposeSmsActivity.class.getSimpleName();
  //  static List<Contact> mContactList;
  Context context;
  //  @BindView(R.id.compose_sms_recycle_view)
  RecyclerView recyclerView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
//    new ContactAsyncTask().execute(context);
    setContentView(R.layout.activity_contact_sms);
    setToolbar();
    recyclerView =  findViewById(R.id.compose_sms_recycle_view);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setHasFixedSize(true);
//    ContactsProvider provider = new ContactsProvider(this);
//    List<Contact> list = provider.getContacts().getList();
//
    Parcelable[] uris = {ContactsContract.CommonDataKinds.Phone.CONTENT_URI};
    List<PhoneContact> list = PhoneContactList.blockingGetByUris(uris);
    Log.i(TAG, Integer.toString(list.size()));
    ComposeAdapter adapter = new ComposeAdapter(list, this);
    recyclerView.setAdapter(adapter);
//    new ContactAsyncTask().execute(this);
  }

  private void setToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
    toolbar.setTitle(R.string.title_activity_contact);
    setSupportActionBar(toolbar);
  }


//  public class ContactAsyncTask extends AsyncTask<Context, Void, List<Contact>> {
//
//    @Override
//    protected List<Contact> doInBackground(Context... contexts) {
//
//      return list;
//    }
//    @Override
//    protected void onPostExecute(List<Contact> contactList) {
//      super.onPostExecute(contactList);
//
//      ComposeAdapter adapter = new ComposeAdapter(contactList);
//      recyclerView.setAdapter(adapter);
//    }
//  }
}

