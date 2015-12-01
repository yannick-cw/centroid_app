package com.niem.gladow.centroid.Database;

import java.util.Map;

/**
 * Created by yannick_uni on 11/30/15.
 */
public interface StringDB {
    public String loadString (String fileName);
    public void saveString (String string, String fileName);
}
