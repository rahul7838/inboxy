package in.smslite.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.adapter.SelectContactAdapter;
import in.smslite.contacts.Contact;
import in.smslite.utils.ContactUtils;

/**
 * Created by rahul1993 on 4/27/2018.
 */

public class SelectContactActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
  private static final String TAG = SelectContactActivity.class.getSimpleName();
  @BindView(R.id.recyclerView_select_list_id)
  RecyclerView recyclerView;
  @BindView(R.id.editText_select_list_id)
  EditText editText;
  @BindView(R.id.select_contact_activity_back_arrow)
  ImageView imageView;

  private Context context;
  List<String> list = new ArrayList<>();
  List<String> phoneNumberList = new ArrayList<>();

  private static final String[] PROJECTION =
      {ContactsContract.Data._ID,
          ContactsContract.Contacts.HAS_PHONE_NUMBER,
          ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
          ContactsContract.CommonDataKinds.Phone.NUMBER
      };

  //  private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
  private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?"
      + " AND " + ContactsContract.Data.MIMETYPE + " LIKE ?";
//      + " OR " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?";
  // Defines a variable for the search string
  private String mSearchString;
  // Defines the array to hold values that replace the ?
  private String[] mSelectionArgs = {mSearchString,
      ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
  android.support.v4.app.LoaderManager.LoaderCallbacks callbacks;
  public static SelectContactAdapter selectContactAdapter;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    callbacks = this;
    setContentView(R.layout.activity_select_contact2);
    ButterKnife.bind(this);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        StringBuilder text = new StringBuilder();
        text.append(s);
        mSearchString = text.toString();
        getSupportLoaderManager().restartLoader(0, null, callbacks);
//        getLoaderManager().initLoader(0,null, callbacks);
        Log.d(TAG, "afterTextChsnged");
      }
    });

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(linearLayoutManager);
    selectContactAdapter = new SelectContactAdapter(list, phoneNumberList);
    recyclerView.setAdapter(selectContactAdapter);
  }


  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
    phoneNumberList.clear();
    list.clear();
    mSelectionArgs[0] = "%" + mSearchString + "%";
//    mSelectionArgs[2] = "%" + mSearchString + "%";
    Log.d(TAG, mSearchString + " string");
    android.support.v4.content.CursorLoader cursor = new android.support.v4.content.CursorLoader(this, ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION,
        mSelectionArgs, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC");
    Log.d(TAG, "onCreateLoader");
    return cursor;
  }

  @Override
  public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
    if (mSearchString.equals("")) {
      SelectContactAdapter.updateList(list, phoneNumberList);
    } else {
      data.moveToFirst();
      HashSet<String> set = new HashSet<>();
      int size = data.getCount();
      List<String> nameList = new ArrayList<>();
      List<String> numberList = new ArrayList<>();
//      String lastNumber = "";
      do {
        if (data.getCount() != 0) {
          if(data.getInt(1) == 1) {
            if (!data.getString(3).matches(".*[a-zA-Z].*")) {
              if (set.add(formatNumber(data.getString(3)))) {
                nameList.add(data.getString(2));
                numberList.add(data.getString(3));
              }
            }
          }
        }
      } while (data.moveToNext());
      Log.i(TAG, Integer.toString(nameList.size()));
      Log.i(TAG, Integer.toString(numberList.size()));
      SelectContactAdapter.updateList(nameList, numberList);
//      SelectContactAdapter.updateList(list, phoneNumberList);
    }
  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

  }

  private String formatNumber(String number) {
//    if (number == null || number.isEmpty()) {
//      throw new RuntimeException("Phone number can never be null");
//    }
//    number = PhoneNumberUtils.stripSeparators(number);
    number = number.replaceAll("-", "");
    number = number.replaceAll(" ", "");
    if (number.length() < 10 || number.charAt(0) == '+') {
      return number;
    } else {
      String e164number = PhoneNumberUtils.formatNumberToE164(number, "IN");
      if (e164number == null || e164number.isEmpty()) {
        return number;
//                throw new RuntimeException("Phone number can't be null");
      }
      return e164number;
    }
  }
}