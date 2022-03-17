package com.josephm101.pricecalc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;

public final class PriceHandling {
    private static final DecimalFormat priceFormat = new DecimalFormat("0.00");

    // Return the default tax rate as defined in the app preferences. Not recommended for use, but a starter for a fallback value when the defined tax rate is potentially invalid.
    public static double getDefaultTaxRatePercentage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String value = sharedPreferences.getString("taxRate_Preference", Preferences.DefaultValues.DefaultTaxRate); // Default tax rate for U.S.
        return Double.parseDouble(value);
    }

    // Calculate the price using the tax rate, and return the final result as a double
    public static Double calculatePriceDouble(double itemCost, double taxRatePercentage) {
        double taxPrice = getTaxCost(itemCost, taxRatePercentage);
        return itemCost + taxPrice;
    }

    // Calculate the price using the tax rate, and return the final result as a double
    // Same as the above method, but factors in quantity
    public static Double calculatePriceDouble(double itemCost, double taxRatePercentage, int quantity) {
        double newCost = itemCost * quantity; // Multiply the cost by the quantity
        double taxPrice = getTaxCost(newCost, taxRatePercentage); // Calculate the tax
        return newCost + taxPrice; // Add tax to newCost, and return
    }

    // Calculate the price using the tax rate, and return the final result as a formatted string
    public static String calculatePrice(double itemCost, double taxRatePercentage) {
        // double taxPrice = getTaxCost(itemCost, taxRatePercentage);
        // double totalCost = itemCost + taxPrice;
        return PriceToString(calculatePriceDouble(itemCost, taxRatePercentage));
    }

    // Calculate the price using the tax rate, and return the final result as a formatted string
    public static String calculatePrice(double itemCost, double taxRatePercentage, int quantity) {
        double newCost = itemCost * quantity;
        double taxPrice = getTaxCost(newCost, taxRatePercentage);
        double totalCost = newCost + taxPrice;
        return PriceToString(totalCost);
    }

    public static String PriceToString(double price) {
//        String stringBuilder = "$" +
//                priceFormat.format(price);
//        return stringBuilder;

        return "$" +
                priceFormat.format(price);
    }

    public static double getTaxCost(double cost, double taxRate) {
        //return (double) ((taxRate * cost) / 100);
        return (taxRate * cost) / 100;
    }

    public static double calculateTip(double cost, int percentage) {
        return (percentage / 100) * cost;
    }
/*
    public static double calculateSplitTip(double cost, int percentage) {
        return (percentage / 100) * cost;
    }
 */
}

/* Formula for Item Cost + Tax
Multiply item cost by tax rate percentage (Example: 6.25%), then divide that by 100.

Example formula:
Item cost: $62.99
Tax Rate: 6.25%
Find tax cost: multiply 62.99 by 6.25, then divide by 100.

----------------------
    62.99 * 6.25
    ————————————  = 3.94 (rounded to the nearest hundredth)
        100
----------------------

Now add 3.94 to the item's net price ($62.99), and you have your total cost.

    62.99 + 3.94 = 66.93

Total cost after tax: $66.93
 */