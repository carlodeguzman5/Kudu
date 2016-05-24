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
import java.util.Scanner;

public class OrganizationListActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{

    //ArrayList<HashMap<String, String>> notifsList;
    SwipeRefreshLayout swipeRefreshLayout;

    OrganizationListAdapter adapter;
/*
    private static final String TAG_HOST_ID = "event_id";
    private static final String TAG_HOST_NAME = "event_name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_SCHOOL = "school_id";*/

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

        adapter = new OrganizationListAdapter(OrganizationListActivity.this, notifications);

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


    public void onRefresh() {
        new LoadAllEvents().execute();
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OrganizationListActivity.this);
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
                                Intent intent = new Intent(context, OrganizationPageActivity.class);
                                intent.putExtra("hostId", item.getString("host_id"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });

            InputStream is = null;
            try {
                is = context.getAssets().open("db/db.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(is);
            String prefix = scanner.nextLine();
            scanner.close();

            String url = prefix + "index.php/Host_controller/readHosts/format/json/";

            // Hashmap for ListView
           /* notifsList = new ArrayList<HashMap<String, String>>();*/


            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("tag", args[0]));

            JSONArray json = new JSONArray();

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

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

           /* try {*/
                // Getting Array of Contacts
                notifications = json;

                // looping through All Contacts
                /*for (int i = 0; i < notifications.length(); i++) {
                    JSONObject c = notifications.getJSONObject(i);

                    // Storing each json item in variable
                    String title = c.getString(TAG_HOST_NAME);
                    String message = c.getString(TAG_DESCRIPTION);
                    String school  = (String) c.get(TAG_SCHOOL);
                    String hostId = (String) c.get(TAG_HOST_ID);


                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_HOST_NAME, title);
                    map.put(TAG_SCHOOL, school);
                    map.put(TAG_DESCRIPTION, message);
                    map.put(TAG_HOST_ID, hostId);

                    // adding HashList to ArrayList
                    notifsList.add(map);
                }*//*
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

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
                    setListAdapter(adapter);
                    adapter.updateData(notifications);
                    adapter.notifyDataSetChanged();
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
