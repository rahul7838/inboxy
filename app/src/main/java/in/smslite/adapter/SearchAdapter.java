package in.smslite.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import java.util.ArrayList;
import java.util.List;

import in.smslite.R;
import in.smslite.activity.MainActivity;
import in.smslite.activity.SearchActivity;
import in.smslite.db.Message;
import in.smslite.viewHolder.SMSViewHolder;
import in.smslite.viewHolder.SearchSmsViewHolder;

/**
 * Created by rahul1993 on 4/22/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchSmsViewHolder> {
  private static List<Message> list = new ArrayList<>();
  private Context context;
//  private String searchText;

  public SearchAdapter(List<Message> list, Context context) {
    SearchAdapter.list = list;
    this.context = context;
//    this.searchText = searchText;
  }

  @Override
  public SearchSmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_sms_item, parent, false);
    return new SearchSmsViewHolder(view, parent.getContext());
  }

  @Override
  public void onBindViewHolder(SearchSmsViewHolder holder, int position) {

    String address = list.get(position).getAddress();
    String summary = list.get(position).getBody();

    holder.setTime(list.get(position).getTimestamp());
    holder.setAddress(address);

    SpannableStringBuilder sbSummary = null;
//    SpannableStringBuilder sbAddress = null;
    if(SearchActivity.searchKeyword != null){
    if (SearchActivity.searchKeyword.length() > 0) {
      //color your text here
      int indexSummary = summary.toLowerCase().indexOf(SearchActivity.searchKeyword.toLowerCase());
//      int indexAddress = address.toLowerCase().indexOf(SearchActivity.searchKeyword.toLowerCase());
//      if(indexAddress >= 0){
//        sbAddress = new SpannableStringBuilder(address);
//        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(0, 0, 255)); //specify color here
//        sbAddress.setSpan(fcs, indexAddress, indexAddress + SearchActivity.searchKeyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        holder.setAddress(sbAddress);
//      } else {
//        holder.setAddress(Html.fromHtml(address));
//      }
      if (indexSummary >= 0) {
        sbSummary = new SpannableStringBuilder(summary);
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(0, 0, 255)); //specify color here
        sbSummary.setSpan(fcs, indexSummary, indexSummary + SearchActivity.searchKeyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.setSummary(sbSummary);
      } else {
        holder.setSummary(Html.fromHtml(summary));
      }
    } else {
      holder.setSummary(Html.fromHtml(summary));
//      holder.setAddress(Html.fromHtml(address));
    }
    }
  }

  @Override
  public int getItemCount() {
    if (list != null) {
      return list.size();
    }
    return 0;
  }

  public static void swapData(List<Message> data) {
    if (data == null)
      return;
    if (data != null)
      if (list != null) {
        list.clear();
        list.addAll(data);
        SearchActivity.searchAdapter.notifyDataSetChanged();
    }
  }
}

