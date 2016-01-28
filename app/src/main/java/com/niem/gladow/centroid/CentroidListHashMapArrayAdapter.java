package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class CentroidListHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView members, time, inviteId, status, placeToMeet;
        ImageView image, statusImage;
        TextDrawable textDrawable;
    }

    public CentroidListHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<Long, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.centroid_list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.members = (TextView) convertView.findViewById(R.id.centroidListItemName);
            _viewHolder.time = (TextView) convertView.findViewById(R.id.centroidListItemDate);
            _viewHolder.status = (TextView) convertView.findViewById(R.id.centroidListItemStatus);
            _viewHolder.inviteId = (TextView) convertView.findViewById(R.id.centroidListItemInviteId);
            _viewHolder.placeToMeet = (TextView) convertView.findViewById(R.id.centroidListItemPlaceInfo);
            _viewHolder.image = (ImageView) convertView.findViewById(R.id.centroidListItemImage);
            _viewHolder.statusImage = (ImageView) convertView.findViewById(R.id.centroidListItemStatusImage);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<Long, Invite> _entryViewAtPosition = (Map.Entry<Long, Invite>) this.getItem(position);
        // get the corresponding Invite for this Element
        Invite _invite = InviteHandler.getInstance().getInviteByTime(_entryViewAtPosition.getKey());

        //build One-Letter Image with first letter of hostName
        String _hostName = _invite.getInviteNumberName().split(" ")[0];

        //getColor for Status of Invite
        _viewHolder.textDrawable = TextDrawable.builder()
                .buildRound(_hostName.substring(0, 1)
                        , getContext().getResources().getInteger(Util.getInstance()
                        .getColorForStatus(_invite.getStatus(), _invite.isDeprecated())));
        _viewHolder.image.setImageDrawable(_viewHolder.textDrawable);

        //construct members StyledTextString incl. status
        String _members = _invite.getAllMemberSurNames(Invite.WITHOUT, Invite.WITHOUT);
        if (_members.matches("")) {       //check if host is the only member
            _members = _hostName + "  ";
        } else {
            _members = _hostName + ", " + _members;
        }
        SpannableString _styledMembers = new SpannableString(_members);
        setStringStyles(_invite, _styledMembers, 0, _hostName.length() + 2);
        _viewHolder.members.setText(_styledMembers);


        //setTextViews/Imageviews
        Typeface _typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/Vera.ttf");
        _viewHolder.time.setText(String.valueOf(Util.getInstance().getShortDate(_invite)));
        _viewHolder.time.setTypeface(_typeFace);
        _viewHolder.inviteId.setText(String.valueOf(_invite.getStartTime()));
        _viewHolder.inviteId.setTypeface(_typeFace);
        _viewHolder.status.setText(_invite.getStatus().toString());
        _viewHolder.status.setTypeface(_typeFace);
        _viewHolder.statusImage.setImageResource(Util.getInstance()
                .getResIdForTransportationImage(_invite.getTransportationMode()));
        _viewHolder.placeToMeet.setTypeface(_typeFace);


        //check for placeToMeet and update TextView accordingly
        if (_invite.getChosenPlace() != null) {
            _viewHolder.placeToMeet.setText(_invite.getLocationName() + "\n@"
                    + _invite.getLocationAdress());
            _viewHolder.placeToMeet.setVisibility(View.VISIBLE);
        } else {
            _viewHolder.placeToMeet.setVisibility(View.INVISIBLE);
        }

        if (_invite.isDeprecated()) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    //method that sets the different layouts to member statusImage and shows them in listViewItem
    private void setStringStyles(Invite _invite, SpannableString _styledMembers, int _start, int _end) {
        InviteReply _memberReply;
        String _tmpName;
        TransportationMode _memberTransportation;

        //set style for hostName
        _styledMembers.setSpan(new StyleSpan(Typeface.BOLD), _start, _end, 0);

        //check for each member statusImage and apply StyleSpan
        for (Map.Entry<String, InviteStatus> _memberEntry : _invite.getAllMembers(Invite.WITHOUT, Invite.WITHOUT).entrySet()) {
            //update _start
            _start = _end;

            //check if number could be converted to name (for shifting _end correctly)
            _tmpName = _memberEntry.getValue().getRealName().split(" ")[0];
            if (_tmpName.matches("")) {
                _end += _memberEntry.getKey().length();

            } else {
                _end += _memberEntry.getValue().getRealName().split(" ")[0].length();
            }

            //check the statusImage of the member and apply style accordingly
            _memberTransportation = _memberEntry.getValue().getTransportationMode();
            _styledMembers.setSpan(new ForegroundColorSpan(ContextCompat
                    .getColor(getContext(), Util.getInstance().getColorForTranspMode(_memberTransportation))), _start, _end, 0);

            //shift space for ", " used to separate names
            _end += 2;

        }
    }
}