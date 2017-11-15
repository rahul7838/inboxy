package in.inboxy.utils;

import android.content.Context;

import java.util.List;

import in.inboxy.db.Message;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class MessageUtils {
  public static List<Sms> getAllMessages(Context context) {
    TelephonyProvider messageProvider = new TelephonyProvider(context);
    List<Sms> list = messageProvider.getSms(TelephonyProvider.Filter.ALL).getList();
    return list;

  }

  public static long getTimeStamp(long receivedTime, long sentTime, Message.MessageType type) {
    if (Message.MessageType.SENT.compareTo(type) == 0 && sentTime != 0) {
      return sentTime;
    } else {
      return receivedTime;
    }
  }
}
