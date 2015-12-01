package com.niem.gladow.centroid.Database;

import java.util.Map;

/**
 * Created by yannick_uni on 11/30/15.
 */
public interface MapDB {
    public boolean saveMap(Map<String, String> map, String fileName);
    public Map<String, String> loadMap(String fileName);
}
