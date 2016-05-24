package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class NewsfeedListAdapter extends BaseAdapter implements ListAdapter{

    private final Activity activity;
    private final JSONArray jsonArray;
    private final Typeface tf_futura_bold;
    private final Typeface tf_opensans_regular;
    private final Typeface tf_futura;
    private final Typeface tf_futura_condensed;
    protected NewsfeedListAdapter(Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;

        tf_futura_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_opensans_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        tf_futura = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT.ttf");
        tf_futura_condensed= Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Condensed.ttf");

    }

    @Override
    public int getCount() {
        if(null==jsonArray)
            return 0;
        else
            return jsonArray.length();
    }

    @Override public JSONObject getItem(int position) {
        if(null==jsonArray) return null;
        else
            return jsonArray.optJSONObject(position);
    }

    @Override public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);

        return jsonObject.optLong("id");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.notif_list_item, null);


        TextView title_text =(TextView)convertView.findViewById(R.id.title);
        TextView message_text =(TextView)convertView.findViewById(R.id.message);
        TextView date_text =(TextView)convertView.findViewById(R.id.smallDate);


        title_text.setTypeface(tf_futura_condensed);
        message_text.setTypeface(tf_futura);

        JSONObject json_data = getItem(position);
        if(null!=json_data ){
            String title = null;
            String message = null;
            String day = null;
            String month = null;
            String year = null;

            try {

                title = json_data.getString("event_name");
                message = json_data.getString("description");
                day = json_data.getString("event_date").split("-")[2];
                month = json_data.getString("event_date").split("-")[1];
                year = json_data.getString("event_date").split("-")[0];

            } catch (JSONException e) {
                e.printStackTrace();
            }

            title_text.setText(title);
            message_text.setText(message);
            date_text.setText(month+"/"+day+"/"+year);


        }

        return convertView;
    }
}