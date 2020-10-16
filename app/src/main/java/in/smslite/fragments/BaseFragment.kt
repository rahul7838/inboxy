package `in`.smslite.fragments

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    protected val encryptedSharedPreferences: SharedPreferences by inject()
}