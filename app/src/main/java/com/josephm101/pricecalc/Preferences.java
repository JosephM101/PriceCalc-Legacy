package com.josephm101.pricecalc;

//Extra preferences for other parts of the app

public class Preferences {
    public static class MainSettings {
        public static String PreferenceGroup = "SAVED";
        public static String ItemCount = "LISTVIEW_ITEMS";
    }

    public static class BetaMode {
        public static final String PreferenceGroup = "BETA_MODE_MESSAGE";
        public static final String ENTRY_BETA_MODE_MSG_PREF = "doNotShowAgain";
    }

    public static class WelcomeScreen {
        public static final String PreferenceGroup = "WELCOME_SCREEN";
        public static final String ENTRY_SHOWN = "shown";
    }

    public static class DefaultValues {
        public static final String DefaultTaxRate = "6.25";
        public static final String DefaultTheme = "1";
    }

    public static class General {
        public static final String PreferenceGroup = "GENERAL";
        public static final String LastUuid_Pref = "LAST_UUID";
    }
}