package org.app.compsat.compsatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

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
import org.app.compsat.compsatapplication.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SettingsActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void createOrg(View view){
        Intent intent = new Intent(this, CreateOrganizationActivity.class);
        startActivity(intent);
    }

    public void logout(View view){
        Intent loginScreen=new Intent(this, LoginActivity.class);
        SharedPreferences prefs;
        prefs = this.getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);
        prefs.edit().clear().apply();
        loginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginScreen);
        this.finish();
    }




}
