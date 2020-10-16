package `in`.smslite.receiver

import `in`.smslite.repository.MessageRepository
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by rahul1993 on 4/18/2018.
 */
// when Inboxy is not default sms app the message is read by by this receiver
class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject()
    override fun onReceive(context: Context, intent: Intent) {
/*
        if (!MessageUtils.checkIfDefaultSms(context)) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val bodyText = StringBuilder()
            var number = "0"
            var body = ""
            PhoneContact.initialize(context)
            val contact: Contact = ContactUtils.getContact(messages[0].displayOriginatingAddress, context, true)
            val customNotification = false
            var serviceCenterAddress: String? = null
            for (sms in messages) {
                bodyText.append(sms.messageBody)
                body = bodyText.toString()
                number = if (contact.category == Contact.PRIMARY) {
                    ContactUtils.normalizeNumber(sms.displayOriginatingAddress)
                } else {
                    sms.displayOriginatingAddress
                }
                message.body = body
                message.address = number
                message.isRead = false
                message.isSeen = false
                message.timestamp = sms.timestampMillis
                message.threadId = 123
                message.type = Message.MessageType.INBOX
                //        TODO line 54 is crashing the app
                val value: Int = messageRepository.askSendFutureMessage(number)
                if (value == 1) {
                    message.category = messageRepository.findCategory(number)
                } else {
                    message.category = contact.category
                }
                serviceCenterAddress = sms.serviceCenterAddress
                //      message.widget = isWidgetMessage(context, message.body);
            }
            BroadcastMessage.broadcastMessage(context, message, contact, customNotification)
//            BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)
        }
*/
    }


}