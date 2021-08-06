package com.eosinfotech.restaurantcustomerui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.ArrayList;

public class TableBookingAdapter extends BaseAdapter {

    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;
    private ArrayList<String> listTableNumber;
    private ArrayList<String> listTableStatus;
    private Activity activity;

    public TableBookingAdapter(Activity activity,ArrayList<String> listCountry, ArrayList<Integer> listFlag, ArrayList<String> listTableNumber, ArrayList<String> listTableStatus) {
        super();
        this.listCountry = listCountry;
        this.listFlag = listFlag;
        this.listTableNumber = listTableNumber;
        this.listTableStatus = listTableStatus;
        this.activity = activity;      }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listCountry.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return listCountry.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {
        public ImageView imgViewFlag;
        public BoldTextView txtViewTitle;
        public RegularTextView tableNumber, tableStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.item_table_booking, null);

            view.txtViewTitle = (BoldTextView) convertView.findViewById(R.id.textView1);
            view.imgViewFlag = (ImageView) convertView.findViewById(R.id.imageView1);
            view.tableNumber = (RegularTextView) convertView.findViewById(R.id.tableNumber);
            view.tableStatus = (RegularTextView) convertView.findViewById(R.id.tableStatus);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(listCountry.get(position));
        view.imgViewFlag.setImageResource(listFlag.get(position));
        view.tableNumber.setText("T.N" + " "+listTableNumber.get(position));
        view.tableStatus.setText("" + " "+listTableStatus.get(position));

        return convertView;
    }
}