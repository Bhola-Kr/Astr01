package com.astro4callapp.astro4call.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class PreferenceManager {


    public SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void pustBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {

        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

//    public void putLongVal(String key, Long value) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong(key, value);
//        editor.apply();
//    }


    public void putFloatVal(String key, Float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }


    public Float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0);
    }

//    public Long getLong(String key) {
//        return sharedPreferences.getLong(key, 0);
//    }




//    public void update(String key, Long val){
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong( key, val );
//        editor.commit();
//    }

    public void updateFloat(String key, Float val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat( key, val );
        editor.commit();
        //editor.apply();
    }
}
