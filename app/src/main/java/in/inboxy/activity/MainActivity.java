package in.inboxy.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.inboxy.Adapter.SMSAdapter;
import in.inboxy.R;
import in.inboxy.db.Message;
import in.inboxy.viewModel.LocalMessageDbViewModel;

public class MainActivity extends AppCompatActivity {
  LocalMessageDbViewModel localMessageDbViewModel;
  Context context;
  @BindView(R.id.sms_list) RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    localMessageDbViewModel = ViewModelProviders.of(this).get(LocalMessageDbViewModel.class);
    ButterKnife.bind(this);
    subscribeUi();
  }

  public void subscribeUi() {
    localMessageDbViewModel.messageLiveData.observe(this, new Observer<List<Message>>() {
      @Override
      public void onChanged(@Nullable List<Message> messages) {
        showUi(messages);
      }
    });
  }

  public void showUi(List<Message> messages) {
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(llm);
    recyclerView.setHasFixedSize(true);
    SMSAdapter smsAdapter = new SMSAdapter(messages);
    recyclerView.setAdapter(smsAdapter);


    /*List<String> bodyList = new ArrayList<>();
    for (Message x : messages) {
      bodyList.add(0, x.body);
    }
    *//*ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, bodyList);
    listView = (ListView) findViewById(R.id.listView);
    listView.setAdapter(adapter);*/
  }
}

