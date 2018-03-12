package in.smslite.contacts;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.smslite.R;
import in.smslite.utils.ContactUtils;


public class CompanyContact extends Contact {
    private static final String TAG = CompanyContact.class.getSimpleName();
    private static HashMap<String, CompanyContact> cache = new HashMap<>();
    private String uriPhoto;

    private CompanyContact(final String address, final Context context) {
        mSource = Source.OTHER;
        mNumber = address;
        mCategory = UNCATEGORIZED;
        if(TextUtils.isEmpty(address)) {
            mName = "Unknown Number";
            mCategory = PROMOTIONS;
//            callListeners(CompanyContact.this);
        }/* else {
            FirebaseUtils.getShortcodesRef().child(address)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String companyKey = dataSnapshot.getValue(String.class);
                            if (!TextUtils.isEmpty(companyKey)) {
                                CompanyContact.this.mThreadId = companyKey;
                                FirebaseUtils.getCompaniesRef().child(companyKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.i(TAG, String.valueOf(dataSnapshot));
                                        CompanyContact.this.mName = dataSnapshot.child("name").getValue(String.class);
                                        CompanyContact.this.uriPhoto = dataSnapshot.child("uriPhoto").getValue(String.class);
                                        CompanyContact.this.mCategory = dataSnapshot.child("category").getValue(Integer.class);
                                        CompanyContact.this.mSource = Source.FIREBASE;
                                        callListeners(CompanyContact.this);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });*/
                            else {
//                                FirebaseUtils.getShortcodesRef().child(address).setValue("");
                                CompanyContact.this.mCategory = CompanyContact.this.categorizeSMS(address, context);
//                                callListeners(CompanyContact.this);
                            }
//                        }
//                    }

                  /*  @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, databaseError.toString());
                    }
                });
        }*/
    }

    public static CompanyContact get(String address, Context context) {
        if (cache.containsKey(address)) {
//            callListeners(cache.get(address));
            return cache.get(address);
        } else {
            CompanyContact contact = new CompanyContact(address, context);
            cache.put(address, contact);
            return contact;
        }
    }

    private int categorizeSMS(String address, Context context) {
        List<String> shortcodeList = Arrays.asList(context
                .getResources()
                .getStringArray(R.array.finance_short_codes));
        String shortCode = ContactUtils.getShortcode(address.toLowerCase());
        if (shortCode != null && shortcodeList.contains(shortCode)) {
            return FINANCE;
        } else if (shortCode != null && shortCode.toLowerCase()
                .matches("[a-zA-Z]+")) {
            return UPDATES;
        } else {
            return PROMOTIONS;
        }
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

}
