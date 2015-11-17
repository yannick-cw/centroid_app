package com.niem.gladow.centroid;

import android.util.Log;

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
        Log.d("NUMBERS", numbers);
        _tmp = _tmp.replaceAll("[+]", "");
        _tmp = _tmp.replaceAll("[^0-9]", "");
        _tmp = _tmp.replaceAll("^[0]{2}", "");
        _tmp = _tmp.replaceAll("^[0]", "49");

        //removes leading comma

        Log.d("afterClean", _tmp);

        return _tmp;
    }

    public String cleanKeySet(String keySet) {
        String _tmp = keySet;
        _tmp = _tmp.replaceAll("[^0-9,]", "");
        return _tmp;
    }
}
