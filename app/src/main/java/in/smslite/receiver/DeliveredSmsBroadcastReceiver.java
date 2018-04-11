package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by rahul1993 on 4/10/2018.
 */

public class DeliveredSmsBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = DeliveredSmsBroadcastReceiver.class.getSimpleName();
  @Override
  public void onReceive(Context context, Intent intent) {

    Log.i(TAG, "sms delivered");
  }
}
