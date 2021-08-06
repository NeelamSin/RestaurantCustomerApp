package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OrderStatus;
import com.eosinfotech.restaurantcustomerui.Utils.Orientation;
import com.eosinfotech.restaurantcustomerui.Utils.TimeLineModel;
import com.eosinfotech.restaurantcustomerui.Utils.TimeLineViewHolder;
import com.eosinfotech.restaurantcustomerui.Utils.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.item_order_track, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        TimeLineModel timeLineModel = mFeedList.get(position);

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, R.color.colorSecondaryText));
            holder.mMessage.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            holder.mDate.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker), ContextCompat.getColor(mContext, R.color.colorHeadText));
            holder.mMessage.setTextColor(mContext.getResources().getColor(R.color.colorHeadText));
            holder.mDate.setTextColor(mContext.getResources().getColor(R.color.colorLightBrand));
            holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.colorHeadText));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker), ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        }

        holder.mDate.setVisibility(View.VISIBLE);
        holder.mDate.setText(timeLineModel.getDate());
        holder.mMessage.setText(timeLineModel.getMessage());
        holder.image.setImageResource(timeLineModel.getImage());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
