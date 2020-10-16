package `in`.smslite.receiver

import `in`.smslite.SMSApplication
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import `in`.smslite.threads.BroadcastMessageAsyncTask
import `in`.smslite.utils.ContactUtils
import `in`.smslite.utils.MessageUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.provider.Telephony

/**
 * Created by rahul1993 on 4/18/2018.
 */
// when Inboxy is not default sms app the message is read by by this receiver
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!MessageUtils.checkIfDefaultSms(context)) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val bodyText = StringBuilder()
            var number = "0"
            var body = ""
            PhoneContact.initialize(context)
            val contact: Contact = ContactUtils.getContact(messages[0].displayOriginatingAddress, context, true)
            val message = Message()
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
                message.read = false
                message.seen = false
                message.timestamp = sms.timestampMillis
                message.threadId = 123
                message.type = Message.MessageType.INBOX
                //        TODO line 54 is crashing the app
                val value: Int = MessageDatabase.getInMemoryDatabase(SMSApplication.application).messageDao().askSendFutureMessage(number)
                if (value == 1) {
                    message.category = MessageDatabase.getInMemoryDatabase(SMSApplication.application).messageDao().findCategory(number)
                } else {
                    message.category = contact.category
                }
                serviceCenterAddress = sms.serviceCenterAddress
                //      message.widget = isWidgetMessage(context, message.body);
            }
            BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)
        }
    }
}