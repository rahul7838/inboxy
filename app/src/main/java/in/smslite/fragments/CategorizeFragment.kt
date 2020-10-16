package `in`.smslite.fragments

import `in`.smslite.R
import `in`.smslite.activity.WelcomeActivity
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.utils.MessageUtils
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class CategorizeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categorize, container, false)
    }

    fun callSync() {
        PhoneContact.initialize(requireContext())
        //      MainActivity.localMessageDbViewModel.CDB();
//      getActivity().runOnUiThread(complete());
        AsyncTask.execute {
            val smsList = MessageUtils.getAllMessages(context)
            MessageUtils.sync(context, smsList)
            //                Context.runOnUiThread()
            activity!!.runOnUiThread(complete())
        }
    }

    fun complete(): Runnable {
        return Runnable {
            Toast.makeText(context, "Analysis Complete", Toast.LENGTH_SHORT).show()
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(activity)
            preferences.edit().putBoolean(getString(R.string.key_sms_categorized), true)
                    .apply()
            (activity as WelcomeActivity?)?.openMainActivity(requireActivity())
        }
    }

    companion object {
        private val TAG = CategorizeFragment::class.java.simpleName
        fun newInstance(): CategorizeFragment {
            return CategorizeFragment()
        }
    }
}