package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class AlternateCalendarActivity extends Activity {
    private Activity context;
    private JSONArray years;
    private RecyclerView mRecyclerView;
    private YearAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_calendar);
        context = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.alternate_calendar_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new YearAdapter(this, years);
        mRecyclerView.setAdapter(mAdapter);



        new LoadYears().execute();
    }

    class LoadYears extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AlternateCalendarActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading years. Please wait...");
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

            String yearUrl = prefix + "index.php/Event_controller/subscriptionsYears/format/json/";

            SharedPreferences prefs = getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);

            //Rest Client
            RestClient restClientYears = new RestClient(yearUrl);
            restClientYears.addParam("userId", prefs.getString("username", ""));

            restClientYears.execute();

            years = new JSONArray();
            if(restClientYears.getStatusCode() == 200) {
                years = restClientYears.getResponse();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    pDialog.dismiss();
                    mAdapter.updateData(years);

                }
            });

        }
    }
}
