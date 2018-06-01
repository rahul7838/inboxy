package in.smslite.others;

import android.app.Activity;
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
import in.smslite.activity.ArchiveMessageActivity;
import in.smslite.activity.BlockedMessageActivity;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.adapter.SMSAdapter;
import in.smslite.db.Message;
import in.smslite.utils.MessageUtils;


import static in.smslite.activity.MainActivity.localMessageDbViewModel;



/**
 * Created by rahul1993 on 5/8/2018.
 */

public class MainActivityHelper {
  private static final String TAG = MainActivityHelper.class.getSimpleName();
  public static ActionMode mActionMode;
  public static boolean isMultiSelect = false;
  public static List<String> selectedAddressList = new ArrayList<>();
  private RecyclerView recyclerView;
  private FloatingActionButton fab;
  private BottomNavigationView bottomNavigationView;
  private SMSAdapter smsAdapter1;
  private Context context;
  private String address;
  private List<Message> selectedItem = new ArrayList<>();
  private Activity activity;
  private String whichActivity;
  private List<Message> listOfItem = new ArrayList<>();

//  public MainActivityHelper(Context context)

  public void contextualActionMode(RecyclerView recyclerView, FloatingActionButton fab,
                                   BottomNavigationView bottomNavigationView, SMSAdapter smsAdapter,
                                   Activity activity, Context context, String string, List<Message> listOfItem) {

    this.fab = fab;
    this.bottomNavigationView = bottomNavigationView;
    this.context = context;
    this.recyclerView = recyclerView;
    this.smsAdapter1 = smsAdapter;
    this.activity = activity;
    this.whichActivity = string;
    this.listOfItem = listOfItem;

    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        if (isMultiSelect) {
          address = smsAdapter1.messages.get(position).getAddress();
          multi_select(position, mActionModeCallback, selectedItem, listOfItem, address);
          refreshAdapter();
        } else {
          address = smsAdapter1.messages.get(position).getAddress();
          Intent i = new Intent(context, CompleteSmsActivity.class);
          i.putExtra(view.getResources().getString(R.string.address_id), address);
          context.startActivity(i);
        }
      }

      @Override
      public void onItemLongClick(View view, int position) {
        Log.d(TAG, Integer.toString(listOfItem.size()) + listOfItem.get(0).getAddress());
        if (!isMultiSelect) {
          bottomNavigationView.setVisibility(View.GONE);
          fab.setVisibility(View.GONE);
          address = smsAdapter1.messages.get(position).getAddress();
          isMultiSelect = true;
          if (mActionMode == null) {
            if (whichActivity.equals("blocked")) {
              mActionMode = ((BlockedMessageActivity) context).startActionMode(mActionModeCallback);
            } else if (whichActivity.equals("mainActivity")) {
              mActionMode = ((MainActivity) context).startActionMode(mActionModeCallback);
            } else {
              mActionMode = ((ArchiveMessageActivity) context).startActionMode(mActionModeCallback);
            }
          }
        }
        multi_select(position, mActionModeCallback, selectedItem, listOfItem, address);
        refreshAdapter();
      }
    }));
  }

  private static void multi_select(int position, ActionMode.Callback callback, List<Message> selectedItem,
                                   List<Message> listOfItem, String address) {
    if (mActionMode != null) {
      if (selectedItem.contains(listOfItem.get(position))) {
        selectedItem.remove(listOfItem.get(position));
        selectedAddressList.remove(address);
      } else {
        selectedItem.add(listOfItem.get(position));
        selectedAddressList.add(address);
      }
      if (selectedItem.size() > 0) {
        mActionMode.setTitle("" + selectedItem.size());
      } else {
        callback.onDestroyActionMode(mActionMode);
      }
    }
  }

  private static void alertDialog(Context context, ActionMode.Callback callback, List<Message> selectedItem) {
    deleteDialog(context, selectedItem, (dialog, which) -> {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          int size = selectedAddressList.size();
          for (int i = 0; i < size; i++) {
//              All the messages having same address will get deleted from below query.
            localMessageDbViewModel.deleteSelectedConversation(selectedAddressList.get(i));
            String where = Telephony.TextBasedSmsColumns.ADDRESS + " LIKE ?";
            String[] arg = {selectedAddressList.get(i)};
            int modifiedRows = context.getContentResolver().delete(Telephony.Sms.CONTENT_URI, where, arg);
            Log.d(TAG, "Integer.toString(count)" + " count " + Integer.toString(modifiedRows) + " modified rows");
          }
          callback.onDestroyActionMode(mActionMode);
          break;
        case DialogInterface.BUTTON_NEGATIVE:
          callback.onDestroyActionMode(mActionMode);
          break;
      }
    });
  }

  public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      // Inflate a menu resource providing context menu items
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.menu_multi_select, menu);
      menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      menu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      mActionMode = mode;
      activity.getWindow().setStatusBarColor(context.getResources().getColor(R.color.contextual_status_bar_color));
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
          if (MessageUtils.checkIfDefaultSms(context)) {
            MainActivityHelper.alertDialog(context, mActionModeCallback, selectedItem);
          } else {
            MessageUtils.setDefaultSms(context);
          }
          Log.d(TAG, "Action delete");
          return true;
        case R.id.action_select_all:
          Log.d(TAG, "select all");
          selectedItem.clear();
          selectedItem.addAll(listOfItem);
          int size = selectedItem.size();
          selectedAddressList.clear();
          for (int i = 0; i < size; i++) {
            selectedAddressList.add(selectedItem.get(i).getAddress());
          }
          mActionMode.setTitle("" + selectedItem.size());
          refreshAdapter();
          return true;
        case R.id.menu_multi_select_move_to:
          CustomDialog dialog = new CustomDialog(context, selectedItem, mActionModeCallback, whichActivity);
          dialog.show();
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      mActionMode.finish();
      mActionMode = null;
      isMultiSelect = false;
      selectedItem.clear();
      smsAdapter1.notifyDataSetChanged();
      selectedAddressList.clear();
      setVisibility();
      activity.getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
      refreshAdapter();
    }
  };

  private void setVisibility() {
    if (whichActivity.equals("blocked") || whichActivity.equals("archive")) {
      fab.setVisibility(View.GONE);
      bottomNavigationView.setVisibility(View.GONE);
    } else {
      fab.setVisibility(View.VISIBLE);
      bottomNavigationView.setVisibility(View.VISIBLE);
    }
  }

  private void refreshAdapter() {
    smsAdapter1.selectedItemAdapter = selectedItem;
    smsAdapter1.listOfItemAdapter = listOfItem;
    smsAdapter1.notifyDataSetChanged();
  }

  private static void deleteDialog(Context context, List<Message> selectedItem, DialogInterface.OnClickListener clicked) {
    new AlertDialog.Builder(context)
        .setMessage("Are you sure you would like to delete Convsersations?")
        .setTitle("Delete " + selectedItem.size() + " conversations")
        .setPositiveButton("YES", clicked)
        .setNegativeButton("CANCEL", clicked)
        .create()
        .show();
  }
}
