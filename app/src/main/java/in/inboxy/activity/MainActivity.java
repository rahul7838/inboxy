package in.inboxy.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
  final int MY_PERMISSIONS_REQUEST_READ_SMS = 0;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
    }
    else{
      setContentView(R.layout.activity_main);
      localMessageDbViewModel = ViewModelProviders.of(this).get(LocalMessageDbViewModel.class);
      ButterKnife.bind(this);
      subscribeUi();
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_READ_SMS: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          setContentView(R.layout.activity_main);
          localMessageDbViewModel = ViewModelProviders.of(this).get(LocalMessageDbViewModel.class);
          ButterKnife.bind(this);
          subscribeUi();
          // permission was granted, yay! Do the
          // contacts-related task you need to do.

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
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

  }
}

