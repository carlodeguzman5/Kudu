package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.app.compsat.compsatapplication.StickyHeaders.DividerDecoration;
import org.json.JSONArray;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by carlo on 4/25/2016.
 */
public class AlternateMonthsActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Activity context = this;
    private ArrayList<HashMap<String, String>> eventsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressDialog pDialog;
    private JSONArray events;
    private JSONArray monthsJson = new JSONArray();
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ff5e49"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);

        year = getIntent().getExtras().getString("year");
        setTitle(year + " Events");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MyRecyclerAdapter(context ,monthsJson, null, events);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerDecoration(context));

        //headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        //mRecyclerView.addItemDecoration(headersDecor);
        // Add decoration for dividers between list items
        //mRecyclerView.addItemDecoration(new DividerDecoration(context));

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
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        new LoadAllEvents().execute();
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AlternateMonthsActivity.this);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
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

            String monthsUrl = prefix + "index.php/Event_controller/subscriptionsMonthsPerYear/format/json/";
            String eventsUrl = prefix + "index.php/Event_controller/subscriptionsPerYear/format/json/";

            SharedPreferences prefs = getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);

            // Hashmap for ListView
            eventsList = new ArrayList<>();

            //Rest Client
            RestClient restClientEvents = new RestClient(eventsUrl);
            RestClient restClientMonths = new RestClient(monthsUrl);

            restClientMonths.addParam("userId", prefs.getString("username", ""));
            restClientMonths.addParam("year", year);

            restClientEvents.addParam("userId", prefs.getString("username", ""));
            restClientEvents.addParam("year", year);

            restClientEvents.execute();
            restClientMonths.execute();

            events = new JSONArray();
            if(restClientEvents.getStatusCode() == 200) {
                events = restClientEvents.getResponse();
                monthsJson = restClientMonths.getResponse();
                Log.d("MONTHS", monthsJson.toString());
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
                    Log.d("monthsLoadedOnStart", monthsJson.toString());
                    mAdapter.updateData(events, monthsJson);
                    pDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


}
