package `in`.smslite.contacts

import `in`.smslite.LogTag
import `in`.smslite.R
import `in`.smslite.SMSApplication
import `in`.smslite.utils.MessageUtils
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.sqlite.SqliteWrapper
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import java.io.IOException
import java.nio.CharBuffer
import java.util.*
import java.util.concurrent.locks.ReentrantLock

//import in.inboxy.activity.ComposeSmsActivity;
class PhoneContact : Contact {
    var contactMethodId // Id in phone or email Uri returned by provider of current
            : Long = 0
        private set

    // PhoneContact, -1 is invalid. e.g. contact method id is 20 when
    // current contact has phone content://.../phones/20.
    var contactMethodType = 0
        private set
    private var mNumberE164: String? = null

    @get:Synchronized
    var nameAndNumber // for display, e.g. Fred Flintstone <670-782-1123>
            : String? = null
        private set
    var isNumberModified // true if the number is modified
            = false

    @get:Synchronized
    @set:Synchronized
    var recipientId // used to find the Recipient cache entry
            : Long = 0

    @get:Synchronized
    var label: String? = null
        private set
    private var mPersonId: Long = 0

    @get:Synchronized
    var presenceResId // TODO: make this a state instead of a res ID
            = 0
        private set
    var presenceText: String? = null
        private set
    private var mIsStale = false
    private var mQueryPending = false
    var isMe // true if this contact is me!
            = false
        private set
    var sendToVoicemail // true if this contact should not put up notification
            = false
        private set

    private constructor(number: String, name: String) {
        init(number, name)
    }

    /*
     * Make a basic contact object with a phone number.
     */
    private constructor(number: String) {
        init(number, "")
    }

    private constructor(isMe: Boolean) {
        init(SELF_ITEM_KEY, "")
        this.isMe = isMe
    }

    private fun init(number: String, name: String) {
        contactMethodId = CONTACT_METHOD_ID_UNKNOWN.toLong()
        mName = name
        number(number)
        isNumberModified = false
        label = ""
        mPersonId = 0
        threadId = null
        presenceResId = 0
        mIsStale = true
        sendToVoicemail = false
        source = Source.PHONE
        category = PRIMARY
    }

    override fun toString(): String {
        return String.format(Locale.ENGLISH, "{ number=%s, name=%s, nameAndNumber=%s, label=%s, person_id=%d, hash=%d method_id=%d }",
                if (number != null) number else "null",
                if (mName != null) mName else "null",
                if (nameAndNumber != null) nameAndNumber else "null",
                if (label != null) label else "null",
                mPersonId, hashCode(),
                contactMethodId)
    }

    fun removeFromCache() {
        sContactCache!!.remove(this)
    }

    @Synchronized
    fun reload() {
        mIsStale = true
        sContactCache!![number, false]
    }

    @Synchronized
    fun number(number: String) {
//        if (!MessageUtils.isEmailAddress(number)) {
//            mNumber = PhoneNumberUtils.formatNumber(number, mNumberE164, SMSApplication.getApplication().getCurrentCountryIso());
//        } else {
        this.number = number
        //        }
//        notSynchronizedUpdateNameAndNumber();
//        mNumberIsModified = true;
    }

    @get:Synchronized
    val name: String?
        get() = if (TextUtils.isEmpty(mName)) {
            number
        } else {
            mName
        }

    @get:Synchronized
    val isNamed: Boolean
        get() = !TextUtils.isEmpty(mName)

    private fun notSynchronizedUpdateNameAndNumber() {
        nameAndNumber = formatNameAndNumber(mName, number, mNumberE164)
    }

