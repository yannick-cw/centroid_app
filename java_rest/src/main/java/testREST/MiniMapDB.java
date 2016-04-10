package testREST;

import java.util.Map;

/**
 * Created by yannick_uni on 11/16/15.
 */
public interface MiniMapDB {
    public void saveMap(Map<String, String> map, String fileName);
    public Map<String, String> loadMap(String fileName);
}
