package in.smslite.others;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import in.smslite.R;

/**
 * Created by rahul1993 on 5/8/2018.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
  public static interface OnItemClickListener
  {
    public void onItemClick(View view, int position);
    public void onItemLongClick(View view, int position);
  }

  private OnItemClickListener mListener;
  private GestureDetector mGestureDetector;

  public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener)
  {
    mListener = listener;

    mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
    {
      @Override
      public boolean onSingleTapUp(MotionEvent e) {
//        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
//
//        if(childView != null && mListener != null) {
//          mListener.onItemClick(childView, recyclerView.getChildLayoutPosition(childView));
//        }
//
        return true;
      }

      @Override
      public void onLongPress(MotionEvent e)
      {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if(childView != null && mListener != null)
        {
          mListener.onItemLongClick(childView, recyclerView.getChildLayoutPosition(childView));
        }
      }
    });
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e)
  {
    View childView = view.findChildViewUnder(e.getX(), e.getY());

    if(childView != null && mListener != null && mGestureDetector.onTouchEvent(e))
    {
      mListener.onItemClick(childView, view.getChildPosition(childView));
    }

    return false;
  }

  @Override
  public void onTouchEvent(RecyclerView view, MotionEvent motionEvent){}

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

  }

}
