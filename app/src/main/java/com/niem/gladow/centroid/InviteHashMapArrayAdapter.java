package com.niem.gladow.centroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class InviteHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView members;
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
            _viewHolder.members  = (TextView) convertView.findViewById(R.id.inviteListItemName);
            _viewHolder.time     = (TextView) convertView.findViewById(R.id.inviteListItemDate);
            _viewHolder.status   = (TextView) convertView.findViewById(R.id.inviteListItemStatus);
            _viewHolder.inviteId = (TextView) convertView.findViewById(R.id.inviteListItemInviteId);
            _viewHolder.image       = (ImageView) convertView.findViewById(R.id.inviteListItemImage);
            _viewHolder.statusImage = (ImageView) convertView.findViewById(R.id.inviteListItemStatusImage);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        Map.Entry<Long, Invite> _entryViewAtPosition = (Map.Entry<Long, Invite>) this.getItem(position);
        // get the corresponding Invite for this Element
        Invite _invite = InviteHandler.getInstance().getInviteByTime(_entryViewAtPosition.getKey());

        //build images
        String _hostName = _invite.getInviteNumberName().split(" ")[0];
        int _color = _colorGenerator.getColor(_hostName);
        _viewHolder.textDrawable = TextDrawable.builder()
                .buildRound(_hostName.substring(0,1), _color);
        _viewHolder.image.setImageDrawable(_viewHolder.textDrawable);

        //apply members to StyledTextString
        String _members = _invite.getAllMemberSurNames(false, false);
        if(_members.matches("")){
            _members = _hostName+"  ";
        }else{
            _members = _hostName+": "+_members;
        }
        SpannableString _styledMembers = new SpannableString(_members);
        setStringStyles(_invite, _styledMembers, 0, _hostName.length()+2);
        _viewHolder.members.setText(_styledMembers);


        //setTextViews/Imageviews
        _viewHolder.time.setText(String.valueOf(Util.getInstance().getShortDate(_invite.getStartTime())));
        _viewHolder.inviteId.setText(String.valueOf(_invite.getStartTime()));
        _viewHolder.status.setText(_invite.getStatus().toString());
        _viewHolder.statusImage
                .setImageResource(Util.getInstance()
                        .getResIdForTransportationImage(_invite.getTransportationMode()));
        return convertView;
    }

    //TODO Layout for a lot of members
    //method that sets the different layouts to member status and shows them in listViewItem
    private void setStringStyles(Invite _invite, SpannableString _styledMembers, int _start, int _end) {
        InviteReply _memberReply;
        _styledMembers.setSpan(new StyleSpan(Typeface.BOLD), _start, _end, 0);
        //check for each member status and apply StyleSpan
        for (Map.Entry<String, InviteStatus> _memberEntry : _invite.getAllMembers(false, false).entrySet())
        {
            _start = _end;
            _end += _memberEntry.getValue().getRealName().split(" ")[0].length();
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
            _end += 2; //shift space for ", " used to separate names

        }
    }
}