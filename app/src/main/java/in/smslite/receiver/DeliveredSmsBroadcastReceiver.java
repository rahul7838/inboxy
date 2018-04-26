package in.smslite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import in.smslite.activity.CompleteSmsActivity;
import in.smslite.db.Message;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 4/10/2018.
 */

public class DeliveredSmsBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = DeliveredSmsBroadcastReceiver.class.getSimpleName();
  @Override
  public void onReceive(Context context, Intent intent) {
//    Bundle bundle = intent.getExtras();
    Long timeStamp = intent.getLongExtra("timeStamp123",0);
//    String key = intent.getStringExtra("deliveredSms");
//    Log.i(TAG, timeStamp  + "null");
//    CompleteSmsActivity.message.type = Message.MessageType.OUTBOX;
    db.messageDao().deliveredSmsSuccessfully(timeStamp);
    Log.i(TAG, "sms delivered");
  }
}
