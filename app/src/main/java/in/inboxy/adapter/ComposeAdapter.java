package in.inboxy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.inboxy.R;
import in.inboxy.contacts.PhoneContact;
import in.inboxy.viewHolder.ComposeViewHolder;

/**
 * Created by rahul1993 on 1/7/2018.
 */

public class ComposeAdapter extends RecyclerView.Adapter<ComposeViewHolder>{
  private List<PhoneContact> contactList;
  public ComposeAdapter(List<PhoneContact> contactList){
    this.contactList = contactList;
  }
  @Override
  public ComposeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View viewHolder =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contact_sms_item,parent,false);
    return new ComposeViewHolder(viewHolder);
  }

  @Override
  public void onBindViewHolder(ComposeViewHolder holder, int position) {
    ComposeViewHolder composeViewHolder = (ComposeViewHolder) holder;
//    composeViewHolder.setContactNameView(contactList.get(position));
//    composeViewHolder.setContactNumberView(contactList.get(position));
//    composeViewHolder.setAvatarView(contactList.get(position));
  }

  @Override
  public int getItemCount() {
    return contactList.size();
  }
}
