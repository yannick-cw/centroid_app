package com.niem.gladow.centroid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class RestConnector extends AsyncTask<String, String, String> {
    public static final String POST = "1", GET = "2", SEND = "3", SYNC = "4";
//    private static final String HOST_ADDRESS = "http://192.168.26.10:8080";
    private static final String HOST_ADDRESS = "http://schnutentier.ddns.net";
    private Context context;

    public RestConnector(Context context) {
        this.context = context;
    }

    //depending on post, get or send (send all contacts) goes to method to communicate with server
    @Override
    protected String doInBackground(String... params) {
        String result;
        switch (params[0]) {
            case POST:
                result = restPost(params[1]);
                break;
            case GET:
                result = restGet(params[1]);
                break;
            case SEND:
                result = restGet(params[1]);
                PersistenceHandler.getInstance().createFriendMap(result);
                PersistenceHandler.getInstance().saveFriendMapToDB();
                Log.d("friend Map", PersistenceHandler.getInstance().getFriendMap().values().toString());
                break;
            case SYNC:
                result = restGet(params[1]);
                InviteHandler.getInstance().syncInvite(result, context);
            default:
                result = "wrong input";
        }
        return result;
    }

    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }


    private String restPost(String urlString) {
        String result;
        try {
            Log.d("urlparams", urlString);
            URL url = new URL(HOST_ADDRESS+urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("test");
            out.close();

            Log.d("responseCode", new Integer(connection.getResponseCode()).toString());

            result = Util.getInstance().convertInputStreamToString(connection.getInputStream());
            Log.d("reader", result);


        } catch (Exception e) {
            result = e.getMessage();
        }

        return result;
    }

    private String restGet(String urlString) {
        String result = "";
        try {
            Log.d("urlparams", urlString);
            URL url = new URL(HOST_ADDRESS+urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            Log.d("responseCode", new Integer(connection.getResponseCode()).toString());
            if (new Integer(connection.getResponseCode()) == 200) {
                result = Util.getInstance().convertInputStreamToString(connection.getInputStream());
            }
            Log.d("reader", result);


        } catch (Exception e) {
            result = e.getMessage();
        }

        return result;
    }
}
