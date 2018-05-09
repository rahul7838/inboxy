package in.smslite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.List;

import in.smslite.R;
import in.smslite.db.Message;
import in.smslite.viewHolder.SMSViewHolder;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class SMSAdapter extends RecyclerView.Adapter<SMSViewHolder> {
    List<Message> messages;
    private final static int READ = 0;
    private final static int UNREAD = 1;
  public SMSAdapter(List<Message> messages){
    this.messages = messages;
  }

  @Override
  public SMSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == READ) {
      View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_read_sms, parent, false);
      return new SMSViewHolder(viewHolder, parent.getContext());
    }
    else {
      View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_unread_sms, parent, false);
      return new SMSViewHolder(viewHolder, parent.getContext());
    }
  }

  @Override
  public void onBindViewHolder(SMSViewHolder holder, int position) {
    holder.setAddress(messages.get(position).address);
    holder.setSummary(messages.get(position).body);
    holder.setTime(messages.get(position).timestamp);
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  @Override
  public int getItemViewType(int position){
    return (messages.get(position).read) ? READ : UNREAD;
  }

  public void setMessage(List<Message> messages){
    this.messages = messages;
  }
}

