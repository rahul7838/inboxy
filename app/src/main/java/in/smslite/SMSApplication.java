package in.smslite;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.facebook.stetho.Stetho;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class SMSApplication extends Application {
  private static SMSApplication sSMSApp = null;
  private String mCountryIso;
  public void onCreate() {
    super.onCreate();
    sSMSApp = this;
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
