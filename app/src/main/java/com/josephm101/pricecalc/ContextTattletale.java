package com.josephm101.pricecalc;

import android.content.Context;

// This class gets context data, and then makes it available to all classes.
public final class ContextTattletale {
    private static Context localContext;
    public static Context getLocalContext() {
        return localContext;
    }
    public static void setLocalContext(Context localContext) {
        ContextTattletale.localContext = localContext;
    }
}
