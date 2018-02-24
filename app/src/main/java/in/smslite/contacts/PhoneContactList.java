package in.smslite.contacts;

import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.smslite.LogTag;


public class PhoneContactList extends ArrayList<PhoneContact> {
    private static final long serialVersionUID = 1L;

    public static PhoneContactList getByNumbers(Iterable<String> numbers, boolean canBlock) {
        PhoneContactList list = new PhoneContactList();
        for (String number : numbers) {
            if (!TextUtils.isEmpty(number)) {
                list.add(PhoneContact.get(number, canBlock));
            }
        }
        return list;
    }

    public static PhoneContactList getByNumbers(String semiSepNumbers,
                                           boolean canBlock,
                                           boolean replaceNumber) {
        PhoneContactList list = new PhoneContactList();
        for (String number : semiSepNumbers.split(";")) {
            if (!TextUtils.isEmpty(number)) {
                PhoneContact contact = PhoneContact.get(number, canBlock);
                if (replaceNumber) {
                    contact.setNumber(number);
                }
                list.add(contact);
            }
        }
        return list;
    }

    /**
     * Returns a ContactList for the corresponding recipient URIs passed in. This method will
     * always block to query provider. The given URIs could be the phone data URIs or tel URI
     * for the numbers don't belong to any contact.
     *
     * @param uris phone URI to create the ContactList
     */
    public static PhoneContactList blockingGetByUris(Parcelable[] uris) {
        PhoneContactList list = new PhoneContactList();
        if (uris != null && uris.length > 0) {
            for (Parcelable p : uris) {
                Uri uri = (Uri) p;
                if ("tel".equals(uri.getScheme())) {
                    PhoneContact contact = PhoneContact.get(uri.getSchemeSpecificPart(), true);
                    list.add(contact);
                }
            }
            final List<PhoneContact> contacts = PhoneContact.getByPhoneUris(uris);
            if (contacts != null) {
                list.addAll(contacts);
            }
        }
        return list;
    }

    /**
     * Returns a ContactList for the corresponding recipient ids passed in. This method will
     * create the contact if it doesn't exist, and would inject the recipient id into the contact.
     */
    public static PhoneContactList getByIds(String spaceSepIds, boolean canBlock) {
        PhoneContactList list = new PhoneContactList();
        for (RecipientIdCache.Entry entry : RecipientIdCache.getAddresses(spaceSepIds)) {
            if (entry != null && !TextUtils.isEmpty(entry.number)) {
                PhoneContact contact = PhoneContact.get(entry.number, canBlock);
                contact.setRecipientId(entry.id);
                list.add(contact);
            }
        }
        return list;
    }

    public int getPresenceResId() {
        // We only show presence for single contacts.
        if (size() != 1)
            return 0;

        return get(0).getPresenceResId();
    }

    public String formatNames(String separator) {
        String[] names = new String[size()];
        int i = 0;
        for (PhoneContact c : this) {
            names[i++] = c.getName();
        }
        return TextUtils.join(separator, names);
    }

    public String formatNamesAndNumbers(String separator) {
        String[] nans = new String[size()];
        int i = 0;
        for (PhoneContact c : this) {
            nans[i++] = c.getNameAndNumber();
        }
        return TextUtils.join(separator, nans);
    }

    public String serialize() {
        return TextUtils.join(";", getNumbers());
    }

    public boolean containsEmail() {
        for (PhoneContact c : this) {
            if (c.isEmail()) {
                return true;
            }
        }
        return false;
    }

    public String[] getNumbers() {

        List<String> numbers = new ArrayList<>();
        String number;
        for (PhoneContact c : this) {
            number = c.getNumber();

            // Don't add duplicate numbers. This can happen if a contact name has a comma.
            // Since we use a comma as a delimiter between contacts, the code will consider
            // the same recipient has been added twice. The recipients UI still works correctly.
            // It's easiest to just make sure we only send to the same recipient once.
            if (!TextUtils.isEmpty(number) && !numbers.contains(number)) {
                numbers.add(number);
            }
        }
        return numbers.toArray(new String[numbers.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            PhoneContactList other = (PhoneContactList)obj;
            // If they're different sizes, the contact
            // set is obviously different.
            if (size() != other.size()) {
                return false;
            }

            // Make sure all the individual contacts are the same.
            for (PhoneContact c : this) {
                if (!other.contains(c)) {
                    return false;
                }
            }

            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private void log(String msg) {
        Log.d(LogTag.TAG, "[ContactList] " + msg);
    }
}
