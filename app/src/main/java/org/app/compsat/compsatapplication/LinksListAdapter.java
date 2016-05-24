package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class LinksListAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;
    private final Typeface tf_futura_bold;
    private final Typeface tf_opensans_regular;
    private final Typeface tf_futura;
    private final Typeface tf_futura_condensed;
    protected LinksListAdapter(Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;

        tf_futura_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_opensans_regular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.link_list_item, null);


        TextView title_text =(TextView)convertView.findViewById(R.id.title);
        //TextView link_text =(TextView)convertView.findViewById(R.id.message);

        title_text.setTypeface(tf_futura_condensed);

        JSONObject json_data = getItem(position);
        if(null!=json_data ){
            String title = null;
            try {
                title = json_data.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            title_text.setText(title);
        }

        return convertView;
    }
}
