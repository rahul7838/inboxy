package in.inboxy.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import in.inboxy.Adapter.CompleteSmsAdapter;
import in.inboxy.R;
import in.inboxy.db.Message;
import in.inboxy.viewModel.CompleteSmsActivityViewModel;
import me.everything.providers.android.contacts.Contact;

public class CompleteSmsActivity extends AppCompatActivity {
  private static final String TAG = CompleteSmsActivity.class.getSimpleName();
  private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
  private Contact contact;
  private View coView;
  public static String address;
  RecyclerView  completeSmsRecycleView;
  CompleteSmsActivityViewModel completeSmsActivityViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getIntent().getExtras();
    address = bundle.getString(getString(R.string.address_id));
//    contact = ContactUtils.getContact(address, this, true);
    completeSmsActivityViewModel = ViewModelProviders.of(this).get(CompleteSmsActivityViewModel.class);
//    ButterKnife.bind(this);
    subscribeUi();
  }

  public void subscribeUi() {
    completeSmsActivityViewModel.messageListByAddress.observe(this, new Observer<List<Message>>() {
      @Override
      public void onChanged(@Nullable List<Message> messages) {
        showUi(messages);
      }
    });
  }

  public void showUi(List<Message> messages) {
    setContentView(R.layout.activity_sms_complete);
    completeSmsRecycleView = (RecyclerView) findViewById(R.id.complete_sms_recycle_view);
    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    llm.setStackFromEnd(true);

    CompleteSmsAdapter completeSmsAdapter = new CompleteSmsAdapter(messages);


//    setToolbar();
//    coView = findViewById(R.id.coordinator_layout);
    completeSmsRecycleView.setLayoutManager(llm);
    completeSmsRecycleView.setHasFixedSize(true);
    completeSmsRecycleView.setAdapter(completeSmsAdapter);
  }
}

 /* public void setToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setDisplayHomeAsUpEnabled(true);
    }
    setTitle(contact.getDisplayName());
  }*/

/*
  public List<Message> getSmsList(String address) {

    return MessageDatabase.messageDao().getMessageListByAddress();
  }
*/

  /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.activity_completesms, menu);
    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.call_icon) {
      checkCallPermission();
    } else if(id == R.id.contact_details) {
      if (Contact.Source.PHONE.equals(contact.getSource()) && !contact.getNumber().equals(contact.getDisplayName())) {
        PhoneContact phoneContact = (PhoneContact) contact;
        Log.i(TAG, phoneContact.toString());
        ContactsContract.QuickContact.showQuickContact(CompleteSmsActivity.this, coView,
                phoneContact.getUri(),
                ContactsContract.QuickContact.MODE_LARGE, null);
      } else {
        Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                Uri.fromParts("tel", contact.getNumber(), null));
        CompleteSmsActivity.this.startActivity(intent);
      }
    } else if (id == android.R.id.home) {
      onBackPressed();
    }
    return true;
  }*/

 /* public void checkCallPermission() {
    int permissionCheckCall = ContextCompat.checkSelfPermission(CompleteSmsActivity.this,
            Manifest.permission.CALL_PHONE);
    if (permissionCheckCall != PackageManager.PERMISSION_GRANTED) {
      requestPermissions();
    } else {
      performCalling();
    }
  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(CompleteSmsActivity.this,
            new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          performCalling();
        } else {
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            showDialogOK("Phone permission required for this app to perform this action", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                    checkCallPermission();
                    break;
                  case DialogInterface.BUTTON_NEGATIVE:
                }
              }
            });
          } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
          }
        }
      }
    }
  }


  private void showGotoSettings() {
    // TODO: Should directly open app settings page
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CompleteSmsActivity.this);
    alertDialogBuilder.setTitle("Click ok to Exit");
    alertDialogBuilder.setMessage("Go to settings --> Apps and enable permissions");
    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
      }
    })
            .create()
            .show();
  }

  private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(CompleteSmsActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show();
  }

  private void performCalling() {
    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
    startActivity(callIntent);
  }*/