    @get:Synchronized
    val uri: Uri
        get() = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, mPersonId)

    @Synchronized
    fun existsInDatabase(): Boolean {
        return mPersonId > 0
    }

    @get:Synchronized
    val isEmail: Boolean
        get() = MessageUtils.isEmailAddress(number)

    @get:Synchronized
    val phoneUri: Uri
        get() = if (existsInDatabase()) {
            ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, contactMethodId)
        } else {
            val ub = Uri.Builder()
            ub.scheme(TEL_SCHEME)
            ub.encodedOpaquePart(number)
            ub.build()
        }

    private class ContactsCache(private val mContext: Context) {
        private val mTaskQueue = TaskStack()
        private val mContactsHash = HashMap<String, ArrayList<PhoneContact>>()
        fun dump() {
            synchronized(this@ContactsCache) {
                Log.d(TAG, "**** PhoneContact cache dump ****")
                for (key in mContactsHash.keys) {
                    val alc = mContactsHash[key]!!
                    for (c in alc) {
                        Log.d(TAG, "$key ==> $c")
                    }
                }
            }
        }

        fun pushTask(r: Runnable?) {
            mTaskQueue.push(r)
        }

        fun getMe(canBlock: Boolean): PhoneContact {
            return get(SELF_ITEM_KEY, true, canBlock)
        }

        operator fun get(number: String?, canBlock: Boolean): PhoneContact {
            return get(number, false, canBlock)
        }

        private operator fun get(number: String?, isMe: Boolean, canBlock: Boolean): PhoneContact {
            var number = number
            if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                logWithTrace(TAG, "get(%s, %s, %s)", number, isMe, canBlock)
            }
            if (TextUtils.isEmpty(number)) {
                number = "" // In some places (such as Korea), it's possible to receive
                // a message without the sender's address. In this case,
                // all such anonymous messages will get added to the same
                // thread.
            }

            // Always return a PhoneContact object, if if we don't have an actual contact
            // in the contacts db.
            val contact = internalGet(number, isMe)
            var r: Runnable? = null
            synchronized(contact) {

                // If there's a query pending and we're willing to block then
                // wait here until the query completes.
                while (canBlock && contact.mQueryPending) {
                    try {
                        contact
                    } catch (ex: InterruptedException) {
                        // try again by virtue of the loop unless mQueryPending is false
                    }
                }

                // If we're stale and we haven't already kicked off a query then kick
                // it off here.
                if (contact.mIsStale && !contact.mQueryPending) {
                    contact.mIsStale = false
                    if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
                        log("async update for " + contact.toString() + " canBlock: " + canBlock +
                                " isStale: " + contact.mIsStale)
                    }
                    r = Runnable { updateContact(contact) }

                    // set this to true while we have the lock on contact since we will
                    // either run the query directly (canBlock case) or push the query
                    // onto the queue.  In either case the mQueryPending will get set
                    // to false via updateContact.
                    contact.mQueryPending = true
                }
            }
            // do this outside of the synchronized so we don't hold up any
            // subsequent calls to "get" on other threads
            if (r != null) {
                if (canBlock) {
                    r!!.run()
                } else {
                    pushTask(r)
                }
            }
            return contact
        }

        /**
         * Get CacheEntry list for given phone URIs. This method will do single one query to
         * get expected contacts from provider. Be sure passed in URIs are not null and contains
         * only valid URIs.
         */
        fun getContactInfoForPhoneUris(uris: Array<Parcelable>): List<PhoneContact>? {
            if (uris.size == 0) {
                return null
            }
            val idSetBuilder = StringBuilder()
            var first = true
            for (p in uris) {
                val uri = p as Uri
                if ("content" == uri.scheme) {
                    if (first) {
                        first = false
                        idSetBuilder.append(uri.lastPathSegment)
                    } else {
                        idSetBuilder.append(',').append(uri.lastPathSegment)
                    }
                }
            }
            // Check whether there is content URI.
            if (first) return null
            var cursor: Cursor? = null
            if (idSetBuilder.length > 0) {
                val whereClause = ContactsContract.CommonDataKinds.Phone._ID + " IN (" + idSetBuilder.toString() + ")"
                cursor = mContext.contentResolver.query(
                        PHONES_WITH_PRESENCE_URI, CALLER_ID_PROJECTION, whereClause, null, null)
            }
            if (cursor == null) {
                return null
            }
            val entries: MutableList<PhoneContact> = ArrayList()
            try {
                while (cursor.moveToNext()) {
                    val entry = PhoneContact(cursor.getString(PHONE_NUMBER_COLUMN),
                            cursor.getString(CONTACT_NAME_COLUMN))
                    fillPhoneTypeContact(entry, cursor)
                    val value = ArrayList<PhoneContact>()
                    value.add(entry)
                    // Put the result in the cache.
                    mContactsHash[key(entry.number ?: "", sStaticKeyBuffer)] = value
                    entries.add(entry)
                }
            } finally {
                cursor.close()
            }
            return entries
        }

        private fun contactChanged(orig: PhoneContact, newContactData: PhoneContact): Boolean {
            // The phone number should never change, so don't bother checking.
            // TODO: Maybe update it if it has gotten longer, i.e. 650-234-5678 -> +16502345678?

            // Do the quick check first.
            if (orig.contactMethodType != newContactData.contactMethodType) {
                return true
            }
            if (orig.contactMethodId != newContactData.contactMethodId) {
                return true
            }
            if (orig.mPersonId != newContactData.mPersonId) {
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
//                    Log.d(TAG, "person id changed")
                }
                return true
            }
            if (orig.presenceResId != newContactData.presenceResId) {
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
//                    Log.d(TAG, "presence changed")
                }
                return true
            }
            if (orig.sendToVoicemail != newContactData.sendToVoicemail) {
                return true
            }
            val oldName = emptyIfNull(orig.mName)
            val newName = emptyIfNull(newContactData.mName)
            if (oldName != newName) {
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
//                    Log.d(TAG, String.format("name changed: %s -> %s", oldName, newName))
                }
                return true
            }
            val oldLabel = emptyIfNull(orig.label)
            val newLabel = emptyIfNull(newContactData.label)
            if (oldLabel != newLabel) {
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
//                    Log.d(TAG, String.format("label changed: %s -> %s", oldLabel, newLabel))
                }
                return true
            }
            if (!Arrays.equals(orig.mAvatarData, newContactData.mAvatarData)) {
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
//                    Log.d(TAG, "avatar changed")
                }
                return true
            }
            return false
        }

        private fun updateContact(c: PhoneContact?) {
            val lock = ReentrantLock()
            if (c == null) {
                return
            }
            val entry = getContactInfo(c)
            synchronized(c) {
                if (contactChanged(c, entry)) {
                    if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
                        log("updateContact: contact changed for " + entry.mName)
                    }
                    c.number = entry.number
                    c.label = entry.label
                    c.mPersonId = entry.mPersonId
                    c.threadId = entry.threadId
                    c.presenceResId = entry.presenceResId
                    c.presenceText = entry.presenceText
                    c.mAvatarData = entry.mAvatarData
                    c.mAvatar = entry.mAvatar
                    c.contactMethodId = entry.contactMethodId
                    c.contactMethodType = entry.contactMethodType
                    c.mNumberE164 = entry.mNumberE164
                    c.mName = entry.mName
                    c.sendToVoicemail = entry.sendToVoicemail
                    c.notSynchronizedUpdateNameAndNumber()

                    // We saw a bug where we were updating an empty contact. That would trigger
                    // l.onUpdate() below, which would call ComposeMessageActivity.onUpdate,
                    // which would call the adapter's notifyDataSetChanged, which would throw
                    // away the message items and rebuild, eventually calling updateContact()
                    // again -- all in a vicious and unending loop. Break the cycle and don't
                    // notify if the number (the most important piece of information) is empty.
                    /* if (!TextUtils.isEmpty(c.mNumber)) {
                            // clone the list of listeners in case the onUpdate call turns around and
                            // modifies the list of listeners
                            // access to mListeners is synchronized on ContactsCache
                            HashSet<UpdateListener> iterator;
                            synchronized (c.mListeners) {
                                iterator = (HashSet<UpdateListener>) c.mListeners.clone();
                            }
                            for (UpdateListener l : iterator) {
                                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                                    Log.d(TAG, "updating " + l);
                                }
                                l.onUpdate(c);
                            }
                    }*/
                }
                synchronized(c) {
                    c.mQueryPending = false
//                    c.notifyAll()
                }
            }
        }

        /**
         * Returns the caller info in PhoneContact.
         */
        private fun getContactInfo(c: PhoneContact): PhoneContact {
            return if (c.isMe) {
                contactInfoForSelf
                //            } else if (MessageUtils.isEmailAddress(c.mNumber)) {
//                return getContactInfoForEmailAddress(c.mNumber);
//            } else if (isAlphaNumber(c.mNumber)) {
                // first try to look it up in the email field
//                PhoneContact contact = getContactInfoForEmailAddress(c.mNumber);
//                if (contact.existsInDatabase()) {
//                    return contact;
//                }
                // then look it up in the phone field
//                return getContactInfoForPhoneNumber(c.mNumber);
            } else {
                // it's a real phone number, so strip out non-digits and look it up
                //                final String strippedNumber = android.telephony.PhoneNumberUtils.stripSeparators(c.mNumber);
                getContactInfoForPhoneNumber(c.number!!)
            }
        }

        // Some received sms's have addresses such as "OakfieldCPS" or "T-Mobile". This
        // function will attempt to identify these and return true. If the number contains
        // 3 or more digits, such as "jello123", this function will return false.
        // Some countries have 3 digits shortcodes and we have to identify them as numbers.
        //    http://en.wikipedia.org/wiki/Short_code
        // Examples of input/output for this function:
        //    "Jello123" -> false  [3 digits, it is considered to be the phone number "123"]
        //    "T-Mobile" -> true   [it is considered to be the address "T-Mobile"]
        //    "Mobile1"  -> true   [1 digit, it is considered to be the address "Mobile1"]
        //    "Dogs77"   -> true   [2 digits, it is considered to be the address "Dogs77"]
        //    "****1"    -> true   [1 digits, it is considered to be the address "****1"]
        //    "#4#5#6#"  -> true   [it is considered to be the address "#4#5#6#"]
        //    "AB12"     -> true   [2 digits, it is considered to be the address "AB12"]
        //    "12"       -> true   [2 digits, it is considered to be the address "12"]
        private fun isAlphaNumber(number: String): Boolean {
            // TODO: PhoneNumberUtils.isWellFormedSmsAddress() only check if the number is a valid
            // GSM SMS address. If the address contains a dialable char, it considers it a well
            // formed SMS addr. CDMA doesn't work that way and has a different parser for SMS
            // address (see CdmaSmsAddress.parse(String address)). We should definitely fix this!!!
            var number = number
            if (!PhoneNumberUtils.isWellFormedSmsAddress(number)) {
                // The example "T-Mobile" will exit here because there are no numbers.
                return true // we're not an sms address, consider it an alpha number
            }
            number = PhoneNumberUtils.extractNetworkPortion(number)
            return if (TextUtils.isEmpty(number)) {
                true // there are no digits whatsoever in the number
            } else number.length < 3
            // At this point, anything like "Mobile1" or "Dogs77" will be stripped down to
            // "1" and "77". "#4#5#6#" remains as "#4#5#6#" at this point.
        }

        /**
         * Queries the caller id info with the phone number.
         * @return a PhoneContact containing the caller id info corresponding to the number.
         */
        private fun getContactInfoForPhoneNumber(number: String): PhoneContact {
            val entry = PhoneContact(number)
            entry.contactMethodType = CONTACT_METHOD_TYPE_PHONE
            if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                log("queryContactInfoByNumber: number=$number")
            }

