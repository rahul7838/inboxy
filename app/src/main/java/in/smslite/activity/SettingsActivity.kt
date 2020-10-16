package `in`.smslite.activity

import `in`.smslite.databinding.SettingActivityBinding
import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        this.setTitle("Settings ");
        binding = SettingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //    android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.setting_fragment_id);
//    setSupportActionBar(toolbar);
//    toolbar.setTitle("Setting");
        val actionBar = this.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


//    getFragmentManager().beginTransaction()
//        .replace(android.R.id.content, new SettingFragment())
//        .commit();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = SettingsActivity::class.java.simpleName
        const val NEW_NOTIFICATION_MESSAGE = "new_notification"
    }
}