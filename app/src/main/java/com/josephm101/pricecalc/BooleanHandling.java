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

    public static String BoolToString(Boolean b, String trueString, String falseString, String suffix) {
        if (b) {
            return trueString + suffix;
        } else {
            return falseString + suffix;
        }
    }

    public static String BoolToString(Boolean b, String trueString, String falseString, String prefix, String suffix) {
        if (b) {
            return prefix + trueString + suffix;
        } else {
            return prefix + falseString + suffix;
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

    public static class StringDoesNotMatch extends Exception {
        public StringDoesNotMatch(String errorMessage) {
            super(errorMessage);
        }
    }
}