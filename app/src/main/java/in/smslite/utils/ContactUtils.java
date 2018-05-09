package in.smslite.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import in.smslite.contacts.CompanyContact;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;

/**
 * Contains utility function related to contacts
 */
public class ContactUtils {
    private static final String TAG = ContactUtils.class.getSimpleName();

    public static Contact getContact(String number, Context context, boolean canBlock) {
      if (number == null) {
          return CompanyContact.get(number,context);
      } else if (number.matches("(\\+)?(\\s)?(0|91)?(\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?" +
          "[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?[0-9](\\s)?")) {
          return PhoneContact.get(number, canBlock);
      } else {
          return CompanyContact.get(number, context);
      }
      //number.matches("(\\+)?(0|91)?[798][0-9]{9}")
    }


  public static String formatAddress(String number) {
        if (TextUtils.isEmpty(number)) {
            return number;
        }
        String address;
        address = number.replace("-","").replace(" ", "");
        return address;
//        String first2Char = address.substring(0, 2).toLowerCase();
//        if (first2Char.matches("[a-z]+") && address.length() == 8) {
//            return address.substring(address.length() - 6).toLowerCase();
//        } else {
//            return address.toLowerCase();
//        }
    }

    public static String getShortcode(String address) {
      address = address.replace("-", "");
      if (address.length() == 8) {
        return address.substring(address.length() - 6);
      } else {
        return address;
      }
    }

    @NonNull
    public static String normalizeNumber(String number) {
      StringBuilder builder = null;
        if (number == null || number.isEmpty()) {
            throw new RuntimeException("Phone number can never be null");
        }
        number = PhoneNumberUtils.stripSeparators(number);
        if (number.length() < 10 || number.charAt(0) == '+') {
            return number;
        } else {
          if(number.charAt(0) == '0'){
            number = number.substring(1);
            builder = new StringBuilder();
            builder.append("+91");
            builder.append(number);
        } else{
            builder = new StringBuilder();
            builder.append("+91");
            builder.append(number);
          }
          if (builder != null) {
            return builder.toString();
          }
          return number;

//          TODO check why phoneNumerUtil method is not working
         /* String e164number = PhoneNumberUtils.formatNumberToE164(number, "IN");
            if (e164number == null || e164number.isEmpty()) {
              return number;
//                throw new RuntimeException("Phone number can't be null");
            }
            return e164number;*/
        }
    }
}
