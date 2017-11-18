package in.inboxy.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
}
