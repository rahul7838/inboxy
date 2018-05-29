package in.smslite.activity;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.crashlytics.android.core.CrashlyticsCore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.smslite.BottomNavigationViewHelper;
import in.smslite.R;
import in.smslite.adapter.SMSAdapter;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.others.MainActivityHelper;
import in.smslite.others.RecyclerItemClickListener;
import in.smslite.threads.UpdateSentMsgThread;
import in.smslite.utils.AppStartUtils;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.MessageUtils;
import in.smslite.viewModel.LocalMessageDbViewModel;
import io.fabric.sdk.android.Fabric;

import static in.smslite.others.MainActivityHelper.isMultiSelect;
import static in.smslite.others.MainActivityHelper.mActionMode;
import static in.smslite.others.MainActivityHelper.multi_select;
import static in.smslite.others.MainActivityHelper.selectedAddressList;
import static in.smslite.utils.NotificationUtils.BROADCAST_SMS_CATEGORY_KEY;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static MessageDatabase db;
  private static final int PICK_CONTACT_REQUEST_CODE = 12;
  public static final String WIDGET_UPDATE_DB_COLUMN_KEY = "updateWidgetDb";
  public static final String MAINACTIVTY_CATEGORY_TASKSTACK_KEY = "category";
  public static LocalMessageDbViewModel localMessageDbViewModel;
  public List<Message> selectedItem = new ArrayList<>();
  public List<Message> listOfItem = new ArrayList<>();
  public  Activity activity;
  private int currentVisiblePostion = 0;
  LiveData<List<Message>> liveDataListMsg;
  Observer<List<Message>> observer = null;
  LinearLayoutManager llm;
  private SMSAdapter smsAdapter;
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.empty_main_view)
  RelativeLayout emptyView;
  @BindView(R.id.empty_text_view)
  TextView emptyText;
  @BindView(R.id.empty_image_view)
  ImageView emptyImage;
  @BindView(R.id.bottom_navigation)
  BottomNavigationView bottomNavigationView;
  @BindView(R.id.sms_list)
  RecyclerView recyclerView;
  List<String> permissionNeeded;
  public SharedPreferences sharedPreferences;
  private Context context;
  private String address;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setDefaultValue should be defined at a place from where app can enter first.
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    context = this;
//    Fabric.with(this, new Crashlytics());---- setup crashlytics for debug and release build
    Log.d(TAG, "onCreate");
    // Set up Crashlytics, disabled for debug builds
    Crashlytics crashlyticsKit = new Crashlytics.Builder()
        .core(new CrashlyticsCore.Builder().disabled(in.smslite.BuildConfig.DEBUG).build())
        .build();
// Initialize Fabric with the debug-disabled crashlytics.
    Fabric.with(this, crashlyticsKit);

//    registerReceiverForSmsBroadCast();
    localMessageDbViewModel = ViewModelProviders.of(this).get(LocalMessageDbViewModel.class);
    db = MessageDatabase.getInMemoryDatabase(this);
    boolean smsCategorized = sharedPreferences.getBoolean(getString(R.string.key_sms_categorized), false);
    activity = this;
