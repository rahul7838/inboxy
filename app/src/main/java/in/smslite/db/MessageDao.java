package in.smslite.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Dao
public interface MessageDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertMessage(Message message);

  @Query("Select * from Message group by address order by timestamp desc")
  LiveData<List<Message>> getMessage();

  @Query("Select * from Message Where address = :address order by timestamp asc")
  LiveData<List<Message>> getMessageListByAddress(String address);

  //  @Query("Select * from Message Where Category = :category group by address order by timestamp desc")
  @Query("select t1.* from message t1 Join (select address, MAX(timestamp) timestamp from message group by address)" +
      "t2 on t1.address = t2.address and t1.timestamp = t2.timestamp where category = :category order by timestamp desc")
  LiveData<List<Message>> getMessageListByCategory(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category")
  Cursor getUnseenSmsCount(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category order by timestamp desc")
  List<Message> getNotificationSummary(int category);

  @Query("Update Message Set seen = 1 Where Category = :category")
  void markAllSeen(int category);

  @Query("Update Message Set seen=1, read = 1 Where address = :address")
  void markAllRead(String address);

  @Query("select * from message where Type Like 2 order by timestamp desc")
  Cursor getSentSmsCount();

  @Query("select * from message where body Like \"%otp%\" group by address")
  List<Message> getOTPFOrTest();

  @Query("Update Message set type = 2 where timestamp LIKE :time")
  void updateSentSuccessful(Long time);

  @Query("Update Message set type = 4 where timestamp LIKE :time")
  void deliveredSmsSuccessfully(Long time);

  //  Query for failed sms
  @Query("Update Message set type = 5 where timestamp = :time")
  void updateSentFailedSms(Long time);

  @Query("Select body from Message where timestamp = :time")
  String getFailedSmsText(Long time);

  @Query("Delete from message where timestamp = :time")
  void deleteFailedMsg(Long time);

  //  Query for message search
//  @Query("Select * from Message where body LIKE :keyword or address LIKE :keyword group by address order by timestamp desc")
  @Query("select t1.* from message t1 Join (select address, MAX(timestamp) timestamp from message group by address)" +
      "t2 on t1.address = t2.address and t1.timestamp = t2.timestamp where body LIKE :keyword or t1.address LIKE :keyword" +
      " group by t1.address order by t1.timestamp desc")
  List<Message> searchMsg(String keyword);

  @Query("Delete from message where address = :address")
  void deleteSelectedConversation(String address);

  @Query("Delete from message where timestamp = :timestamp")
  void deleteSelectedMessage(Long timestamp);

//  Update the Category of message to blocked
  @Query("Update Message set category = :category where address Like :address")
  void moveToCategory(String address, int category);

//  Query to select all archive messages
  @Query("Select t1.* from message t1 JOIN (select address, Max(timestamp) timestamp from message where category = 6 group by address)" +
      "t2 on t1.address = t2.address AND t1.timestamp = t2.timestamp order by timestamp desc")
  LiveData<List<Message>> getArchiveMessage ();

  @Query("select t1.* from message t1 Inner Join (select address, Max(timestamp) timestamp from message where category = 5 group by address) t2" +
      " on t1.address = t2.address and t1.timestamp = t2.timestamp order by timestamp Desc")
  LiveData<List<Message>> getBlockedMessage();

//  @Query("Select * from Message Where widget = 1 order by timestamp desc" )
//  public List<Message> getWidgetMessage();
//
//  @Query("Update Message set widget = 1 where body like :name ")
//  public void updateWidgetMessage(String name);
}
