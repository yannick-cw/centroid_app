package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class HashMapArrayAdapter extends ArrayAdapter {
    private ArrayList<Boolean> checkList;

    private static class ViewHolder {
        TextView number;
        TextView name;
        boolean checked;
    }

    public HashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.number = (TextView) convertView.findViewById(R.id.friend_number);
            _viewHolder.name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) this.getItem(position);

        _viewHolder.number.setText(entry.getKey());
        _viewHolder.name.setText(entry.getValue().toString());
        _viewHolder.checked = checkList.get(position);

        if(_viewHolder.checked){
            convertView.setBackgroundResource(R.color.accepted);
        }else{
            convertView.setBackgroundResource(R.color.unanswered);
        }

        return convertView;
    }


    // setter, getter and updater for state of the views
    public void setCheckList(int size){
        this.checkList = new ArrayList<>(Arrays.asList(new Boolean[size]));
        Collections.fill(this.checkList, Boolean.FALSE); // initiate list with False
        notifyDataSetChanged();
    }

    public void toggleCheckList(int position) {
        this.checkList.set(position, !this.checkList.get(position));
    }

    public boolean getCheckStatus(int position){
        return this.checkList.get(position);
    }

}