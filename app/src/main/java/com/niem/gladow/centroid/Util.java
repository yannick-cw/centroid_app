package com.niem.gladow.centroid;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        _tmp = _tmp.replaceAll("^00|[^0-9]", "");
        _tmp = _tmp.replaceAll("^0", "49");
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
        return DateFormat.format("E dd.MM, HH:mm", cal).toString();
    }

    public String getShortDate(Invite invite) {
        long _inviteTime = invite.getStartTime();
        long _currentTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(_inviteTime);
            if(_currentTime-_inviteTime < TimeUnit.DAYS.toMillis(1)){
            return DateFormat.format("HH:mm", cal).toString();
        } else {
            invite.setIs_deprecated(true);
            return DateFormat.format("dd. MMM", cal).toString();
        }
    }

    public int getResIdForTransportationImage(TransportationMode transportationMode) {
        switch (transportationMode) {
            case FOOT:
                return R.drawable.feet;
            case BIKE:
                return R.drawable.bike;
            case CAR:
                return R.drawable.car;
            case PUBLIC:
                return R.drawable.publictransport;
            case DECLINED:
                return R.drawable.declined;
            case DEFAULT:
                return R.drawable.unanswered;
            default:
                return R.drawable.unanswered;
        }
    }

    public int getColorForStatus(InviteReply inviteStatus, boolean is_deprecated) {
        if (is_deprecated) {
            switch (inviteStatus) {
                case READY:
                    return R.color.depr_ready;
                case UNANSWERED:
                    return R.color.depr_unanswered;
                case DECLINED:
                    return R.color.depr_declined;
                case ACCEPTED:
                    return R.color.depr_accepted;
                default:
                    return R.color.unanswered_dark_1;
            }
        } else {
            switch (inviteStatus) {
                case READY:
                    return R.color.invite_ready;
                case UNANSWERED:
                    return R.color.invite_unanswered;
                case DECLINED:
                    return R.color.invite_declined;
                case ACCEPTED:
                    return R.color.invite_accepted;
                default:
                    return R.color.unanswered_dark_1;
            }
        }


    }

    public int getColorForTranspMode(TransportationMode transportationMode) {
        switch (transportationMode) {
            case DECLINED:
                return R.color.transp_declined;
            case DEFAULT:
                return R.color.transp_unanswered;
            default:
                return R.color.transp_chosen;
        }
    }

    public int getButtonColor(Context context) {
        return ContextCompat.getColor(context, R.color.button_color);
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
