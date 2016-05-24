package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

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

public class SignUpActivity extends Activity {

    static EditText usernameReg, passwordReg, emailReg;
    private String array_spinner[];
    Spinner s;

    private String url = "http://192.168.1.3/PEDC/index.php/User_controller/createUser/format/json/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        array_spinner=new String[4];
        array_spinner[0]="ADMU";
        array_spinner[1]="DLSU";
        array_spinner[2]="UPD";
        array_spinner[3]="UST";
        s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);

        usernameReg = (EditText) findViewById(R.id.usernameReg);
        passwordReg = (EditText) findViewById(R.id.passwordReg);
        emailReg = (EditText) findViewById(R.id.emailReg);
    }

    public void reg(View view) throws JSONException {

        String school = "";
        switch((int) s.getSelectedItemId()){
            case 0:
                school = "admu";
                break;
            case 1:
                school = "dlsu";
                break;
            case 2:
                school = "upd";
                break;
            case 3:
                school = "ust";
                break;
        }

        new RegisterTask().execute(usernameReg.getText().toString(), passwordReg.getText().toString(), emailReg.getText().toString(), school);

    }

    class RegisterTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            JSONArray json = new JSONArray();

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("userId",params[0]);//usernameReg.getText().toString());
                jsonParam.put("password",params[1]);//passwordReg.getText().toString());
                jsonParam.put("email",params[2]);//emailReg.getText().toString());
                jsonParam.put("schoolId",params[3]);
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
}
