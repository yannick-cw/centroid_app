package com.niem.gladow.centroid;

import android.content.Context;
import android.util.Log;

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

    public static String getInviteList() {
      return Util.getInstance().cleanKeySet(inviteList.toString());
    }

    public static void addToInviteList(String number) {
        PersistenceHandler.inviteList.add(number);
    }

    InputStream inputStream;
    String readFile;
/*    try {
        inputStream = context.openFileInput("friend_list");
        readFile = convertInputStreamToString(inputStream);
        Log.d("readFile", readFile);
        new RestConnector(context).execute(GET, INVITE_FRIENDS + ownNumber + PersistenceHandler.getInviteList());
    } catch (Exception e) {
        e.printStackTrace();
    }
    return true;*/

    public static Map<String, String> getFriendMap() {
        return friendMap;
    }
}
