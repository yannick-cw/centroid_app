package com.niem.gladow.centroid;

import android.text.format.DateFormat;
import android.util.Log;

import com.niem.gladow.centroid.Enums.TransportationMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by yannick_uni on 11/17/15.
 */
public final class Util {
    private static Util instance;

    private Util() {
    }

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public String cleanNumberString(String numbers) {
        String _tmp = numbers;
        //Log.d("NUMBERS", numbers);
        _tmp = _tmp.replaceAll("^00|[^0-9]", "");
        _tmp = _tmp.replaceAll("^0", "49");

        //Log.d("afterClean", _tmp);

        return _tmp;
    }

    public String cleanKeySet(String keySet) {
        String _tmp = keySet;
        _tmp = _tmp.replaceAll("[^0-9,]", "");
        return _tmp;
    }

    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
    }

    public String getShortDate(long inviteTime) {
        long _currentTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(inviteTime);
        //TODO exchange lines below
        //if(_currentTime-inviteTime < TimeUnit.DAYS.toMillis(1)){
        if(_currentTime-inviteTime < TimeUnit.MINUTES.toMillis(30)){
            return DateFormat.format("HH:mm:ss", cal).toString();
        }else{
            return DateFormat.format("dd. MMM", cal).toString();
        }
    }

    public int getResIdForTransportationImage(TransportationMode transportationMode){
        switch (transportationMode) {
            case FOOT:
                return R.drawable.feet;
            case BIKE:
                return R.drawable.bike;
            case CAR:
                return R.drawable.car;
            case PUBLIC:
                return R.drawable.publictransport;
            case DEFAULT:
                return R.drawable.ic_media_play;
            default:
                return R.drawable.ic_media_pause;
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
