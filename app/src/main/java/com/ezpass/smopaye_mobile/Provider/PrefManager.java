package com.ezpass.smopaye_mobile.Provider;

import android.content.Context;
import android.content.SharedPreferences;

import com.ezpass.smopaye_mobile.RemoteFragments.APIService;

/**
 * permet de définir la structure des sharedpreferences qui seront utilisés dans toute l'application
 *
 * @see PrefManager
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "pref_mobile";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setBoolean(String PREF_NAME, Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }
    public void setString(String PREF_NAME, String VAL) {
        editor.putString(PREF_NAME, VAL);
        editor.commit();
    }
    public void setInt(String PREF_NAME, int VAL) {
        editor.putInt(PREF_NAME, VAL);
        editor.commit();
    }
    public boolean getBoolean(String PREF_NAME) {
        return pref.getBoolean(PREF_NAME,true);
    }
    public void remove(String PREF_NAME){
        if(pref.contains(PREF_NAME)){
            editor.remove(PREF_NAME);
            editor.commit();
        }
    }
    public String getString(String PREF_NAME) {
        if(pref.contains(PREF_NAME)){
            return pref.getString(PREF_NAME,null);
        }
        return  "";
    }

    public int getInt(String key) {
        return pref.getInt(key,0);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
