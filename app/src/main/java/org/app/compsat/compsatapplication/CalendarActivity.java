package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.app.compsat.compsatapplication.StickyHeaders.DividerDecoration;
import org.app.compsat.compsatapplication.StickyHeaders.StickyRecyclerHeadersDecoration;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;


public class CalendarActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private Activity context = this;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;

    private JSONArray eventsJson;
    private JSONArray monthsJson = new JSONArray();
    private JSONArray yearsJson = new JSONArray();
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private StickyRecyclerHeadersDecoration headersDecor;
    private EndlessRecyclerViewScrollListener scrollListener;

    private final int LOAD_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Set layout manager
        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        mLayoutManager = new LinearLayoutManager(this, orientation, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MyRecyclerAdapter(context ,monthsJson, yearsJson, eventsJson);
        mRecyclerView.setAdapter(mAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new LoadAllEvents().execute(String.valueOf(totalItemsCount+LOAD_COUNT));
            }
        };

        mRecyclerView.setOnScrollListener(scrollListener);

        headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);
        // Add decoration for dividers between list items
        mRecyclerView.addItemDecoration(new DividerDecoration(context));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        new LoadAllEvents().execute(String.valueOf(LOAD_COUNT));
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
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
        swipeRefreshLayout.setRefreshing(false);
        scrollListener.resetCounter();
        new LoadAllEvents().execute(String.valueOf(LOAD_COUNT));
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CalendarActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            InputStream is = null;
            try {
                is = context.getAssets().open("db/db.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(is);
            String prefix = scanner.nextLine();
            scanner.close();

            String url = prefix + "index.php/Event_controller/subscriptionsLimitedByMonthsCount/format/json/";
            String monthUrl = prefix + "index.php/Event_controller/subscriptionsMonthsLimited/format/json/";

            SharedPreferences prefs = getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);

            //Rest Client
            RestClient restClientEvents = new RestClient(url);
            RestClient restClientMonths = new RestClient(monthUrl);

            restClientEvents.addParam("userId", prefs.getString("username", ""));
            restClientEvents.addParam("count", args[0]);

            restClientMonths.addParam("userId", prefs.getString("username", ""));
            restClientMonths.addParam("count", args[0]);

            restClientEvents.execute();
            restClientMonths.execute();


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            eventsJson = new JSONArray();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                if(restClientEvents.getStatusCode() == 200) {
                    eventsJson = restClientEvents.getResponse();
                    monthsJson = restClientMonths.getResponse();
                    //Log.d("MONTHS", monthsJson.toString());
                    writeToFile("events.dat", eventsJson.toString());
                    writeToFile("months.dat", monthsJson.toString());
                }
            }
            else{
                try {
                    eventsJson = new JSONArray(readFromFile("events.dat"));
                    monthsJson = new JSONArray(readFromFile("months.dat"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                mAdapter.updateData(eventsJson, monthsJson);
                if(pDialog.isShowing()) pDialog.dismiss();
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
            Log.e("Calendar", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Calendar", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
