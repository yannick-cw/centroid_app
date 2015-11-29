import com.niem.gladow.centroid.Util;

import junit.framework.Assert;

import org.junit.Test;

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
}
