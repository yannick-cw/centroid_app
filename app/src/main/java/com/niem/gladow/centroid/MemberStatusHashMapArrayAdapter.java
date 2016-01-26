package com.niem.gladow.centroid;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.List;
import java.util.Map;

/**
 * Created by clem on 24/11/15.
 */
public class MemberStatusHashMapArrayAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView memberName;
        TextView memberId;
        ImageView   statusImage;
        InviteReply inviteReply;
    }

    public MemberStatusHashMapArrayAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Object>> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String _realName;
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
        _realName = member.getValue().getRealName();
        if(_realName.matches("")){
            _viewHolder.memberName.setText(member.getKey()+" (" + member.getValue().getInviteReply() + ")");

        }else {
            _viewHolder.memberName.setText(member.getValue().getRealName()+" (" + member.getValue().getInviteReply() + ")");
        }
        _viewHolder.inviteReply = member.getValue().getInviteReply();


        switch (_viewHolder.inviteReply){
            case DECLINED:
                _viewHolder.memberName.setTextColor(ContextCompat.getColor(getContext(), R.color.declined_1));
                _viewHolder.statusImage.setImageResource(R.drawable.declined);
                _viewHolder.statusImage.setTag(TransportationMode.DECLINED);
                break;
            case ACCEPTED:
                _viewHolder.memberName.setTextColor(ContextCompat.getColor(getContext(), R.color.centroid_1));
                _viewHolder.statusImage.setImageResource(Util.getInstance().getResIdForTransportationImage(member.getValue().getTransportationMode()));
                _viewHolder.statusImage.setTag(member.getValue().getTransportationMode());
                break;
            default:
                _viewHolder.memberName.setTextColor(ContextCompat.getColor(getContext(), R.color.unanswered_1));
                _viewHolder.statusImage.setTag(TransportationMode.DEFAULT);
                break;
        }

        return convertView;
    }

}