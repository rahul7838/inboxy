package `in`.smslite.fragments

import `in`.smslite.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.legacy.app.ActivityCompat
import com.github.paolorotolo.appintro.ISlidePolicy

class ContactsPermissionFragment : Fragment(), ISlidePolicy {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organize, container, false)
    }

    override fun isPolicyRespected(): Boolean {
        return ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    override fun onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "Permission request")
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_CONTACTS), 3)
    }

    companion object {
        private val TAG = ContactsPermissionFragment::class.java.simpleName
        fun newInstance(): ContactsPermissionFragment {
            return ContactsPermissionFragment()
        }
    }
}