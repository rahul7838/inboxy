package in.smslite.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static in.smslite.utils.NotificationUtils.BUNDLE_OTP_KEY;

/**
 * Created by rahul1993 on 3/18/2018.
 */

public class OTPService extends IntentService {
  public static final String NOTIFICATION_ID = "notificationId";
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param // Used to name the worker thread, important only for debugging.
   */
  public OTPService() {
    super("OTPService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    assert intent != null;
    Bundle bundle = intent.getExtras();
    String OTP = bundle.getString(BUNDLE_OTP_KEY);
    int notificationId = bundle.getInt(NOTIFICATION_ID);
    ClipboardManager clipboard = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("Copied text", OTP);
    clipboard.setPrimaryClip(clip);
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getBaseContext(),"OTP Copied!", Toast.LENGTH_SHORT).show();
      }
    });

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(notificationId);
  }
}
