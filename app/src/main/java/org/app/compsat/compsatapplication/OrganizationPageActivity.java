package org.app.compsat.compsatapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class OrganizationPageActivity extends Activity {

    TextView orgText, descText;
    ImageButton subs;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_page);

        context = this;

        String hostId = getIntent().getExtras().getString("hostId");
        subs = (ImageButton) findViewById(R.id.imageButton5);
        orgText = (TextView) findViewById(R.id.hostName);
        descText = (TextView) findViewById(R.id.descText);

        Resources resources = getResources();
        subs.setImageDrawable(resources.getDrawable(R.drawable.subscribe));


        new LoadEventPage().execute(hostId);
    }

    public void subscribe(View view){
        SharedPreferences prefs;
        prefs = getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);
        String hostId = getIntent().getExtras().getString("hostId");
        new SubscribeToHost().execute(hostId,prefs.getString("username",""));
        Toast.makeText(this, "You have subscribed", Toast.LENGTH_LONG).show();

    }

    class LoadEventPage extends AsyncTask<String, String, String> {
        JSONArray json = null;
        @Override
        protected String doInBackground(String... params) {

            InputStream is = null;
            try {
                is = context.getAssets().open("db/db.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(is);
            String prefix = scanner.nextLine();
            scanner.close();

            String url = prefix + "index.php/Host_controller/getHostById/format/json";

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("hostId", params[0]);
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
                Log.d("Result", statusCode + "");
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
                orgText.setText(jsonOb.getString("name"));
                descText.setText(jsonOb.getString("description"));




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



    class SubscribeToHost extends AsyncTask<String, String, String> {
        JSONArray json = null;
        @Override
        protected String doInBackground(String... params) {

            InputStream is = null;
            try {
                is = context.getAssets().open("db/db.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(is);
            String prefix = scanner.nextLine();
            scanner.close();

            String url = prefix + "index.php/Host_controller/createSubscription/format/json";

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("hostId", params[0]);
                jsonParam.put("userId", params[1]);
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
                Log.d("Result", statusCode + "");
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
    }


}
