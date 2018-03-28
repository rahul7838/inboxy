package in.smslite.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import in.smslite.R;
import in.smslite.activity.WelcomeActivity;
import in.smslite.contacts.PhoneContact;
import in.smslite.utils.MessageUtils;
import me.everything.providers.android.telephony.Sms;

public class CategorizeFragment extends Fragment {

    private static final String TAG = CategorizeFragment.class.getSimpleName();

    public CategorizeFragment() {
        // Required empty public constructor
    }

    public static CategorizeFragment newInstance() {
        return new CategorizeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categorize, container, false);
    }

    public void callSync() {
        PhoneContact.init(getContext());
//      MainActivity.localMessageDbViewModel.CDB();
//      getActivity().runOnUiThread(complete());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Sms> smsList = MessageUtils.getAllMessages(getContext());
                MessageUtils.sync(getContext(), smsList);
//                Context.runOnUiThread()
                getActivity().runOnUiThread(complete());
            }
        });
    }

    public Runnable complete() {
        return new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Analysis Complete", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                preferences.edit().putBoolean(getString(R.string.key_sms_categorized), true)
                        .apply();
                ((WelcomeActivity) getActivity()).openMainActivity(getActivity());
            }
        };
    }
}
