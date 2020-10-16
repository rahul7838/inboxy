package `in`.smslite.contacts

import `in`.smslite.R
import `in`.smslite.utils.ContactUtils
import android.content.Context
import android.text.TextUtils
import java.util.*

class CompanyContact private constructor(address: String, context: Context) : Contact() {
    val uriPhoto: String? = null
    private fun categorizeSMS(address: String, context: Context): Int {
        val shortcodeList = Arrays.asList(*context
                .resources
                .getStringArray(R.array.finance_short_codes))
        val shortCode: String = ContactUtils.getShortcode(address.toLowerCase())
        return if (shortCode != null && shortcodeList.contains(shortCode)) {
            FINANCE
        } else if (shortCode != null && shortCode.toLowerCase()
                        .matches(Regex("[a-zA-Z]+"))) {
            UPDATES
        } else {
            PROMOTIONS
        }
    }

    companion object {
        private val TAG = CompanyContact::class.java.simpleName
        private val cache = HashMap<String, CompanyContact>()

        @JvmStatic
        operator fun get(address: String, context: Context): CompanyContact? {
            return if (cache.containsKey(address)) {
//            callListeners(cache.get(address));
                cache[address]
            } else {
                val contact = CompanyContact(address, context)
                cache[address] = contact
                contact
            }
        }
    }

    init {
        source = Source.OTHER
        number = address
        category = UNCATEGORIZED
        if (TextUtils.isEmpty(address)) {
            mName = "Unknown Number"
            category = PROMOTIONS
            //            callListeners(CompanyContact.this);
        } /* else {
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
                                });*/ else {
//                                FirebaseUtils.getShortcodesRef().child(address).setValue("");
            category = categorizeSMS(address, context)
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
}