package com.example.baustelle;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalSettings{
    private final SharedPreferences appSharedPrefs;

    public LocalSettings(Context context){
        this.appSharedPrefs = context.getSharedPreferences("com.example.baustelle",MODE_PRIVATE);
    }

    public String getValue_string(String stringKeyValue) {
        return appSharedPrefs.getString(stringKeyValue, "");
    }

    public void setValue_string(String stringKeyValue, String _stringValue) {
        appSharedPrefs.edit().putString(stringKeyValue, _stringValue).commit();
    }

}
