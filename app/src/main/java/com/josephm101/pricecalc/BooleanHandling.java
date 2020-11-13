package com.josephm101.pricecalc;

public class BooleanHandling {
    public static final String PositiveValue = "Yes";
    public static String NegativeValue = "No";

    public static String BoolToString(Boolean b, String trueString, String falseString) {
        if (b) {
            return trueString;
        } else {
            return falseString;
        }
    }

    public static Boolean StringToBool(String b, String trueString) {
        return b.contains(trueString);
    }

    public static Boolean StringToBool(String b, String trueString, String falseString) throws StringDoesNotMatch {
        if (b.contains(trueString) || b.contains(falseString)) {
            return b.contains(trueString);
        } else {
            throw new StringDoesNotMatch("The string doesn't match the true or false strings specified." +
                    "Input String" + b +
                    "True String: " + trueString +
                    "False String: " + falseString);
        }
    }
}

class StringDoesNotMatch extends Exception {
    public StringDoesNotMatch(String errorMessage) {
        super(errorMessage);
    }
}