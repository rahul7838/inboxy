package in.inboxy.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.provider.Telephony;

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Entity
public class Message {

  public String body;
  public String address;
  public boolean read;
  public boolean seen;
  public int category;
  public int threadId;
  public long timestamp;
  public Message.MessageType type;
  @PrimaryKey(autoGenerate = true)
  int id;


  public enum MessageType {
    All(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL),
    INBOX(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX),
    SENT(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT),
    DRAFT(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT),
    OUTBOX(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX),
    FAILED(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED),
    QUEUED(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED);

    int val;

    MessageType(int val) {
      this.val = val;
    }

    public static MessageType fromval(int val) {
      for (MessageType messageType : values()) {
        if (messageType.val == val) {
          return messageType;
        }
      }
      return null;
    }
  }

  static class MessageTypeConverter {

    @TypeConverter
    public MessageType convertToEntityProperty(Integer databaseValue) {
      return MessageType.fromval(databaseValue);
    }

    @TypeConverter
    public Integer convertToDatabaseValue(MessageType entityProperty) {
      if (entityProperty == null) {
        return null;
      } else {
        return entityProperty.val;
      }
    }
  }
}

