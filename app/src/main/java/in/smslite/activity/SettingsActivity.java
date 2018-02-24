package in.smslite.activity;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import in.smslite.R;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(
                    this.getResources().getString(R.string.pref_key_category));
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

    @Override
  public boolean onOptionsItemSelected(MenuItem item){
      int id = item.getItemId();
      if(id == android.R.id.home){
        super.onBackPressed();
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
}