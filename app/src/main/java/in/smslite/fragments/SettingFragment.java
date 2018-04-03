package in.smslite.fragments;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import in.smslite.R;

/**
 * Created by rahul1993 on 4/1/2018.
 */

public class SettingFragment extends PreferenceFragment {
  private final static String TAG = SettingFragment.class.getSimpleName();
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
    MultiSelectListPreference pref = (MultiSelectListPreference)
        findPreference(this.getResources().getString(R.string.pref_key_category));
    pref.setSummary(convertToSummary(pref.getValues().toString()));
    pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.i(TAG, newValue.toString());
        if (newValue.toString().length() > 2) {
          preference.setSummary(convertToSummary(newValue.toString()));
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

