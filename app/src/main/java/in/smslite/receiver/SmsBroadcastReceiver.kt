package `in`.smslite.receiver

import `in`.smslite.SMSApplication
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import `in`.smslite.threads.BroadcastMessageAsyncTask
import `in`.smslite.utils.ContactUtils
import `in`.smslite.utils.ContentProviderUtil
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.provider.Telephony
import android.util.Log

/**
 *
 * When the app is default sms app (@link SmsBroadCastReceiver) is called.
 *
 * Created by rahul1993 on 11/12/2017.
 */
class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//    this.abortBroadcast();
//    this.setResultData(null);
        Log.i("SmsBroadcastReceiver", "Executed")
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val bodyText = StringBuilder()
        var number = "0"
        var body = ""
        PhoneContact.dump()
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
            message.threadId = 0
            message.type = Message.MessageType.INBOX
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

        //write sms to content provider
        ContentProviderUtil.writeReceivedSms(message, serviceCenterAddress, context)
    }
}