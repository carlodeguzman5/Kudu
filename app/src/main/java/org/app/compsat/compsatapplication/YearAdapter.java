package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by carlo on 4/24/2016.
 */
public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> {

    private JSONArray years = new JSONArray();
    private Activity context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        public ViewHolder(View v){
            super(v);
            mTextView = (TextView)v.findViewById(R.id.yearTextView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlternateMonthsActivity.class);
                    intent.putExtra("year",mTextView.getText().toString().trim());
                    context.startActivity(intent);
                }
            });
        }
    }

    public YearAdapter(Activity context, JSONArray years){
        this.context = context;
        this.years = years;
    }

    @Override
    public YearAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.year_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(YearAdapter.ViewHolder holder, int position) {
        try {
            holder.mTextView.setText(years.getJSONObject(position).getString("year"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(years != null){
            return years.length();
        }
        return 0;
    }

    public void updateData(JSONArray years){
        this.years = years;
        notifyDataSetChanged();
    }
}
