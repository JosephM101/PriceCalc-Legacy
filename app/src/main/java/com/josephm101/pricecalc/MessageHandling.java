package com.josephm101.pricecalc;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MessageHandling {
    public static void ShowMessage(Context context, String title, String message, String positiveText) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> dialog.dismiss())
                .setCancelable(true);
        materialAlertDialogBuilder.show();
    }

    public static void ShowMessage(Context context, String title, String message, String positiveText, String negativeText) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss())
                .setCancelable(true);
        materialAlertDialogBuilder.show();
    }

    public static void ShowMessage(Context context, String title, String message, String positiveText, @DrawableRes int iconID) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> dialog.dismiss())
                .setIcon(iconID)
                .setCancelable(true);
        materialAlertDialogBuilder.show();
    }
}