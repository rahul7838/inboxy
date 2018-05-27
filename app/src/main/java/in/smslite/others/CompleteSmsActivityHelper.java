package in.smslite.others;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.utils.MessageUtils;

import static in.smslite.activity.MainActivity.db;



/**
 * Created by rahul1993 on 5/13/2018.
 */

public class CompleteSmsActivityHelper {
  private static final String TAG = CompleteSmsActivityHelper.class.getSimpleName();
  private static ActionMode mActionMode;
  private static boolean isMultiSelect = false;
  private static Context mContext;
  private static Long timeStamp;
  private static List<Long> selectedTimeStampList = new ArrayList<>();
  private static Menu mMenu;
  private static boolean createActionMode = true;

  public static void contextualActionMode(RecyclerView recyclerView, Context context){
    mContext = context;
    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        if (isMultiSelect) {
          timeStamp = CompleteSmsActivity.completeSmsAdapter.SmsConversation.get(position).getTimestamp();
          multi_select(position, view);
          if (CompleteSmsActivity.selectedItem.size()>1){
            mMenu.getItem(2).setVisible(false);
          } else if(CompleteSmsActivity.selectedItem.size()>0) {
            mMenu.getItem(2).setVisible(true);
            mMenu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
          }
        }
      }

      @Override
      public void onItemLongClick(View view, int position) {
        if (!isMultiSelect) {
//          address = smsAdapter.messages.get(position).getAddress();
          timeStamp = CompleteSmsActivity.listOfItem.get(position).getTimestamp();
          isMultiSelect = true;
          if (mActionMode == null) {
            mActionMode = ((CompleteSmsActivity) mContext).startActionMode(mActionModeCallback);
          }
        }
        multi_select(position, view);
      }

    }));
}

  private static void multi_select(int position, View view) {
    if (mActionMode != null) {
      if (CompleteSmsActivity.selectedItem.contains(CompleteSmsActivity.listOfItem.get(position))) {
        CompleteSmsActivity.selectedItem.remove(CompleteSmsActivity.listOfItem.get(position));
        selectedTimeStampList.remove(timeStamp);
      } else {
        CompleteSmsActivity.selectedItem.add(CompleteSmsActivity.listOfItem.get(position));
        selectedTimeStampList.add(timeStamp);
      }
      if (CompleteSmsActivity.selectedItem.size() > 0) {
        mActionMode.setTitle("" + CompleteSmsActivity.selectedItem.size());
      } else {
        mActionModeCallback.onDestroyActionMode(mActionMode);
      }

//
      refreshAdapter();
//
    }
  }

  private static void refreshAdapter() {
    CompleteSmsActivity.completeSmsAdapter.selectedItemAdapter = CompleteSmsActivity.selectedItem;
    CompleteSmsActivity.completeSmsAdapter.listOfItemAdapter = CompleteSmsActivity.listOfItem;
    CompleteSmsActivity.completeSmsAdapter.notifyDataSetChanged();
  }

  private static ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      // Inflate a menu resource providing context menu items
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.menu_multi_select_complete_sms_activity, menu);
      mActionMode = mode;
      mMenu = menu;
      mMenu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      mMenu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      mMenu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//      if(selectedItem.size() > 1) {
//        menu.getItem(2).setVisible(false);
//      }
      CompleteSmsActivity.activity.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.contextual_status_bar_color));
//      context_menu = menu;
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false; // Return false if nothing is done
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.action_delete:
          if(MessageUtils.checkIfDefaultSms(mContext)) {
            alertDialog();
          } else {
            MessageUtils.setDefaultSms(mContext);
          }
          Log.d(TAG, "Action delete");
          return true;
        case R.id.action_select_all:
          Log.d(TAG, "select all");
          CompleteSmsActivity.selectedItem.clear();
          CompleteSmsActivity.selectedItem.addAll(CompleteSmsActivity.listOfItem);
          int size = CompleteSmsActivity.selectedItem.size();
          selectedTimeStampList.clear();
          for(int i = 0; i<size; i++){
            selectedTimeStampList.add(CompleteSmsActivity.selectedItem.get(i).getTimestamp());
          }
          mActionMode.setTitle("" + CompleteSmsActivity.selectedItem.size());
          if (CompleteSmsActivity.selectedItem.size()>1){
            mMenu.getItem(2).setVisible(false);
          }
          refreshAdapter();
          return true;
        case R.id.action_copy:
          Log.d(TAG, CompleteSmsActivity.selectedItem.size() + " body");
          String body = CompleteSmsActivity.selectedItem.get(0).getBody();
          ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
          ClipData clip = ClipData.newPlainText("Copied text", body);
          if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
          }
          Toast.makeText(mContext, "Message copied!", Toast.LENGTH_SHORT).show();
          mActionModeCallback.onDestroyActionMode(mActionMode);
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      mActionMode.finish();
      createActionMode = true;
      mMenu = null;
      mActionMode = null;
      isMultiSelect = false;
      CompleteSmsActivity.selectedItem.clear();
      CompleteSmsActivity.completeSmsAdapter.notifyDataSetChanged();
      selectedTimeStampList.clear();
      CompleteSmsActivity.activity.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
      refreshAdapter();
    }
  };


  private static void alertDialog() {
    deleteDialog(new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            int size = selectedTimeStampList.size();
            for(int i =0; i<size;i++) {
              Log.d(TAG, Long.toString(selectedTimeStampList.get(i)));
//              All the messages having same address will get deleted from below query.
              db.messageDao().deleteSelectedMessage(selectedTimeStampList.get(i));
//              String[] projection = {Telephony.TextBasedSmsColumns.DATE, Telephony.TextBasedSmsColumns.DATE_SENT};
              String where = Telephony.TextBasedSmsColumns.DATE + " LIKE ? "
                  + "OR " + Telephony.TextBasedSmsColumns.DATE_SENT + " LIKE ?";
              String[] arg = {Long.toString(selectedTimeStampList.get(i))};
//              String[] arg = {"1526307785635"};
              int modifiedRows = mContext.getContentResolver().delete(Telephony.Sms.CONTENT_URI, where, arg);
//              Cursor cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, projection, where, arg,null);
//              int count = 0;
//              if (cursor != null) {
//                count = cursor.getCount();
//              }
//              cursor.moveToFirst();
//              String time = cursor.getString(0);
              Log.d(TAG,  " "+ "Integer.toString(count)"+ " count "  + Integer.toString(modifiedRows)+ " modified rows");
            }
            mActionModeCallback.onDestroyActionMode(mActionMode);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            mActionModeCallback.onDestroyActionMode(mActionMode);
            break;
        }
      }
    });

  }

  private static void deleteDialog(DialogInterface.OnClickListener clicked) {
    new AlertDialog.Builder(mContext)
        .setMessage("Are you sure you would like to delete messages?")
        .setTitle("Delete " + CompleteSmsActivity.selectedItem.size() + " messages")
        .setPositiveButton("YES", clicked)
        .setNegativeButton("CANCEL", clicked)
        .create()
        .show();
  }
}
