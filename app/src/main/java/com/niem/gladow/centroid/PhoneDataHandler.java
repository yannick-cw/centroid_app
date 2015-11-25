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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
    private static String ownNumber = "";
    private EditText mEdit;

    public PhoneDataHandler(Context context) {
        this.context = context;
        mEdit = (EditText)((Activity)context).getWindow().getDecorView().findViewById(R.id.phone_number);

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
        // Try to read if number is stored in Phone and
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(ownNumber.equals("")){ownNumber = Util.getInstance().cleanNumberString(telephonyManager.getLine1Number()) + "/";}
        Log.d("ownNumber", ownNumber);

        // Check if number was stored correctly in Phone, ask for User Input
        if(ownNumber.equals("/")){
            Log.d("sendOwnNumber","if");
            mEdit.setVisibility(View.VISIBLE);
            mEdit.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode    == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter-key press
                        Toast.makeText(context, mEdit.getText(), Toast.LENGTH_SHORT).show();
                        ownNumber = Util.getInstance().cleanNumberString(mEdit.getText().toString()) + "/";
                        mEdit.setVisibility(View.INVISIBLE);

                        // Hide Keypad after input
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        return true;
                    }
                    return false;
                }
            });
         }
        return (ownNumber);
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
