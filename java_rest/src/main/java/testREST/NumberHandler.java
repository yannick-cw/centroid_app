package testREST;

import java.util.*;

/**
 * Created by yannick_uni on 11/8/15.
 */
public class NumberHandler {

    private MiniMapDB miniMapDB = new MiniDataBase();
    private final static String FILE_NAME = Util.getInstance().USER_FILE_NAME;
    private static Map<String, String> allNumbers = new HashMap<>();

    public NumberHandler() {
        if(allNumbers.isEmpty()) {
            allNumbers = miniMapDB.loadMap(FILE_NAME);
        }
    }
    public boolean addNumber(String number, String token) {
        allNumbers.put(number, token);
        miniMapDB.saveMap(allNumbers,FILE_NAME);
        return true;
    }

    public String checkNumbers(String ownNumber, String contactNumbers) {
        List<String> _contacts = Util.getInstance().stringToList(contactNumbers);
        String contacts = "";
        for (String s : _contacts) {
            for (String str : allNumbers.keySet()) {
                if (s.equals(str) && !str.equals(ownNumber)) {
                    contacts += "," + str;
                }
            }
        }
        return !contacts.equals("") ? contacts.substring(1) : "false";
    }

    public static Map<String, String> getAllNumbers() {
        return allNumbers;
    }
}
