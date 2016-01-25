package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class InviteFriendsHashMapArrayAdapter extends ArrayAdapter {
    private ArrayList<Boolean> checkList;

    private static class ViewHolder {
        TextView number;
        TextView name;
        ImageView image;
        ImageView statusImage;
        boolean checked;
        TextDrawable textDrawable;
    }

    public InviteFriendsHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;

        //TODO colorgenerator cleanup
        int[] _colors = getContext().getResources().getIntArray(R.array.colorArray);
        List<Integer> intList = new ArrayList<>();
        for (int index = 0; index < _colors.length; index++)
        {
            intList.add(_colors[index]);
        }
        ColorGenerator _colorGenerator = ColorGenerator.create(intList);
        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invite_friends_list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.image = (ImageView) convertView.findViewById(R.id.friendListView_image);
            _viewHolder.number = (TextView) convertView.findViewById(R.id.friend_number);
            _viewHolder.name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();

        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) this.getItem(position);

        //build image
        String _name = entry.getValue().toString();
        int _color = _colorGenerator.getColor(_name);
        _viewHolder.textDrawable = TextDrawable.builder()
                .buildRound(_name.substring(0,1), _color);
        _viewHolder.image.setImageDrawable(_viewHolder.textDrawable);

        _viewHolder.number.setText(entry.getKey());
        _viewHolder.name.setText(_name);
        _viewHolder.checked = checkList.get(position);



        if(_viewHolder.checked){
            convertView.setBackgroundResource(R.color.unanswered_1);
        }else{
            //TODO COLOR
            convertView.setBackgroundColor(Color.WHITE);
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