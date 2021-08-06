package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.ItemRateCommentView;
import com.eosinfotech.restaurantcustomerui.OwnClasses.RoundRectCornerImageView;
import com.eosinfotech.restaurantcustomerui.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ItemRateCommentAdapter extends RecyclerView.Adapter<ItemRateCommentAdapter.ViewHolder> {

    private Context context;
    private List<ItemRateCommentView> personUtils;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    public ItemRateCommentAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating_comment_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        ItemRateCommentView pu = personUtils.get(position);

        holder.userName.setText(pu.getUserName());
        holder.userDate.setText(pu.getUserDate());
        holder.userComment.setText(pu.getUserComment());
        Picasso.get().load(pu.getImage()).placeholder(R.drawable.ic_user).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.roundRectCornerImageView);
        holder.ratingBar.setRating(Float.parseFloat(pu.getRating()));

    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "_" + personUtils.size());
        Log.i("Refresh Token", "_" + FirebaseInstanceId.getInstance().getToken());
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public RegularTextView userName;
        public RatingBar ratingBar;
        public RoundRectCornerImageView roundRectCornerImageView;
        public LightTextView userDate , userComment;

        public ViewHolder(View itemView) {
            super(itemView);

            roundRectCornerImageView = (RoundRectCornerImageView) itemView.findViewById(R.id.circleviewimg1);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            userName = (RegularTextView) itemView.findViewById(R.id.userName);
            userDate = (LightTextView) itemView.findViewById(R.id.userDate);
            userComment = (LightTextView) itemView.findViewById(R.id.userComment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { ItemRateCommentView cpu = (ItemRateCommentView) view.getTag();
                    //Toast.makeText(view.getContext(), cpu.getPersonName()+" is "+ cpu.getJobProfile(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
