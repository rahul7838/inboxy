package in.smslite.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.annotation.Nullable;

import in.smslite.R;

/**
 * Created by rahul1993 on 1/7/2018.
 */


public class ComposeSmsActivity extends AppCompatActivity {
  //  static List<Contact> mContactList;
  Context context;
  //
  // @BindView(R.id.compose_sms_recycle_view)
//  static RecyclerView recyclerView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getApplicationContext();
//    new ContactAsyncTask().execute(context);
    setContentView(R.layout.activity_contact_sms);
    setToolbar();



    Button button = findViewById(R.id.send_sms_to_select_button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EditText editView  =  findViewById(R.id.send_sms_to_id);
        String address = editView.getText().toString();
        Intent intent = new Intent(getApplicationContext(), CompleteSmsActivity.class);
        intent.putExtra(getString(R.string.address_id), address);
        startActivity(intent);
      }
    });

//    recyclerView = (RecyclerView) findViewById(R.id.compose_sms_recycle_view);/
//    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//    recyclerView.setLayoutManager(linearLayoutManager);
//    recyclerView.setHasFixedSize(true);
//    ComposeAdapter adapter = new ComposeAdapter();
//    recyclerView.setAdapter(adapter);

  }

  private void setToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
    toolbar.setTitle(R.string.title_activity_contact);
    setSupportActionBar(toolbar);
  }
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
      ComposeAdapter adapter = new ComposeAdapter(contactList);
      recyclerView.setAdapter(adapter);
    }
  }*//*

}
*/
