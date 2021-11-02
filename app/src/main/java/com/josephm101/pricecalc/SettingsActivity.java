package com.josephm101.pricecalc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

@SuppressWarnings("ALL")
public class SettingsActivity extends AppCompatActivity {
    Context thisContext;
    SharedPreferences prefs;

    @SuppressWarnings("EmptyTryBlock")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Settings");
        thisContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //MessageHandling.ShowMessage(thisContext, "Restart required", "The app will need to be restarted for changes to take effect.", "OK", R.drawable.ic_baseline_info_24);
        SharedPreferences.OnSharedPreferenceChangeListener prefListener = (prefs1, key) -> {
            switch (key) {
                case "appTheme_Preference":
                    Log.i("Settings", "Theme changed");
                    RefreshTheme();
                    break;
                case "floatingDock_Preference":
                    //noinspection EmptyTryBlock,EmptyTryBlock
                    try {
                        //MessageHandling.ShowMessage(thisContext, "Restart required", "The app will need to be restarted for changes to take effect.", "OK", R.drawable.ic_baseline_info_24);
                        //Toaster.pop(thisContext, "App restart required.");
                    } catch (Exception ignored) {
                    }
                    break;
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    void RefreshTheme() {
        ThemeHandling.ApplyTheme(this.getApplicationContext());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            androidx.preference.EditTextPreference editTextPreference = getPreferenceManager().findPreference("taxRate_Preference");
            assert editTextPreference != null;
            editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
        }
    }
}