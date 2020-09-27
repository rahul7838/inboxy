package in.smslite.adapter;

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
import in.smslite.db.Message;
import in.smslite.viewHolder.SearchSmsViewHolder;

/**
 * Created by rahul1993 on 4/22/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchSmsViewHolder> {
    private List<Message> list = new ArrayList<>();
    private String searchKeyword;

    public SearchAdapter(List<Message> list, String searchKeyword) {
        this.list = list;
        this.searchKeyword = searchKeyword;
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

        SpannableStringBuilder sb;
        if (searchKeyword != null) {
            if (searchKeyword.length() > 0) {
                //color your text here
                int index = summary.toLowerCase().indexOf(searchKeyword.toLowerCase());
                if (index >= 0) {
                    sb = new SpannableStringBuilder(summary);
                    ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(0, 0, 255)); //specify color here
                    sb.setSpan(fcs, index, index + searchKeyword.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    holder.setSummary(sb);
                } else {
                    holder.setSummary(Html.fromHtml(summary));
                }
            } else {
                holder.setSummary(Html.fromHtml(summary));
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

    public void swapData(List<Message> data, String searchKeyword) {
        if (data == null)
            return;
        if (list != null) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }
        this.searchKeyword = searchKeyword;
    }
}

