package in.smslite.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.adapter.CompleteSmsAdapter;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.ContentProviderUtil;
import in.smslite.utils.MessageUtils;
import in.smslite.viewHolder.CompleteSmsSentViewHolder;
import in.smslite.viewModel.CompleteSmsActivityViewModel;

import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static in.smslite.activity.MainActivity.db;

public class CompleteSmsActivity extends AppCompatActivity {
  private static final String TAG = CompleteSmsActivity.class.getSimpleName();
  //  private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
//  private Contact contact;
  private static final int SMS_SEND_INTENT_REQUEST = 100;
  private static final int SMS_DELIVER_INTENT_REQUEST = 101;
  private static final int SEND_TEXT_SMS_REQUEST = 102;
  @BindView(R.id.coordinator_layout)
  View coView;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
  public static String address;
  @BindView(R.id.complete_sms_recycle_view)
  RecyclerView completeSmsRecycleView;
//  @BindView(R.id.reply_sms_edit_text_box_id)
  static EditText editText;
  @BindView(R.id.send_button_id)
  ImageButton imageButton;
  CompleteSmsActivityViewModel completeSmsActivityViewModel;
  public static String phoneNumber;
  static Contact contact;
  private static Context context;
  static Message message;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    db = MessageDatabase.getInMemoryDatabase(context);
    if (getIntent().getData() != null) {
      getDataFromOtherAppIntent();
    } else {
      Bundle bundle = getIntent().getExtras();
      address = bundle.getString(getString(R.string.address_id));
    }
//     Thread to update read and seen field of db when clicking on notification
    UpdateDbNotiClickedThread.start();

    PhoneContact.init(this);
    contact = ContactUtils.getContact(address, this, true);


