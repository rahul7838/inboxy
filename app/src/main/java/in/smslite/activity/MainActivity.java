package in.smslite.activity;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.BottomNavigationViewHelper;
import in.smslite.R;
import in.smslite.adapter.SMSAdapter;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.AppStartUtils;
import in.smslite.viewModel.LocalMessageDbViewModel;
import io.fabric.sdk.android.Fabric;

import static in.smslite.utils.NotificationUtils.BROADCAST_SMS_CATEGORY_KEY;
import static in.smslite.utils.NotificationUtils.NOTIFICATION_BUNDLE_CATEGORY_KEY;
import static in.smslite.utils.NotificationUtils.notificationBundle;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static MessageDatabase db;
  public static final String WIDGET_UPDATE_DB_COLUMN_KEY = "updateWidgetDb";
  public static final String MAINACTIVTY_CATEGORY_TASKSTACK_KEY = "category";
  public static LocalMessageDbViewModel localMessageDbViewModel;
  private int currentVisiblePostion = 0;
  LiveData<List<Message>> liveDataListMsg;
  Observer<List<Message>> observer = null;
  LinearLayoutManager llm;
  SMSAdapter smsAdapter;
//  @BindView(R.id.fab)
//  FloatingActionButton fab;
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
  @BindView(R.id.sms_list) RecyclerView recyclerView;
  final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
  List<String> permissionNeeded;
  public SharedPreferences sharedPreferences;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setDefaultValue should be defined at a place from where app can enter first.
    PreferenceManager.setDefaultValues(this, R.xml.preferences,false);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//    Fabric.with(this, new Crashlytics());---- setup crashlytics for debug and release build
    Log.i(TAG, "onCreate");
    // Set up Crashlytics, disabled for debug builds
    Crashlytics crashlyticsKit = new Crashlytics.Builder()
            .core(new CrashlyticsCore.Builder().disabled(in.smslite.BuildConfig.DEBUG).build())
            .build();
// Initialize Fabric with the debug-disabled crashlytics.
    Fabric.with(this, crashlyticsKit);
    localMessageDbViewModel = ViewModelProviders.of(this).get(LocalMessageDbViewModel.class);
    db = MessageDatabase.getInMemoryDatabase(this);
    boolean smsCategorized = sharedPreferences.getBoolean(getString(R.string.key_sms_categorized), false);
//    if(!sharedPreferences.getBoolean(MAINACTIVTY_CATEGORY_TASKSTACK_KEY, false)) {
//      sharedPreferences.edit().putBoolean(MAINACTIVTY_CATEGORY_TASKSTACK_KEY, false).apply();
//    }
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
//    if (llm != null) {
//      currentVisiblePostion = (llm).findLastCompletelyVisibleItemPosition();
//    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume");
//    if (llm != null) {
//      llm.scrollToPosition(currentVisiblePostion);
//    }
  }

  private void initiUi() {
//    updateWidgetColumn();

    Log.i(TAG, "initiUi");
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setLinearLayout();
    //divide recycler view item
    DividerItemDecoration  dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), llm.getOrientation());
    recyclerView.addItemDecoration(dividerItemDecoration);

    setToolbar();
    PhoneContact.init(this);

//    if Mainactivity is open through pending intent, the below code ensure which sms category to display.
      if ((getIntent().getExtras()) != null && getIntent().getExtras().getInt(BROADCAST_SMS_CATEGORY_KEY) != 0) {
        Bundle bundle = getIntent().getExtras();
        final int broadcastSmsCategory = bundle.getInt(BROADCAST_SMS_CATEGORY_KEY);
        new Thread(new Runnable() {
          @Override
          public void run() {
            db.messageDao().markAllSeen(broadcastSmsCategory);
            Log.i("MainActivity", "markAllseenDone");
          }
        }).start();
        subscribeUi(broadcastSmsCategory);
        setItemMenuChecked(broadcastSmsCategory);
        Log.i(TAG, "BRoadcast");
//    } else if(sharedPreferences.getBoolean(MAINACTIVTY_CATEGORY_TASKSTACK_KEY,false)) {
//        Log.i(TAG, "Mainactivity open through taskStack");
//        sharedPreferences.edit().putBoolean(MAINACTIVTY_CATEGORY_TASKSTACK_KEY,false).apply();
//        int category = notificationBundle.getInt(NOTIFICATION_BUNDLE_CATEGORY_KEY);
//        subscribeUi(category);
//        setItemMenuChecked(category);
      }
    else {
//      setLinearLayout();
        subscribeUi(Contact.PRIMARY);
        Log.i(TAG, "addParentStack");
      }
    setBottomNavigation();
//    setClickListener();
  }

  private void setLinearLayout(){
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
    if(observer != null){
      liveDataListMsg.removeObserver(observer);
    }
    liveDataListMsg = localMessageDbViewModel.getMessageListByCategory(category);
    observer = new Observer<List<Message>>() {
      @Override
      public void onChanged(@Nullable List<Message> messages) {
        setMessageList(messages, category);
      }
    };
    liveDataListMsg.observe(this,observer);
  }

  private void setBottomNavigation() {
//    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bottomNav_primary) {
          subscribeUi(Contact.PRIMARY);
//          setMessageList(messages, Contact.PRIMARY);
          toolbar.setTitle(R.string.title_primary);
        } else if (id == R.id.bottomNav_finance) {
          subscribeUi(Contact.FINANCE);
//          setMessageList(messages, Contact.FINANCE);
          toolbar.setTitle(R.string.title_finance);
        } else if (id == R.id.bottomNav_promotion) {
          subscribeUi(Contact.PROMOTIONS);
//          setMessageList(messages, Contact.PROMOTIONS);
          toolbar.setTitle(R.string.title_promotions);
        } else if (id == R.id.bottomNav_updates) {
          subscribeUi(Contact.UPDATES);
//          setMessageList(messages, Contact.UPDATES);
          toolbar.setTitle(R.string.title_updates);
        }
        return true;
      }
    });
  }

  private void setMessageList(List<Message> messageList, int category) {
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
      smsAdapter = new SMSAdapter(messageList);
      recyclerView.setAdapter(smsAdapter);
//      context = getApplicationContext();
      /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      Set<String> defValues = new HashSet<String>();
      defValues.add("rahul");
      Set<String> set = sharedPreferences.getStringSet(getString(R.string.pref_key_category), defValues);*/
//
//      Log.i("MainActivity", Integer.toString(set.size()));
    }
  }

  public void setItemMenuChecked(int category) {
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    switch (category) {
      case 1:
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(R.string.title_primary);
//        setMessageList(messages, category);
        break;
      case 2:
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        toolbar.setTitle(R.string.title_finance);
//        setMessageList(messages, category);
        break;
      case 3:
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        toolbar.setTitle(R.string.title_promotions);
//        setMessageList(messages, category);
        break;
      case 4:
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        toolbar.setTitle(R.string.title_updates);
//        setMessageList(messages, category);
        break;
    }
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    int id = item.getItemId();
    if(id == R.id.menu_share){
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      String message = getString(R.string.share_message);
      message = message + Html.fromHtml(getString(R.string.playstore_link));
      shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
      startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
    } else if(id == R.id.menu_rate_us){
      Intent rateIntent = new Intent(Intent.ACTION_VIEW,
              Uri.parse(getString(R.string.playstore_link)));
      startActivity(rateIntent);
    } else if(id == R.id.menu_settings){
      Intent intent = new Intent(this, SettingsActivity.class);
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

