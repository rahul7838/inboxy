package in.smslite.viewHolder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.smslite.R;
import me.everything.providers.android.contacts.Contact;

/**
 * Created by rahul1993 on 1/7/2018.
 */


public class ComposeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private View view;
  private TextView contactNameView;
  private TextView contactNumberView;
  private ImageView avatarView;

  public ComposeViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    contactNameView = (TextView) view.findViewById(R.id.contact_name);
    contactNumberView = (TextView) view.findViewById(R.id.contact_number);
    avatarView = (ImageView) view.findViewById(R.id.contact_avatar);

    view.setOnClickListener(this);

  }


  public void setContactNameView(String name){
    contactNameView.setText(name);
  }

  public  void setContactNumberView( String num){
    contactNumberView.setText(num);
  }

  public void setAvatarView(Drawable drawable){
    avatarView.setImageDrawable(drawable);
  }

  @Override
  public void onClick(View v) {

  }
}
