package com.niem.gladow.centroid.Database;

import java.util.Map;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public interface MapDB {
    public boolean saveMap(Map map, String fileName);
    public <T,S> Map<T, S> loadMap(String fileName);
}
