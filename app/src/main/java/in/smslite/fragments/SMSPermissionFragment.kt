package `in`.smslite.fragments

import `in`.smslite.R
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.legacy.app.ActivityCompat
import com.github.paolorotolo.appintro.ISlidePolicy
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class SMSPermissionFragment : Fragment(), ISlidePolicy {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun isPolicyRespected(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "Permission request")
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE), 2)
        onDisplayPopupPermission()
    }

    private fun onDisplayPopupPermission() {
        if (isMIUI) {
            try {
                // MIUI 8
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                localIntent.putExtra("extra_pkgname", activity!!.packageName)
                startActivity(localIntent)
            } catch (e: Exception) {
                try {
                    // MIUI 5/6/7
                    val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                    localIntent.putExtra("extra_pkgname", activity!!.packageName)
                    startActivity(localIntent)
                } catch (e1: Exception) {
                    // Otherwise jump to application details
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity!!.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        private val TAG = SMSPermissionFragment::class.java.simpleName
        fun newInstance(): SMSPermissionFragment {
            return SMSPermissionFragment()
        }

        private val isMIUI: Boolean
            private get() {
                val device = Build.MANUFACTURER
                if (device == "Xiaomi") {
                    try {
                        val prop = Properties()
                        prop.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
                        return prop.getProperty("ro.miui.ui.version.code", null) != null || prop.getProperty("ro.miui.ui.version.name", null) != null || prop.getProperty("ro.miui.internal.storage", null) != null
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return false
            }
    }
}