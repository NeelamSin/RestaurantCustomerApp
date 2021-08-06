package com.eosinfotech.restaurantcustomerui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Restaurant.sqlite";
    private static final int DATABASE_VERSION = 13;

    /*-------------- Cart Table & Columns --------------------- */
    private static final String TABLE_NAME_CART = "Cart";

    private static final String KEY_Id = "Id";
    private static final String KEY_UserId = "UserId";
    private static final String KEY_ItemId = "ItemId";
    private static final String KEY_ItemName = "ItemName";
    private static final String KEY_ItemDescription = "ItemDescription";
    private static final String KEY_ItemImage = "ItemImage";
    private static final String KEY_ItemPrice = "ItemPrice";
    private static final String KEY_ItemQuantity = "ItemQuantity";
    private static final String KEY_ItemTotalPrice = "ItemTotalPrice";
    private static final String KEY_PriceListId = "PriceListId";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(2);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
    }

    public void deleteItem(int itemid,int userid) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM "+TABLE_NAME_CART+" where ItemId="+itemid+" and UserId="+userid);
        database.close();
    }

    public void deleteCart(int userid) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM "+TABLE_NAME_CART+" where UserId="+userid);
        database.close();
    }

    public long createCart(Cart cartModel) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(KEY_Id, cartModel.getId());
        values.put(KEY_UserId,cartModel.getUserid());
        values.put(KEY_ItemId,cartModel.getItemid());
        values.put(KEY_ItemName,cartModel.getItemname());
        values.put(KEY_ItemDescription,cartModel.getItemdescription());
        values.put(KEY_ItemImage,cartModel.getItemimage());
        values.put(KEY_ItemPrice,cartModel.getItemprice());
        values.put(KEY_ItemQuantity,cartModel.getItemquantity());
        values.put(KEY_ItemTotalPrice,cartModel.getItemtotalprice());
        values.put(KEY_PriceListId,cartModel.getPricelistid());

        Log.i("CartAdd",""+values);

        return db.insert(TABLE_NAME_CART, null, values);
    }

    public long editCart(Cart cartModel)
    {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(KEY_Id,cartModel.getId());
        values.put(KEY_UserId,cartModel.getUserid());
        values.put(KEY_ItemId,cartModel.getItemid());
        values.put(KEY_ItemName,cartModel.getItemname());
        values.put(KEY_ItemDescription,cartModel.getItemdescription());
        values.put(KEY_ItemImage,cartModel.getItemimage());
        values.put(KEY_ItemPrice,cartModel.getItemprice());
        values.put(KEY_ItemQuantity,cartModel.getItemquantity());
        values.put(KEY_ItemTotalPrice,cartModel.getItemtotalprice());
        values.put(KEY_PriceListId,cartModel.getPricelistid());

        Log.i("CartEdit",""+values);

        return db.update(TABLE_NAME_CART,values,"ItemId = ? and UserId=?", new String[]{String.valueOf(cartModel.getItemid()),String.valueOf(cartModel.getUserid())});
    }

    public int getCartItem(int itemid,int userid) {
        int count=0;
        Cursor cursor=null;
        try {
            cursor=getReadableDatabase().rawQuery("SELECT  * FROM Cart where ItemId=" + itemid + " and UserId="+userid, null);
            count=cursor.getCount();
            cursor.close();
        }
        catch (Exception e)
        {
            Log.i("CartCount",""+e.getLocalizedMessage());
           // cursor.close();
        }

        return count;
    }

    public int getCartCount(int userid) {
        int count=0;
        Cursor cursor=null;
        try {
            cursor=getReadableDatabase().rawQuery("SELECT  * FROM Cart where UserId="+userid, null);
            count=cursor.getCount();
            cursor.close();
        }
        catch (Exception e)
        {
            Log.i("CartCount",""+e.getLocalizedMessage());
            // cursor.close();
        }

        return count;
    }

    public String getTotal(int userid) {

        String total="0";
        String selectQuery = "SELECT  sum(ItemTotalPrice) FROM "+TABLE_NAME_CART+" WHERE UserId="+userid;

        try {
            Cursor cursor = getWritableDatabase().rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {

                    total=String.valueOf(cursor.getFloat(0));
                    Log.i("total",""+total);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }
        return total;
    }

    public ArrayList<Cart> getAllCartItems(int userid) {

        ArrayList<Cart> cartList = new ArrayList();
        String selectQuery = "SELECT  * FROM "+TABLE_NAME_CART+" WHERE UserId="+userid;
        try {
            Cursor cursor = getWritableDatabase().rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    cartList.add(new Cart(0,cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getInt(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),"N"));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        return cartList;
    }

    public int getRecomCartItems(int userid,String itemid) {

        int cartList = 0;
        String selectQuery = "SELECT  * FROM "+TABLE_NAME_CART+" WHERE UserId="+userid+" and ItemId=" + itemid ;
        try {
            Cursor cursor = getWritableDatabase().rawQuery(selectQuery,null);
            if (cursor.moveToFirst()) {
                do {
                    cartList=cursor.getInt(6);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        }

        return cartList;
    }

}
