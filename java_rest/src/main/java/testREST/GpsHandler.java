package testREST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yannick_uni on 11/16/15.
 */
public class GpsHandler {

    private MiniMapDB miniMapDb = new MiniDataBase();
    private static Map<String, String> gpsMap = new HashMap<>();

    public GpsHandler() {
        if(gpsMap.isEmpty()) {
            gpsMap = miniMapDb.loadMap(Util.getInstance().GPS_DATA_FILE_NAME);
            Logger.getLogger(MiniDataBase.class.getName()).log(Level.INFO,gpsMap.values().toString());
        }
    }

    public boolean saveGPS (String ownNumber, String latitude, String longitude) {
        gpsMap.put(ownNumber, latitude + "," + longitude);
        miniMapDb.saveMap(gpsMap, Util.getInstance().GPS_DATA_FILE_NAME);
        Logger.getLogger(MiniDataBase.class.getName()).log(Level.INFO,gpsMap.values().toString());
        return true;
    }

    public List<String> getGPS (String number) {
        return Util.getInstance().stringToList(gpsMap.get(number));
    }
}
