package com.niem.gladow.centroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class HashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView tV1;
        TextView tV2;
    }

    public HashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tV1 = (TextView) convertView.findViewById(R.id.friend_number);
            viewHolder.tV2 = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) this.getItem(position);

        viewHolder.tV1.setText(entry.getKey());
        viewHolder.tV2.setText(entry.getValue().toString());
        return convertView;
    }
}