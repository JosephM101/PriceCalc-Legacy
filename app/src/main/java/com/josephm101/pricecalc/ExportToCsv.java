package com.josephm101.pricecalc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportToCsv {
    // Gather all items, extract the data, and export everything to a .csv file.
    public static boolean ExportToCSV(Context context, ArrayList<DataModel> data, String DestinationFile) throws IOException {
        FileWriter fileWriter = new FileWriter(DestinationFile);
        ICSVWriter csvWriter = new CSVWriterBuilder(fileWriter).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
        String[] headerRecord = {"Item Name", "Item Price", "Quantity", "Tax Cost", "Total"};
        String[] emptyLine = {"", "", "", "", ""};
        String[] endHeader = {"", "", "", "Total:", ""};

        csvWriter.writeNext(headerRecord);

        List<String[]> list = generateCsvData(data, context);
        for (String[] item : list) {
            csvWriter.writeNext(item);
        }

        // Add total to bottom of file if the setting "csvExport_AddTotalToFile_Preference" is set to true.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean("csvExport_AddTotalToFile_Preference", false)) {
            // Empty line, followed by data
            csvWriter.writeNext(emptyLine);
            csvWriter.writeNext(endHeader);
        }

        csvWriter.close();
        fileWriter.close();
        return true;
    }

    public static ArrayList<String[]> generateCsvData(ArrayList<DataModel> data, Context context) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        for (DataModel dm : data) {
            String itemName = dm.getItemName();
            double itemPrice = Double.parseDouble(dm.getItemPrice());
            int itemQuantity = Integer.parseInt(dm.getItemQuantity());
            //boolean isItemTaxable = dm.getIsTaxable();

            double priceWithQuantity = (itemPrice * itemQuantity);
            double taxRate = PriceHandling.getDefaultTaxRatePercentage(context.getApplicationContext());
            double priceTax = ((priceWithQuantity * taxRate) / 100);
            double totalCostOverall = priceWithQuantity + priceTax;

            // Convert values to strings
            String str_itemPrice = PriceHandling.PriceToString(itemPrice);
            String str_itemQuantity = String.valueOf(itemQuantity);
            String str_priceTax = PriceHandling.PriceToString(priceTax);
            String str_totalCost = PriceHandling.PriceToString(totalCostOverall);

            list.add(new String[]{itemName, str_itemPrice, str_itemQuantity, str_priceTax, str_totalCost});
        }
        return list;
    }
}
