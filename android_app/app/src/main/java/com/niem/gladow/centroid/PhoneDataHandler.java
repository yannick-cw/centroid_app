package com.niem.gladow.centroid;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public class PhoneDataHandler extends AsyncTask<String, String, String> {
    private Context context;
    //creates public object from interface and is initialized with null, later NumberLogicHandler is assigned to it
    public AsyncResponse delegate = null;

    public PhoneDataHandler(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        setContactsMap();
        String _contacts = PersistenceHandler.getInstance().getCleanNumbers();
        Log.d("Contacts", _contacts);
        return _contacts;
    }

    protected void onPostExecute(String result) {
        //starts method in NumberLogicHandler.processFinish
        delegate.processFinish(result);
    }

    //gets all numbers from phone
    private void setContactsMap() {
        Map numbersNames = new HashMap();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = Util.getInstance().cleanNumberString(phoneNumber);
            numbersNames.put(phoneNumber, name);
        }
        phones.close();
        PersistenceHandler.getInstance().setContactsMap(numbersNames);
    }
}
