package `in`.smslite.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val PERMISSION_REQUEST_CODE = 1

class PermissionFragment : Fragment() {

    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }

    fun reqPermission(
            listOfPermission: ArrayList<String>,
            onPermissionGranted: () -> Unit,
            onPermissionDenied: (() -> Unit)? = null
    ) {
        this.onPermissionGranted = onPermissionGranted
        this.onPermissionDenied = onPermissionDenied
        listOfPermission.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }.also {
            if (it.isEmpty()) {
                onPermissionGranted.invoke()
            } else {
                requestPermissions(it.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted?.invoke()
                } else {
                    if (onPermissionDenied != null) onPermissionDenied?.invoke() else handleDenial(permission)
                    onPermissionDenied?.invoke()
                }
            }
        }
    }

    private fun handleDenial(permission: String) {
        if (true) {
            AlertDialog.Builder(requireContext())
                    .setMessage("App can not work with out sms read permission")
                    .setPositiveButton("ok") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("cancel") { dialog, which ->
                        Process.killProcess(Process.myPid())
                        System.exit(1)
                    }
                    .create()
                    .show()
        } else {
            Toast.makeText(requireContext(), "Compulsory permission denied. Please try again", Toast.LENGTH_SHORT)
                    .show()
        }
    }
}