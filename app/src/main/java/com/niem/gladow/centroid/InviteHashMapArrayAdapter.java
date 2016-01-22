package com.niem.gladow.centroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class InviteHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView  host;
        TextView  time;
        TextView  inviteId;
        TextView  status;
        ImageView image;
        ImageView statusImage;
        TextDrawable textDrawable;
    }

    public InviteHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<Long, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;
        ColorGenerator _colorGenerator = ColorGenerator.MATERIAL;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invite_list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.host = (TextView) convertView.findViewById(R.id.inviteListItemName);
            _viewHolder.time = (TextView) convertView.findViewById(R.id.inviteListItemDate);
            _viewHolder.status = (TextView) convertView.findViewById(R.id.inviteListItemHints);
            _viewHolder.inviteId = (TextView) convertView.findViewById(R.id.inviteListItemInviteId);
            _viewHolder.image = (ImageView) convertView.findViewById(R.id.inviteListItemImage);
            _viewHolder.statusImage = (ImageView) convertView.findViewById(R.id.inviteListItemStatusImage);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<Long, Invite> entry = (Map.Entry<Long, Invite>) this.getItem(position);
        // get the corresponding Invite for this Element
        Invite _invite = InviteHandler.getInstance().getInviteByTime(entry.getKey());

        //build images
        String _name = _invite.getInviteNumberName();
        int _color = _colorGenerator.getColor(_name);
        _viewHolder.textDrawable = TextDrawable.builder()
                .buildRound(_name.substring(0,1), _color);
        _viewHolder.image.setImageDrawable(_viewHolder.textDrawable);

        //setTextViews/Imageviews
        _viewHolder.time.setText(""+Util.getInstance().getShortDate(_invite.getStartTime()));
        _viewHolder.host.setText(_name);
        _viewHolder.inviteId.setText(""+_invite.getStartTime());
        _viewHolder.status.setText(_invite.getStatus().toString());
        //TODO scale Image correctly
        _viewHolder.statusImage
                .setImageResource(Util.getInstance()
                .getResIdForTransportationImage(_invite.getTransportationMode()));
        return convertView;
    }
}