import org.junit.Assert;
import org.junit.Test;
import testREST.Enums.TransportationModes;
import testREST.Invite;
import testREST.Enums.InviteReply;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yannick_uni on 12/8/15.
 */
public class InviteSzenario {
    private final String ownNumber = "123";
    private final String friendNumbers = "1,2,3";


    @Test
    public void inviteFriendsBasics () {
        Invite _invite = new Invite(ownNumber, friendNumbers, TransportationModes.DEFAULT);
        Assert.assertEquals(4,_invite.getAllNumbers().size());
        Assert.assertFalse(_invite.isInviteComplete());
        _invite.responseToInvite("1", InviteReply.ACCEPTED, TransportationModes.DEFAULT);
        _invite.responseToInvite("2", InviteReply.DECLINED, TransportationModes.DEFAULT);
        Assert.assertEquals(false, _invite.isInviteComplete());
        List<String> _acceptedList = new LinkedList<>();
        _acceptedList.add("1");
        _acceptedList.add("123");
        Assert.assertEquals(_acceptedList,_invite.getInviteAcceptedFriendsAndHost());
        _invite.responseToInvite("3", InviteReply.ACCEPTED, TransportationModes.DEFAULT);
        Assert.assertEquals(true, _invite.isInviteComplete());
        _acceptedList.add("3");
        Assert.assertFalse(_invite.responseToInvite("5", InviteReply.ACCEPTED, TransportationModes.DEFAULT));
        Assert.assertEquals(_acceptedList,_invite.getInviteAcceptedFriendsAndHost());

    }
}
