package in.smslite.activity;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import in.smslite.R;
import in.smslite.fragments.SettingFragment;


public class SettingsActivity extends AppCompatActivity {

  private static final String TAG = SettingsActivity.class.getSimpleName();
  public static final String NEW_NOTIFICATION_MESSAGE = "new_notification";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

//        this.setTitle("Settings ");

    setContentView(R.layout.setting_activity);
//    android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.setting_fragment_id);
//    setSupportActionBar(toolbar);
//    toolbar.setTitle("Setting");

    ActionBar actionBar = this.getSupportActionBar();
    if(actionBar!=null){
      actionBar.setDisplayHomeAsUpEnabled(true);
    }


//    getFragmentManager().beginTransaction()
//        .replace(android.R.id.content, new SettingFragment())
//        .commit();

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

