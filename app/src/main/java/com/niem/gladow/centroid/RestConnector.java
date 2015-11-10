package com.niem.gladow.centroid;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class RestConnector extends AsyncTask<String, String, String> {
    private static final String POST = "1", GET = "2";

    @Override
    protected String doInBackground(String... params) {
        String result;
        switch (params[0]){
            case POST:
                result = restPost(params[1]);
                break;
            case GET:
                result = restGet(params[1]);
                break;
            default:
                result = "wrong input";
        }
        return result;
    }

    protected void onPostExecute(String result) {

    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private String restPost (String urlString) {
        String result;
        try {
            Log.d("urlparams", urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            Log.d("responseCode", new Integer(connection.getResponseCode()).toString());

            result = convertInputStreamToString(connection.getInputStream());
            Log.d("reader", result);



        } catch (Exception e) {
            result = e.getMessage();
        }

        return result;
    }

    private String restGet (String urlString) {
        String result;
        try {
            Log.d("urlparams", urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            Log.d("responseCode", new Integer(connection.getResponseCode()).toString());

            result = convertInputStreamToString(connection.getInputStream());
            Log.d("reader", result);



        } catch (Exception e) {
            result = e.getMessage();
        }

        return result;
    }




} // end CallAPI
