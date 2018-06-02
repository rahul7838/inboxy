package in.smslite.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.smslite.R;
import in.smslite.adapter.CompleteSmsAdapter;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.others.CompleteSmsActivityHelper;
import in.smslite.utils.ConstantUtils;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.ContentProviderUtil;
import in.smslite.utils.MessageUtils;
import in.smslite.utils.ThreadUtils;
import in.smslite.viewHolder.CompleteSmsSentViewHolder;
import in.smslite.viewModel.CompleteSmsActivityViewModel;

import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static in.smslite.activity.MainActivity.db;

public class CompleteSmsActivity extends AppCompatActivity {
  private static final String TAG = CompleteSmsActivity.class.getSimpleName();
  @BindView(R.id.coordinator_layout)
  View coView;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.send_button_id)
  ImageButton imageButton;
  @BindView(R.id.complete_sms_recycle_view)
  RecyclerView completeSmsRecycleView;
  @BindView(R.id.complete_sms_attach_id)
  ImageButton attachContactButton;
  private static final int SMS_SEND_INTENT_REQUEST = 100;
  private static final int SMS_DELIVER_INTENT_REQUEST = 101;
  private static final int SEND_TEXT_SMS_REQUEST = 102;
  static EditText editText;
  static Contact contact;
  private static Context context;
  public static Message message;
  public static Long timeStampForBroadCast;
  private LinearLayoutManager llm;
  final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
  private final int MY_PERMISSION_REQUEST_READ_PHONE_STATE = 2;
  public  String address;
  public static CompleteSmsActivityViewModel completeSmsActivityViewModel;
  private LiveData<List<Message>> messageListByAddress;
  private Observer<List<Message>> observer;
  public static List<Message> selectedItem = new ArrayList<>();
  public static List<Message> listOfItem = new ArrayList<>();
  public static Activity activity;
  public static CompleteSmsAdapter completeSmsAdapter;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    activity = this;
//    completeSmsActivityViewModel = ViewModelProviders.of(this).get(CompleteSmsActivityViewModel.class);
//    db = MessageDatabase.getInMemoryDatabase(context);
    if (getIntent().getData() != null) {
      getDataFromOtherAppIntent();
    } else {
      Bundle bundle = getIntent().getExtras();
      address = bundle.getString(getString(R.string.address_id));
    }
//     Thread to update read and seen field of db when clicking on notification


    PhoneContact.init(this);
    contact = ContactUtils.getContact(address, this, true);


    if (contact.getCategory() == Contact.PRIMARY) {
      address = ContactUtils.normalizeNumber(address);
    } else {
      address = contact.getNumber();
    }
    Log.d(TAG, address + address);
    completeSmsActivityViewModel = ViewModelProviders.of(this).get(CompleteSmsActivityViewModel.class);
    new ThreadUtils.UpdateDbNotiClickedThread(address).run();
