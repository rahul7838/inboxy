package `in`.smslite.threads

import `in`.smslite.R
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.repository.MessageRepository
import android.content.Context
import android.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import java.util.regex.Pattern

/**
 * Created by rahul1993 on 11/12/2017.
 */
object BroadcastMessage : KoinComponent {

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject()

    fun broadcastMessage(contexts: Context, message: Message, contact: Contact, customNotification: Boolean) = ioScope.launch {
        PreferenceManager.setDefaultValues(contexts, R.xml.preferences, false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contexts)
        val OTPKeywords = Arrays.asList<String>(*contexts.resources.getStringArray(R.array.OTP_keyword))
        val OTPKeywordsSize = OTPKeywords.size
        messageRepository.insertMessage(message)
//    boolean l =sharedPreferences.getBoolean(contexts[0].getString(R.string.pref_key_notification), false);
//    Log.d(TAG, String.valueOf(l)+" preferencel");
        //    boolean l =sharedPreferences.getBoolean(contexts[0].getString(R.string.pref_key_notification), false);
//    Log.d(TAG, String.valueOf(l)+" preferencel");
        if (sharedPreferences.getBoolean(contexts.getString(R.string.pref_key_notification), false)) {
            if (message.category != Contact.PRIMARY) {
                for (i in 0 until OTPKeywordsSize) {
                    val pattern = Pattern.compile(OTPKeywords[i].toString() + "[\\s]{1}")
                    //        Pattern pattern = Pattern.compile("[^a-z0-9A-Z]{1}"+OTPKeywords.get(i)+ "[^a-z0-9A-Z]{1}");
                    val match = pattern.matcher(message.body.toLowerCase())
                    if (match.find()) {
//                        if (NotificationUtils.getOTPFromString(message.body) != null) {
//                            NotificationUtils.sendCustomNotification(contexts, message.address, message.body, message.timestamp, contact)
//                            customNotification = true
//                            messageRepository.markAllSeen(contact.category)
//                            break
//                        } else {
//                            NotificationUtils.sendGroupedNotification(contexts, contact, message)
//                        }
                    }
                }
            }
            if (!customNotification) {
//                NotificationUtils.sendGroupedNotification(contexts, contact, message)
            }
        }
    }
}