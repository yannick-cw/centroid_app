package com.niem.gladow.centroid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class RestConnector extends AsyncTask<String, String, String> {
    public static final String POST_NO_RESULT = "1", GET_NO_RESULT = "2", SEND_CONTACTS = "3", SYNC_INVITE = "4", SYNC_ALL_INVITES = "5";
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
            case POST_NO_RESULT:
                result = restPost(params[1]);
                break;
            case GET_NO_RESULT:
                result = restGet(params[1]);
                break;
            case SEND_CONTACTS:
                result = restGet(params[1]);
                new NumberLogicHandler(context).saveFriendMap(result);
                break;
            case SYNC_INVITE:
                result = restGet(params[1]);
                InviteHandler.getInstance().syncInvite(result, context);
                break;
            case SYNC_ALL_INVITES:
                result = restGet(params[1]);
                InviteHandler.getInstance().syncAllInvites(result, context);
                break;
            default:
                result = "wrong input";
        }
        return result;
    }

    private String restPost(String urlString) {
        String result;
        try {
            Log.d("urlparams", urlString);
            URL url = new URL(HOST_ADDRESS + urlString);
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
            URL url = new URL(HOST_ADDRESS + urlString);
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
