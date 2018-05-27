package in.smslite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.smslite.R;
import in.smslite.activity.SelectContactActivity;
import in.smslite.viewHolder.SelectContactViewHolder;

/**
 * Created by rahul1993 on 5/27/2018.
 */

public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactViewHolder> {
  private static List<String> list;
  private static List<String> phoneNumberList;

  public SelectContactAdapter(List<String> list, List<String> phoneNumberList) {
    this.list = list;
    this.phoneNumberList = phoneNumberList;
  }

  @Override
  public SelectContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_select_contact, parent, false);
    return new SelectContactViewHolder(view, parent.getContext());
  }

  @Override
  public void onBindViewHolder(SelectContactViewHolder holder, int position) {
    holder.setContactName(list.get(position));
    holder.setPhoneNumber(phoneNumberList.get(position));
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public static void updateList(List<String> nameList,List<String> numberList) {
      list.clear();
      phoneNumberList.clear();
      list.addAll(nameList);
      phoneNumberList.addAll(numberList);
      SelectContactActivity.selectContactAdapter.notifyDataSetChanged();
    }
  }

