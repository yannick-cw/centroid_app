package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.niem.gladow.centroid.Enums.InviteReply;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class InviteHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView number;
        TextView name;
    }

    public InviteHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<Long, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invite_list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.number = (TextView) convertView.findViewById(R.id.invite_id);
            _viewHolder.name = (TextView) convertView.findViewById(R.id.invite_friend);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<Long, Invite> entry = (Map.Entry<Long, Invite>) this.getItem(position);
        // get the corresponding Invite for this Element
        Invite _invite = InviteHandler.getInstance().getInviteByTime(entry.getKey());

        //TODO Display Date and StartTime(=TimeStamp) as ID at the same time
        _viewHolder.number.setText(entry.getKey().toString());
        _viewHolder.name.setText(entry.getValue().getStatus().toString());

        if(_invite.getStatus() != InviteReply.UNANSWERED){
            convertView.setBackgroundColor(Color.GREEN);
        }else{
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }
}