package com.niem.gladow.centroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class PhoneDataHandler extends AsyncTask<String, String, String> {
    private Context context;
    private static Map numbersNames;
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

    public String getOwnNumber() {
        //TODO check if number is null, implement alternative method to get number directly from user
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (telephonyManager.getLine1Number());
    }



    //gets all numbers from phone
    private void setContactsMap() {
        numbersNames = new HashMap();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = Util.getInstance().cleanNumberString(phoneNumber);
            numbersNames.put(phoneNumber,name);
        }
        phones.close();
        PersistenceHandler.getInstance().setContactsMap(numbersNames);
    }

    public static Map getNumbersNames() {
        return numbersNames;
    }
}
