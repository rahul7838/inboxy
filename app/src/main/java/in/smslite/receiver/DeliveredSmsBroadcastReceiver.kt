package `in`.smslite.receiver

import `in`.smslite.activity.MainActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by rahul1993 on 4/10/2018.
 */
class DeliveredSmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//    Bundle bundle = intent.getExtras();
        val timeStamp = intent.getLongExtra("timeStamp123", 0)
        //    String key = intent.getStringExtra("deliveredSms");
//    Log.i(TAG, timeStamp  + "null");
//    CompleteSmsActivity.message.type = Message.MessageType.OUTBOX;
        MainActivity.db!!.messageDao().deliveredSmsSuccessfully(timeStamp)
        Log.i(TAG, "sms delivered")
    }

    companion object {
        private val TAG = DeliveredSmsBroadcastReceiver::class.java.simpleName
    }
}