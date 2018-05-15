package in.smslite.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Telephony;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.utils.MessageUtils;

import static in.smslite.activity.MainActivity.db;
import static in.smslite.activity.MainActivity.listOfItem;
import static in.smslite.activity.MainActivity.selectedItem;
import static in.smslite.activity.MainActivity.smsAdapter;


/**
 * Created by rahul1993 on 5/8/2018.
 */

public class MainActivityHelper {
  private static final String TAG = MainActivityHelper.class.getSimpleName();
  private static ActionMode mActionMode;
  private Menu context_menu;
  public static boolean isMultiSelect = false;
  private static Context mContext;
  private static String address;
  private static List<String> selectedAddressList = new ArrayList<>();
  private static FloatingActionButton floatingActionButton;
  private static BottomNavigationView mBottomNavigationView;

  public static void contextualActionMode(RecyclerView recyclerView, final FloatingActionButton fab, BottomNavigationView bottomNavigationView, final Context context) {
    mContext = context;
    floatingActionButton = fab;
    mBottomNavigationView = bottomNavigationView;
    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        if (isMultiSelect) {
          address = smsAdapter.messages.get(position).getAddress();
          multi_select(position, view);
        } else {
          address = smsAdapter.messages.get(position).getAddress();
//          Log.d(TAG, address);
//          Log.d(TAG, "Action mode is off");
          Intent i = new Intent(mContext, CompleteSmsActivity.class);
          i.putExtra(view.getResources().getString(R.string.address_id), address);
          mContext.startActivity(i);

          new Thread(new Runnable() {
            @Override
            public void run() {
              db.messageDao().markAllRead(address);
              Log.d(TAG, "markAllRead");
            }
          }).start();
        }
      }

      @Override
      public void onItemLongClick(View view, int position) {
        if (!isMultiSelect) {
//          MainActivity.selectedItem = new ArrayList<>();
          mBottomNavigationView.setVisibility(View.GONE);
          floatingActionButton.setVisibility(View.GONE);
//          setVisibilityGone();
          address = smsAdapter.messages.get(position).getAddress();
          isMultiSelect = true;
          if (mActionMode == null) {
            mActionMode = ((MainActivity) context).startActionMode(mActionModeCallback);
          }
        }
        multi_select(position, view);
      }
    }));
  }

//  private static void setVisibilityGone() {
//    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//    layoutParams.setMargins(0,0,0,0);
//    MainActivity.relativeLayout.setLayoutParams(layoutParams);
//    floatingActionButton.setVisibility(View.GONE);
//    mBottomNavigationView.setVisibility(View.GONE);
//  }


  private static void multi_select(int position, View view) {
    if (mActionMode != null) {
      if (MainActivity.selectedItem.contains(MainActivity.listOfItem.get(position))) {
        MainActivity.selectedItem.remove(MainActivity.listOfItem.get(position));
        selectedAddressList.remove(address);
//        view.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
      } else {
        MainActivity.selectedItem.add(MainActivity.listOfItem.get(position));
        selectedAddressList.add(address);
//        view.setBackgroundColor(mContext.getResources().getColor(R.color.regular_gray));
      }
      if (MainActivity.selectedItem.size() > 0) {
        mActionMode.setTitle("" + MainActivity.selectedItem.size());
      } else {
        mActionModeCallback.onDestroyActionMode(mActionMode);
      }

//
      refreshAdapter();
//
    }
  }

  private static void refreshAdapter() {
    smsAdapter.selectedItemAdapter = selectedItem;
    smsAdapter.listOfItemAdapter = listOfItem;
    smsAdapter.notifyDataSetChanged();
  }

  private static ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      // Inflate a menu resource providing context menu items
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.menu_multi_select, menu);
      mActionMode = mode;
      MainActivity.activity.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.contextual_status_bar_color));
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
//          alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
//          db.messageDao().deleteSelectedConversation(address);
//          String where = Telephony.TextBasedSmsColumns.ADDRESS + " LIKE?";
//          String[] arg = {address};
//          mContext.getContentResolver().delete(Telephony.Sms.CONTENT_URI,where, arg);
          return true;
        case R.id.action_select_all:
          Log.d(TAG, "select all");
          selectedItem.clear();
          MainActivity.selectedItem.addAll(MainActivity.listOfItem);
          int size = selectedItem.size();
          selectedAddressList.clear();
          for(int i = 0; i<size; i++){
            selectedAddressList.add(selectedItem.get(i).getAddress());
          }
          mActionMode.setTitle("" + MainActivity.selectedItem.size());
//          HashSet<Message> se = new HashSet<>();

          refreshAdapter();
          return true;
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      mActionMode.finish();
      mActionMode = null;
      isMultiSelect = false;
      MainActivity.selectedItem.clear();
      smsAdapter.notifyDataSetChanged();
      selectedAddressList.clear();
      floatingActionButton.setVisibility(View.VISIBLE);
      mBottomNavigationView.setVisibility(View.VISIBLE);
      MainActivity.activity.getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
//      MainActivity.selectedItem = new ArrayList<>();
      refreshAdapter();
    }
  };


  private static void alertDialog() {
    deleteDialog(new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            int size = selectedAddressList.size();
            for(int i =0; i<size;i++) {
//              All the messages having same address will get deleted from below query.
              db.messageDao().deleteSelectedConversation(selectedAddressList.get(i));
              String where = Telephony.TextBasedSmsColumns.ADDRESS + " LIKE ?";
              String[] arg = {selectedAddressList.get(i)};
              int modifiedRows = mContext.getContentResolver().delete(Telephony.Sms.CONTENT_URI, where, arg);
//              Cursor cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, where, arg,null);
//              int count = 0;
//              if (cursor != null) {
//                count = cursor.getCount();
//              }
              Log.d(TAG, "Integer.toString(count)"+" count " + Integer.toString(modifiedRows)+ " modified rows");
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
        .setMessage("Are you sure you would like to delete Convsersations?")
        .setTitle("Delete " + MainActivity.selectedItem.size() + " conversations")
        .setPositiveButton("YES", clicked)
        .setNegativeButton("CANCEL", clicked)
        .create()
        .show();
  }

  // AlertDialog Callback Functions


}
