package in.inboxy;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class SMSApplication extends Application {
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}
