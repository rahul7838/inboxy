package `in`.smslite.db

import android.provider.Telephony
import android.provider.Telephony.TextBasedSmsColumns
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Entity
data class Message(var address: String,
                   var body: String,
                   var receivedDate: Long,
                   var sentDate: Long,
                   var errorCode: Int,
                   var locked: Boolean,
                   var person: Int,
                   var protocol: Int,
                   var read: Boolean,
                   var seen: Boolean,
                   var serviceCenter: String?,
                   var status: MessageStatus,
                   var subject: String?,
                   var threadId: Int,
                   var type: MessageType,
                   var category: Int = 0,
                   var futureCategory: Int = 0,
                   var isSendFutureMessage: Boolean = false,
                   @PrimaryKey(autoGenerate = true)
                   var uid: Int = 0) {


    enum class MessageType(var type: Int) {
        ALL(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL),
        INBOX(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX),
        SENT(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT),
        DRAFT(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT),
        OUTBOX(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX),
        FAILED(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED),
        QUEUED(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED);

        companion object {
            fun fromType(type: Int): MessageType? {
                for (messageType in values()) {
                    if (messageType.type == type) {
                        return messageType
                    }
                }
                return null
            }
        }
    }

    enum class MessageStatus(var status: Int) {
        NONE(TextBasedSmsColumns.STATUS_NONE),
        COMPLETE(TextBasedSmsColumns.STATUS_COMPLETE),
        PENDING(TextBasedSmsColumns.STATUS_PENDING),
        FAILED(TextBasedSmsColumns.STATUS_FAILED);

        companion object {
            fun fromStatus(status: Int): MessageStatus? {
                for (messageStatus in values()) {
                    if (messageStatus.status == status) {
                        return messageStatus
                    }
                }
                return null
            }
        }
    }

    internal class MessageTypeConverter {
        @TypeConverter
        fun convertToMessageType(databaseValue: Int): MessageType? {
            return MessageType.fromType(databaseValue)
        }

        @TypeConverter
        fun convertToMessageTypeValue(entityProperty: MessageType): Int? {
            return entityProperty.type
        }
    }

    internal class MessageStatusConverter {
        @TypeConverter
        fun convertToMessageStatus(databaseValue: Int): MessageStatus? {
            return MessageStatus.fromStatus(databaseValue)
        }

        @TypeConverter
        fun convertToMessageStatusValue(entityProperty: MessageStatus?): Int? {
            return entityProperty?.status
        }
    }
}