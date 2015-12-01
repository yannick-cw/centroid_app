package com.niem.gladow.centroid;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

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
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
