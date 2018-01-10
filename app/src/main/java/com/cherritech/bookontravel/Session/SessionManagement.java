package com.cherritech.bookontravel.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    // Shared Preferences
    private SharedPreferences pref;

    //Editor for SharedPreferences pref
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // SharedPreferences file name
    private static final String PREF_NAME = "GPS Alarm";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // commit changes
        editor.commit();
        }

    /**
     * Create login session
     * */
    public void updateLoginSession(){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // commit changes
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }
}
