package in.smslite.viewHolder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.activity.MainActivity;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.db.MessageDatabase;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.TimeUtils;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class SMSViewHolder extends RecyclerView.ViewHolder {
  private static final String TAG = "SMSViewHolder";
  private View view;
  private String address;
  private Context mContext;
  private TextView summaryView;
//  private TextView titleView;
  private TextView timeView;
  private ImageView imageView;
  private RecyclerView recyclerView;
  private View dividerLine;
 @BindView(R.id.sms_title)
 TextView titleView;
//  private static ActionMode mActionMode;
//  private Menu context_menu;
//  public static boolean isMultiSelect = false;

  public SMSViewHolder(View itemView, Context mContext) {
    super(itemView);
    ButterKnife.bind(this,itemView);
    this.mContext = mContext;
    view = itemView;
    summaryView = (TextView) view.findViewById(R.id.sms_summary);
    timeView = (TextView) view.findViewById(R.id.sms_time);
    imageView = (ImageView) view.findViewById(R.id.avatar);
    dividerLine = (View) view.findViewById(R.id.itemDivider);
  }

  public  void setDividerLineVisible(){
    dividerLine.setVisibility(View.VISIBLE);
  }

  public void setDividerLineInvisible(){
    dividerLine.setVisibility(View.INVISIBLE);
  }

  public void setAddress(String address) {
    if (address == null || address.isEmpty()) {
      address = "Unknown Sender";
    }
    this.address = address;
    Contact contact = PhoneContact.get(address, true);
//    Contact contact = ContactUtils.getContact(address, mContext, true);
    titleView.setText(contact.getDisplayName());
//    titleView.setText(address);
    setAvatar(contact);
  }

  public void setSummary(String smsBody) {
    summaryView.setText(smsBody);
  }

  public void setTime(long x) {
//    Date date = new Date(x);// we have to pass Date in getPrettyElaspsedTime, convert long into Date.
    String time = TimeUtils.getPrettyElapsedTime(x);
//        Log.i(TAG, time);
    timeView.setText(time);
  }

  private void setAvatar(Contact contact) {
   /* if (Contact.Source.FIREBASE.equals(contact.getSource())) {
      CompanyContact companyContact = (CompanyContact) contact;
      Glide.with(mContext)
              .using(new FirebaseImageLoader())
              .load(FirebaseUtils.getStorageRef().child(companyContact.getUriPhoto()))
              .placeholder(R.drawable.ic_account)
              .error(R.drawable.ic_account)
              .into(imageView);
    } else {*/
    Drawable drawable = contact.getAvatar(mContext);
    imageView.setImageDrawable(drawable);
    imageView.setOnClickListener(onImageClick(contact));
  }

  private View.OnClickListener onImageClick(final Contact contact) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Contact.Source.PHONE.equals(contact.getSource())) {
          PhoneContact phoneContact = (PhoneContact) contact;
          ContactsContract.QuickContact.showQuickContact(mContext, view,
              phoneContact.getUri(),
              ContactsContract.QuickContact.MODE_LARGE, null);
        } else {
          Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
              Uri.fromParts("tel", address, null));
          v.getContext().startActivity(intent);
        }
      }
    };
  }

  public void setBackgroundColorWhite() {
    view.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
  }

  public void setBackgroundColor() {
    view.setBackgroundColor(mContext.getResources().getColor(R.color.item_selected));
  }

//  @Override
//  public void onClick(View view) {
    /*if (isMultiSelect) {
      Log.d(TAG, "Action mode is on");
//      multi_select(getLayoutPosition());
    } else {
      Log.d(TAG, "Action mode is off");
      Intent i = new Intent(mContext, CompleteSmsActivity.class);
      i.putExtra(view.getResources().getString(R.string.address_id), address);
      mContext.startActivity(i);

      new Thread(new Runnable() {
        @Override
        public void run() {
          db.messageDao().markAllReadByAddress(address);
          Log.d(TAG, "markAllReadByAddress");
        }
      }).start();*/
//    }
//  }

//  @Override
//  public boolean onLongClick(View v) {
    /*if (!isMultiSelect) {
//          MainActivity.selectedItem = new ArrayList<>();
      isMultiSelect = true;
      if (mActionMode == null) {
        mActionMode = ((MainActivity) mContext).startActionMode(mActionModeCallback);
      }
    }
    multi_select(getLayoutPosition());*/
//    return true;
//  }

  /*private void multi_select(int position) {
    if (mActionMode != null) {
      if (MainActivity.selectedItem.contains(MainActivity.listOfItem.get(position))) {
        MainActivity.selectedItem.remove(MainActivity.listOfItem.get(position));
        view.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
        Log.d(TAG, "white");
      } else {
        MainActivity.selectedItem.add(MainActivity.listOfItem.get(position));
        view.setBackgroundColor(mContext.getResources().getColor(R.color.regular_gray));
        Log.d(TAG, "color");
      }
      if (MainActivity.selectedItem.size() > 0) {
        mActionMode.setTitle("" + MainActivity.selectedItem.size());
      } else {
        mActionModeCallback.onDestroyActionMode(mActionMode);
      }
//
//      refreshAdapter();
//
    }
  }

  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      // Inflate a menu resource providing context menu items
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.menu_multi_select, menu);
      mActionMode = mode;
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
          Log.d(TAG, "Action delete");
//          alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
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
//      refreshAdapter();
    }
  };*/
}

