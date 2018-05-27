package in.smslite.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.utils.ContactUtils;

/**
 * Created by rahul1993 on 5/27/2018.
 */

public class SelectContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private View view;
  private String address;
  private TextView contactName, phoneNumber;
  private ImageView avatar;
  private Context context;
  public SelectContactViewHolder(View itemView, Context context) {
    super(itemView);
    view = itemView;
    contactName = view.findViewById(R.id.textview1_select_contact_id);
    phoneNumber = view.findViewById(R.id.textView2_select_contact_id);
    avatar = view.findViewById(R.id.imageView_select_contact_id);
    this.context = context;
    view.setOnClickListener(this);
  }

  public void setContactName(String name){
    contactName.setText(name);
    setAvatar(name);
  }

  public void setPhoneNumber(String number){
    this.address = number;
    phoneNumber.setText(number);
  }

  public void setAvatar(String displayName){
    Drawable drawable = in.smslite.drawable.DataSource.getInstance(context).getDrawable(displayName);
//    Bitmap b = in.smslite.utils.DrawableUtils.getBitmap(drawable);
//
//  Drawable drawable1 = RoundedBitmapDrawableFactory.create(context.getResources(), b);
    avatar.setImageDrawable(drawable);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent(context, CompleteSmsActivity.class);
    intent.putExtra(context.getResources().getString(R.string.address_id), ContactUtils.normalizeNumber(address));
    context.startActivity(intent);
  }
}
