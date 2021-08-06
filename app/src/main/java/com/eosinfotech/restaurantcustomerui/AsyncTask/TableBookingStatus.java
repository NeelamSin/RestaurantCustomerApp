package com.eosinfotech.restaurantcustomerui.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eosinfotech on 28-09-2018.
 */

public class TableBookingStatus extends AsyncTask<String, String, String>
{
    boolean result=false;
    private Context mContext;
    Runnable runnable;
    Handler handler;
    String tableid="";
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public TableBookingStatus(Context context) {
        this.mContext = context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.runnable=runnable;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params)
    {

        String result="";
        Date cDate = new Date();

        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


        try {
            String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.fetchtablereservationsbyidUrl);
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
            jsonObject.put("ReservationDate",fDate);
            jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
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
            Log.i("Status","_"+jsonObject.toString());
            if(status==200)
            {
                JSONObject responseObject=new JSONObject(response.body().string());

                if(!(responseObject.get("tabledetails") instanceof String)) {
                    JSONArray jsonArray = responseObject.getJSONArray("tabledetails");
                    JSONObject tabledetailsJsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                    String statusres = tabledetailsJsonObject.getString("status");
                    result = tabledetailsJsonObject.getString("status");
                    if(result.equals(""))
                    {
                        result="Requested";
                    }
                    tableid = tabledetailsJsonObject.getString("table_number");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean.equals("Confirmed"))
        {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableconfirmed", "confirmed");
                editor.putString("tableid", tableid);

            editor.commit();
                Log.i("Rpt","Rpt");
        }
        else  if (aBoolean.equals("Accepted"))
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableconfirmed", "accepted");
            editor.commit();
            Log.i("Rpt","Rpt");
        }
        else  if (aBoolean.equals("Rejected"))
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableconfirmed", "rejected");
            editor.commit();
        }
        else  if (aBoolean.equals("Requested"))
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableconfirmed", "requested");
            editor.commit();
        }
    }
}
