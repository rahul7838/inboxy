package `in`.smslite.utils

/**
 * Created by rahul1993 on 2/4/2018.
 */
//object ThreadUtils {
//    private val TAG = ThreadUtils::class.java.simpleName
//
//    class getUnreadSmsAysnTask internal constructor(var contact: Contact, var context: Context) : AsyncTask<Void?, Void?, Int>() {
//        protected override fun doInBackground(vararg void: Void?): Int {
//            val mDB: MessageDatabase = MessageDatabase.getInMemoryDatabase(context)
//            val cursor: Cursor? = mDB.messageDao().getUnseenSmsCount(contact.category)
//            return cursor!!.count
//        }
//    }
//
//    class getNotiSummaryAsyncTask internal constructor(var category: Int, var context: Context) : AsyncTask<Void?, Void?, List<Message>>() {
//        protected override fun doInBackground(vararg voids: Void?): List<Message> {
//            val mDB: MessageDatabase = MessageDatabase.getInMemoryDatabase(context)
//            return mDB.messageDao().getNotificationSummary(category)
//        }
//    }
//
//    class UpdateDbNotiClickedThread(var address: String) : Thread() {
//        override fun run() {
//            super.run()
//            CompleteSmsActivity.completeSmsViewModel?.markAllRead(address)
//        }
//    }
//
//    class UpdateMessageCategory(var context: Context, var selectedItem: List<Message>, var category: Int, var presentCategory: Int, var checked: Boolean) : Thread() {
//        override fun run() {
//            super.run()
//            val length = selectedItem.size
//            val mDB: MessageDatabase = MessageDatabase.getInMemoryDatabase(context)
//            for (i in 0 until length) {
//                Log.d(TAG, selectedItem[i].getAddress())
//                mDB.messageDao().moveToCategory(selectedItem[i].getAddress(), category, presentCategory)
//                if (checked) {
//                    mDB.messageDao().updateSendFutureMessage(selectedItem[i].getAddress(), 1)
//                    mDB.messageDao().updateFutureCategory(selectedItem[i].getAddress(), category)
//                } else {
////          mDB.messageDao().updateSendFutureMessage(selectedItem.get(i).getAddress(), 0);
//                }
//            }
//        }
//    }
//
//    class MarkAllReadThread : Thread() {
//        var mDB: MessageDatabase = MessageDatabase.getInMemoryDatabase(SMSApplication.application)
//        override fun run() {
//            super.run()
//            //      mDB.messageDao().markAllRead();
//            val contentValues = ContentValues()
//            contentValues.put(Telephony.TextBasedSmsColumns.READ, 1)
//            contentValues.put(Telephony.TextBasedSmsColumns.SEEN, 1)
//            val updatedRows: Int = SMSApplication.application?.getContentResolver()?.update(Telephony.Sms.CONTENT_URI, contentValues, null, null)
//                    ?: 1
//            Log.d(TAG, Integer.toString(updatedRows) + " updated rows")
//        }
//    }
//
//    class cachePrimaryContactName : Thread() {
//        override fun run() {
//            super.run()
//            PhoneContact.initialize(SMSApplication.application?.applicationContext!!)
//            val list: List<Message> = MessageDatabase.getInMemoryDatabase(SMSApplication.application)
//                    .messageDao().primaryMessage
//            val size = list.size
//            for (i in 0 until size) {
//                val contact: Contact = PhoneContact.get(list[i].getAddress(), true)
//            }
//        }
//    }
//}