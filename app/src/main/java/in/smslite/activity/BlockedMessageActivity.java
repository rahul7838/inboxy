package in.smslite.activity;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import in.smslite.viewModel.BlockedMessageActivityViewModel;

/**
 * Created by rahul1993 on 5/31/2018.
 */

public class BlockedMessageActivity extends AppCompatActivity {
  private static final String TAG = BlockedMessageActivity.class.getSimpleName();
  //  @BindView(R.id.bottom_navigation)
  BottomNavigationView fragmentBottomNavigationView;
  //  @BindView(R.id.fab)
  FloatingActionButton fragmentFab;
  //  @BindView(R.id.sms_list)
  RecyclerView fragmentRecyclerView;
  //  @BindView(R.id.toolbar)
  private Toolbar fragmentToolbar;
  private List<Message> selectedItem = new ArrayList<>();
  private List<Message> listOfItem = new ArrayList<>();
  private Activity activity;
  private Context context;
  private BlockedMessageActivityViewModel blockedMessageViewModel;
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
    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getString(R.string.dialog_option), Contact.BLOCKED).apply();
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

    blockedMessageViewModel = ViewModelProviders.of(this).get(BlockedMessageActivityViewModel.class);

    List<Message> list = new ArrayList<>();
    smsAdapter = new SMSAdapter(list, selectedItem, listOfItem);
    fragmentRecyclerView.setAdapter(smsAdapter);
    MainActivityHelper mainActivityHelper = new MainActivityHelper();
    mainActivityHelper.contextualActionMode(fragmentRecyclerView,fragmentFab, fragmentBottomNavigationView,smsAdapter,
        activity, context, "blocked", listOfItem);
    subscribeUI();
  }

  private void setLinearLayout() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    fragmentRecyclerView.setLayoutManager(linearLayoutManager);
    fragmentRecyclerView.setHasFixedSize(true);
  }

  private void setToolbar() {
    setSupportActionBar(fragmentToolbar);
    setTitle("Blocked");
    ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void subscribeUI() {
    liveMsgList = blockedMessageViewModel.getBlockedMessage();
    liveMsgList.observe(this, this::setMessage);

  }

  private void setMessage(List<Message> messages) {
    if (messages.isEmpty()) {
      fragmentRecyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
        emptyImage.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp));
        emptyText.setText("No sender is Blocked");
    } else {
      listOfItem.clear();
      listOfItem.addAll(messages);
      smsAdapter.setMessage(messages);
      fragmentRecyclerView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);


    }
  }
}
