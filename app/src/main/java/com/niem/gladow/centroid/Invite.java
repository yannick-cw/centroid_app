package com.niem.gladow.centroid;

import com.niem.gladow.centroid.Enums.InviteReply;

import java.util.Date;

/**
 * Created by yannick_uni on 12/15/15.
 */
public class Invite {
    private String inviteNumber;
    private long startTime;
    private Centroid centroid;
    private InviteReply status = InviteReply.UNANSWERED;
    private boolean existsCentroid = false;

    public Invite(String inviteNumber, long startTime) {
        this.inviteNumber = inviteNumber;
        this.startTime = startTime;
    }

    public Centroid getCentroid() {
        return centroid;
    }

    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
        existsCentroid = true;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getInviteNumber() {
        return inviteNumber;
    }

    public InviteReply getStatus() {
        return status;
    }

    public boolean existsCentroid() {
        return existsCentroid;
    }

    public void setExistsCentroid(boolean existsCentroid) {
        this.existsCentroid = existsCentroid;
    }

    public void setStatus(InviteReply status) {
        this.status = status;
    }


}
