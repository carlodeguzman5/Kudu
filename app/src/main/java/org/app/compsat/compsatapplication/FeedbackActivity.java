package org.app.compsat.compsatapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class FeedbackActivity extends Activity {

    private Context context = this;
    private EditText feedbackText, subjectText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FuturaLT.ttf");
        feedbackText = (EditText) findViewById(R.id.feedbackText);
        feedbackText.setTypeface(tf);

        subjectText = (EditText) findViewById(R.id.subjectText);
        subjectText.setTypeface(tf);

        button = (Button) findViewById(R.id.feedbackBtn);
        button.setTypeface(tf);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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

    public void submitFeedback(View view) throws JSONException {
        new ExecutePost("http://app.compsat.org/index.php/Feedback_controller/feedback/format/json", subjectText.getText().toString(), feedbackText.getText().toString() ).execute();
    }
/*
    class CompSAtAppClient{
        public void sendFeedback(RequestParams params) throws JSONException {
            CompSAtAppRestClient.post("Feedback_controller/feedback/format/json", params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.d("START", "WOOH");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("SUCCESS", "YEHEY");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // Pull out the first event on the public timeline
                    Log.d("SUCCESS", "YEHEY");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable){
                    Log.e("Exception", throwable.getStackTrace().toString());
                    Log.d("TEST", "ERROR");
                }

            });
        }
    }*/

    public class ExecutePost extends AsyncTask<String, String, String> {

        String url, feedback, subject;
        public ExecutePost(String url, String subject, String feedback){
            this.url = url;
            this.feedback = feedback;
            this.subject = subject;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject json = new JSONObject();
            try {
                SharedPreferences prefs = getSharedPreferences(
                        "org.app.compsat.compsatapplication", Context.MODE_PRIVATE);

                json.put("subject", subject);
                json.put("feedback", feedback);
                json.put("name", prefs.getString("name", ""));
                org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity( json.toString());
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

                } else if(statusCode == 500) {
                    Toast.makeText(context, "Internal Error", Toast.LENGTH_LONG).show();
                } else{
                    Log.d("STATUS", statusCode+"");
                }
            }catch(ClientProtocolException e){
                Log.d("STATUS", "ERROR");
            } catch (IOException e){
                Log.d("STATUS", "ERROR");
            } catch (JSONException e) {
                Log.d("STATUS", "ERROR");
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            /*
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Your Feedback has been sent!")
                    .setTitle("Success").setNeutralButton("Dismiss", null);

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
            */

            Toast.makeText(context, "Feedback has been submitted!", Toast.LENGTH_LONG).show();

            feedbackText.setText("");
            subjectText.setText("");

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
/*
    static class CompSAtAppRestClient {
        private static final String BASE_URL = "http://app.compsat.org/index.php/";

        private static AsyncHttpClient client = new AsyncHttpClient();

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            StringEntity entity = null;
            try {
                entity = new StringEntity(params.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        }

        private static String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
    }*/
}
