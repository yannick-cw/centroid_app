package com.niem.gladow.centroid.Database;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick_uni on 11/25/15.
 */
public class MiniDB implements MapDB, StringDB{

    private Context context;
    private static MiniDB instance;

    private MiniDB() {
    }

    public static void init(Context context) {
        assert(instance == null);
        instance = new MiniDB();
        instance.context = context;
    }

    public static MiniDB getInstance() {
        assert (instance != null);
        return instance;
    }


    public void saveString (String string, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(string);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String loadString (String fileName) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("MiniDB", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("MiniDB", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public boolean saveMap(Map map, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE))) {
            out.writeObject(map);
            return true;
        } catch (IOException e) {
            Log.e("save map", e.getMessage());
            return false;
        }
    }

    public <T,L> Map<T, L> loadMap(String fileName) {
        Map<T, L> map = new HashMap<>();

        try (ObjectInputStream in = new ObjectInputStream(context.openFileInput(fileName))){
            map = (Map<T, L>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("load map", e.getMessage());
        } catch (IOException e) {
            Log.e("load map", e.getMessage());
        }

        return map;
    }
}
