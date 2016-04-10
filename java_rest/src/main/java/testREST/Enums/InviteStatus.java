package testREST.Enums;

/**
 * Created by yannick_uni on 1/12/16.
 */
public class InviteStatus {
    private InviteReply inviteReply;
    private TransportationModes transportationMode;

    public InviteStatus() {
        this.inviteReply = InviteReply.UNANSWERED;
        this.transportationMode = TransportationModes.DEFAULT;
    }

    public InviteReply getInviteReply() {
        return inviteReply;
    }

    public void setInviteReply(InviteReply inviteReply) {
        this.inviteReply = inviteReply;
    }

    public TransportationModes getTransportationMode() {
        return transportationMode;
    }

    public void setTransportationMode(TransportationModes transportationMode) {
        this.transportationMode = transportationMode;
    }
}
