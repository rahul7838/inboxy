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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public boolean isSeen() {
    return seen;
  }

  public void setSeen(boolean seen) {
    this.seen = seen;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public int getThreadId() {
    return threadId;
  }

  public void setThreadId(int threadId) {
    this.threadId = threadId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }


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

