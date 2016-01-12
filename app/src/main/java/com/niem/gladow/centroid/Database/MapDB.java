package com.niem.gladow.centroid.Database;

import java.util.Map;

/**
 * Created by yannick_uni on 11/30/15.
 */
public interface MapDB {
    public boolean saveMap(Map map, String fileName);
    public <T,S> Map<T, S> loadMap(String fileName);
}
