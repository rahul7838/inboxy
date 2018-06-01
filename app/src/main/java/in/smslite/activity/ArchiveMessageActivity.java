package in.smslite.activity;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.adapter.SMSAdapter;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.others.MainActivityHelper;
import in.smslite.viewModel.ArchiveMsgViewModel;
import in.smslite.viewModel.BlockedMessageActivityViewModel;

/**
 * Created by rahul1993 on 6/1/2018.
 */

public class ArchiveMessageActivity extends AppCompatActivity {
  private static final String TAG = ArchiveMessageActivity.class.getSimpleName();
  //  @BindView(R.id.bottom_navigation)
  private BottomNavigationView fragmentBottomNavigationView;
  //  @BindView(R.id.fab)
  private FloatingActionButton fragmentFab;
  //  @BindView(R.id.sms_list)
  private RecyclerView fragmentRecyclerView;
  //  @BindView(R.id.toolbar)
  private Toolbar fragmentToolbar;
  private List<Message> selectedItem = new ArrayList<>();
  private List<Message> listOfItem = new ArrayList<>();
  private Activity activity;
  private Context context;
  private ArchiveMsgViewModel archiveMessageViewModel;
  private LiveData<List<Message>> liveMsgList;
  private SMSAdapter smsAdapter;
  private ImageView emptyImage;
  private TextView emptyText;
  private RelativeLayout emptyView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    context = this;
    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getString(R.string.dialog_option), Contact.ARCHIVE).apply();
    setContentView(R.layout.activity_main);

    fragmentToolbar = findViewById(R.id.toolbar);
    fragmentFab = findViewById(R.id.fab);
    fragmentBottomNavigationView = findViewById(R.id.bottom_navigation);
    fragmentRecyclerView = findViewById(R.id.sms_list);
    emptyImage = findViewById(R.id.empty_image_view);
    emptyText = findViewById(R.id.empty_text_view);
    emptyView = findViewById(R.id.empty_main_view);

    fragmentBottomNavigationView.setVisibility(View.GONE);
    fragmentFab.setVisibility(View.GONE);

    setToolbar();
    setLinearLayout();

    archiveMessageViewModel = ViewModelProviders.of(this).get(ArchiveMsgViewModel.class);

    List<Message> list = new ArrayList<>();
    smsAdapter = new SMSAdapter(list, selectedItem, listOfItem);
    fragmentRecyclerView.setAdapter(smsAdapter);
    MainActivityHelper mainActivityHelper = new MainActivityHelper();
    mainActivityHelper.contextualActionMode(fragmentRecyclerView,fragmentFab, fragmentBottomNavigationView,smsAdapter,
        activity, context, "archive", listOfItem);
    subscribeUI();
  }

  private void setLinearLayout() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    fragmentRecyclerView.setLayoutManager(linearLayoutManager);
    fragmentRecyclerView.setHasFixedSize(true);
  }

  private void setToolbar() {
    setSupportActionBar(fragmentToolbar);
    setTitle("Archive");
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void subscribeUI() {
    liveMsgList = archiveMessageViewModel.getArchiveMessage();
    liveMsgList.observe(this, this::setMessage);
  }

  private void setMessage(List<Message> messages) {
    if (messages.isEmpty()) {
      fragmentRecyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
      emptyImage.setImageDrawable(getDrawable(R.drawable.ic_archive_black_24dp));
      emptyText.setText("You do not have any archived message");
    } else {
      listOfItem.clear(); // if dont do this on 2nd long press if u try to move message it will not move
      listOfItem.addAll(messages);
      smsAdapter.setMessage(messages);
      fragmentRecyclerView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    activity.finish();
  }
}
