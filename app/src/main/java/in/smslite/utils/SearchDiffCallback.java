package in.smslite.utils;

import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by rahul1993 on 4/22/2018.
 */

public class SearchDiffCallback extends DiffUtil.Callback{

  @Override
  public int getOldListSize() {
    return 0;
  }

  @Override
  public int getNewListSize() {
    return 0;
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return false;
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    return false;
  }
}
