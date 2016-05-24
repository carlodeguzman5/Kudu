package org.app.compsat.compsatapplication;

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

/**
 * Created by carlo on 4/20/2016.
 */
public class RestClient {
    private JSONObject paramList;
    private HttpPost httpPost;
    private JSONArray output;
    private int statusCode;
    private StatusLine statusLine;

    public RestClient(String url){
        paramList = new JSONObject();
        httpPost = new HttpPost(url);
    }

    public void addParam(String key, String value){
        try {
            paramList.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        try {
            HttpClient client = new DefaultHttpClient();
            StringEntity se = new StringEntity( paramList.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            HttpResponse response = client.execute(httpPost);
            statusLine = response.getStatusLine();
            output =  new JSONArray(EntityUtils.toString(response.getEntity()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getStatusCode(){
        return statusLine.getStatusCode();
    }

    public JSONArray getResponse(){
        return output;
    }

}
