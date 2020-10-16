package in.smslite.db;

import android.provider.Telephony;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.Serializable;

/**
 * Created by rahul1993 on 11/11/2017.
 */
@Entity
public class Message implements Serializable {

  public String body;
  public String address;
  public boolean read;
  public boolean seen;
  public int category;
  public int threadId;
  public long timestamp;
  public int futureCategory;
  public boolean sendFutureMessage;
  public Message.MessageType type;
  @PrimaryKey(autoGenerate = true)
  int id;

//  public boolean widget;


  public int getFutureCategory() {
    return futureCategory;
  }

  public void setFutureCategory(int futureCategory) {
    this.futureCategory = futureCategory;
  }

  public boolean isSendFutureMessage() {
    return sendFutureMessage;
  }

  public void setSendFutureMessage(boolean sendFutureMessage) {
    this.sendFutureMessage = sendFutureMessage;
  }

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

//  public boolean isWidget() {
//    return widget;
//  }

//  public void setWidget(boolean widget) {
//    this.widget = widget;
//  }

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

