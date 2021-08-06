package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.OwnClasses.AddToCartButton;
import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<Cart> cartList;
    private Context context;
    DatabaseHandler db;
    OnRefreshViewListner mRefreshListner;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RegularTextView itemCost, itemQuantity;
        public BoldTextView itemName;
        public AddToCartButton addToCart;
        public CustomEditText specialInstruction;

        public MyViewHolder(View view) {
            super(view);
            itemName = (BoldTextView) view.findViewById(R.id.itemName);
            itemQuantity = (RegularTextView) view.findViewById(R.id.itemQuantity);
            itemCost = (RegularTextView) view.findViewById(R.id.itemCost);
            addToCart = (AddToCartButton) view.findViewById(R.id.number_button3);
            specialInstruction = (CustomEditText) view.findViewById(R.id.address_Id);
        }
    }


    public CartAdapter(Context context , List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        db=new DatabaseHandler(context);
        mRefreshListner = (OnRefreshViewListner)context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Cart cart = cartList.get(position);
        holder.itemName.setText(cart.getItemname());
        holder.itemQuantity.setText(context.getString(R.string.item_quantity , String.valueOf(cart.getItemquantity())));
        holder.itemCost.setText(context.getString(R.string.item_cost , cart.getItemtotalprice()));
        holder.addToCart.setNumber(""+cart.getItemquantity());

        if(cart.getStatus().equals("N"))
        {
            holder.addToCart.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.addToCart.setVisibility(View.INVISIBLE);
            holder.specialInstruction.setKeyListener(null);
            holder.specialInstruction.setTextColor(context.getResources().getColor(R.color.colorSecondaryText));
            holder.specialInstruction.setText("you cannot give the special instruction for already placed order");
        }

        holder.addToCart.setOnClickListener(new AddToCartButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = holder.addToCart.getNumber();
                //textView.setText(number);
                holder.addToCart.setNumber(number);
                if(Integer.parseInt(number)!=0) {
                    int count = db.getCartItem(cartList.get(position).getItemid(), Integer.parseInt(sharedpreferences.getString("userid",null)));
                    if (count != 0) {
                        //Toast.makeText(mContext,"_"+count,Toast.LENGTH_SHORT).show();
                        long result = db.editCart(new Cart(
                                0,
                                Integer.parseInt(sharedpreferences.getString("userid",null)),
                               cartList.get(position).getItemid(),
                                cartList.get(position).getItemname(),
                                cartList.get(position).getItemdescription(),
                                cartList.get(position).getItemprice(),
                                Integer.parseInt(number),
                                "",
                                ""+(Integer.parseInt(number)*Float.parseFloat(cartList.get(position).getItemprice()))
                                ,cartList.get(position).getPricelistid()));
                        if (result > 0) {
                            mRefreshListner.refreshView(false,"","");

                        }
                    } else {
                        long result = db.createCart(new Cart(
                                0,
                                Integer.parseInt(sharedpreferences.getString("userid",null)),
                                cartList.get(position).getItemid(),
                                cartList.get(position).getItemname(),
                                cartList.get(position).getItemdescription(),
                                cartList.get(position).getItemprice(),
                                Integer.parseInt(number),
                                "",
                                ""+(Integer.parseInt(number)*Float.parseFloat(cartList.get(position).getItemprice())),cartList.get(position).getPricelistid()));
                        if (result > 0) {
                            mRefreshListner.refreshView(false,"","");
                        }
                    }
                }else
                {
                    db.deleteItem(cartList.get(position).getItemid(),Integer.parseInt(sharedpreferences.getString("userid",null)));
                     mRefreshListner.refreshView(true,"","");
                }
            }
        });

        Log.i("Price","_"+cart.getItemprice());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
