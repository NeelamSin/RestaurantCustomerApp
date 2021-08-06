package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.DuplicateModel;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.List;


public class DuplicateMenuAdapter extends RecyclerView.Adapter<DuplicateMenuAdapter.ViewHolder> {

    private Context context;
    private List<DuplicateModel> personUtils;

    public DuplicateMenuAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duplicate_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        DuplicateModel pu = personUtils.get(position);

        holder.pName.setText(pu.getPersonName());
        holder.pJobProfile.setText(pu.getJobProfile());

    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public BoldTextView pName;
        public RegularTextView pJobProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            pName = (BoldTextView) itemView.findViewById(R.id.pNametxt);
            pJobProfile = (RegularTextView) itemView.findViewById(R.id.pJobProfiletxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DuplicateModel cpu = (DuplicateModel) view.getTag();
                    Toast.makeText(view.getContext(), cpu.getPersonName()+"  ", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}
