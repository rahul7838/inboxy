package `in`.smslite.fragments

import `in`.smslite.R
import `in`.smslite.activity.WelcomeActivity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SMSPermissionFragment : Fragment(), WelcomeActivity.SlidingPolicy {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun isPolicyRespected(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onUserIllegallyRequestedNextPage() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE), 2)
    }

    companion object {
        fun newInstance(): SMSPermissionFragment {
            return SMSPermissionFragment()
        }
    }
}