package com.josephm101.pricecalc;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    public static void pop(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void pop(Context context, String text, int ToastLength) {
        Toast.makeText(context, text, ToastLength).show();
    }
}