package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class CalendarListAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;
    private final Typeface tf_futura_bold;
    private final Typeface tf_opensans_regular;
    private final Typeface tf_opensans_bold;
    private final Typeface tf_futura;
    private final Typeface tf_futura_condensed;
    protected CalendarListAdapter(Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;

        tf_futura_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_opensans_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        tf_opensans_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Bold.ttf");
        tf_futura = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT.ttf");
        tf_futura_condensed= Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Condensed.ttf");

    }


    @Override public int getCount() {
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


    String currentMonth = "";
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item, null);


        TextView separator = (TextView)convertView.findViewById(R.id.separator);
        TextView event_text =(TextView)convertView.findViewById(R.id.event_name);
        TextView date_text =(TextView)convertView.findViewById(R.id.date);
        View lineSeparator = convertView.findViewById(R.id.lineSeparator);

        separator.setTypeface(tf_futura_condensed);
        event_text.setTypeface(tf_futura_bold);
        date_text.setTypeface(tf_opensans_regular);

        JSONObject json_data = getItem(position);
        if(null!=json_data ){
            String name = null;
            String day = null;
            String month = null;
            String year = null;
            try {
                name = json_data.getString("event");
                day = json_data.getString("day");
                month = json_data.getString("month");
                year = json_data.getString("year");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*
             * Separator
             */
            boolean needSeparator = true;

            if(currentMonth.equalsIgnoreCase("") || !currentMonth.equalsIgnoreCase(month)){
                needSeparator = true;
            }
            else{

                needSeparator = false;
            }

            event_text.setText(name);
            date_text.setText(day +" "+ month +" "+ year);
            if(!needSeparator){
                separator.setVisibility(View.GONE);
                lineSeparator.setVisibility(View.GONE);
            }
            else{
                separator.setText(month.toUpperCase());
                currentMonth = month;
            }
        }
        return convertView;
    }
}