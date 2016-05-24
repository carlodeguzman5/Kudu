package org.app.compsat.compsatapplication;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsfeedActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{

    ArrayList<HashMap<String, String>> notifsList;
    SwipeRefreshLayout swipeRefreshLayout;

    JSONParser jParser = new JSONParser();

    private static final String TAG_EVENT_ID = "event_id";
    private static final String TAG_EVENT = "event_name";
    private static final String TAG_DATE = "event_date";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_HOST = "host_id";

    private Context context = this;
    private ProgressDialog pDialog;

    // events JSONArray
    JSONArray notifications = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        ImageButton search = (ImageButton) findViewById(R.id.imageButton4);

        Resources resources = getResources();
        search.setImageDrawable(resources.getDrawable(R.drawable.searchorange));

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout2);
        //swipeRefreshLayout.setOnRefreshListener(this);
        //new LoadAllEvents().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onRefresh() {
        new LoadAllEvents().execute();
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewsfeedActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading Newsfeed. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All news from url
         */
        protected String doInBackground(String... args) {
            final ListView listView = getListView();
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            JSONObject item = (JSONObject) parent.getItemAtPosition(position);
                            try {
                                Intent intent = new Intent(context, EventPage.class);
                                intent.putExtra("eventId", item.getString("event_id"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });

            String url = "http://192.168.1.3/PEDC/index.php/Event_controller/tagEvents/format/json";

            // Hashmap for ListView
            notifsList = new ArrayList<HashMap<String, String>>();


            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("tag", args[0]));

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();



            JSONArray json = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("tag", args[0]);
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
                }else{
                    json = new JSONArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }



            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                // getting JSON string from URL
                //json = jParser.makeHttpRequest(url, "POST", params);
                //writeToFile(json.toString());
            }
            else{
                try {
                    json = new JSONArray(readFromFile());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                // Getting Array of Contacts
                notifications = json;

                // looping through All Contacts
                for (int i = 0; i < notifications.length(); i++) {
                    JSONObject c = notifications.getJSONObject(i);

                    // Storing each json item in variable
                    String title = c.getString(TAG_EVENT);
                    String message = c.getString(TAG_DESCRIPTION);
                    String date = (String) c.get(TAG_DATE);
                    String eventId = (String) c.get(TAG_EVENT_ID);


                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_EVENT_ID, eventId);
                    map.put(TAG_EVENT, title);
                    map.put(TAG_DESCRIPTION, message);
                    map.put(TAG_DATE, date);

                    // adding HashList to ArrayList
                    notifsList.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        // updating UI from Background Thread

        /**
         * After completing background task Dismiss the progress dialog
         * **/

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    NewsfeedListAdapter adapter = new NewsfeedListAdapter(NewsfeedActivity.this, notifications);
                    setListAdapter(adapter);
                    pDialog.dismiss();
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("newsfeed.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("newsfeed.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("News Feed", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("News Feed", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void search(View view){
        EditText searchBox = (EditText)findViewById(R.id.editText);
        new LoadAllEvents().execute(searchBox.getText().toString());
    }

}
