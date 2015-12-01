package com.niem.gladow.centroid;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.niem.gladow.centroid.Database.MapDB;
import com.niem.gladow.centroid.Database.MiniDB;
import com.niem.gladow.centroid.Database.StringDB;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick_uni on 11/17/15.
 */
public class PersistenceHandler {
    private Map<String, String> contactsMap;
    private Map<String,String> friendMap = new HashMap<>();
    private List<String> inviteList = new LinkedList<>();
    private String ownNumber = "";
    private String token = "";

    /* these are the file names */
    private final String OWN_NUMBER_FILE = "ownNumber";
    private final String TOKEN_FILE = "ownToken";
    private final String FRIEND_MAP_FILE = "friendMap";

    private static PersistenceHandler instance;

    private PersistenceHandler() {
    }

    public static PersistenceHandler getInstance() {
        if (instance == null) {
            instance = new PersistenceHandler();
        }
        return instance;
    }

    public void createFriendMap (String contacts) {
        List<String> numbers = Arrays.asList(contacts.split(","));
        //TODO old invalid contacts
        for (String str: numbers) {
            if(contactsMap.containsKey(str)){
                friendMap.put(str, contactsMap.get(str));
            }
        }
    }

    public Map<String, String> getFriendMap() {
        return friendMap;
    }

    public boolean loadFriendMapFromDB (Context context) {
        MapDB _mapDB = new MiniDB(context);
        friendMap = _mapDB.loadMap(FRIEND_MAP_FILE);
        return !friendMap.isEmpty();
    }

    public boolean saveFriendMapToDB (Context context) {
        MapDB _mapDB = new MiniDB(context);
        return _mapDB.saveMap(friendMap,FRIEND_MAP_FILE);
    }

    public void setContactsMap(Map<String, String> contactsMap) {
        this.contactsMap = contactsMap;
    }

    public String getCleanNumbers() {
        return Util.getInstance().cleanKeySet(contactsMap.keySet().toString());
    }

    public String getInviteList() {
      return Util.getInstance().cleanKeySet(inviteList.toString());
    }

    /**
     * adds the number to the invite list, if he is not already on it
     * */
    public void addToInviteList(String number) {
        for (String str: inviteList) {
            if (str == number) {
                return;
            }
        }
        this.inviteList.add(number);
    }

    public void removeFromInviteList(String number) {
        for (int i = 0; i < this.inviteList.size(); i++) {
            if(this.inviteList.get(i).equals(number)){
                this.inviteList.remove(i);
                break;
            }
        }
    }

    public void clearInviteList(){
        this.inviteList.clear();
    }

    public String getOwnNumber() {
        return this.ownNumber;
    }

    /**
     * try to load onwNumber and Token from the database and store them in local value
     * returns true if both values aren't empty
    */
    public boolean firstLoadOwnNumberAndToken (Context context) {
        final StringDB _stringDb = new MiniDB(context);
        ownNumber = _stringDb.loadString(OWN_NUMBER_FILE);
        token = _stringDb.loadString(TOKEN_FILE);
        Log.i("DB loaded ownNumber", ownNumber);
        Log.i("DB loaded token", token);
        if (!ownNumber.isEmpty() && !token.isEmpty()) {
            return true;
        }
        ownNumber = "/";
        return false;
    }

    /**
     * tries to access the own number from the telephone and saves it
     * if this is not possible waits for the user input of the number and saves it
     * */
    public void saveOwnNumber(Context context) {
        final Context _context = context;
        final EditText _mEdit = (EditText) ((Activity) _context).getWindow().getDecorView().findViewById(R.id.phone_number);

        TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ownNumber.equals("/")) {
            ownNumber = Util.getInstance().cleanNumberString(telephonyManager.getLine1Number()) + "/";
            new MiniDB(_context).saveString(ownNumber, OWN_NUMBER_FILE);
        }
        Log.d("ownNumber", ownNumber);

        // Check if number was stored correctly in Phone, ask for User Input
        if (ownNumber.equals("/")) {
            Log.d("sendOwnNumber", "if");
            _mEdit.setVisibility(View.VISIBLE);
            _mEdit.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter-key press
                        Toast.makeText(_context, _mEdit.getText(), Toast.LENGTH_SHORT).show();
                        ownNumber = Util.getInstance().cleanNumberString(_mEdit.getText().toString()) + "/";
                        new MiniDB(_context).saveString(ownNumber, OWN_NUMBER_FILE);
                        _mEdit.setVisibility(View.INVISIBLE);

                        // Hide Keypad after input
                        InputMethodManager in = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public String getToken() {
        return token;
    }

    public void saveOwnToken(String token, Context context) {
        this.token = token;
        new MiniDB(context).saveString(token, TOKEN_FILE);
    }
}
