package com.esstudio.gallary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.DropBoxManager.Entry;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings {

    public static final String USER_AGENT = ""
            + "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) "
            + "AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19";

    public static final String PREF_URL = "pURL";
    public static final String PREF_NAME = "pName";
    public static final String PREF_LIMIT = "pLimit";
    public static final String PREF_COLUMN = "pColumn";
    public static final String PREF_KEYWORD = "pKeyword";
    public static final String PREF_IMAGEONLY = "pImageOnly";
    public static final String PREF_MAXWORKER = "pMaxWorker";
    public static final String PREF_REVERSEMODE = "pReverseMode";
    public static final String PREF_MYGALLERYSET = "pMyGallerySet";

    private static SharedPreferences pref;

    public Settings() {
    }

    public static String getPrefsString(Context context, String key) {
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.getAll().isEmpty())
                setPrefsDefault(context);
        }
        return pref.getString(key, null);
    }

    public static int getPrefsInteger(Context context, String key) {
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.getAll().isEmpty())
                setPrefsDefault(context);
        }

        String v = pref.getString(key, null);

        return v == null ? -1 : Integer.parseInt(pref.getString(key, null));
    }

    public static void setPrefsDefault(Context context) {
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        Log.i("tag", "Restore Default Setting");
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_KEYWORD, "");
        editor.putString(PREF_NAME, "taeyeon_new1");
        editor.putString(PREF_URL,
            "http://m.dcinside.com/view.php?id=$NAME&no=");
        editor.putString(PREF_COLUMN, "3");
        editor.putString(PREF_LIMIT, "50");
        editor.putString(PREF_MAXWORKER, "3");
        // editor.putBoolean(PREF_IMAGEONLY, true);

        editor.putString(PREF_REVERSEMODE, "false");

        editor.apply();
    }

    public static void setPrefsString(Context context, String key, String value) {
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        log.out("pref : " + key + " " + value);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static void setProperties(Context context, String key, String value) {

        File dataDir = new File(
            android.os.Environment.getExternalStorageDirectory(),
            "GalleryViewer/data/");
        if (!dataDir.exists())
            dataDir.mkdirs();

        File file = new File(dataDir.getPath() + "/settings.properties");
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        properties.setProperty(key, value);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setProperties(HashMap<String, String> items) {

        File dataDir = new File(
            android.os.Environment.getExternalStorageDirectory(),
            "GalleryViewer/data/");

        if (!dataDir.exists())
            dataDir.mkdirs();

        File file = new File(dataDir.getPath() + "/settings.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        properties.clear();

        Set<String> keys = items.keySet();
        Collection<String> values = items.values();

        Object[] k = keys.toArray();
        Object[] v = values.toArray();

        for (int i = 0; i < keys.size(); i++) {
            properties.setProperty(k[i].toString(), v[i].toString());
            log.out("k: " + k[i].toString() + " v: " + v[i].toString());

        }

        try {
            properties.store(new FileOutputStream(file), null);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static HashMap<String, String> getProperties(Context context) {

        File dataDir = new File(
            android.os.Environment.getExternalStorageDirectory(),
            "GalleryViewer/data/");
        if (!dataDir.exists())
            dataDir.mkdirs();

        File file = new File(dataDir.getPath() + "/settings.properties");
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<String, String>();
        Set<Object> keys = properties.keySet();
        Collection<Object> values = properties.values();

        Object[] k = keys.toArray();
        Object[] v = values.toArray();

        for (int i = 0; i < keys.size(); i++) {
            map.put(k[i].toString(), v[i].toString());
            log.out("k: " + k[i].toString() + " v: " + v[i].toString());
        }

        return map;

    }

    public static void setProferencesListener(
                                              OnSharedPreferenceChangeListener listener) {
        pref.registerOnSharedPreferenceChangeListener(listener);

    }

    public static boolean getReverseMode() {

        String Rvrs = pref.getString(PREF_REVERSEMODE, null);
        if (Rvrs == null)
            Rvrs = "false";

        return Boolean.valueOf(Rvrs);
    }

    public static void setReverseIndex(String key, int maxIndex) {
        // SharedPreferences.Editor editor = pref.edit();
        // editor.putInt(key, maxIndex);
        // editor.apply();

        File dataDir = new File(
            android.os.Environment.getExternalStorageDirectory(),
            "GalleryViewer/data/");
        if (!dataDir.exists())
            dataDir.mkdirs();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                dataDir.getPath() + "/" + key, true));
            bw.write("\n" + String.valueOf(maxIndex));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static int getReverseIndex(String key) {

        File dataDir = new File(
            android.os.Environment.getExternalStorageDirectory(),
            "GalleryViewer/data/");
        if (!dataDir.exists())
            dataDir.mkdirs();

        String line;
        int maxIndex = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                dataDir.getPath() + "/" + key));

            String tmp = "0";
            while ((line = br.readLine()) != null) {
                // pass
                tmp = line;
            }

            br.close();
            maxIndex = Integer.valueOf(tmp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // return pref.getInt(key, 0);
        return maxIndex;
    }
}