//            String normalizedNumber = PhoneNumberUtils.normalizeNumber(number);
            val minMatch = PhoneNumberUtils.toCallerIDMinMatch(number)
            if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(minMatch)) {
                val numberLen = number.length.toString()
                //                String numberE164 = PhoneNumberUtils.formatNumberToE164(
//                        number, SMSApplication.getApplication().getCurrentCountryIso());
                val selection: String
                val args: Array<String>
                if (TextUtils.isEmpty(number)) {
                    selection = CALLER_ID_SELECTION_WITHOUT_E164
                    args = arrayOf(minMatch, numberLen, number, numberLen)
                } else {
                    selection = CALLER_ID_SELECTION
                    args = arrayOf(
                            minMatch, number, numberLen, number, numberLen)
                }
                val cursor = mContext.contentResolver.query(
                        PHONES_WITH_PRESENCE_URI, CALLER_ID_PROJECTION, selection, args, null)
                if (cursor == null) {
                    Log.w(TAG, "queryContactInfoByNumber(" + number + ") returned NULL cursor!"
                            + " contact uri used " + PHONES_WITH_PRESENCE_URI)
                    return entry
                }
                try {
                    if (cursor.moveToFirst()) {
                        fillPhoneTypeContact(entry, cursor)
                    }
                } finally {
                    cursor.close()
                }
            }
            return entry
        }

        /**
         * @return a PhoneContact containing the info for the profile.
         */
        private val contactInfoForSelf: PhoneContact
            private get() {
                val entry = PhoneContact(true)
                entry.contactMethodType = CONTACT_METHOD_TYPE_SELF
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                    log("getContactInfoForSelf")
                }
                val cursor = mContext.contentResolver.query(
                        ContactsContract.Profile.CONTENT_URI, SELF_PROJECTION, null, null, null)
                if (cursor == null) {
                    Log.w(TAG, "getContactInfoForSelf() returned NULL cursor!"
                            + " contact uri used " + ContactsContract.Profile.CONTENT_URI)
                    return entry
                }
                try {
                    if (cursor.moveToFirst()) {
                        fillSelfContact(entry, cursor)
                    }
                } finally {
                    cursor.close()
                }
                return entry
            }

        private fun fillPhoneTypeContact(contact: PhoneContact, cursor: Cursor) {
            synchronized(contact) {
                contact.contactMethodType = CONTACT_METHOD_TYPE_PHONE
                contact.contactMethodId = cursor.getLong(PHONE_ID_COLUMN)
                contact.label = cursor.getString(PHONE_LABEL_COLUMN)
                contact.mName = cursor.getString(CONTACT_NAME_COLUMN)
                contact.mPersonId = cursor.getLong(CONTACT_ID_COLUMN)
                contact.threadId = contact.mPersonId.toString()
                contact.presenceResId = getPresenceIconResourceId(
                        cursor.getInt(CONTACT_PRESENCE_COLUMN))
                contact.presenceText = cursor.getString(CONTACT_STATUS_COLUMN)
                contact.mNumberE164 = cursor.getString(PHONE_NORMALIZED_NUMBER)
                contact.sendToVoicemail = cursor.getInt(SEND_TO_VOICEMAIL) == 1
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                    log("fillPhoneTypeContact: name=" + contact.mName + ", number="
                            + contact.number + ", presence=" + contact.presenceResId
                            + " SendToVoicemail: " + contact.sendToVoicemail)
                }
            }
            val data = loadAvatarData(contact)
            synchronized(contact) { contact.mAvatarData = data }
        }

        private fun fillSelfContact(contact: PhoneContact, cursor: Cursor) {
            synchronized(contact) {
                contact.mName = cursor.getString(SELF_NAME_COLUMN)
                if (TextUtils.isEmpty(contact.mName)) {
                    contact.mName = mContext.getString(R.string.messagelist_sender_self)
                }
                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                    log("fillSelfContact: name=" + contact.mName + ", number="
                            + contact.number)
                }
            }
            val data = loadAvatarData(contact)
            synchronized(contact) { contact.mAvatarData = data }
        }

        /*
         * Load the avatar data from the cursor into memory.  Don't decode the data
         * until someone calls for it (see getAvatar).  Hang onto the raw data so that
         * we can compare it when the data is reloaded.
         * TODO: consider comparing a checksum so that we don't have to hang onto
         * the raw bytes after the image is decoded.
         */
        private fun loadAvatarData(entry: PhoneContact): ByteArray? {
            var data: ByteArray? = null
            if (!entry.isMe && entry.mPersonId == 0L || entry.mAvatar != null) {
                return null
            }
            if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                log("loadAvatarData: name=" + entry.mName + ", number=" + entry.number)
            }

            // If the contact is "me", then use my local profile photo. Otherwise, build a
            // uri to get the avatar of the contact.
            val contactUri = if (entry.isMe) ContactsContract.Profile.CONTENT_URI else ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, entry.mPersonId)
            val avatarDataStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    mContext.contentResolver,
                    contactUri, true)
            try {
                if (avatarDataStream != null) {
                    data = ByteArray(avatarDataStream.available())
                    avatarDataStream.read(data, 0, data.size)
                }
            } catch (ex: IOException) {
                //
            } finally {
                try {
                    avatarDataStream?.close()
                } catch (e: IOException) {
                }
            }
            return data
        }

        private fun getPresenceIconResourceId(presence: Int): Int {
            // TODO: must fix for SDK
            return if (presence != ContactsContract.Presence.OFFLINE) {
                ContactsContract.Presence.getPresenceIconResourceId(presence)
            } else 0
        }

        /**
         * Query the contact email table to get the name of an email address.
         */
        private fun getContactInfoForEmailAddress(email: String): PhoneContact {
            val entry = PhoneContact(email)
            entry.contactMethodType = CONTACT_METHOD_TYPE_EMAIL
            val cursor = SqliteWrapper.query(mContext, mContext.contentResolver,
                    EMAIL_WITH_PRESENCE_URI,
                    EMAIL_PROJECTION,
                    EMAIL_SELECTION, arrayOf(email),
                    null)
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        var found = false
                        synchronized(entry) {
                            entry.contactMethodId = cursor.getLong(EMAIL_ID_COLUMN)
                            entry.presenceResId = getPresenceIconResourceId(
                                    cursor.getInt(EMAIL_STATUS_COLUMN))
                            entry.mPersonId = cursor.getLong(EMAIL_CONTACT_ID_COLUMN)
                            entry.threadId = entry.mPersonId.toString()
                            entry.sendToVoicemail = cursor.getInt(EMAIL_SEND_TO_VOICEMAIL_COLUMN) == 1
                            var name = cursor.getString(EMAIL_NAME_COLUMN)
                            if (TextUtils.isEmpty(name)) {
                                name = cursor.getString(EMAIL_CONTACT_NAME_COLUMN)
                            }
                            if (!TextUtils.isEmpty(name)) {
                                entry.mName = name
                                if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
                                    log("getContactInfoForEmailAddress: name=" + entry.mName +
                                            ", email=" + email + ", presence=" +
                                            entry.presenceResId)
                                }
                                found = true
                            }
                        }
                        if (found) {
                            val data = loadAvatarData(entry)
                            synchronized(entry) { entry.mAvatarData = data }
                            break
                        }
                    }
                } finally {
                    cursor.close()
                }
            }
            return entry
        }

        // Invert and truncate to five characters the phoneNumber so that we
        // can use it as the key in a hashtable.  We keep a mapping of this
        // key to a list of all contacts which have the same key.
        private fun key(phoneNumber: String, keyBuffer: CharBuffer): String {
            keyBuffer.clear()
            keyBuffer.mark()
            var position = phoneNumber.length
            var resultCount = 0
            while (--position >= 0) {
                val c = phoneNumber[position]
                if (Character.isDigit(c)) {
                    keyBuffer.put(c)
                    if (++resultCount == STATIC_KEY_BUFFER_MAXIMUM_LENGTH) {
                        break
                    }
                }
            }
            keyBuffer.reset()
            return if (resultCount > 0) {
                keyBuffer.toString()
            } else {
                // there were no usable digits in the input phoneNumber
                phoneNumber
            }
        }

        private fun internalGet(numberOrEmail: String?, isMe: Boolean): PhoneContact {
            synchronized(this@ContactsCache) {

                // See if we can find "number" in the hashtable.
                // If so, just return the result.
                val isNotRegularPhoneNumber = isMe || MessageUtils.isEmailAddress(numberOrEmail)
                val key = if (isNotRegularPhoneNumber) numberOrEmail else numberOrEmail?.let { key(it, sStaticKeyBuffer) }
                var candidates = mContactsHash[key]
                if (candidates != null) {
                    val length = candidates.size
                    for (i in 0 until length) {
                        val c = candidates[i]
                        if (isNotRegularPhoneNumber) {
                            if (numberOrEmail == c.number) {
                                return c
                            }
                        } else {
                            if (PhoneNumberUtils.compare(numberOrEmail, c.number)) {
                                return c
                            }
                        }
                    }
                } else {
                    candidates = ArrayList()
                    // call toString() since it may be the static CharBuffer
                    mContactsHash[key!!] = candidates
                }
                val c = if (isMe) PhoneContact(true) else PhoneContact(numberOrEmail!!)
                candidates.add(c)
                return c
            }
        }

        fun invalidate() {
            // Don't remove the contacts. Just mark them stale so we'll update their
            // info, particularly their presence.
            synchronized(this@ContactsCache) {
                for (alc in mContactsHash.values) {
                    for (c in alc) {
                        synchronized(c) { c.mIsStale = true }
                    }
                }
            }
        }

        // Remove a contact from the ContactsCache based on the number or email address
        fun remove(contact: PhoneContact) {
            synchronized(this@ContactsCache) {
                val number = contact.number
                val isNotRegularPhoneNumber = contact.isMe ||
                        MessageUtils.isEmailAddress(number)
                val key = if (isNotRegularPhoneNumber) number else key(number!!, sStaticKeyBuffer)
                val candidates = mContactsHash[key]
                if (candidates != null) {
                    val length = candidates.size
                    for (i in 0 until length) {
                        val c = candidates[i]
                        if (isNotRegularPhoneNumber) {
                            if (number == c.number) {
                                candidates.removeAt(i)
                                break
                            }
                        } else {
                            if (PhoneNumberUtils.compare(number, c.number)) {
                                candidates.removeAt(i)
                                break
                            }
                        }
                    }
                    if (candidates.isEmpty()) {
                        mContactsHash.remove(key)
                    }
                }
            }
        }

        private class TaskStack {
            private val mThingsToLoad: ArrayList<Runnable?>
            var mWorkerThread: Thread
            fun push(r: Runnable?) {
                synchronized(mThingsToLoad) {
                    mThingsToLoad.add(r)
//                    mThingsToLoad.notify()
                }
            }

            init {
                mThingsToLoad = ArrayList()
                mWorkerThread = Thread({
                    while (true) {
                        var r: Runnable? = null
                        synchronized(mThingsToLoad) {
                            if (mThingsToLoad.isEmpty()) {
                                try {
//                                    mThingsToLoad.wait()
                                } catch (ex: InterruptedException) {
//                                    break // Exception sent by PhoneContact.init() to stop Runnable
                                }
                            }
                            if (!mThingsToLoad.isEmpty()) {
                                r = mThingsToLoad.removeAt(0)
                            }
                        }
                        if (r != null) {
                            r!!.run()
                        }
                    }
                }, "PhoneContact.ContactsCache.TaskStack worker thread")
                mWorkerThread.priority = Thread.MIN_PRIORITY
                mWorkerThread.start()
            }
        }

        companion object {
            // Reuse this so we don't have to allocate each time we go through this
            // "get" function.
            const val STATIC_KEY_BUFFER_MAXIMUM_LENGTH = 5
            private const val SEPARATOR = ";"

            /**
             * For a specified phone number, 2 rows were inserted into phone_lookup
             * table. One is the phone number's E164 representation, and another is
             * one's normalized format. If the phone number's normalized format in
             * the lookup table is the suffix of the given number's one, it is
             * treated as matched CallerId. E164 format number must fully equal.
             *
             * For example: Both 650-123-4567 and +1 (650) 123-4567 will match the
             * normalized number 6501234567 in the phone lookup.
             *
             * The min_match is used to narrow down the candidates for the final
             * comparison.
             */
            // query params for caller id lookup
            private const val CALLER_ID_SELECTION = (" Data._ID IN "
                    + " (SELECT DISTINCT lookup.data_id "
                    + " FROM "
                    + " (SELECT data_id, normalized_number, length(normalized_number) as len "
                    + " FROM phone_lookup "
                    + " WHERE min_match = ?) AS lookup "
                    + " WHERE lookup.normalized_number = ? OR"
                    + " (lookup.len <= ? AND "
                    + " substr(?, ? - lookup.len + 1) = lookup.normalized_number))")

            // query params for caller id lookup without E164 number as param
            private const val CALLER_ID_SELECTION_WITHOUT_E164 = (" Data._ID IN "
                    + " (SELECT DISTINCT lookup.data_id "
                    + " FROM "
                    + " (SELECT data_id, normalized_number, length(normalized_number) as len "
                    + " FROM phone_lookup "
                    + " WHERE min_match = ?) AS lookup "
                    + " WHERE "
                    + " (lookup.len <= ? AND "
                    + " substr(?, ? - lookup.len + 1) = lookup.normalized_number))")

            // Utilizing private API
            private val PHONES_WITH_PRESENCE_URI = ContactsContract.Data.CONTENT_URI
            private val CALLER_ID_PROJECTION = arrayOf(
                    ContactsContract.CommonDataKinds.Phone._ID,  // 0
                    ContactsContract.CommonDataKinds.Phone.NUMBER,  // 1
                    ContactsContract.CommonDataKinds.Phone.LABEL,  // 2
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,  // 3
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  // 4
                    ContactsContract.CommonDataKinds.Phone.CONTACT_PRESENCE,  // 5
                    ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS,  // 6
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,  // 7
                    ContactsContract.Contacts.SEND_TO_VOICEMAIL // 8
            )
            private const val PHONE_ID_COLUMN = 0
            private const val PHONE_NUMBER_COLUMN = 1
            private const val PHONE_LABEL_COLUMN = 2
            private const val CONTACT_NAME_COLUMN = 3
            private const val CONTACT_ID_COLUMN = 4
            private const val CONTACT_PRESENCE_COLUMN = 5
            private const val CONTACT_STATUS_COLUMN = 6
            private const val PHONE_NORMALIZED_NUMBER = 7
            private const val SEND_TO_VOICEMAIL = 8
            private val SELF_PROJECTION = arrayOf(
                    ContactsContract.CommonDataKinds.Phone._ID,  // 0
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            private const val SELF_ID_COLUMN = 0
            private const val SELF_NAME_COLUMN = 1

            // query params for contact lookup by email
            private val EMAIL_WITH_PRESENCE_URI = ContactsContract.Data.CONTENT_URI
            private const val EMAIL_SELECTION = ("UPPER(" + ContactsContract.CommonDataKinds.Email.DATA + ")=UPPER(?) AND "
                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'")
            private val EMAIL_PROJECTION = arrayOf(
                    ContactsContract.CommonDataKinds.Email._ID,  // 0
                    ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,  // 1
                    ContactsContract.CommonDataKinds.Email.CONTACT_PRESENCE,  // 2
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID,  // 3
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,  // 4
                    ContactsContract.Contacts.SEND_TO_VOICEMAIL // 5
            )
            private const val EMAIL_ID_COLUMN = 0
            private const val EMAIL_NAME_COLUMN = 1
            private const val EMAIL_STATUS_COLUMN = 2
            private const val EMAIL_CONTACT_ID_COLUMN = 3
            private const val EMAIL_CONTACT_NAME_COLUMN = 4
            private const val EMAIL_SEND_TO_VOICEMAIL_COLUMN = 5
            var sStaticKeyBuffer = CharBuffer.allocate(STATIC_KEY_BUFFER_MAXIMUM_LENGTH)
        }
    }

    companion object {
        const val CONTACT_METHOD_TYPE_UNKNOWN = 0
        const val CONTACT_METHOD_TYPE_PHONE = 1
        const val CONTACT_METHOD_TYPE_EMAIL = 2
        const val CONTACT_METHOD_TYPE_SELF = 3 // the "Me" or profile contact
        const val TEL_SCHEME = "tel"
        const val CONTENT_SCHEME = "content"
        private const val CONTACT_METHOD_ID_UNKNOWN = -1
        private const val TAG = "PhoneContact"
        private const val SELF_ITEM_KEY = "Self_Item_Key"
        private var sContactCache: ContactsCache? = null

        //    private static final ContentObserver sContactsObserver = new ContentObserver(new Handler()) {
        //        @Override
        //        public void onChange(boolean selfUpdate) {
        //            if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
        //                log("contact changed, invalidate cache");
        //            }
        //            invalidateCache();
        //        }
        //    };
        private val sPresenceObserver: ContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfUpdate: Boolean) {
                if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
                    log("presence changed, invalidate cache")
                }
                invalidateCache()
            }
        }

        fun logWithTrace(tag: String?, msg: String?, vararg format: Any?) {
            val current = Thread.currentThread()
            val stack = current.stackTrace
            val sb = StringBuilder()
            sb.append("[")
            sb.append(current.id)
            sb.append("] ")
            sb.append(String.format(msg!!, *format))
            sb.append(" <- ")
            val stop = if (stack.size > 7) 7 else stack.size
            for (i in 3 until stop) {
                val methodName = stack[i].methodName
                sb.append(methodName)
                if (i + 1 != stop) {
                    sb.append(" <- ")
                }
            }
            Log.d(tag, sb.toString())
        }

        @JvmStatic
        operator fun get(number: String, canBlock: Boolean): PhoneContact {
            //        callListeners(contact);
            return sContactCache!![number, canBlock]
        }

        fun getMe(canBlock: Boolean): PhoneContact {
            return sContactCache!!.getMe(canBlock)
        }

        @JvmStatic
        fun getByPhoneUris(uris: Array<Parcelable>): List<PhoneContact>? {
            return sContactCache!!.getContactInfoForPhoneUris(uris)
        }

        fun invalidateCache() {
            if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
                log("invalidateCache")
            }

            // While invalidating our local Cache doesn't remove the contacts, it will mark them
            // stale so the next time we're asked for a particular contact, we'll return that
            // stale contact and at the same time, fire off an asyncUpdateContact to update
            // that contact's info in the background. UI elements using the contact typically
            // call addListener() so they immediately get notified when the contact has been
            // updated with the latest info. They redraw themselves when we call the
            // listener's onUpdate().
            sContactCache!!.invalidate()
        }

        private fun emptyIfNull(s: String?): String {
            return s ?: ""
        }

        /**
         * Fomat the name and number.
         *
         * @param name
         * @param number
         * @param numberE164 the number's E.164 representation, is used to get the
         * country the number belongs to.
         * @return the formatted name and number
         */
        fun formatNameAndNumber(name: String?, number: String?, numberE164: String?): String? {
            // Format like this: Mike Cleron <(650) 555-1234>
            //                   Erick Tseng <(650) 555-1212>
            //                   Tutankhamun <tutank1341@gmail.com>
            //                   (408) 555-1289
            var formattedNumber = number
            if (!MessageUtils.isEmailAddress(number)) {
                formattedNumber = PhoneNumberUtils.formatNumber(number, numberE164, SMSApplication.application?.currentCountryIso)
            }
            return if (!TextUtils.isEmpty(name) && name != number) {
                "$name <$formattedNumber>"
            } else {
                formattedNumber
            }
        }

        @JvmStatic
        fun initialize(context: Context) {
//        if (sContactCache != null) { // Stop previous Runnable
//            sContactCache.mTaskQueue.mWorkerThread.interrupt();
//        }
            if (sContactCache == null) {
                sContactCache = ContactsCache(context)
            }
            //        RecipientIdCache.init(context);

            // it maybe too aggressive to listen for *any* contact changes, and rebuild MMS contact
            // cache each time that occurs. Unless we can get targeted updates for the contacts we
            // care about(which probably won't happen for a long time), we probably should just
            // invalidate cache peoridically, or surgically.
            /*
        context.getContentResolver().registerContentObserver(
                Contacts.CONTENT_URI, true, sContactsObserver);
        */
        }

        @JvmStatic
        fun dump() {
            sContactCache!!.dump()
        }

        private fun log(msg: String) {
            Log.d(TAG, msg)
        }
    }
}