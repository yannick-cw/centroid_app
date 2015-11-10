package com.niem.gladow.centroid;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class PhoneDataHandler extends AsyncTask <String, String, String> {
    private Context context;
    private String numbers;
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
        delegate.processFinish(result);
    }

    private String getContactsNumbers() {
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
           // String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numbers += "," + phoneNumber;

        }
        phones.close();
        return numbers;
    }

}
