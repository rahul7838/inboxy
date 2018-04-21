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
  public void insertMessage(Message message);

  @Query("Select * from Message group by address order by timestamp desc")
  public LiveData<List<Message>> getMessage();

  @Query("Select * from Message Where address = :address order by timestamp asc")
  public LiveData<List<Message>> getMessageListByAddress(String address);

//  @Query("Select * from Message Where Category = :category group by address order by timestamp desc")
@Query("select t1.* from message t1 Join (select address, MAX(timestamp) timestamp from message group by address)" +
    "t2 on t1.address = t2.address and t1.timestamp = t2.timestamp where category = :category order by timestamp desc")
  public LiveData<List<Message>> getMessageListByCategory(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category")
  public Cursor getUnseenSmsCount(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category order by timestamp desc")
  public List<Message> getNotificationSummary(int category);

  @Query("Update Message Set seen = 1 Where Category = :category")
  public void markAllSeen(int category);

  @Query("Update Message Set seen=1, read = 1 Where address = :address")
  public void markAllRead(String address);

  @Query("select * from message where Type Like 2 order by timestamp desc")
  public Cursor getSentSmsCount();

  @Query("select * from message where body Like \"%otp%\" group by address")
  public List<Message> getOTPFOrTest();

  @Query("Update Message set type = 5 where timestamp = :time")
  public void updateSentFailedSms(Long time);

  @Query("Select body from Message where timestamp = :time")
  public String getFailedSmsText(Long time);

  @Query("Delete from message where timestamp = :time")
  public void deleteFailedMsg(Long time);
//  @Query("Select * from Message Where widget = 1 order by timestamp desc" )
//  public List<Message> getWidgetMessage();
//
//  @Query("Update Message set widget = 1 where body like :name ")
//  public void updateWidgetMessage(String name);
}
