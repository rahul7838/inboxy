package in.inboxy.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;

import in.inboxy.R;


public class SMSPermissionFragment extends Fragment implements ISlidePolicy {
    private static final String TAG = SMSPermissionFragment.class.getSimpleName();

    public SMSPermissionFragment() {
        // Required empty public constructor
    }

    public static SMSPermissionFragment newInstance() {
        return new SMSPermissionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public boolean isPolicyRespected() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "Permission request");
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 2);
    }


}
