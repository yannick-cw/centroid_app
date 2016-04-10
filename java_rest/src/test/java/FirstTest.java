import org.junit.Assert;
import org.junit.Test;
import testREST.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yannick_uni on 11/29/15.
 */
public class FirstTest {
    @Test
    public void firstTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void StringToList() {
        List<String> _testList = new ArrayList<>();
        _testList.add("Hallo");
        _testList.add("Test");
        List<String> _testList1 = new ArrayList<>();
        _testList1.add("");

        Assert.assertEquals("String to List",_testList,Util.getInstance()
                .stringToList("Hallo,Test"));
        Assert.assertEquals("empty List",_testList1,Util.getInstance()
                .stringToList(""));

    }

}
