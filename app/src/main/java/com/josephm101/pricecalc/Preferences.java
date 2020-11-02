package com.josephm101.pricecalc;

//Extra preferences for other parts of the app

public class Preferences {
    public static void SetPreference(String preferenceString, int value) {

    }

    public static class BetaMode {
        public static String PreferenceGroup = "BETA_MODE_MESSAGE";
        public static String ENTRY_BETA_MODE_MSG_PREF = "doNotShowAgain";
    }

    public static class DefaultValues {
        public static String DefaultTaxRate = "6.25";
        public static String DefaultTheme = "1";
    }

}