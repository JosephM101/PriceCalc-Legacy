package com.josephm101.pricecalc;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AppBeta {
    public static void ShowBetaMessage(final Context context) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences(Preferences.BetaMode.PreferenceGroup, 0);
        int showMessage = settings.getInt(Preferences.BetaMode.ENTRY_BETA_MODE_MSG_PREF, 0);
        if (showMessage != 1) {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Beta Mode")
                    .setMessage("This app is still currently in beta. As a result, issues such as instability, inconsistencies, and occasional bugs will be present throughout the application.\r\n \r\nYou have been warned.")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("OK, Continue", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Continue, and don't show again", (dialog, which) -> {
                        SharedPreferences settings1 = context.getApplicationContext().getSharedPreferences(Preferences.BetaMode.PreferenceGroup, 0);
                        SharedPreferences.Editor editor = settings1.edit();
                        editor.putInt(Preferences.BetaMode.ENTRY_BETA_MODE_MSG_PREF, 1);
                        editor.apply();
                        dialog.dismiss();
                    })
                    .setCancelable(true);
            materialAlertDialogBuilder.show();
        }
    }
}