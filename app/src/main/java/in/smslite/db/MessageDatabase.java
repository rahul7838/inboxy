package in.smslite.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Database(entities = {Message.class}, version = 2)
@TypeConverters(Message.MessageTypeConverter.class)
public abstract class MessageDatabase extends RoomDatabase{

  private static MessageDatabase INSTANCE;

  public abstract MessageDao messageDao();

  static final Migration MIGRATION_1_2 = new Migration(1,2) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase INSTANCE) {
      INSTANCE.execSQL("ALTER TABLE Message " + " ADD COLUMN sendFutureMessage INTEGER DEFAULT 0 NOT NULL");
      INSTANCE.execSQL("ALTER TABLE Message" + " ADD COLUMN futureCategory INTEGER DEFAULT 0 NOT NULL");
    }
  };

  public static MessageDatabase getInMemoryDatabase(Context context) {
        if(INSTANCE == null) {
          INSTANCE =
                  Room.databaseBuilder(context, MessageDatabase.class, "Message")
                          .addMigrations(MIGRATION_1_2)
                          .allowMainThreadQueries()
                          // To simplify the codelab, allow queries on the main thread.
                          // Don't do this on a real app! See PersistenceBasicSample for an example.
                          .build();
        }
    return INSTANCE;
  }
}
