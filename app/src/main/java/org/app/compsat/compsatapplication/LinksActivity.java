package org.app.compsat.compsatapplication;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LinksActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<HashMap<String, String>> eventsList;
    private JSONParser jParser = new JSONParser();
    private Context context = this;
    private SwipeRefreshLayout swipeRefreshLayout;

    // url to get all calandar_events
    private static final String TAG_STRING = "title";
    private static final String TAG_LINK = "url";
    private ProgressDialog pDialog;

    // events JSONArray
    private JSONArray links = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);


        new LoadAllEvents().execute();
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

    @Override
    public void onRefresh() {
        new LoadAllEvents().execute();
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LinksActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading links. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
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
                                Intent intent = new Intent(context, WebsiteActivity.class);
                                intent.putExtra("url", item.getString("url"));

                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });

            String url = "http://app.compsat.org/index.php/Link_controller/links/format/json";

            // Hashmap for ListView
            eventsList = new ArrayList<HashMap<String, String>>();


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONArray json = null;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();


            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                // getting JSON string from URL
                json = jParser.makeHttpRequest(url, "POST", params);
                writeToFile("links.txt", json.toString());
            }
            else{
                try {
                    json = new JSONArray(readFromFile("links.txt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            try {
                // Getting Array of Contacts
                links = json;

                // looping through All Contacts
                for (int i = 0; i < links.length(); i++) {
                    JSONObject c = links.getJSONObject(i);

                    // Storing each json item in variable
                    String string = c.getString(TAG_STRING);
                    String link = c.getString(TAG_LINK);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_STRING, string);
                    map.put(TAG_LINK, link);


                    // adding HashList to ArrayList
                    eventsList.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //pDialog.dismiss();
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
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    LinksListAdapter adapter = new LinksListAdapter(LinksActivity.this, links);

                    // updating listview
                    //ListView lv = (ListView)findViewById(R.layout.list);

                    setListAdapter(adapter);
                    pDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
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

    private void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

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
            Log.e("Links", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Links", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
