package com.niem.gladow.centroid.Enums;

import java.io.Serializable;

/**
 * Created by yannick_uni on 1/12/16.
 */
public class InviteStatus implements Serializable{
    private InviteReply inviteReply;
    private TransportationMode transportationMode;
    private String realName;

    public InviteStatus() {
        this.inviteReply = InviteReply.UNANSWERED;
        this.transportationMode = TransportationMode.DEFAULT;
        this.realName = "";
    }

    public InviteReply getInviteReply() {
        return inviteReply;
    }

    public void setInviteReply(InviteReply inviteReply) {
        this.inviteReply = inviteReply;
    }

    public TransportationMode getTransportationMode() {
        return transportationMode;
    }

    public void setTransportationMode(TransportationMode transportationMode) {
        this.transportationMode = transportationMode;
    }
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
