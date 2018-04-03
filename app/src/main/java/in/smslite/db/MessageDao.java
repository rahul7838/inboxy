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

  @Query("Select * from Message Where Category = :category group by address order by timestamp desc")
  public LiveData<List<Message>> getMessageListByCategory(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category")
  public Cursor getUnseenSmsCount(int category);

  @Query("Select * from Message Where seen = 0 and Category = :category order by timestamp desc")
  public List<Message> getNotificationSummary(int category);

  @Query("Update Message Set seen = 1 Where Category = :category")
  public void markAllSeen(int category);

  @Query("Update Message Set seen=1, read = 1 Where address = :address")
  public void markAllRead(String address);

  @Query("Select * from Message Where widget = 1 order by timestamp desc" )
  public List<Message> getWidgetMessage();

  @Query("Update Message set widget = 1 where body like :name ")
  public void updateWidgetMessage(String name);
}
