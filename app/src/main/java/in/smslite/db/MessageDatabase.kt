package `in`.smslite.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Database(entities = [Message::class], version = 2, exportSchema = false)
@TypeConverters(value = [Message.MessageStatusConverter::class, Message.MessageTypeConverter::class])
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao?

    companion object {
        private var INSTANCE: MessageDatabase? = null
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(INSTANCE: SupportSQLiteDatabase) {
                INSTANCE.execSQL("ALTER TABLE Message " + " ADD COLUMN sendFutureMessage INTEGER DEFAULT 0 NOT NULL")
                INSTANCE.execSQL("ALTER TABLE Message" + " ADD COLUMN futureCategory INTEGER DEFAULT 0 NOT NULL")
            }
        }

        fun getInMemoryDatabase(context: Context?): MessageDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context!!, MessageDatabase::class.java, "Message")
                        .addMigrations(MIGRATION_1_2) // To simplify the codelab, allow queries on the main thread.
                        // Don't do this on a real app! See PersistenceBasicSample for an example.
                        .build()
            }
            return INSTANCE
        }
    }
}