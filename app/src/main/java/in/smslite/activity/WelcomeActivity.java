package in.smslite.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.util.LogHelper;

import java.lang.reflect.Array;

import in.smslite.R;
import in.smslite.fragments.CategorizeFragment;
import in.smslite.fragments.ContactsPermissionFragment;
import in.smslite.fragments.SMSPermissionFragment;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class WelcomeActivity extends AppIntro {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private int numFragment = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean smsCategorized = sharedPreferences.getBoolean(getString(R.string.key_sms_categorized), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                addSlide(SMSPermissionFragment.newInstance());
                numFragment++;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                addSlide(ContactsPermissionFragment.newInstance());
                numFragment++;
            }


            if (!smsCategorized ) {
                addSlide(CategorizeFragment.newInstance());
                numFragment++;
            }
        } else {
            addSlide(SMSPermissionFragment.newInstance());
            addSlide(ContactsPermissionFragment.newInstance());
            addSlide(CategorizeFragment.newInstance());
            numFragment += 3;
        }

        showSkipButton(false);
        setProgressButtonEnabled(false);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (newFragment instanceof CategorizeFragment) {
            CategorizeFragment fragment = (CategorizeFragment) newFragment;
            fragment.callSync();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //Note:- Don't use 1 as it already used for something else by AppIntro in parents method
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    next();
                } else {
                    handleDenial(Manifest.permission.READ_SMS, R.string.permission_Read_SMS_rationale,
                            getResources().getString(R.string.permission_READ_SMS_OK), getResources().getString(R.string.permission_READ_SMS_denial));
                }
                break;
            case 3:
                if (Array.getLength(grantResults) != 0 && grantResults[0] == PERMISSION_GRANTED) {
                    next();
                } else {
                    handleDenial(Manifest.permission.READ_CONTACTS, R.string.permission_Read_CONTACTS_rationale,
                            getResources().getString(R.string.permission_READ_CONTACTS_OK), getResources().getString(R.string.permission_READ_CONTACTS_denial));
                }
                break;
            default:
                LogHelper.e(TAG, "Unexpected request code");
        }
    }

    private void handleDenial(String permission, int rationalResId, String ok, String cancel) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setMessage(rationalResId)
                    .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .create()
                    .show();
        } else {
            Toast.makeText(getBaseContext(), "Compulsory permission denied. Please try again", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void openMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        activity.finish();
    }

    public void next() {
        if (getPager().getCurrentItem() == numFragment - 1) {
            openMainActivity(this);
        } else {
            getPager().setCurrentItem(getPager().getCurrentItem() + 1);
        }
    }

}
