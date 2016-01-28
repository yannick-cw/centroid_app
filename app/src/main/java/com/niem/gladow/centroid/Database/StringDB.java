package com.niem.gladow.centroid.Database;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public interface StringDB {
    String loadString(String fileName);
    void saveString(String string, String fileName);
}
