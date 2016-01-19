import com.niem.gladow.centroid.Util;

import junit.framework.Assert;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yannick_uni on 11/29/15.
 */

public class UtilTests {
    @Test
    public void firstTest() {
        Assert.assertTrue(true);
    }

    /**
     * cleanNumberString tests
     * */
    @Test
    public void easyNumbers () {
        String [] _numbers = {"0167532534","004912312312","+4921342343254","+4412343254325","123124324","00123234325","5453"};
        String [] _result = {"49167532534","4912312312","4921342343254","4412343254325","123124324","123234325","5453"};

        for (int i = 0; i < _numbers.length; i++) {
            Assert.assertEquals("check some easy numbers", _result[i], Util.getInstance().cleanNumberString(_numbers[i]));
        }
    }

    @Test
    public void nullNumber () {
        Assert.assertEquals("clean empty number", "", Util.getInstance().cleanNumberString(""));
    }

    @Test
    public void tooManySpaces () {
        Assert.assertEquals("clean spaces", "4412312123212131", Util.getInstance().cleanNumberString("004412312 12321 2131"));
    }

    @Test
    public void throwAwayStrangeNumbers () {
        Assert.assertEquals("clean strange stuff", "21316765453", Util.getInstance().cleanNumberString("00dasdas2131@#$+=ds=gdf6765./;453"));
    }

    @Test
    public void cleanNumberString () {

    }





    @Test
    public void testSortByValue()
    {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<String, Integer>(1000);
        for(int i = 0 ; i < 1000 ; ++i) {
            testMap.put( "SomeString" + random.nextInt(), random.nextInt());
        }

        testMap = Util.sortByValue( testMap );
        Assert.assertEquals( 1000, testMap.size() );

        Integer previous = null;
        for(Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull( entry.getValue() );
            if (previous != null) {
                Assert.assertTrue( entry.getValue() >= previous );
            }
            previous = entry.getValue();
        }
    }

}
