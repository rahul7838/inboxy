package in.inboxy.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import in.inboxy.R;
import in.inboxy.activity.CompleteSmsActivity;
import in.inboxy.utils.TimeUtils;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class SMSViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private static final String TAG = "SMSViewHolder";
  View view;
  String address;
  Context mContext;
  TextView summaryView;
  TextView titleView;
  TextView timeView;
  ImageView imageView;

  public SMSViewHolder(View itemView, Context mContext) {
    super(itemView);
    this.mContext = mContext;
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
    titleView.setText(address);
    /*Contact contact = ContactUtils.getContact(address, mContext, true);
    titleView.setText(contact.getDisplayName());
    setAvatar(contact);*/
  }

  public void setSummary(String smsBody) {
    summaryView.setText(smsBody);
  }

  public void setTime(long x) {
    Date date = new Date(x);// we have to pass Date in getPrettyElaspsedTime, convert long into Date.
    String time = TimeUtils.getPrettyElapsedTime(date);
//        Log.i(TAG, time);
    timeView.setText(time);
  }

  @Override
  public void onClick(View view) {
    Intent i = new Intent(mContext, CompleteSmsActivity.class);
    i.putExtra(view.getResources().getString(R.string.address_id), address);
    mContext.startActivity(i);
  }
}
