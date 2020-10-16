package `in`.smslite.activity

import `in`.smslite.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Created by rahul1993 on 1/7/2018.
 */
class ComposeSmsActivity : AppCompatActivity() {
    //  static List<Contact> mContactList;
    var context: Context? = null

    //
    // @BindView(R.id.compose_sms_recycle_view)
    //  static RecyclerView recyclerView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        //    new ContactAsyncTask().execute(context);
        setContentView(R.layout.activity_contact_sms)
        setToolbar()
        val button = findViewById<Button>(R.id.send_sms_to_select_button)
        button.setOnClickListener {
            val editView = findViewById<EditText>(R.id.send_sms_to_id)
            val address = editView.text.toString()
            val intent = Intent(applicationContext, CompleteSmsActivity::class.java)
            intent.putExtra(getString(R.string.address_id), address)
            startActivity(intent)
        }

//    recyclerView = (RecyclerView) findViewById(R.id.compose_sms_recycle_view);/
//    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//    recyclerView.setLayoutManager(linearLayoutManager);
//    recyclerView.setHasFixedSize(true);
//    ComposeAdapter adapter = new ComposeAdapter();
//    recyclerView.setAdapter(adapter);
    }

    private fun setToolbar() {
        val toolbar = findViewById<View>(R.id.contact_toolbar) as Toolbar
        toolbar.setTitle(R.string.title_activity_contact)
        setSupportActionBar(toolbar)
    }
}