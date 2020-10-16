package `in`.smslite.receiver

import `in`.smslite.repository.MessageRepository
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

/**
 *
 * When the app is default sms app (@link SmsBroadCastReceiver) is called.
 *
 * Created by rahul1993 on 11/12/2017.
 */
class SmsBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject(named("IO"))

    override fun onReceive(context: Context, intent: Intent) {
//    this.abortBroadcast();
//    this.setResultData(null);
        Log.i("SmsBroadcastReceiver", "Executed")
//        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
//        val bodyText = StringBuilder()
//        var number = "0"
//        var body = ""
//        PhoneContact.dump()
//        PhoneContact.initialize(context)
//        val contact: Contact? = ContactUtils.getContact(messages[0].displayOriginatingAddress, context, true)
//        val customNotification = false
//        var serviceCenterAddress: String? = null
//        for (sms in messages) {
//            bodyText.append(sms.messageBody)
//            body = bodyText.toString()
//            number = if (contact?.category == Contact.PRIMARY) {
//                ContactUtils.normalizeNumber(sms.displayOriginatingAddress)
//            } else {
//                sms.displayOriginatingAddress
//            }
//            message.body = body
//            message.address = number
//            message.isRead = false
//            message.isSeen = false
//            message.timestamp = sms.timestampMillis
//            message.threadId = 0
//            message.type = Message.MessageType.INBOX
//            val value: Int = messageRepository.askSendFutureMessage(number)
//            if (value == 1) {
//                message.category = messageRepository.findCategory(number)
//            } else {
//                message.category = contact.category
//            }
//            serviceCenterAddress = sms.serviceCenterAddress
//            //      message.widget = isWidgetMessage(context, message.body);
//        }
//        BroadcastMessage.broadcastMessage(context, message, contact, customNotification)
//        //write sms to content provider
//        ContentProviderUtil.writeReceivedSms(message, serviceCenterAddress, context)
    }
}