package `in`.smslite.fragments

import `in`.smslite.R
import `in`.smslite.activity.MainActivity
import `in`.smslite.extension.navigateTo
import `in`.smslite.viewModel.WelcomeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CategorizeFragment : BaseFragment() {

    private val viewModel: WelcomeViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categorize, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        PhoneContact.initialize(requireContext())
        initObserver()
        viewModel.categoriseSms()
    }

    private fun initObserver() {
        completeEventObserver()
    }

    private fun completeEventObserver() {
        Timber.d(Thread.currentThread().name)
        viewModel.completeEvent.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "Analysis Complete", Toast.LENGTH_SHORT).show()
            encryptedSharedPreferences.edit().putBoolean(getString(R.string.key_sms_categorized), true).apply()
            requireActivity().navigateTo<MainActivity>()
            requireActivity().finish()
        })
    }

    companion object {
        fun newInstance(): CategorizeFragment {
            return CategorizeFragment()
        }
    }
}