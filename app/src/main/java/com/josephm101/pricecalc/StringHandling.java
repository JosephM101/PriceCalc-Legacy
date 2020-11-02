package com.josephm101.pricecalc;

public class StringHandling {
    public static String combineStrings(String main, String prefix)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(main);
        return stringBuilder.toString();
    }
    public static String combineStrings(String main, String prefix, String suffix)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(main);
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
