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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yannick_uni on 11/17/15.
 */
public class PersistenceHandler {
    private static Map<String, String> contactsMap;
    private static Map<String,String> friendMap = new HashMap<>();
    private static List<String> inviteList = new LinkedList<>();
    private static String ownNumber = "";
    private static EditText mEdit;
    private static Context _context;
    private static String token;


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
        for (String str: numbers) {
            if(contactsMap.containsKey(str)){
                friendMap.put(str, contactsMap.get(str));
            }
        }
        //ToDo save to file
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

    public void addToInviteList(String number) {
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


    InputStream inputStream;
/*     InputStream inputStream;
    String readFile;
   try {
        inputStream = context.openFileInput("friend_list");
        readFile = convertInputStreamToString(inputStream);
        Log.d("readFile", readFile);
        new RestConnector(context).execute(GET, INVITE_FRIENDS + ownNumber + PersistenceHandler.getInviteList());
    } catch (Exception e) {
        e.printStackTrace();
    }
    return true;*/

    public Map<String, String> getFriendMap() {
        return friendMap;
    }

    public static String getOwnNumber() {
        return ownNumber;
    }

    public static void saveOwnNumber(Context context) {
        _context = context;
        mEdit = (EditText) ((Activity) _context).getWindow().getDecorView().findViewById(R.id.phone_number);

        TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ownNumber.equals("")) {
            ownNumber = Util.getInstance().cleanNumberString(telephonyManager.getLine1Number()) + "/";
        }
        Log.d("ownNumber", ownNumber);

        // Check if number was stored correctly in Phone, ask for User Input
        if (ownNumber.equals("/")) {
            Log.d("sendOwnNumber", "if");
            mEdit.setVisibility(View.VISIBLE);
            mEdit.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter-key press
                        Toast.makeText(_context, mEdit.getText(), Toast.LENGTH_SHORT).show();
                        ownNumber = Util.getInstance().cleanNumberString(mEdit.getText().toString()) + "/";
                        mEdit.setVisibility(View.INVISIBLE);

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

    public static String getToken() {
        return token;
    }

    public static void saveOwnToken(String token) {
        PersistenceHandler.token = token;
    }
}
