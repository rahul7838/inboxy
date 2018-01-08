package in.inboxy.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.inboxy.R;

/**
 * Created by rahul1993 on 1/7/2018.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
  private View view;
  private TextView contactNameView;
  private TextView contactNumberView;
  private ImageView avatarView;

  public ContactViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    contactNameView =(TextView) view.findViewById(R.id.contact_name);
    contactNumberView =(TextView) view.findViewById(R.id.contact_number);
    avatarView =(ImageView) view.findViewById(R.id.contact_avatar);

    view.setOnClickListener(this);

  }

  public void setContactNameView(in.inboxy.contacts.Contact contact){
    contactNameView.setText(contact.getDisplayName());
  }

//  public  void setContactNumberView( in.inboxy.contacts.Contact contact){
//    contactNumberView.setText(contact.phone);
//  }

//  public void setAvatarView(Contact contact){
//    String uri = contact.uriPhoto;
//
//  }

  @Override
  public void onClick(View v) {

  }
}
