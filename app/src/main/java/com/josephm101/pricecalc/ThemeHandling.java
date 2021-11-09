package com.josephm101.pricecalc;

import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeHandling {
    public static void ApplyTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String value = sharedPreferences.getString("appTheme_Preference", Preferences.DefaultValues.DefaultTheme);

        switch (value) {
            case "1":
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                Log.i("THEME", "Switched to MODE_NIGHT_FOLLOW_SYSTEM");
                break;
            case "2":
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.i("THEME", "Switched to MODE_NIGHT_NO");
                break;
            case "3":
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.i("THEME", "Switched to MODE_NIGHT_YES");
                break;
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}