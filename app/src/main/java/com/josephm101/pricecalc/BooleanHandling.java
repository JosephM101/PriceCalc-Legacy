package com.josephm101.pricecalc;

public class BooleanHandling {
    public static String BoolToString(Boolean b, String trueString, String falseString) {
        if (b == true) {
            return trueString;
        } else {
            return falseString;
        }
    }
    public static Boolean StringToBool(String b, String trueString, String falseString) {
        if (b == trueString) {
            return true;
        } else {
            return false;
        }
    }
}