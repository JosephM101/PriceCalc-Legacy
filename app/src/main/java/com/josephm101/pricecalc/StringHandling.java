package com.josephm101.pricecalc;

public class StringHandling {
    public static String combineStrings(String main, String prefix) {
        return prefix +
                main;
    }

    public static String combineStrings(String main, String prefix, String suffix) {
        return prefix +
                main +
                suffix;
    }
}
