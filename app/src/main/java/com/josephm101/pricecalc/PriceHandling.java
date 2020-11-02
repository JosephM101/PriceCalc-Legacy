package com.josephm101.pricecalc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PriceHandling {
    private static final DecimalFormat priceFormat = new DecimalFormat("0.00");

    public static double getDefaultTaxRatePercentage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String value = sharedPreferences.getString("taxRate_Preference", Preferences.DefaultValues.DefaultTaxRate); //Default tax rate for U.S.
        return Double.parseDouble(value);
    }

    public static String calculatePrice(double itemCost, double taxRatePercentage) {
        double taxPrice = getTaxCost(itemCost, taxRatePercentage);
        double totalCost = itemCost + taxPrice;
        return PriceToString(totalCost);
    }

    public static Double calculatePriceDouble(double itemCost, double taxRatePercentage) {
        double taxPrice = getTaxCost(itemCost, taxRatePercentage);
        return itemCost + taxPrice;
    }

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