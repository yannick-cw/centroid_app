package com.niem.gladow.centroid;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class PhoneDataHandler extends AsyncTask<String, String, String> {
    private Context context;
    private String numbers;
    //creates public object from interface and is initialized with null, later NumberLogicHandler is assigned to it
    public AsyncResponse delegate = null;

    public PhoneDataHandler(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String _contacts = getContactsNumbers();
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
    private String getContactsNumbers() {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            // String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numbers += "," + phoneNumber;

        }
        phones.close();
        return numbers;
    }

}
