package in.smslite.activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.adapter.SelectContactAdapter;
import in.smslite.utils.ContactUtils;


/**
 * Created by rahul1993 on 4/27/2018.
 */

public class SelectContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
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

  // Defines a variable for the search string
  private String mSearchString;
  // Defines the array to hold values that replace the ?
  LoaderManager.LoaderCallbacks callbacks;
  public static SelectContactAdapter selectContactAdapter;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    callbacks = this;
    setContentView(R.layout.activity_select_contact2);
    ButterKnife.bind(this);
    imageView.setOnClickListener(v -> onBackPressed());
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
//    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
    selectContactAdapter = new SelectContactAdapter(list, phoneNumberList);
    recyclerView.setAdapter(selectContactAdapter);
  }


  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    phoneNumberList.clear();
    list.clear();
    if (!mSearchString.matches("[0-9]*")) {
      Log.d(TAG, mSearchString + " string");

      String[] projection = {
          ContactsContract.Data._ID,
          ContactsContract.Contacts.HAS_PHONE_NUMBER,
          ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
          ContactsContract.CommonDataKinds.Phone.NUMBER
      };
      String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ? "+
//          + " OR "+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?" +
          " AND " + ContactsContract.Data.MIMETYPE + " LIKE ?";
      String[] selectionCriteria = {"%"+mSearchString+"%",
          ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};


      CursorLoader cursor = new CursorLoader(this, ContactsContract.Data.CONTENT_URI, projection, selection,
          selectionCriteria, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC" + " LIMIT 200 ");
      Log.d(TAG, "onCreateLoader");
      return cursor;
    } else {
      Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
      String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
          + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
          + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
          +ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?";
//      String[] arg = {"_" + mSearchString + "%" + " OR " + "___" + mSearchString + "%" + " OR " + mSearchString + "%"};
      int length = mSearchString.length();
      String newSearchString = null;
      StringBuilder builder = new StringBuilder();
      for(int i = 0; i < length; i++){
        newSearchString = mSearchString.substring(i, i+1);
        builder.append(newSearchString);
        if(builder.length() == 2 || builder.length() == 5 || builder.length() == 8 || builder.length() == 11){
          builder.append("%");
        }
      }
      Log.d(TAG, builder.toString());
      String[] arg = {"+91" + builder.toString() + "%", "+91 "+ builder.toString()+"%", builder.toString() + "%", "0" + builder.toString() + "%"};
      CursorLoader cursor = new CursorLoader(context, uri, null, selection, arg,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC" + " LIMIT 200 ");
      return cursor;
    }
  }


  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (mSearchString.equals("")) {
      SelectContactAdapter.updateList(list, phoneNumberList);
    } else {
      Log.d(TAG, Integer.toString(data.getCount()));
      if (!mSearchString.matches("[0-9]*")) {
        textIsAlphaNumeric(data);
      } else {
        textIsNumber(data);
      }
    }
  }

  private void textIsNumber(Cursor cursor) {
//    String number = null;
    List<String> nameList = new ArrayList<>();
    List<String> numberList = new ArrayList<>();
    HashSet<String> set = new HashSet<>();
    numberList.add(mSearchString);
    nameList.add(mSearchString);
    try {
      cursor.moveToFirst();
      int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
      int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      do {
        String formatedNumber = ContactUtils.normalizeNumber(cursor.getString(numberIndex));
//        if (set.add(formatNumber(cursor.getString(numberIndex)))) {
        if (set.add(formatedNumber)) {
          nameList.add(cursor.getString(nameIndex));
          numberList.add(formatedNumber);
        }
      } while (cursor.moveToNext());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cursor.close();
    }
    SelectContactAdapter.updateList(nameList, numberList);
  }


  private void textIsAlphaNumeric(Cursor cursor) {
    List<String> nameList = new ArrayList<>();
    List<String> numberList = new ArrayList<>();
    HashSet<String> set = new HashSet<>();
    cursor.moveToFirst();

    try {
      int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
      int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      do {
//          if (cursor.getInt(1) == 1) {
        if (!cursor.getString(numberIndex).matches(".*[a-zA-Z].*")) {
          String formatedNumber = ContactUtils.normalizeNumber(cursor.getString(numberIndex));
          if (set.add(formatedNumber)) {
            nameList.add(cursor.getString(nameIndex));
            numberList.add(formatedNumber);
          }
        }
      } while (cursor.moveToNext());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      cursor.close();
    }
    Log.d(TAG, Integer.toString(nameList.size()));
    Log.d(TAG, Integer.toString(numberList.size()));
    SelectContactAdapter.updateList(nameList, numberList);
  }


  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }

  private String formatNumber(String number) {
    number = number.replaceAll("-", "");
    number = number.replaceAll(" ", "");
    if (number.charAt(0) == '+') {
      number = number.substring(3);
      return number;
    }
    if (number.charAt(0) == '0') {
      number = number.substring(1);
      return number;
    }
    return number;
  }

  public class WrapContentLinearLayoutManager extends LinearLayoutManager {

    public WrapContentLinearLayoutManager(Context context) {
      super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
      try {
        super.onLayoutChildren(recycler, state);
      } catch (IndexOutOfBoundsException e) {
        Log.e("probe", "meet a IOOBE in recycler view");
      }
    }
  }
}
