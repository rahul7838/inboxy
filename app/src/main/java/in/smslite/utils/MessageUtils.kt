package `in`.smslite.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Telephony
import android.text.TextUtils
import android.util.Patterns
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.regex.Pattern

/**
 * Created by rahul1993 on 11/11/2017.
 */
object MessageUtils : KoinComponent {

    private val sharedPreferences: SharedPreferences by inject()
    private val context: Context by inject()
    private val NAME_ADDR_EMAIL_PATTERN = Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*")


//    fun getTimeStamp(receivedTime: Long, sentTime: Long, type: Message.MessageType?): Long {
//        return if (Message.MessageType.SENT.compareTo(type) == 0 && sentTime != 0L) {
//            sentTime
//        } else {
//            receivedTime
//        }
//    }

    fun isEmailAddress(address: String?): Boolean {
        if (TextUtils.isEmpty(address)) {
            return false
        }
        val s = extractAddrSpec(address)
        val match = Patterns.EMAIL_ADDRESS.matcher(s)
        return match.matches()
    }

    private fun extractAddrSpec(address: String?): String? {
        val match = NAME_ADDR_EMAIL_PATTERN.matcher(address)
        return if (match.matches()) {
            match.group(2)
        } else address
    }

    fun checkIfDefaultSms(context: Context): Boolean {
        return Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
    }

    fun setDefaultSms(context: Context) {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
        context.startActivity(intent)
    }
}