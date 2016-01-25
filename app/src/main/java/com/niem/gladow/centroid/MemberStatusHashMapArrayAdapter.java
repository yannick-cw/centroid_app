package com.niem.gladow.centroid;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class MemberStatusHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView memberName;
        TextView memberId;
        ImageView statusImage;
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
            _viewHolder.memberId = (TextView) convertView.findViewById(R.id.memberID);
            _viewHolder.memberName = (TextView) convertView.findViewById(R.id.memberName);
            _viewHolder.statusImage = (ImageView) convertView.findViewById(R.id.memberListStatusImage);
            convertView.setTag(_viewHolder);
        } else {
            /* We recycle a View that already exists */
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        // Once we have a reference to the View we are returning, we set its values.
        // member is the member at given position
        Map.Entry<String, InviteStatus> member = (Map.Entry<String, InviteStatus>) this.getItem(position);

        _viewHolder.memberId.setText(member.getKey());
        _viewHolder.memberName.setText(member.getValue().getRealName() + " (" + member.getValue().getInviteReply() + ")");
        _viewHolder.inviteReply = member.getValue().getInviteReply();

        switch (_viewHolder.inviteReply){
            case DECLINED:
                convertView.setBackgroundResource(R.color.declined);
                _viewHolder.statusImage.setImageResource(R.drawable.declined_black);
                break;
            case ACCEPTED:
                convertView.setBackgroundResource(R.color.accepted);
                _viewHolder.statusImage.setImageResource(getSafeStatus(member));
                break;
            default:
                convertView.setBackgroundResource(R.color.unanswered);
                break;
        }



        return convertView;
    }

    //TODO auto-refresh check :), needed InviteID...
    private int getSafeStatus(Map.Entry<String, InviteStatus> member){
        int _tmp  = Util.getInstance().getResIdForTransportationImage(member.getValue().getTransportationMode());
//        if(_tmp == R.drawable.unanswered){
//            new RestConnector(getContext()).execute(RestConnector.SYNC, "/android/updateInvite/" + member.getValue().);
//        }
        return _tmp;

    }
}