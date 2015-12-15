package com.niem.gladow.centroid;

import com.niem.gladow.centroid.Enums.InviteReply;


/**
 * This class represents invites
 */

public class Invite {
    //is the unique id of the invite
    private long startTime;
    //number of person who invited
    private String inviteNumber;
    private Centroid centroid;
    private InviteReply status = InviteReply.UNANSWERED;
    private boolean existsCentroid = false;

    //todo add all participants numbers, no need for invite number only
    public Invite(String inviteNumber, long startTime) {
        this.inviteNumber = inviteNumber;
        this.startTime = startTime;
    }

    public Centroid getCentroid() {
        assert(centroid != null);
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

    public void setStatus(InviteReply status) {
        this.status = status;
    }


}
