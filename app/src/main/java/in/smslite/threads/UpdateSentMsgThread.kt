package `in`.smslite.threads

import `in`.smslite.repository.MessageRepository
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Thread to update the sent message when inboxy was not default sms app
 *
 *
 * Created by rahul1993 on 5/4/2018.
 */
class UpdateSentMsgThread(private val mContext: Context) : Thread(), KoinComponent {
    private val messageRepository: MessageRepository by inject()
    private val iopScope: CoroutineScope by inject()

    override fun run() {
        super.run()
//        if (MessageUtils.checkIfDefaultSms(mContext)) {
//            var timeStampLocalDb: String? = null
//            try {
//                messageRepository.getSentSmsCount().use { localDbcur ->
//                    val localDbCount = localDbcur!!.count //localDbcur can be null
//                    if (localDbCount != 0) {
//                        localDbcur.moveToFirst()
//                        timeStampLocalDb = localDbcur.getString(localDbcur.getColumnIndex("timestamp"))
//                    }
//                }
//            } catch (e: NullPointerException) {
//                e.printStackTrace()
//                currentThread().interrupt()
//            }
//            val projection = arrayOf(Telephony.TextBasedSmsColumns.ADDRESS,
//                    Telephony.TextBasedSmsColumns.BODY, Telephony.TextBasedSmsColumns.DATE)
//            //      cursor.setNotificationUri(mContext.getContentResolver(), Telephony.Sms.Sent.CONTENT_URI);
//            val message = Message()
//            try {
//                mContext.contentResolver.query(Telephony.Sms.Sent.CONTENT_URI, projection,
//                        null, null, Telephony.TextBasedSmsColumns.DATE + " DESC").use { cursor ->
//                    val size = cursor!!.count
//                    val dateIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)
//                    val addressIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS)
//                    val bodyIndex = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY)
//                    if (size != 0) {
//                        cursor.moveToFirst()
//                        do {
//                            val time = cursor.getString(dateIndex)
//                            if (time.toLong() > timeStampLocalDb!!.toLong()) {
//                                var address = cursor.getString(addressIndex)
//                                val body = cursor.getString(bodyIndex)
//                                val contact: Contact = ContactUtils.getContact(address, mContext, false)
//                                address = ContactUtils.normalizeNumber(address)
//                                message.address = address
//                                message.isSeen = true
//                                message.isRead = true
//                                message.body = body
//                                message.receivedDate = time.toLong()
//                                message.threadId = 0 // TODO retrive the correct threadId
//                                message.type = Message.MessageType.SENT
//                                val category: Int = runBlocking { messageRepository.findCategory(address) }
//                                if (category != 0) {
//                                    message.category = category
//                                } else {
//                                    message.category = contact.category
//                                }
//                                iopScope.launch { messageRepository.insertMessage(message) }
//                            } else {
//                                break
//                            }
//                        } while (cursor.moveToNext())
//                    }
//                }
//            } catch (e: NullPointerException) {
//                e.printStackTrace()
//            } catch (e: NumberFormatException) {
//                e.printStackTrace()
//            }
//        }
    }
}