package in.smslite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.activity.MainActivity;
import in.smslite.db.Message;
import in.smslite.viewHolder.SMSViewHolder;

/**
 * Created by rahul1993 on 11/15/2017.
 */

public class SMSAdapter extends RecyclerView.Adapter<SMSViewHolder> {
    public  List<Message> messages;
    private final static int READ = 0;
    private final static int UNREAD = 1;
    public  List<Message> selectedItemAdapter = new ArrayList<>();
    public  List<Message> listOfItemAdapter = new ArrayList<>();

  public SMSAdapter(List<Message> messages, List<Message> selectedItemAdapter, List<Message> listOfItemAdapter){
    this.messages = messages;
    this.listOfItemAdapter = listOfItemAdapter;
    this.selectedItemAdapter =selectedItemAdapter;
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
    if(selectedItemAdapter.contains(listOfItemAdapter.get(position))){
      holder.setBackgroundColor();
      holder.setDividerLineInvisible();
    } else {
      holder.setBackgroundColorWhite();
      holder.setDividerLineVisible();
    }
    holder.setAddress(messages.get(position).getAddress());
    holder.setSummary(messages.get(position).getBody());
    holder.setTime(messages.get(position).getTimestamp());
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
    notifyDataSetChanged();
  }
}

