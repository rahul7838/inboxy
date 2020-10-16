package `in`.smslite.repository

import `in`.smslite.db.Message
import `in`.smslite.db.MessageDao
import android.database.Cursor
import androidx.lifecycle.LiveData

class MessageRepository(val messageDao: MessageDao) {

    suspend fun insertMessage(message: Message?) {
        messageDao.insertMessage(message)
    }

    fun getMessage(): LiveData<List<Message>> {
        return messageDao.getMessage()
    }

    fun getArchiveMessage(): LiveData<List<Message>> {
        return messageDao.getArchiveMessage()
    }

    fun getBlockedMessage(): LiveData<List<Message>> {
        return messageDao.blockedMessage
    }

    fun getMessageListByAddress(address: String?, category: Int): LiveData<List<Message>> {
        return messageDao.getMessageListByAddress(address, category)
    }

    fun getMessageListByCategory(category: Int?): LiveData<List<Message>> {
        return messageDao.getMessageListByCategory(category)
    }

    fun getUnseenSmsCount(category: Int): Cursor? {
        return messageDao.getUnseenSmsCount(category)
    }

    suspend fun getNotificationSummary(category: Int): List<Message> {
        return messageDao.getNotificationSummary(category)
    }

    suspend fun markAllSeen(category: Int?) {
        messageDao.markAllSeen(category)
    }

    suspend fun markAllReadByAddress(address: String?) {
        messageDao.markAllReadByAddress(address)
    }

    suspend fun markAllRead() {
        messageDao.markAllRead()
    }

    fun getSentSmsCount(): Cursor? {
        return messageDao.getSentSmsCount()
    }

    suspend fun updateSentSuccessful(time: Long?) {
        messageDao.updateSentSuccessful(time)
    }

    suspend fun deliveredSmsSuccessfully(time: Long?) {
        messageDao.deliveredSmsSuccessfully(time)
    }

    //  Query for failed sms
    suspend fun updateSentFailedSms(time: Long?) {
        messageDao.updateSentFailedSms(time)
    }

    suspend fun getFailedSmsText(time: Long?): String? {
        return messageDao.getFailedSmsText(time)
    }

    suspend fun deleteFailedMsg(time: Long?) {
        messageDao.deleteFailedMsg(time)
    }

    //  Query for message search
    //  @Query("Select * from Message where body LIKE :keyword or address LIKE :keyword group by address order by timestamp desc")
    fun searchMsg(keyword: String?): LiveData<List<Message>> {
        return messageDao.searchMsg(keyword)
    }

    suspend fun deleteSelectedConversation(address: String?) {
        messageDao.deleteSelectedConversation(address)
    }

    suspend fun deleteSelectedMessage(timestamp: Long?) {
        messageDao.deleteSelectedMessage(timestamp)
    }

    //  Update the Category of message to blocked
    suspend fun moveToCategory(address: String?, category: Int, presentCategory: Int) {
        messageDao.moveToCategory(address, category, presentCategory)
    }


//    suspend fun updateSendFutureMessage(address: String?, value: Int) {
//        messageDao.updateSendFutureMessage(address, value)
//    }

    suspend fun askSendFutureMessage(address: String?): Int {
        return 1
    }

    suspend fun findCategory(address: String?): Int {
        return messageDao.findCategory(address)
    }

    suspend fun updateFutureCategory(address: String?, category: Int) {
        messageDao.updateFutureCategory(address, category)
    }

}