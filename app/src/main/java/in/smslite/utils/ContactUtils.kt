package `in`.smslite.utils

import `in`.smslite.contacts.CompanyContact
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import android.content.Context
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Contains utility function related to contacts
 */
object ContactUtils : KoinComponent {
    private val TAG = ContactUtils::class.java.simpleName
    private val context: Context by inject()

    fun getContact(number: String?, canBlock: Boolean): Contact? {

        return when {
            number?.matches(Regex("(\\+)?(\\s)?(0|91)?(\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?" +
                    "[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?")) == true -> {
                PhoneContact[number, canBlock]
            }
            else -> {
                CompanyContact[number ?: "", context]
            }
        }
        //number.matches("(\\+)?(0|91)?[798][0-9]{9}")
    }

    fun formatAddress(number: String): String {
        if (TextUtils.isEmpty(number)) {
            return number
        }
        return number.replace("-", "").replace(" ", "")
        //        String first2Char = address.substring(0, 2).toLowerCase();
//        if (first2Char.matches("[a-z]+") && address.length() == 8) {
//            return address.substring(address.length() - 6).toLowerCase();
//        } else {
//            return address.toLowerCase();
//        }
    }

    fun getShortCode(address: String): String {
        var address = address
        address = address.replace("-", "")
        return if (address.length == 8) {
            address.substring(address.length - 6)
        } else {
            address
        }
    }

    fun normalizeNumber(number: String?): String {
        var number = number
        if (number == null || number.isEmpty()) {
            throw RuntimeException("Phone number can never be null")
        }
        number = PhoneNumberUtils.stripSeparators(number)
        return if (number.length < 10 || number[0] == '+') {
            number
        } else {
            val e164number = PhoneNumberUtils.formatNumberToE164(number, "IN")
            if (e164number == null || e164number.isEmpty()) {
                number
                //                throw new RuntimeException("Phone number can't be null");
            } else e164number
        }
    }
}