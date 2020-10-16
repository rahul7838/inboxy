package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.extension.navigateTo
import `in`.smslite.fragments.CategorizeFragment
import `in`.smslite.fragments.ContactsPermissionFragment
import `in`.smslite.fragments.SMSPermissionFragment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.preference.PreferenceManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.legacy.app.ActivityCompat
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.util.LogHelper

class WelcomeActivity : AppIntro() {
    private var numFragment = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this)
        val smsCategorized = sharedPreferences.getBoolean(getString(R.string.key_sms_categorized), false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                addSlide(SMSPermissionFragment.newInstance())
                numFragment++
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                addSlide(ContactsPermissionFragment.newInstance())
                numFragment++
            }
            if (!smsCategorized) {
                addSlide(CategorizeFragment.newInstance())
                numFragment++
            }
        } else {
            addSlide(SMSPermissionFragment.newInstance())
            addSlide(ContactsPermissionFragment.newInstance())
            addSlide(CategorizeFragment.newInstance())
            numFragment += 3
        }
        showSkipButton(false)
        isProgressButtonEnabled = false
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        if (newFragment is CategorizeFragment) {
            val fragment: CategorizeFragment? = newFragment as CategorizeFragment?
            fragment?.callSync()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                if (grantResults.size > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
//                    next();
                } else {
                    handleDenial(Manifest.permission.READ_SMS, R.string.permission_Read_SMS_rationale,
                            resources.getString(R.string.permission_READ_SMS_OK), resources.getString(R.string.permission_READ_SMS_denial))
                }
                if (grantResults.size > 1 && grantResults[1] == PermissionChecker.PERMISSION_GRANTED) {
                    next()
                } else {
                    handleDenial(Manifest.permission.READ_PHONE_STATE, R.string.permission_Read_SMS_rationale,
                            resources.getString(R.string.permission_READ_SMS_OK), resources.getString(R.string.permission_READ_SMS_denial))
                }
            }
            3 -> if (java.lang.reflect.Array.getLength(grantResults) != 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                next()
            } else {
                handleDenial(Manifest.permission.READ_CONTACTS, R.string.permission_Read_CONTACTS_rationale,
                        resources.getString(R.string.permission_READ_CONTACTS_OK), resources.getString(R.string.permission_READ_CONTACTS_denial))
            }
            else -> LogHelper.e(TAG, "Unexpected request code")
        }
    }

    private fun handleDenial(permission: String, rationalResId: Int, ok: String, cancel: String) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this)
                    .setMessage(rationalResId)
                    .setPositiveButton(ok) { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton(cancel) { dialog, which ->
                        Process.killProcess(Process.myPid())
                        System.exit(1)
                    }
                    .create()
                    .show()
        } else {
            Toast.makeText(baseContext, "Compulsory permission denied. Please try again", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    operator fun next() {
        if (getPager().currentItem == numFragment - 1) {
            navigateTo<MainActivity>()
            finish()
        } else {
            getPager().currentItem = getPager().currentItem + 1
        }
    }

    companion object {
        private val TAG = WelcomeActivity::class.java.simpleName
    }
}