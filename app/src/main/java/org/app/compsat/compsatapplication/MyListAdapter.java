package org.app.compsat.compsatapplication;

import java.util.List;
import java.util.Map;
import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyListAdapter extends SimpleAdapter {
    Context c;

    public MyListAdapter(Context context, List<? extends Map<String, ?>> data,
                       int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        c = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(c);
            v = vi.inflate(R.layout.list_item, null);
        }
        return v;
    }
}