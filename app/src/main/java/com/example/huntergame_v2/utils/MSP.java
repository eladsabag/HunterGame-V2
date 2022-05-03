package com.example.huntergame_v2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.TreeMap;

public class MSP {
    private final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private static MSP me = null;

    private MSP(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
    }

    public static MSP getMe(Context context) {
        if(me == null) {
            me = new MSP(context);
        }
        return me;
    }

    public void putIntToSP(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntFromSP(String key, int def) {
        return prefs.getInt(key, def);
    }

    public void putStringToSP(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringFromSP(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putBooleanToSP(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }


    public boolean getBooleanFromSP(String key, boolean def) { return prefs.getBoolean(key,def); }


    public <T> void putArray(String KEY, ArrayList<T> array) {
        String json = new Gson().toJson(array);
        prefs.edit().putString(KEY, json).apply();
    }

    public <T> ArrayList<T> getArray(String KEY, TypeToken typeToken) {
        try {
            ArrayList<T> arr = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
            if (arr == null) {
                return new ArrayList<>();
            }
            return arr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public <S, T> void putMap(String KEY, TreeMap<S, T> map) {
        String json = new Gson().toJson(map);
        prefs.edit().putString(KEY, json).apply();
    }

    public <S, T> TreeMap<S, T> getMap(String KEY, TypeToken typeToken) {
        try {
            TreeMap<S, T> map = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
            if (map == null) {
                return new TreeMap<>();
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new TreeMap<>();
    }
}
