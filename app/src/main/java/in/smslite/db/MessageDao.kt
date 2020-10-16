package `in`.smslite.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message?)

    @Query("Select * from Message group by address order by receivedDate and sentDate desc")
    fun getMessage(): LiveData<List<Message>>

    @Query("Select * from Message Where address = :address AND category=:category order by receivedDate and sentDate asc")
    fun getMessageListByAddress(address: String?, category: Int): LiveData<List<Message>>

    @Query("select t1.* from message t1 Join (select address, MAX(receivedDate) receivedDate from message where category =:category group by address)" +
            "t2 on t1.address = t2.address and t1.receivedDate = t2.receivedDate where category = :category order by receivedDate desc")
    fun getMessageListByCategory(category: Int?): LiveData<List<Message>>

    @Query("SELECT t1.* from Message t1 Join (SELECT address, MAX(receivedDate) receivedDate from Message where category = :category group by address) t2 on t1.address = t2.address and t1.receivedDate = t2.receivedDate where category = :category order by receivedDate desc")
    fun getMessageListByCategory1(category: Int?): LiveData<List<Message>>

    @Query("Select * from Message Where seen = 0 and Category = :category")
    fun getUnseenSmsCount(category: Int): Cursor?

    @Query("Select * from Message Where seen = 0 and Category = :category order by receivedDate and sentDate desc")
    suspend fun getNotificationSummary(category: Int): List<Message>

    @Query("Update Message Set seen = 1 Where Category = :category")
    suspend fun markAllSeen(category: Int?)

    @Query("Update Message Set seen=1, read = 1 Where address = :address")
    suspend fun markAllReadByAddress(address: String?)

    @Query("Update Message set Read = 1, seen = 1")
    suspend fun markAllRead()

    @Query("select * from message where Type Like 2 order by receivedDate and sentDate desc")
    fun getSentSmsCount(): Cursor?

    @get:Query("select * from message  where category = 4 order by receivedDate and sentDate desc")
    val oTPFOrTest: List<Message?>?

    @Query("Update Message set type = 2 where receivedDate and sentDate LIKE :time")
    suspend fun updateSentSuccessful(time: Long?)

    @Query("Update Message set type = 4 where receivedDate and sentDate LIKE :time")
    suspend fun deliveredSmsSuccessfully(time: Long?)

    //  Query for failed sms
    @Query("Update Message set type = 5 where receivedDate and sentDate = :time")
    suspend fun updateSentFailedSms(time: Long?)

    @Query("Select body from Message where receivedDate and sentDate = :time")
    suspend fun getFailedSmsText(time: Long?): String?

    @Query("Delete from message where receivedDate and sentDate = :time")
    suspend fun deleteFailedMsg(time: Long?)

    //  Query for message search
    //  @Query("Select * from Message where body LIKE :keyword or address LIKE :keyword group by address order by timestamp desc")
    @Query("select t1.* from message t1 Join (select address, MAX(receivedDate) receivedDate from message group by address)" +
            "t2 on t1.address = t2.address and t1.receivedDate = t2.receivedDate where body LIKE :keyword or t1.address LIKE :keyword" +
            " group by t1.address order by t1.receivedDate desc")
    fun searchMsg(keyword: String?): LiveData<List<Message>>

    @Query("Delete from message where address = :address")
    suspend fun deleteSelectedConversation(address: String?)

    @Query("Delete from message where  sentDate = :timestamp")
    suspend fun deleteSelectedMessage(timestamp: Long?)

    //  Update the Category of message to blocked
    @Query("Update Message set category = :category where address Like :address AND category=:presentCategory")
    suspend fun moveToCategory(address: String?, category: Int, presentCategory: Int)

    //  Query to select all archive messages
    @Query("Select t1.* from message t1 JOIN (select address, Max(receivedDate) receivedDate from message where category = 6 group by address)" +
            "t2 on t1.address = t2.address AND t1.receivedDate = t2.receivedDate order by receivedDate desc")
    fun getArchiveMessage(): LiveData<List<Message>>

    @get:Query("select t1.* from message t1 Inner Join (select address, Max(receivedDate) receivedDate from message where category = 5 group by address) t2" +
            " on t1.address = t2.address and t1.receivedDate = t2.receivedDate order by receivedDate Desc")
    val blockedMessage: LiveData<List<Message>>

    @get:Query("select * from message where category=1 order by receivedDate and sentDate desc")
    val primaryMessage: List<Message?>?

    //    @Query("Update message set sendFutureMessage = :value where address = :address")
//    suspend fun updateSendFutureMessage(address: String?, value: Int)
//
//    @Query("select t1.sendFutureMessage from message t1 join (select Min(receivedDate) receivedDate from message where address=:address) " +
//            "t2 on t1.receivedDate=t2.receivedDate where address = :address group by address")
//    suspend fun askSendFutureMessage(address: String?): Int
//
    @Query("select t1.futureCategory from message t1 Join(select Min(receivedDate) receivedDate from message where address=:address) " +
            "t2 on t1.receivedDate = t2.receivedDate where address = :address group by address")
    suspend fun findCategory(address: String?): Int

    @Query("update message set futureCategory =:category where address =:address")
    suspend fun updateFutureCategory(address: String?, category: Int)

}