package in.inboxy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.inboxy.R;
import in.inboxy.contacts.PhoneContact;
import in.inboxy.viewHolder.ContactViewHolder;

/**
 * Created by rahul1993 on 1/7/2018.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder>{
  private List<PhoneContact> contactList;
  public ContactAdapter(List<PhoneContact> contactList){
    this.contactList = contactList;
  }
  @Override
  public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View viewHolder =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contact_sms_item,parent,false);
    return new ContactViewHolder(viewHolder);
  }

  @Override
  public void onBindViewHolder(ContactViewHolder holder, int position) {
    ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
    contactViewHolder.setContactNameView(contactList.get(position));
//    contactViewHolder.setContactNumberView(contactList.get(position));
//    contactViewHolder.setAvatarView(contactList.get(position));
  }

  @Override
  public int getItemCount() {
    return contactList.size();
  }
}
