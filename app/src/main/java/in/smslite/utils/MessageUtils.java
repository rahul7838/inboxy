package in.smslite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

import static in.smslite.db.Message.MessageType.INBOX;
import static in.smslite.db.Message.MessageType.SENT;

/**
 * Created by rahul1993 on 11/11/2017.
 */

public class MessageUtils {

  private static final Pattern NAME_ADDR_EMAIL_PATTERN =
          Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");

  public static List<Sms> getAllMessages(Context context) {
    TelephonyProvider messageProvider = new TelephonyProvider(context);
    List<Sms> list = messageProvider.getSms(TelephonyProvider.Filter.ALL).getList();
    return list;
  }

  public static void sync(Context context, List<Sms> smses) {
    Message message = new Message();
    for (int i = 0; i < smses.size(); i++) {
      Sms sms = smses.get(i);
      Contact contact = ContactUtils.getContact(sms.address, context, true);
      Message.MessageType type = Sms.MessageType.SENT.compareTo(sms.type) == 0 ? SENT : INBOX;
      final Long timeStamp = MessageUtils.getTimeStamp(sms.receivedDate, sms.sentDate, type);
      message.body = sms.body;
      message.address = sms.address;
      message.read = sms.read;
      message.seen = sms.seen;
      message.threadId = sms.threadId;
      message.type = type;
      message.timestamp = timeStamp;
      message.category = contact.getCategory();
      MessageDatabase db = MessageDatabase.getInMemoryDatabase(context);
      db.messageDao().insertMessage(message);
    }
  }

  public static long getTimeStamp(long receivedTime, long sentTime, Message.MessageType type) {
    if (SENT.compareTo(type) == 0 && sentTime != 0) {
      return sentTime;
    } else {
      return receivedTime;
    }
  }

  public static boolean isEmailAddress(String address) {
    if (TextUtils.isEmpty(address)) {
      return false;
    }
    String s = extractAddrSpec(address);
    Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
    return match.matches();
  }

  private static String extractAddrSpec(String address) {
    Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

    if (match.matches()) {
      return match.group(2);
    }
    return address;
  }
}

/*@Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_READ_SMS: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initiUi();
          // permission was granted, yay! Do the
          // contacts-related task you need to do.

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }*/
