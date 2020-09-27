package in.smslite.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.adapter.SearchAdapter;
import in.smslite.db.Message;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 4/21/2018.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    public SearchAdapter searchAdapter;
    EditText editText;
    List<in.smslite.db.Message> msgList = new ArrayList<>();
    public String searchKeyword;
    RecyclerView recyclerView;
    ImageView backArrow;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_msg);
        editText = (EditText) findViewById(R.id.search_editText_id);
        textView = findViewById(R.id.search_msg_note);
        backArrow = (ImageView) findViewById(R.id.search_activity_back_arrow);
        onBackArrowClick();
        searchTextListener();

        recyclerView = (RecyclerView) findViewById(R.id.search_result_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        searchAdapter = new SearchAdapter(msgList, searchKeyword);
        recyclerView.setAdapter(searchAdapter);
    }

    private void searchTextListener() {
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
                searchKeyword = String.valueOf(s);
                msgList = db.messageDao().searchMsg("%" + searchKeyword + "%");
                Log.d(TAG, searchKeyword);
                Log.d(TAG, String.valueOf(msgList.size()));

                if (!searchKeyword.equals("")) {
                    searchAdapter.swapData(msgList, searchKeyword);
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    searchAdapter.swapData(new ArrayList<Message>(),searchKeyword);
                    textView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onBackArrowClick() {
        backArrow.setOnClickListener(v -> onBackPressed());
    }
}
