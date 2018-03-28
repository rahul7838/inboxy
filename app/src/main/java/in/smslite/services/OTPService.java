package in.smslite.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import static in.smslite.utils.NotificationUtils.BUNDLE_OTP_KEY;

/**
 * Created by rahul1993 on 3/18/2018.
 */

public class OTPService extends IntentService {
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

    Bundle bundle = intent.getExtras();

    String OTP = bundle.getString(BUNDLE_OTP_KEY);
    ClipboardManager clipboard = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText("Copied text", OTP);
    clipboard.setPrimaryClip(clip);
    NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(Integer.parseInt(OTP));
  }
}