//    registerSmsReceiverBroadcast();
    switch (AppStartUtils.checkAppStart(this, sharedPreferences)) {
//      case FIRST_TIME_VERSION:
//        // TODO show what's new
//        break;
//      case FIRST_TIME:
      default:
        checkPermission(smsCategorized);
        break;
    }
  }

  public void checkPermission(boolean smsCategorized) {
    Log.i(TAG, "checkPermission");
    permissionNeeded = new ArrayList<>();
    int permissionCheckSms = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
    int permissionCheckContact = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);
    if (permissionCheckSms != PackageManager.PERMISSION_GRANTED) {
      permissionNeeded.add(Manifest.permission.READ_SMS);
    }
    if (permissionCheckContact != PackageManager.PERMISSION_GRANTED) {
      permissionNeeded.add(Manifest.permission.READ_CONTACTS);
    }
    if (!permissionNeeded.isEmpty() || !smsCategorized) {
//    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_SMS);
      Intent intent = new Intent(this, WelcomeActivity.class);
      startActivity(intent);
      finish();
    } else {
      initiUi();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (llm != null) {
      currentVisiblePostion = (llm).findLastCompletelyVisibleItemPosition();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    if (llm != null) {
      llm.scrollToPosition(currentVisiblePostion);
    }

    if(!MessageUtils.checkIfDefaultSms(context)) {
      new UpdateSentMsgThread(context).start();
    }
    //below code is used to test the OTP notification
//    registerReceiver(TestUtil.testNotiBroadCast,new IntentFilter("in.smslite.utils.TEST_NOTIFICATION"));
//    Thread thread = new Thread() {
//      @Override
//      public void run() {
//        super.run();
//        TestUtil.TestOTP(context);
//      }
//    };
//    thread.start();

  }

  private void initiUi() {
//    updateWidgetColumn();

    Log.d(TAG, "initiUi");
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    
    setLinearLayout();
    setToolbar();
    PhoneContact.init(this);

//    if Mainactivity is open through pending intent, the below code ensure which sms category to display.
    if ((getIntent().getExtras()) != null && getIntent().getExtras().getInt(BROADCAST_SMS_CATEGORY_KEY) != 0) {
      Bundle bundle = getIntent().getExtras();
      final int broadcastSmsCategory = bundle.getInt(BROADCAST_SMS_CATEGORY_KEY);
      new Thread(() -> {localMessageDbViewModel.markAllSeen(broadcastSmsCategory);
        Log.i("MainActivity", "markAllseenDone");
      }).start();
      subscribeUi(broadcastSmsCategory);
      setItemMenuChecked(broadcastSmsCategory);
      Log.d(TAG, "BRoadcast");
    } else {
      subscribeUi(Contact.PRIMARY);
      Log.d(TAG, "addParentStack");
    }
    setBottomNavigation();
    clickListener();
//    MainActivityHelper.contextualActionMode(recyclerView, fab, bottomNavigationView, context);
  }

  private void setLinearLayout() {
    llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(llm);
    recyclerView.setHasFixedSize(true);

  }

  private void setToolbar() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.title_primary);
    setSupportActionBar(toolbar);
  }

  public void subscribeUi(final int category) {
    if (observer != null) {
      liveDataListMsg.removeObserver(observer);
    }
    liveDataListMsg = localMessageDbViewModel.getMessageListByCategory(category);
    liveDataListMsg.observe(this, messages -> setMessageList(messages, category));
  }

  @OnClick(R.id.fab)
  public void clickFab(){
    if (!MessageUtils.checkIfDefaultSms(context)) {
      Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
      intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
      startActivityForResult(intent, 8);
    } else {
      launchPickContact();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 8) {
      if (resultCode == RESULT_OK) {
        launchPickContact();
      }
    }
    if (requestCode == PICK_CONTACT_REQUEST_CODE) {
      // Make sure the request was successful
      if (resultCode == RESULT_OK) {
        launchCompleteSmsActivity(data);
        Throwable error = new Error("msg");
        Log.v(TAG, "log", error);
      }
    }
  }


  private void launchPickContact() {
//    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
//    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
//    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
//    Throwable error = new Error("launchPickerror");
//    Log.v(TAG, "logstacktrace", error);

    Intent intent = new Intent(this, SelectContactActivity.class);
    startActivity(intent);
  }

  public void launchCompleteSmsActivity(Intent data){
    String number = localMessageDbViewModel.pickContactSelected(data);
    Intent intent = new Intent(this, CompleteSmsActivity.class);
    intent.putExtra(context.getString(R.string.address_id), number);
    startActivity(intent);
  }

  private void setBottomNavigation() {
//    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      int id = item.getItemId();
      if (id == R.id.bottomNav_primary) {
        subscribeUi(Contact.PRIMARY);
        toolbar.setTitle(R.string.title_primary);
      } else if (id == R.id.bottomNav_finance) {
        subscribeUi(Contact.FINANCE);
        toolbar.setTitle(R.string.title_finance);
      } else if (id == R.id.bottomNav_promotion) {
        subscribeUi(Contact.PROMOTIONS);
        toolbar.setTitle(R.string.title_promotions);
      } else if (id == R.id.bottomNav_updates) {
        subscribeUi(Contact.UPDATES);
        toolbar.setTitle(R.string.title_updates);
      }
      return true;
    });
  }

  private void setMessageList(List<Message> messageList, int category) {
    listOfItem.clear();
    listOfItem.addAll(messageList);
    if (messageList.isEmpty()) {
      switch (category) {
        case Contact.PRIMARY:
          emptyText.setText(R.string.empty_primary_text);
          emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_primary, null));
          break;
        case Contact.FINANCE:
          emptyText.setText(R.string.empty_finance_text);
          emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_finance, null));
          break;
        case Contact.PROMOTIONS:
          emptyText.setText(R.string.empty_promotion_text);
          emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_promotions, null));
          break;
        case Contact.UPDATES:
          emptyText.setText(R.string.empty_update_text);
          emptyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_updates, null));
          break;
      }
      recyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
      smsAdapter = new SMSAdapter(messageList, selectedItem, listOfItem);
      recyclerView.setAdapter(smsAdapter);
    }
  }

  public void clickListener() {
    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        if (isMultiSelect) {
          address = smsAdapter.messages.get(position).getAddress();
          multi_select(position, mActionModeCallback, selectedItem, listOfItem, address);
          refreshAdapter();
        } else {
          address = smsAdapter.messages.get(position).getAddress();
          Intent i = new Intent(context, CompleteSmsActivity.class);
          i.putExtra(view.getResources().getString(R.string.address_id), address);
          context.startActivity(i);
        }
      }

      @Override
      public void onItemLongClick(View view, int position) {
        if (!isMultiSelect) {
          bottomNavigationView.setVisibility(View.GONE);
          fab.setVisibility(View.GONE);
          address = smsAdapter.messages.get(position).getAddress();
          isMultiSelect = true;
          if (mActionMode == null) {
            mActionMode = ((MainActivity) context).startActionMode(mActionModeCallback);
          }
        }
        multi_select(position, mActionModeCallback, selectedItem, listOfItem, address);
        refreshAdapter();
      }
    }));
  }

  public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      // Inflate a menu resource providing context menu items
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.menu_multi_select, menu);
      mActionMode = mode;
      activity.getWindow().setStatusBarColor(context.getResources().getColor(R.color.contextual_status_bar_color));
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false; // Return false if nothing is done
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.action_delete:
          if(MessageUtils.checkIfDefaultSms(context)) {
            MainActivityHelper.alertDialog(context, mActionModeCallback, selectedItem);
          } else {
            MessageUtils.setDefaultSms(context);
          }
          Log.d(TAG, "Action delete");
          return true;
        case R.id.action_select_all:
          Log.d(TAG, "select all");
          selectedItem.clear();
          selectedItem.addAll(listOfItem);
          int size = selectedItem.size();
          selectedAddressList.clear();
          for(int i = 0; i<size; i++){
            selectedAddressList.add(selectedItem.get(i).getAddress());
          }
          mActionMode.setTitle("" + selectedItem.size());
          refreshAdapter();
          return true;
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      mActionMode.finish();
      mActionMode = null;
      isMultiSelect = false;
      selectedItem.clear();
      smsAdapter.notifyDataSetChanged();
      selectedAddressList.clear();
      fab.setVisibility(View.VISIBLE);
      bottomNavigationView.setVisibility(View.VISIBLE);
      activity.getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
      refreshAdapter();
    }
  };

  private void refreshAdapter() {
    smsAdapter.selectedItemAdapter = selectedItem;
    smsAdapter.listOfItemAdapter = listOfItem;
    smsAdapter.notifyDataSetChanged();
  }



  public void setItemMenuChecked(int category) {
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    switch (category) {
      case 1:
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(R.string.title_primary);
//        Answers.getInstance().logContentView(new ContentViewEvent()
//            .putContentName("Primary"));
//        setMessageList(messages, category);
        break;
      case 2:
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        toolbar.setTitle(R.string.title_finance);
        Answers.getInstance().logContentView(new ContentViewEvent()
            .putContentName("Finance"));

//        setMessageList(messages, category);
        break;
      case 3:
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        toolbar.setTitle(R.string.title_promotions);
        Answers.getInstance().logContentView(new ContentViewEvent()
            .putContentName("Promotion"));
//        setMessageList(messages, category);
        break;
      case 4:
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        toolbar.setTitle(R.string.title_updates);
        Answers.getInstance().logContentView(new ContentViewEvent()
            .putContentName("Updates"));
//        setMessageList(messages, category);
        break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    int id = item.getItemId();
    if (id == R.id.menu_share) {
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      String message = getString(R.string.share_message);
      message = message + Html.fromHtml(getString(R.string.playstore_link));
      shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
      startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
      Answers.getInstance().logShare(new ShareEvent());
    } else if (id == R.id.menu_rate_us) {
      Intent rateIntent = new Intent(Intent.ACTION_VIEW,
          Uri.parse(getString(R.string.playstore_link)));
      startActivity(rateIntent);
    } else if (id == R.id.menu_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      Answers.getInstance().logCustom(new CustomEvent("Setting viewed"));
    } else if (id == R.id.menu_search_msg_id){
      Intent intent = new Intent(this, SearchActivity.class);
      startActivity(intent);
    } else if(id == R.id.menu_main_booking) {
      Intent intent = new  Intent(this, BookingActivity.class);
      startActivity(intent);
    }
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }


}

