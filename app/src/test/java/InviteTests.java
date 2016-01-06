import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.InviteHandler;

import org.junit.Assert;
import org.junit.Test;


/**
 * Created by clem on 06/01/16.
 */
public class InviteTests {

    @Test
    public void getLatestInviteTest(){
        InviteHandler.addInvite("1234", 12345, "Clemens,01234,Yannick");
        InviteHandler.getInviteByTime(12345).setStatus(InviteReply.ACCEPTED);
        Assert.assertEquals("1234",InviteHandler.getInviteByTime(12345).getInviteNumber());
    }

}
