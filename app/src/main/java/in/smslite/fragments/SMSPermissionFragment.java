package in.smslite.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.ISlidePolicy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import in.smslite.R;


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
        onDisplayPopupPermission();
    }


  private static boolean isMIUI() {
    String device = Build.MANUFACTURER;
    if (device.equals("Xiaomi")) {
      try {
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        return prop.getProperty("ro.miui.ui.version.code", null) != null
            || prop.getProperty("ro.miui.ui.version.name", null) != null
            || prop.getProperty("ro.miui.internal.storage", null) != null;
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    return false;
  }

  private void onDisplayPopupPermission() {
    if (isMIUI()) {
      try {
        // MIUI 8
        Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        localIntent.putExtra("extra_pkgname", getActivity().getPackageName());
        startActivity(localIntent);
      } catch (Exception e) {
        try {
          // MIUI 5/6/7
          Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
          localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
          localIntent.putExtra("extra_pkgname", getActivity().getPackageName());
          startActivity(localIntent);
        } catch (Exception e1) {
          // Otherwise jump to application details
          Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
          Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
          intent.setData(uri);
          startActivity(intent);
        }
      }
    }
  }

}
