package com.eosinfotech.restaurantcustomerui.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eosinfotech on 28-09-2018.
 */

public class OrderPaymentAsyncTask extends AsyncTask<String, String, Boolean>
{
    boolean result=false;

    private Context mContext;

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public OrderPaymentAsyncTask(Context context) {
        this.mContext = context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        }

    @Override
    protected Boolean doInBackground(String... params)
    {

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

        try {
            String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.UdpateOrderHeaderUrl);
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("Transaction_Amount",params[0]);
            jsonObject.put("Transaction_remarks",params[3]);
            jsonObject.put("transaction_reference",params[2]);
            jsonObject.put("status","Closed");
            jsonObject.put("updated_date",fDate);
            jsonObject.put("updated_by",sharedpreferences.getString("userid",null));
            if(sharedpreferences.contains("discountid"))
            {
                if (!(sharedpreferences.getString("discountid", null).equals("") || sharedpreferences.getString("discountid", null).equals(null)))
                {
                    jsonObject.put("DiscountID",sharedpreferences.getString("discountid",null));
                }
                else
                {
                    jsonObject.put("DiscountID","");

                }
            }
            else
            {
                jsonObject.put("DiscountID","");
            }
            jsonObject.put("order_total",params[0]);
            jsonObject.put("tableno",params[1]);
            jsonObject.put("header_id",sharedpreferences.getString("headerid",null));
            jsonObject.put("clientId",mContext.getResources().getString(R.string.clientId));
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            int status=response.code();
            Log.i("Status","_"+status);
            if(status==200)
            {
               result=true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }


}