//    setUpUi();
//    subscribeUi();
    setContentView(R.layout.activity_sms_complete);

    ButterKnife.bind(this);
    editText = (EditText) findViewById(R.id.reply_sms_edit_text_box_id);
    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    llm.setStackFromEnd(true);
    completeSmsRecycleView.setLayoutManager(llm);
    completeSmsRecycleView.setHasFixedSize(true);
    setToolbar();
    sendButtonClickListener();
    CompleteSmsActivityHelper.contextualActionMode(completeSmsRecycleView, context);
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

  @OnClick(R.id.complete_sms_attach_id)
  public void onClickAttachContact() {
    if (!MessageUtils.checkIfDefaultSms(context)) {
      Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
      intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
      startActivityForResult(intent, ConstantUtils.NOT_DEFAULT_SMS_APP);
    } else {
      attachContact();
    }
  }

  private void attachContact(){
    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
    startActivityForResult(pickContactIntent, ConstantUtils.ON_ATTACH_CONTACT_CLICK);
  }

  private void addTextToMessage(Intent data){
    List<String> nameAndNumber = completeSmsActivityViewModel.queryDataToFindConatact(data);
    String name = nameAndNumber.get(0);
    String number = nameAndNumber.get(1);
    String contact = "Name: "+ name + "\n" + "Phone: " + number;
    editText.setText(contact);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SEND_TEXT_SMS_REQUEST) {
      if (resultCode == RESULT_OK) {
        long l = 0;
        sendTextSms(l, address);
      }
    }
    if(requestCode == ConstantUtils.NOT_DEFAULT_SMS_APP){
      if(resultCode == RESULT_OK){
        attachContact();
      }
    }
    if(requestCode == ConstantUtils.ON_ATTACH_CONTACT_CLICK){
      if(resultCode == RESULT_OK){
        addTextToMessage(data);
      }
    }
  }

  private void sendButtonClickListener() {
    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
          checkPhoneStatePermission();
        } else {
          if (address.matches("[a-zA-Z-]*")) {
            Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT).show();
          } else {
            sendButtonClicked();
          }
        }
      }
    });
  }

  private void checkPhoneStatePermission() {

    int permissionCheckCall = ContextCompat.checkSelfPermission(CompleteSmsActivity.this,
        Manifest.permission.READ_PHONE_STATE);
    if (permissionCheckCall != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(CompleteSmsActivity.this,
          new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_READ_PHONE_STATE);
    } else {
      sendButtonClicked();
    }
  }


  public void subscribeUi() {
    if(observer != null){
      messageListByAddress.removeObserver(observer);
    }
    messageListByAddress = completeSmsActivityViewModel.getMessageListByAddress(address);
    messageListByAddress.observe(this, this::showUi);
  }

    public void showUi(List<Message> messages) {
    listOfItem.clear();
    listOfItem = messages;
    completeSmsAdapter = new CompleteSmsAdapter(messages, address, context, selectedItem, listOfItem);
    completeSmsRecycleView.setAdapter(completeSmsAdapter);
  }

  private void sendButtonClicked() {
    if (!Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName())) {
      Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
      intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
      startActivityForResult(intent, SEND_TEXT_SMS_REQUEST);
    } else {
      long l = 0;
      sendTextSms(l, address);
    }
  }

  public static void sendTextSms(Long time, String address) {
    String msg;
    if (CompleteSmsSentViewHolder.tryFailedSms) {
      CompleteSmsSentViewHolder.tryFailedSms = false;
      msg = db.messageDao().getFailedSmsText(time);
      db.messageDao().deleteFailedMsg(time);
      new thread(msg, address).start();
    } else {
      Editable editableText = editText.getText();
      msg = editableText.toString();
//    if (!msg.isEmpty()) {
      editableText.clear();
//      write sent sms to local database
      new thread(msg, address).start();
    }
  }

  private static class thread extends Thread {
    String msg,address;
    thread(String msg, String address) {
      this.msg = msg;
      this.address = address;
    }

    @Override
    public void run() {
      super.run();
      if (!msg.isEmpty()) {
        timeStampForBroadCast = System.currentTimeMillis();
        message = new Message();
        message.address = address;
        Log.d(TAG, address);
        message.body = msg;
        message.read = true;
        message.seen = true;
        message.category = contact.getCategory();
        message.threadId = 0;
        message.timestamp = timeStampForBroadCast;
        Log.d(TAG, "message.timeStamp " + Long.toString(timeStampForBroadCast));
        message.type = Message.MessageType.QUEUED;
        db.messageDao().insertMessage(message);


        Intent sentIntent = new Intent();
//        sentIntent.putExtra("timeStamp123", timeStampForBroadCast);
        sentIntent.setAction("in.smslite.SEND_SMS_ACTION");
        PendingIntent sentPendingIntent = PendingIntent.
            getBroadcast(context, SMS_SEND_INTENT_REQUEST, sentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent deliveredIntent = new Intent();
        deliveredIntent.setAction("in.smslite.DELIVERED_SMS_ACTION");
//        deliveredIntent.putExtra("deliveredSms", "yes");
        deliveredIntent.putExtra("timeStamp123", timeStampForBroadCast);
        int requestCode = timeStampForBroadCast.intValue();
        PendingIntent deliveredPendingIntent = PendingIntent.
            getBroadcast(context, requestCode, deliveredIntent, PendingIntent.FLAG_ONE_SHOT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
          SmsManager smsManager = SmsManager.getDefault();
          smsManager.sendTextMessage(address, null, msg, sentPendingIntent, deliveredPendingIntent);
        } else {
//          int subscriptionId = SmsManager.getDefaultSmsSubscriptionId();
        }
      } else {
        Handler handler = new Handler(context.getMainLooper());
        Runnable task = new Runnable() {
          @Override
          public void run() {
            Toast.makeText(context, "Please write some text!", Toast.LENGTH_SHORT).show();
          }
        };
        handler.post(task);
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
//          Long timeStamp = intent.getLongExtra("timeStamp123", 0);
//          Log.d(TAG, "timeStamp " + Long.toString(timeStamp));
          message.type = Message.MessageType.SENT;
          db.messageDao().updateSentSuccessful(timeStampForBroadCast);
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
      case MY_PERMISSIONS_REQUEST_CALL_PHONE:
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          performCalling();
        } else {
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            String msg = "Phone permission required for this app to perform the action";
            explainPermissionDialog(msg);
          } else {
            openSetting();
          }
        }
        break;
      case MY_PERMISSION_REQUEST_READ_PHONE_STATE:

        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          sendButtonClicked();
        } else {
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            String msg = "Phone permission required for this app to perform the action";
            explainPermissionDialog(msg);
          } else {
            openSetting();
          }
        }
    }
  }

  private void openSetting() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", getPackageName(), null);
    intent.setData(uri);
    startActivity(intent);
  }

  private void explainPermissionDialog(String msg) {
    showDialogOK(msg, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            checkCallPermission();
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      }
    });
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
        .setNegativeButton("CANCEL", okListener)
        .create()
        .show();
  }

  private void performCalling() {
    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
    startActivity(callIntent);
  }


}










