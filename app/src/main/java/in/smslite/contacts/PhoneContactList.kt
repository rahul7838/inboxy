package `in`.smslite.contacts

import `in`.smslite.LogTag
import `in`.smslite.contacts.PhoneContact.Companion.getByPhoneUris
import android.net.Uri
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import java.util.*

class PhoneContactList : ArrayList<PhoneContact?>() {
    // We only show presence for single contacts.
    val presenceResId: Int
        get() =// We only show presence for single contacts.
            if (size != 1) 0 else get(0)!!.presenceResId

    fun formatNames(separator: String?): String {
        val names = arrayOfNulls<String>(size)
        var i = 0
        for (c in this) {
            names[i++] = c?.name
        }
        return TextUtils.join(separator!!, names)
    }

    fun formatNamesAndNumbers(separator: String?): String {
        val nans = arrayOfNulls<String>(size)
        var i = 0
        for (c in this) {
            nans[i++] = c?.nameAndNumber
        }
        return TextUtils.join(separator!!, nans)
    }

    fun serialize(): String {
        return TextUtils.join(";", numbers)
    }

    fun containsEmail(): Boolean {
        for (c in this) {
            if (c?.isEmail == true) {
                return true
            }
        }
        return false
    }

    // Don't add duplicate numbers. This can happen if a contact name has a comma.
    // Since we use a comma as a delimiter between contacts, the code will consider
    // the same recipient has been added twice. The recipients UI still works correctly.
    // It's easiest to just make sure we only send to the same recipient once.
    val numbers: Array<String?>
        get() {
            val numbers: MutableList<String?> = ArrayList()
            var number: String
            for (c in this) {
                number = c?.number.toString()

                // Don't add duplicate numbers. This can happen if a contact name has a comma.
                // Since we use a comma as a delimiter between contacts, the code will consider
                // the same recipient has been added twice. The recipients UI still works correctly.
                // It's easiest to just make sure we only send to the same recipient once.
                if (!TextUtils.isEmpty(number) && !numbers.contains(number)) {
                    numbers.add(number)
                }
            }
            return numbers.toTypedArray()
        }

    override fun equals(obj: Any?): Boolean {
        return try {
            val other = obj as PhoneContactList?
            // If they're different sizes, the contact
            // set is obviously different.
            if (size != other!!.size) {
                return false
            }

            // Make sure all the individual contacts are the same.
            for (c in this) {
                if (!other.contains(c)) {
                    return false
                }
            }
            true
        } catch (e: ClassCastException) {
            false
        }
    }

    private fun log(msg: String) {
        Log.d(LogTag.TAG, "[ContactList] $msg")
    }

    companion object {
        private const val serialVersionUID = 1L
        fun getByNumbers(numbers: Iterable<String?>, canBlock: Boolean): PhoneContactList {
            val list = PhoneContactList()
            for (number in numbers) {
                if (!TextUtils.isEmpty(number)) {
                    list.add(PhoneContact[number!!, canBlock])
                }
            }
            return list
        }

        fun getByNumbers(semiSepNumbers: String,
                         canBlock: Boolean,
                         replaceNumber: Boolean): PhoneContactList {
            val list = PhoneContactList()
            for (number in semiSepNumbers.split(";").toTypedArray()) {
                if (!TextUtils.isEmpty(number)) {
                    val contact = PhoneContact[number, canBlock]
                    if (replaceNumber) {
                        contact.number(number)
                    }
                    list.add(contact)
                }
            }
            return list
        }

        /**
         * Returns a ContactList for the corresponding recipient URIs passed in. This method will
         * always block to query provider. The given URIs could be the phone data URIs or tel URI
         * for the numbers don't belong to any contact.
         *
         * @param uris phone URI to create the ContactList
         */
        fun blockingGetByUris(uris: Array<Parcelable>?): PhoneContactList {
            val list = PhoneContactList()
            if (uris != null && uris.size > 0) {
                for (p in uris) {
                    val uri = p as Uri
                    if ("tel" == uri.scheme) {
                        val contact = PhoneContact[uri.schemeSpecificPart, true]
                        list.add(contact)
                    }
                }
                val contacts = getByPhoneUris(uris)
                if (contacts != null) {
                    list.addAll(contacts)
                }
            }
            return list
        }

        /**
         * Returns a ContactList for the corresponding recipient ids passed in. This method will
         * create the contact if it doesn't exist, and would inject the recipient id into the contact.
         */
        fun getByIds(spaceSepIds: String?, canBlock: Boolean): PhoneContactList {
            val list = PhoneContactList()
            for (entry in RecipientIdCache.getAddresses(spaceSepIds)) {
                if (entry != null && !TextUtils.isEmpty(entry.number)) {
                    val contact = PhoneContact[entry.number, canBlock]
                    contact.recipientId = entry.id
                    list.add(contact)
                }
            }
            return list
        }
    }
}