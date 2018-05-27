package in.smslite.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.provider.Telephony;
import android.util.Log;
import android.view.ActionMode;



import java.util.ArrayList;
import java.util.List;


import in.smslite.db.Message;



import static in.smslite.activity.MainActivity.localMessageDbViewModel;



/**
 * Created by rahul1993 on 5/8/2018.
 */

public class MainActivityHelper {
  private static final String TAG = MainActivityHelper.class.getSimpleName();
  public static ActionMode mActionMode;
  public static boolean isMultiSelect = false;
  public static List<String> selectedAddressList = new ArrayList<>();

  public static void multi_select(int position, ActionMode.Callback callback, List<Message> selectedItem,
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

  public static void alertDialog(Context context, ActionMode.Callback callback, List<Message> selectedItem) {
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
