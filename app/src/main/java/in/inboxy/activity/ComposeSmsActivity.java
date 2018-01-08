package in.inboxy.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import in.inboxy.R;

/**
 * Created by rahul1993 on 1/7/2018.
 */

public class ComposeSmsActivity extends AppCompatActivity {
//  static List<Contact> mContactList;
  Context context;
  //  @BindView(R.id.compose_sms_recycle_view)
  static RecyclerView recyclerView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getApplicationContext();
//    new ContactAsyncTask().execute(context);
    setContentView(R.layout.activity_contact_sms);
    setToolbar();
    recyclerView = (RecyclerView) findViewById(R.id.compose_sms_recycle_view);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setHasFixedSize(true);
//    ContactAdapter adapter = new ContactAdapter();
//    recyclerView.setAdapter(adapter);

  }

  private void setToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
    toolbar.setTitle(R.string.title_activity_contact);
    setSupportActionBar(toolbar);
  }


/*  public static class ContactAsyncTask extends AsyncTask<Context, Void, List<Contact>> {

    @Override
    protected List<Contact> doInBackground(Context... contexts) {
      ContactsProvider provider = new ContactsProvider(contexts[0]);
      return provider.getContacts().getList();
    }
    @Override
    protected void onPostExecute(List<Contact> contactList) {
      super.onPostExecute(contactList);
      ContactAdapter adapter = new ContactAdapter(contactList);
      recyclerView.setAdapter(adapter);
    }
  }*/
}
