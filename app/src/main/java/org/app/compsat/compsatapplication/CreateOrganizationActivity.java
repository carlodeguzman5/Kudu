
package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CreateOrganizationActivity extends Activity {
    private String url = "http://192.168.1.3/PEDC/index.php/Host_controller/createHosts/format/json/";
    private EditText descriptionText, orgNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization);

        descriptionText = (EditText) findViewById(R.id.desc);
        orgNameText = (EditText) findViewById(R.id.orgName);
    }


    class ExecuteCreateOrg extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONArray json = new JSONArray();

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("userId",params[0]);//usernameReg.getText().toString());
                jsonParam.put("name",params[1]);//passwordReg.getText().toString());
                jsonParam.put("description",params[2]);//passwordReg.getText().toString());
                jsonParam.put("school_id", params[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                StringEntity se = new StringEntity( jsonParam.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);

            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = client.execute(httpPost);
                String responseBody = EntityUtils.toString(response.getEntity());
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.d("Result", statusCode + "");
                if (statusCode == 200) {
                    JSONArray jsonObject = new JSONArray(responseBody);
                    json = jsonObject;

                } else {
                    json = new JSONArray();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            finish();
        }
    }

    public void createOrg(View view){
        SharedPreferences prefs = getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);
        new ExecuteCreateOrg().execute(prefs.getString("username",""),orgNameText.getText().toString(), descriptionText.getText().toString(), prefs.getString("schoolId",""));
    }
}
