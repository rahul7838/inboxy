package in.inboxy.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Database(entities = {Message.class}, version = 1)
@TypeConverters(Message.MessageTypeConverter.class)
public abstract class MessageDatabase extends RoomDatabase{

  private static MessageDatabase INSTANCE;

  public abstract MessageDao messageDao();

  public static MessageDatabase getInMemoryDatabase(Context context) {
        if(INSTANCE == null) {
          INSTANCE =
                  Room.databaseBuilder(context, MessageDatabase.class, "Message")
                          // To simplify the codelab, allow queries on the main thread.
                          // Don't do this on a real app! See PersistenceBasicSample for an example.
                          .build();
        }
    return INSTANCE;
  }
}
