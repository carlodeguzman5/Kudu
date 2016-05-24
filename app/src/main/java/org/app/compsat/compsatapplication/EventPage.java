package org.app.compsat.compsatapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.app.compsat.compsatapplication.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class EventPage extends Activity {

    private static final String TAG_EVENT_ID = "event_id";
    private static final String TAG_EVENT = "event_name";
    private static final String TAG_DATE = "event_date";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_HOST = "host_id";

    private TextView event_name, hostedBy, time, date, location;
    //private EditText event_name = (EditText)findViewById(R.id.eventName);
    //private EditText event_name = (EditText)findViewById(R.id.eventName);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        event_name = (TextView)findViewById(R.id.eventName);
        hostedBy = (TextView)findViewById(R.id.hostedby);
        time = (TextView)findViewById(R.id.eventTime);
        date = (TextView)findViewById(R.id.eventDate);
        location = (TextView)findViewById(R.id.eventvenue);

        Log.d("EVENT ID", getIntent().getExtras().get("eventId") + "");

        new LoadEventPage().execute(getIntent().getExtras().get("eventId") + "");
    }

    class LoadEventPage extends AsyncTask<String, String, String> {
        JSONArray json = null;
        @Override
        protected String doInBackground(String... params) {

            String url = "http://192.168.1.3/PEDC/index.php/Event_controller/eventById/format/json";



            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("eventId", params[0]);
                StringEntity se = new StringEntity( jsonParam.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = client.execute(httpPost);
                String responseBody = EntityUtils.toString(response.getEntity());
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.d("Result", statusCode+"");
                if(statusCode == 200) {
                    JSONArray jsonObject = new JSONArray(responseBody);
                    json = jsonObject;
                    Log.d("JSON", json+"");
                }else{
                    json = new JSONArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }




        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonOb = new JSONObject();
            try{
                jsonOb = new JSONObject(json.get(0)+"");
            }catch(JSONException e){

            }
            try {
                event_name.setText(jsonOb.getString(TAG_EVENT));
                hostedBy.setText(jsonOb.getString("name"));
                String datetime = jsonOb.getString(TAG_DATE);
                String month = datetime.split("-")[1];
                String day = datetime.split("-")[2];
                String year = datetime.split("-")[0];
                date.setText(day + " " + month + " " + year);
                location.setText(jsonOb.getString("location"));




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
