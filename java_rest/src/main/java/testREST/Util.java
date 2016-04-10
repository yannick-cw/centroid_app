package testREST;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by clem on 19/11/15.
 */
public final class Util {
    public final String USER_FILE_NAME = System.getenv("CENTROID_SERVER_USER_LIST");
    public final String GPS_DATA_FILE_NAME = System.getenv("CENTROID_SERVER_GPS_DATA_FILE");

    private static Util instance;
    private Util() {
    }

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }


    public static boolean isMac() {
        String _osString = System.getProperty("os.name").toLowerCase();
        return (_osString.indexOf("mac") >= 0);
    }

    public List<String> stringToList(String str) {
           List<String> _stringList = Arrays.asList(str.split(","));
        return _stringList;
    }

    public String collectionToString(Collection<String> collection) {
        String _result = "";
        for (String s: collection) {
            _result +=  "," + s;
        }
        _result = _result.replaceFirst(",","");
        return _result;
    }

}
