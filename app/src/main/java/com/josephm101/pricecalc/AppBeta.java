package com.josephm101.pricecalc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class AppBeta {

    public static void ShowBetaMessage(final Context context) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences(Preferences.BetaMode.PreferenceGroup, 0);
        int showMessage = settings.getInt(Preferences.BetaMode.ENTRY_BETA_MODE_MSG_PREF, 0);
        if (showMessage != 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Beta Mode")
                    .setMessage("This app is still currently in beta, and is (at the moment) intended for testing purposes only. As a result, issues such as instability, inconsistencies, and occasional bugs will be present throughout the application.\r\n \r\nYou have been warned.")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("OK, Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Continue, and don't show this again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences settings = context.getApplicationContext().getSharedPreferences(Preferences.BetaMode.PreferenceGroup, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(Preferences.BetaMode.ENTRY_BETA_MODE_MSG_PREF, 1);
                            editor.apply();
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true);
            alertDialog.show();
        }
    }
}