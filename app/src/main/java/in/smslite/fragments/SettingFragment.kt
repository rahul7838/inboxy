package `in`.smslite.fragments

import `in`.smslite.R
import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.MultiSelectListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.provider.Telephony
import android.util.Log

/**
 * Created by rahul1993 on 4/1/2018.
 */
class SettingFragment : PreferenceFragment() {
    private var prefDefaultSms: Preference? = null
    var appName: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "smsDefaultName")
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                prefDefaultSms!!.summary = "Inboxy"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // check the default sms app name when the activity resume
        val packageName = Telephony.Sms.getDefaultSmsPackage(activity)
        val pm = activity.packageManager
        var packageInfo: ApplicationInfo? = null
        try {
            packageInfo = pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        appName = pm.getApplicationLabel(packageInfo!!) as String
        prefDefaultSms!!.summary = appName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        //change the app to default sms
        prefDefaultSms = findPreference("defaultSms")
        prefDefaultSms?.setOnPreferenceClickListener(Preference.OnPreferenceClickListener {
            Log.d(TAG, "onclick")
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.packageName)
            startActivityForResult(intent, 123)
            true
        })
        val pref = findPreference(this.resources.getString(R.string.pref_key_category)) as MultiSelectListPreference
        pref.summary = convertToSummary(pref.values.toString())
        pref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            Log.d(TAG, newValue.toString())
            if (newValue.toString().length > 2) {
                preference.summary = convertToSummary(newValue.toString())
                //          Answers.getInstance().logCustom(new CustomEvent("Notify me for")
//              .putCustomAttribute("selected category", newValue.toString()));
                return@OnPreferenceChangeListener true
            }
            false
        }
    }

    private fun convertToSummary(values: String): String {
        return values.substring(1, values.length - 1)
    }

    companion object {
        private val TAG = SettingFragment::class.java.simpleName
    }
}