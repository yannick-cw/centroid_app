package testREST;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yannick_uni on 11/15/15.
 */
public class MiniDataBase implements MiniMapDB {
    private Logger logger = Logger.getLogger(MiniDataBase.class.getName());

    public void saveMap(Map<String, String> map, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(map);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Map<String, String> loadMap(String fileName) {
        Map<String, String> map = new HashMap<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))){
            map = (Map<String, String>) in.readObject();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return map;
    }
}
