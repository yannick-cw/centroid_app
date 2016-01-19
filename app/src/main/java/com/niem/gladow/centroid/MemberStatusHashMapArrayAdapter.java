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
public class MemberStatusHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView member;
        InviteReply inviteReply;
    }

    public MemberStatusHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_list_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.member = (TextView) convertView.findViewById(R.id.textIdMember);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<String, InviteReply> entry = (Map.Entry<String, InviteReply>) this.getItem(position);

        _viewHolder.member.setText(entry.getKey());
        _viewHolder.inviteReply = entry.getValue();

        switch (_viewHolder.inviteReply){
            case DECLINED:
                convertView.setBackgroundColor(Color.RED);
                break;
            case ACCEPTED:
                convertView.setBackgroundColor(Color.GREEN);
                break;
            default:
                convertView.setBackgroundColor(Color.LTGRAY);
                break;
        }

        return convertView;
    }


//    // setter, getter and updater for state of the views
//    public void setCheckList(int size){
//        this.checkList = new ArrayList<>(Arrays.asList(new Boolean[size]));
//        Collections.fill(this.checkList, Boolean.FALSE); // initiate list with False
//        notifyDataSetChanged();
//    }

//    public void toggleCheckList(int position) {
//        this.checkList.set(position, !this.checkList.get(position));
//    }

//    public boolean getCheckStatus(int position){
//        return this.checkList.get(position);
//    }

}