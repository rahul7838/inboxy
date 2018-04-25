package in.smslite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Telephony;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import in.smslite.R;
import in.smslite.utils.MessageUtils;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by rahul1993 on 4/1/2018.
 */

public class SettingFragment extends PreferenceFragment {
  private final static String TAG = SettingFragment.class.getSimpleName();
  private Preference prefDefaultSms;
  String appName;


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "smsDefaultName");
    if(requestCode == 123){
      if(resultCode== Activity.RESULT_OK){
        prefDefaultSms.setSummary("Inboxy");
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    // check the default sms app name when the activity resume
    String packageName = Telephony.Sms.getDefaultSmsPackage(getActivity());
    PackageManager pm = getActivity().getPackageManager();
    ApplicationInfo packageInfo = null;
    try {
      packageInfo = pm.getApplicationInfo(packageName,0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    appName =(String) pm.getApplicationLabel(packageInfo);
    prefDefaultSms.setSummary(appName);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);

    //change the app to default sms
    prefDefaultSms = findPreference("defaultSms");
    prefDefaultSms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Log.d(TAG, "onclick");
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getActivity().getPackageName());
        startActivityForResult(intent,123);
        return true;
      }
    });



    MultiSelectListPreference pref = (MultiSelectListPreference)
        findPreference(this.getResources().getString(R.string.pref_key_category));
    pref.setSummary(convertToSummary(pref.getValues().toString()));
    pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, newValue.toString());
        if (newValue.toString().length() > 2) {
          preference.setSummary(convertToSummary(newValue.toString()));
          Answers.getInstance().logCustom(new CustomEvent("Notify me for")
              .putCustomAttribute("selected category", newValue.toString()));
          return true;
        }
        return false;
      }
    });

  }

  private String convertToSummary(String values) {
    return values.substring(1, values.length() - 1);
  }
}

