package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.compsat.compsatapplication.StickyHeaders.StickyRecyclerHeadersAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carlo on 11/1/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter {
    private JSONArray months;
    private JSONArray events;
    private JSONArray years;
    private Activity context;


    private final Typeface tf_futura_bold;
    private final Typeface tf_opensans_regular;
    private final Typeface tf_opensans_bold;
    private final Typeface tf_opensans_light;
    private final Typeface tf_futura;
    private final Typeface tf_futura_condensed;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        private LinearLayout mLinearLayout;
        private ImageView mImageView;
        public ViewHolder(View v){
            super(v);
            mTextView = (TextView)v.findViewById(R.id.month_text);
            mLinearLayout = (LinearLayout)v.findViewById(R.id.events_list);
            mImageView = (ImageView) v.findViewById(R.id.calendarImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyRecyclerAdapter(Activity context, JSONArray months, JSONArray years, JSONArray events) {
        this.months = months;
        this.events = events;
        this.years = years;
        this.context = context;

        tf_futura_bold = Typeface.createFromAsset(context.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_futura = Typeface.createFromAsset(context.getAssets(), "fonts/FuturaLT.ttf");
        tf_futura_condensed= Typeface.createFromAsset(context.getAssets(), "fonts/FuturaLT-Condensed.ttf");
        tf_opensans_bold= Typeface.createFromAsset(context.getAssets(), "fonts/FuturaLT-Bold.ttf");
        tf_opensans_regular= Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        tf_opensans_light= Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");

    }

    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        try {

            JSONObject tempArray = new JSONObject(months.getString(position));
            holder.mTextView.setText(tempArray.get("month").toString().toUpperCase());
            holder.mTextView.setTypeface(tf_opensans_regular);
            holder.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            switch (tempArray.get("month").toString()){
                case "August": holder.mImageView.setImageResource(R.drawable.august);
                    break;
                case "September": holder.mImageView.setImageResource(R.drawable.september);
                    break;
                case "October": holder.mImageView.setImageResource(R.drawable.october);
                    break;
                case "November": holder.mImageView.setImageResource(R.drawable.november);
                    break;
                case "December": holder.mImageView.setImageResource(R.drawable.december);
                    break;
                case "January": holder.mImageView.setImageResource(R.drawable.january);
                    break;
                case "February": holder.mImageView.setImageResource(R.drawable.february);
                    break;
                case "March": holder.mImageView.setImageResource(R.drawable.march);
                    break;
                case "April": holder.mImageView.setImageResource(R.drawable.april);
                    break;
                case "May": holder.mImageView.setImageResource(R.drawable.may);
                    break;
                case "June": holder.mImageView.setImageResource(R.drawable.june);
                    break;
                case "July": holder.mImageView.setImageResource(R.drawable.june);
                    break;
            }

            holder.mLinearLayout.removeAllViewsInLayout();

            for(int j = 0 ; j < events.length(); j++){
                JSONObject jsonObjectPerEvent = events.getJSONObject(j);

                if(jsonObjectPerEvent.getString("month").equals(tempArray.get("month").toString())
                        && jsonObjectPerEvent.get("year").toString().equals(tempArray.get("year").toString())){

                    View view = context.getLayoutInflater().inflate(R.layout.event_list_item, null);
                    TextView event_text = (TextView) view.findViewById(R.id.event_name);
                    TextView day_text = (TextView) view.findViewById(R.id.day_text);

                    event_text.setTypeface(tf_opensans_light);
                    day_text.setTypeface(tf_opensans_bold);

                    event_text.setText(jsonObjectPerEvent.get("event_name").toString());
                    day_text.setText(jsonObjectPerEvent.get("event_date").toString().split("-")[2]);
                    holder.mLinearLayout.addView(view);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getHeaderId(int position) {
        try {
            return Long.parseLong(getMonthItem(position).getString("year"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        try {
            textView.setText(getMonthItem(position).getString("year"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(months == null){
            return 0;
        }
        return months.length();
    }

    public void updateData(JSONArray events, JSONArray months){
        this.events = null;
        this.months = null;
        this.events = events;
        this.months = months;

        notifyDataSetChanged();
    }

    public JSONObject getMonthItem(int position){
        try {
            return months.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}