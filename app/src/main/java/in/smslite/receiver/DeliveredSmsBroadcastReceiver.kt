package `in`.smslite.receiver

import `in`.smslite.repository.MessageRepository
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

/**
 * Created by rahul1993 on 4/10/2018.
 */
class DeliveredSmsBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject(named("IO"))

    override fun onReceive(context: Context, intent: Intent) {
//    Bundle bundle = intent.getExtras();
        val timeStamp = intent.getLongExtra("timeStamp123", 0)
        //    String key = intent.getStringExtra("deliveredSms");
//    Log.i(TAG, timeStamp  + "null");
//    CompleteSmsActivity.message.type = Message.MessageType.OUTBOX;
        ioScope.launch { messageRepository.deliveredSmsSuccessfully(timeStamp) }
    }

    companion object {
        private val TAG = DeliveredSmsBroadcastReceiver::class.java.simpleName
    }
}