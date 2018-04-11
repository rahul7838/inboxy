package in.smslite.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;

/**
 * Created by rahul1993 on 4/10/2018.
 */

/*
public class SendSmsBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = SendSmsBroadcastReceiver.class.getSimpleName();
  @Override
  public void onReceive(Context context, Intent intent) {
      switch (getResultCode()){
        case Activity.RESULT_OK:

          Log.i(TAG, "sent sms successful");
          break;
        case RESULT_ERROR_NULL_PDU:
          Log.i(TAG, "null pdu code");
          break;
          default:
            Log.i(TAG, "default code");
            break;
      }
  }
}
*/