    if(contact.getCategory() == Contact.PRIMARY) {
      phoneNumber = ContactUtils.normalizeNumber(address);
    } else {
      phoneNumber = contact.getNumber();
    }
    Log.d(TAG, address + phoneNumber);
    completeSmsActivityViewModel = ViewModelProviders.of(this).get(CompleteSmsActivityViewModel.class);
    subscribeUi();
  }

  public void getDataFromOtherAppIntent() {
    Uri uri = getIntent().getData();
    String data = uri.toString();
    Log.d(TAG, data);
    String schema = getIntent().getData().getScheme();
    if (schema.startsWith("smsto") || schema.startsWith("mmsto")) {
      address = data.replace("smsto:", "").replace("mmsto:", "");
    } else {
      address = data.replace("sms:", "").replace("mms:", "");
    }
//      String locale = context.getResources().getConfiguration().locale.getCountry();
    try {
      address = URLDecoder.decode(address, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
//      address = "" + Html.fromHtml(address);
    address = ContactUtils.formatAddress(address);
//      address = PhoneNumberUtils.formatNumber(address, locale);
  }

  private Thread UpdateDbNotiClickedThread = new Thread() {
    @Override
    public void run() {
      super.run();
      db.messageDao().markAllRead(address);
    }
  };


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
    ButterKnife.bind(this);
    editText = (EditText) findViewById(R.id.reply_sms_edit_text_box_id);
    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    llm.setStackFromEnd(true);

    setToolbar();

    CompleteSmsAdapter completeSmsAdapter = new CompleteSmsAdapter(messages);
    completeSmsRecycleView.setLayoutManager(llm);
    completeSmsRecycleView.setHasFixedSize(true);
    completeSmsRecycleView.setAdapter(completeSmsAdapter);

    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendButtonClicked();
      }
    });
  }

  private void sendButtonClicked() {
    if (!Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName())) {
      Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
      intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
      startActivityForResult(intent, SEND_TEXT_SMS_REQUEST);
    } else {
      long l = 0;
      sendTextSms(l);
    }
  }

  public static void sendTextSms(Long time) {
    String msg;
    if(CompleteSmsSentViewHolder.tryFailedSms){
      CompleteSmsSentViewHolder.tryFailedSms = false;
      msg = db.messageDao().getFailedSmsText(time);
      db.messageDao().deleteFailedMsg(time);
      new thread(msg).start();
    } else{
    Editable editableText = editText.getText();
    msg = editableText.toString();
//    if (!msg.isEmpty()) {
      editableText.clear();
//      write sent sms to local database
      new thread(msg).start();
    }
      if(!msg.isEmpty()) {
      Intent sentIntent = new Intent();
      sentIntent.setAction("in.smslite.SEND_SMS_ACTION");
      PendingIntent sentPendingIntent = PendingIntent.
          getBroadcast(context, SMS_SEND_INTENT_REQUEST, sentIntent, 0);

      Intent deliveredIntent = new Intent();
      deliveredIntent.setAction("in.smslite.DELIVERED_SMS_ACTION");
      PendingIntent deliveredPendingIntent = PendingIntent.
          getBroadcast(context, SMS_DELIVER_INTENT_REQUEST, deliveredIntent, 0);

      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber, null, msg, sentPendingIntent, deliveredPendingIntent);
    } else {
      Toast.makeText(context, "Please write some text!", Toast.LENGTH_SHORT).show();
    }
  }

  private static class thread extends Thread {
    String msg;

    thread(String msg) {
      this.msg = msg;
    }

    @Override
    public void run() {
      super.run();
      message = new Message();
      message.address = phoneNumber;
      Log.d(TAG, phoneNumber);
      message.body = msg;
      message.read = true;
      message.seen = true;
      message.category = contact.getCategory();
      message.threadId = 0;
      message.timestamp = System.currentTimeMillis();
      message.type = Message.MessageType.SENT;
      db.messageDao().insertMessage(message);
    }
  }

  ;


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SEND_TEXT_SMS_REQUEST) {
      if (resultCode == RESULT_OK) {
        long l = 0;
        sendTextSms(l);
      }
    }
  }

  private BroadcastReceiver SendSmsBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      switch (getResultCode()) {
        case Activity.RESULT_OK:
//          CompleteSmsSentViewHolder.smsStatusVisiblity("sent");
          Log.d(TAG, "sent sms successful");
//      write sent sms to content provider
          ContentProviderUtil.writeSentSms(message, context);
          break;
        case RESULT_ERROR_NULL_PDU:
//          CompleteSmsSentViewHolder.smsStatusVisiblity("Not sent");
          Log.d(TAG, "null pdu code");
          break;
        default:
          Log.d(TAG, "default code");
          message.type = Message.MessageType.FAILED;
          db.messageDao().updateSentFailedSms(message.timestamp);
          break;
      }
    }
  };


 /* public List<Message> getSmsList(Contact contact) {
    *//*if(contact.getThreadId() != null) {
      return MessageUtils.getConversationListByThreadId(contact.getThreadId());
    }*//*
    return MessageUtils.getConversationListByAddress(contact.getNumber());
  }*/


  public void setToolbar() {
//    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setDisplayHomeAsUpEnabled(true);
    }
    setTitle(contact.getDisplayName());
  }

  /*
    public List<Message> getSmsList(String address) {

      return MessageDatabase.messageDao().getMessageListByAddress();
    }
  */
  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter sentSmsIntentFilter = new IntentFilter();
    sentSmsIntentFilter.addAction("in.smslite.SEND_SMS_ACTION");
    registerReceiver(SendSmsBroadcastReceiver, sentSmsIntentFilter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(SendSmsBroadcastReceiver);
  }

  @Override
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
    } else if (id == R.id.contact_details) {
      if (Contact.Source.PHONE.equals(contact.getSource()) && !contact.getNumber().equals(contact.getDisplayName())) {
        PhoneContact phoneContact = (PhoneContact) contact;
        Log.d(TAG, phoneContact.toString());
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
  }

  public void checkCallPermission() {
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
  }


}










