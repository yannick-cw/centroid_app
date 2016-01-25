package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class CentroidListHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView  members, time, inviteId, status, placeToMeet;
        ImageView image, statusImage;
        TextDrawable textDrawable;
    }

    public CentroidListHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<Long, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder _viewHolder;
        ColorGenerator _colorGenerator = ColorGenerator.MATERIAL;

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
               In this case by inflating an xml layout */
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.centroid_list_view_item, parent, false);
            _viewHolder = new ViewHolder();
            _viewHolder.members  = (TextView) convertView.findViewById(R.id.centroidListItemName);
            _viewHolder.time     = (TextView) convertView.findViewById(R.id.centroidListItemDate);
            _viewHolder.status   = (TextView) convertView.findViewById(R.id.centroidListItemStatus);
            _viewHolder.inviteId = (TextView) convertView.findViewById(R.id.centroidListItemInviteId);
            _viewHolder.placeToMeet = (TextView) convertView.findViewById(R.id.centroidListItemPlaceInfo);
            _viewHolder.image       = (ImageView) convertView.findViewById(R.id.centroidListItemImage);
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
        int _color = _colorGenerator.getColor(_hostName);
        _viewHolder.textDrawable = TextDrawable.builder()
                .buildRound(_hostName.substring(0,1), _color);
        _viewHolder.image.setImageDrawable(_viewHolder.textDrawable);

        //construct members StyledTextString incl. status
        String _members = _invite.getAllMemberSurNames(false, false);
        if(_members.matches("")){       //check if host is the only member
            _members = _hostName+"  ";
        }else{
            _members = _hostName+", "+_members;
        }
        SpannableString _styledMembers = new SpannableString(_members);
        setStringStyles(_invite, _styledMembers, 0, _hostName.length() + 2);
        _viewHolder.members.setText(_styledMembers);


        //setTextViews/Imageviews
        _viewHolder.time.setText(String.valueOf(Util.getInstance().getShortDate(_invite.getStartTime())));
        _viewHolder.inviteId.setText(String.valueOf(_invite.getStartTime()));
        _viewHolder.status.setText(_invite.getStatus().toString());
        _viewHolder.statusImage.setImageResource(Util.getInstance()
                .getResIdForTransportationImage(_invite.getTransportationMode()));


        //check for placeToMeet and update TextView accordingly
        if(_invite.getChosenPlace() != null){
            _viewHolder.placeToMeet.setText(_invite.getPlaceToMeet().getName().toString().split(",")[0] +"\n@"
                                          + _invite.getPlaceToMeet().getAddress().toString().split(",")[0]);
            _viewHolder.placeToMeet.setVisibility(View.VISIBLE);
        }else{
            _viewHolder.placeToMeet.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //TODO Layout for a lot of members
    //method that sets the different layouts to member statusImage and shows them in listViewItem
    private void setStringStyles(Invite _invite, SpannableString _styledMembers, int _start, int _end) {
        InviteReply _memberReply;
        String _tmpName;

        //set style for hostName
        _styledMembers.setSpan(new StyleSpan(Typeface.BOLD), _start, _end, 0);

        //check for each member statusImage and apply StyleSpan
        for (Map.Entry<String, InviteStatus> _memberEntry : _invite.getAllMembers(false, false).entrySet())
        {
            //update _start
            _start = _end;

            //check if number could be converted to name (for shifting _end correctly)
            _tmpName = _memberEntry.getValue().getRealName().split(" ")[0];
            if(_tmpName.matches("")){
                _end += _memberEntry.getKey().length();

            }else{
                _end += _memberEntry.getValue().getRealName().split(" ")[0].length();
            }

            //check the statusImage of the member and apply style accordingly
            _memberReply = _memberEntry.getValue().getInviteReply();
            switch(_memberReply){
                case ACCEPTED:
                    _styledMembers.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), _start, _end, 0);
                    break;
                case DECLINED:
                    _styledMembers.setSpan(new StyleSpan(Typeface.ITALIC),_start, _end, 0);
                    _styledMembers.setSpan(new ForegroundColorSpan(Color.RED), _start, _end, 0);
                    break;
                case UNANSWERED:
                    _styledMembers.setSpan(new StyleSpan(Typeface.ITALIC),_start, _end, 0);
                    _styledMembers.setSpan(new ForegroundColorSpan(Color.LTGRAY), _start, _end, 0);
                    break;
            }

            //shift space for ", " used to separate names
            _end += 2;

        }
    }
}