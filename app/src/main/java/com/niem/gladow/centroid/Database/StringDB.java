package com.niem.gladow.centroid.Database;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public interface StringDB {
    public String loadString (String fileName);
    public void saveString (String string, String fileName);
}
