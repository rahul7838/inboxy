package in.smslite.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.contacts.Contact;
import in.smslite.contacts.PhoneContact;
import in.smslite.utils.ContactUtils;
import in.smslite.utils.TimeUtils;

import static in.smslite.activity.MainActivity.db;

/**
 * Created by rahul1993 on 4/22/2018.
 */

public class SearchSmsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private static final String TAG = SearchSmsViewHolder.class.getSimpleName();
  private View view;
  private String address;
  private Context mContext;
  private TextView summaryView;
  private TextView titleView;
  private TextView timeView;
  private ImageView imageView;
  SMSViewHolder smsViewHolder;

  public SearchSmsViewHolder(View itemView, Context context) {
    super(itemView);
    this.mContext = context;
    view = itemView;
    titleView = (TextView) view.findViewById(R.id.sms_title);
    summaryView = (TextView) view.findViewById(R.id.sms_summary);
    timeView = (TextView) view.findViewById(R.id.sms_time);
    imageView = (ImageView) view.findViewById(R.id.avatar);
    view.setOnClickListener(this);
  }

  public void setAddress(String address) {
    if (address == null || address.isEmpty()) {
      address = "Unknown Sender";
    }
    this.address = address;
//    titleView.setText(address);
    Contact contact = ContactUtils.getContact(address, mContext, true);
    titleView.setText(contact.getDisplayName());
    setAvatar(contact);
  }

  public void setSummary(Spanned smsBody) {
    summaryView.setText(smsBody);
  }

  public void setTime(long x) {
//    Date date = new Date(x);// we have to pass Date in getPrettyElaspsedTime, convert long into Date.
    String time = TimeUtils.getPrettyElapsedTime(x);
//        Log.i(TAG, time);
    timeView.setText(time);
  }

  @Override
  public void onClick(View view) {
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        db.messageDao().markAllRead(address);
//        Log.i(TAG, "markAllRead");
//      }
//    }).start();
    Intent i = new Intent(mContext, CompleteSmsActivity.class);
    i.putExtra(view.getResources().getString(R.string.address_id), address);
    mContext.startActivity(i);
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
}
