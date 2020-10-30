package com.josephm101.pricecalc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AppBeta {
    public static void ShowBetaMessage(Context context)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle("Beta Mode")
                .setMessage("This app is still currently in beta. As a result, bugs or instabilities may (and will) be present throughout the app. \r\nYou have been warned.")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("OK, Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);
        alertDialog.show();
    }
}
