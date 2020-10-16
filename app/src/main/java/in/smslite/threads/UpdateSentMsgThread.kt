package `in`.smslite.threads

import `in`.smslite.SMSApplication
import `in`.smslite.activity.MainActivity
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import `in`.smslite.utils.ContactUtils
import `in`.smslite.utils.MessageUtils
import android.content.Context
import android.provider.Telephony
import android.util.Log

/**
 * Thread to update the sent message when inboxy was not default sms app
 *
 *
 * Created by rahul1993 on 5/4/2018.
 */
class UpdateSentMsgThread     //  int cursorLastCountValue;
(private val mContext: Context) : Thread() {
    override fun run() {
        super.run()
        if (MessageUtils.checkIfDefaultSms(mContext)) {
//      Answers.getInstance().logCustom(new CustomEvent("Default SMS app")
//          .putCustomAttribute("Manufacturer", Build.MANUFACTURER)
//          .putCustomAttribute("Version", Build.VERSION.CODENAME));
        } else {
//      Answers.getInstance().logCustom(new CustomEvent("Not Default SMS app")
//          .putCustomAttribute("Manufacturer", Build.MANUFACTURER)
//          .putCustomAttribute("Version", Build.VERSION.CODENAME));
        }
        var timeStampLocalDb: String? = null
        try {
            MainActivity.localMessageDbViewModel!!.sentSmsCount.use { localDbcur ->
                val localDbCount = localDbcur.count //localDbcur can be null
                if (localDbCount != 0) {
                    Log.d(TAG, Integer.toString(localDbCount) + "localDb")
                    localDbcur.moveToFirst()
                    timeStampLocalDb = localDbcur.getString(localDbcur.getColumnIndex("timestamp"))
                    Log.d(TAG, timeStampLocalDb + "localDbTimeStamp")
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            //      Crashlytics.logException(e);
            currentThread().interrupt()
        }
        val projection = arrayOf(Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.DATE)
        //      cursor.setNotificationUri(mContext.getContentResolver(), Telephony.Sms.Sent.CONTENT_URI);
        val message = Message()
        try {
            mContext.contentResolver.query(Telephony.Sms.Sent.CONTENT_URI, projection,
                    null, null, Telephony.TextBasedSmsColumns.DATE + " DESC").use { cursor ->
                val size = cursor!!.count
                val dateIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)
                val addressIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS)
                val bodyIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY)
                if (size != 0) {
                    Log.d(TAG, Integer.toString(size) + "contentProvider")
                    //          int newRow = size - localDbCount;
                    cursor.moveToFirst()
                    do {
                        val time = cursor.getString(dateIndex)
                        if (time.toLong() > timeStampLocalDb!!.toLong()) {
                            var address = cursor.getString(addressIndex)
                            val body = cursor.getString(bodyIndex)
                            val contact: Contact = ContactUtils.getContact(address, mContext, false)
                            address = ContactUtils.normalizeNumber(address)
                            message.address = address
                            message.seen = true
                            message.read = true
                            message.body = body
                            message.timestamp = time.toLong()
                            message.threadId = 0 // TODO retrive the correct threadId
                            message.type = Message.MessageType.SENT
                            val category: Int = MessageDatabase.getInMemoryDatabase(SMSApplication.application).messageDao().findCategory(address)
                            if (category != 0) {
                                message.category = category
                            } else {
                                message.category = contact.category
                            }
                            MainActivity.localMessageDbViewModel!!.insertMessage(message)
                            Log.d(TAG, "time>timeStampLocal")
                            Log.d(TAG, address + body + time)
                        } else {
                            break
                        }
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = UpdateSentMsgThread::class.java.simpleName
    }
}