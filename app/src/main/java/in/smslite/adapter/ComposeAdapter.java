package in.smslite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.smslite.R;
import in.smslite.contacts.PhoneContact;
import in.smslite.contacts.PhoneContactList;
import in.smslite.viewHolder.ComposeViewHolder;
import me.everything.providers.android.contacts.Contact;

/**
 * Created by rahul1993 on 1/7/2018.
 */

public class ComposeAdapter extends RecyclerView.Adapter<ComposeViewHolder>{
  private List<PhoneContact> contactList;
  private Context context;
  public ComposeAdapter(List<PhoneContact> contactList, Context context){
    this.contactList = contactList;
    this.context = context;
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
    composeViewHolder.setContactNameView(contactList.get(position).getName());
    composeViewHolder.setContactNumberView(contactList.get(position).getNumber());
    composeViewHolder.setAvatarView(contactList.get(position).getAvatar(context));
  }

  @Override
  public int getItemCount() {
    return contactList.size();
  }
}
