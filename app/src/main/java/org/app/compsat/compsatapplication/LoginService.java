package org.app.compsat.compsatapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LoginService extends AsyncTask<String, Void, String> {
    private Context context;
    private String name;
    private LocalBroadcastManager localBroadcastManager;
    private ProgressDialog pDialog;

    public LoginService(Context context) {

        this.context = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setIcon(R.drawable.gear);
        pDialog.setMessage("Logging in...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected String doInBackground(String... str) {
        try {
            String username = (String) str[0];
            String password = (String) str[1];
            name = username;
            InputStream is = context.getAssets().open("db/db.properties");
            Scanner scanner = new Scanner(is);
            String prefix = scanner.nextLine();
            scanner.close();


            String link = prefix + "index.php/User_controller/login/format/json/";

            getJSON(link, username, password);

            return "";
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }

    }

    public String getJSON(String address, String username, String password){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(address);
        JSONObject json = new JSONObject();
        try {
            json.put("userId", username);
            json.put("password", password);
            StringEntity se = new StringEntity( json.toString());
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
            if(statusCode == 200){
                JSONArray jsonObject =  new JSONArray(responseBody);
                //JSONArray jsonArray = new JSONArray(jsonObject.getString(0));
                //Log.d("JSON Array", jsonObject.toString());
                if(jsonObject.length() == 1){
                    Map<String, String> map = jsonToMap(jsonObject.toString());

                    SharedPreferences prefs = context.getSharedPreferences(
                            "org.app.compsat.compsatapplication", Context.MODE_PRIVATE);
                    prefs.edit().putString("username", username)
                            .apply();
                    prefs.edit().putString("password", password)
                            .apply();
                    prefs.edit().putString("schoolId", map.get("schoolId")).apply();
                    prefs.edit().putString("loggedIn", "true")
                            .apply();


                    //context.startActivity(intent);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "org.app.compsat.compsatapplication.close"));

                }
                else{
                    localBroadcastManager.sendBroadcast(new Intent(
                            "org.app.compsat.compsatapplication.wrongcredentials"));
                }
            } else if(statusCode == 404){
                localBroadcastManager.sendBroadcast(new Intent("org.app.compsat.compsatapplication.loginfailed"));
            }
            else{
                localBroadcastManager.sendBroadcast(new Intent("org.app.compsat.compsatapplication.server"));
            }
        }catch(ClientProtocolException e){
            Log.d("STATUS", "ERROR1");
        } catch (IOException e){
            localBroadcastManager.sendBroadcast(new Intent("org.app.compsat.compsatapplication.loginfailed"));
        } catch (JSONException e) {
            Log.d("STATUS", "ERROR3");
        }
        return builder.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        pDialog.dismiss();
    }

    protected Map<String, String> jsonToMap(String json){

        HashMap<String, String> map = new HashMap<>();
        json = json.substring(2, json.length()-2);

        String[] array = json.split(",");
        for(int i = 0; i < array.length; i++){
            String[] temp = array[i].split(":");
            map.put(temp[0].substring(1, temp[0].length() - 1), temp[1].substring(1, temp[1].length() - 1));
        }
        Log.d("Map", map.toString());
        return map;
    }


}