//  public BroadcastReceiver smsReceiver = new BroadcastReceiver() {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//      if (!MessageUtils.checkIfDefaultSms(context)) {
//        Log.d(TAG, "smsReceiver");
//        SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver();
//        smsBroadcastReceiver.onReceive(context, intent);
//      }
//    }
//  };

//  private void registerSmsReceiverBroadcast() {
//    IntentFilter intentFilter = new IntentFilter();
//    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//    intentFilter.setPriority(2147483647);
//    registerReceiver(smsReceiver, intentFilter);
//  }

//  private void registerReceiverForSmsBroadCast() {
//    IntentFilter intentFilter = new IntentFilter();
//    intentFilter.setPriority(2147483647);
//    intentFilter.addAction("android.provider.Telephony.SMS_DELIVER");
//
//    SmsBroadcastReceiver smsReceiver = new SmsBroadcastReceiver();
////    smsReceiver.clearAbortBroadcast();
//    this.registerReceiver(smsReceiver,intentFilter);
//  }

//  private void updateWidgetColumn() {
//    boolean updateWidgetColumnDb = sharedPreferences.getBoolean(WIDGET_UPDATE_DB_COLUMN_KEY, true);
//    if(updateWidgetColumnDb){
//      new Runnable() {
//        @Override
//        public void run() {
//          List<String> widgetKeyword = Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.widget_keyword));
//          int size = widgetKeyword.size();
//          for(int i=0; i<size; i++) {
//            String name = "%" + widgetKeyword.get(i) + "%";
//            Log.i("Mainactivity", name);
//            db.messageDao().updateWidgetMessage(name);
//            }
//          sharedPreferences.edit().putBoolean(WIDGET_UPDATE_DB_COLUMN_KEY, false).apply();
//          }
//      };
//    }
//  }