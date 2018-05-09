package in.smslite.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import in.smslite.R;
import in.smslite.adapter.SearchAdapter;
import in.smslite.contacts.PhoneContact;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 4/21/2018.
 */

public class SearchActivity extends AppCompatActivity {
  private static final String TAG = SearchActivity.class.getSimpleName();
  public static SearchAdapter searchAdapter;
  Context context;
  EditText editText;
  List<in.smslite.db.Message> msgList = new ArrayList<>();
  public static String searchKeyword;
  RecyclerView recyclerView;
  ImageView imageView;
  TextView textView;
//  private final Object lock = new SearchActivity();
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    setContentView(R.layout.search_msg);
    editText = (EditText) findViewById(R.id.search_editText_id);
    textView = (TextView)  findViewById(R.id.search_msg_note);

    imageView = (ImageView) findViewById(R.id.search_activity_back_arrow);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      Log.d(TAG, "onTextChanged");
      }

      @Override
      public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s);
        searchKeyword = stringBuilder.toString();
//        if(searchKeyword != "") {
        Long time = System.currentTimeMillis();
//        synchronized() {
//          try {
//            wait(90000);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }
        msgList = db.messageDao().searchMsg("%" + searchKeyword + "%");
//        msgList.size();
          Log.d(TAG, searchKeyword);
          Log.d(TAG, String.valueOf(msgList.size()));

          if(searchKeyword != "") {
            SearchAdapter.swapData(msgList);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
          } else {
            SearchAdapter.swapData(new ArrayList<in.smslite.db.Message>());
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
          }
          //TODO -- (done)Adapter instance should not be instantiated for each query. Find the way to replace the data
//          searchAdapter = new SearchAdapter(msgList, context, searchKeyword);
//          recyclerView.setAdapter(searchAdapter);
//        } else {
//          List<in.smslite.db.Message> list = new ArrayList<>();
//          searchAdapter = new SearchAdapter(list, context, searchKeyword);
//          recyclerView.setAdapter(searchAdapter);
//        }
      }
    });

    recyclerView = (RecyclerView) findViewById(R.id.search_result_recycler_view);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(llm);
    searchAdapter = new SearchAdapter(msgList,context);
    recyclerView.setAdapter(searchAdapter);
//    PhoneContact.init(this);


  }
}
