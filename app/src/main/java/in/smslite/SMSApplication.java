package in.smslite;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class SMSApplication extends Application {
  private static SMSApplication sSMSApp = null;
  private String mCountryIso;
  public void onCreate() {
    super.onCreate();
    sSMSApp = this;
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);
    // Normal app init code...

    Stetho.initializeWithDefaults(this);
  }

  synchronized public static SMSApplication getApplication() {
    return sSMSApp;
  }

  public String getCurrentCountryIso() {
    if (mCountryIso == null) {
      TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
      mCountryIso = tm.getNetworkCountryIso();
    }
    return mCountryIso;
  }
}
