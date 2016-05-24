package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class CalendarCardListAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;
    private final Typeface tf_futura_bold;
    private final Typeface tf_opensans_regular;
    private final Typeface tf_opensans_bold;
    private final Typeface tf_futura;
    private final Typeface tf_futura_condensed;

    public CalendarCardListAdapter(Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;

        Log.d("CHECK", jsonArray.toString());

        tf_futura_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_opensans_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        tf_opensans_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Bold.ttf");
        tf_futura = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT.ttf");
        tf_futura_condensed= Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Condensed.ttf");

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.event_list_item, null);

        TextView event_text =(TextView)convertView.findViewById(R.id.event_name);
        TextView date_text =(TextView)convertView.findViewById(R.id.date);
        TextView day_text =(TextView)convertView.findViewById(R.id.day_text);

        event_text.setTypeface(tf_futura_bold);
        date_text.setTypeface(tf_opensans_regular);
        day_text.setTypeface(tf_opensans_regular);

        JSONObject item = null;
        try {
            item = (JSONObject)jsonArray.get(position+1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            event_text.setText(item.get("event").toString());
            date_text.setText(item.get("event").toString());
            day_text.setText(item.get("day").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return jsonArray.length();

    }

    @Override
    public boolean isEmpty() {
        if (getCount() == 0)
            return true;
        else
            return false;
    }
}