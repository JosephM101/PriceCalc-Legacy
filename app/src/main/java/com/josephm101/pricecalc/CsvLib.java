package com.josephm101.pricecalc;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CsvLib {
    public static boolean ExportToCSV (Context context, ArrayList<DataModel> data, String DestinationFile) throws IOException {
        FileWriter fileWriter = new FileWriter(DestinationFile);
        ICSVWriter csvWriter = new CSVWriterBuilder(fileWriter).withSeparator('`').build();
        String[] headerRecord = {"ItemName", "ItemPrice", "Quantity", "TaxCost", "Total"};
        csvWriter.writeNext(headerRecord);

        List<String[]> list = generateCsvData(data, context);
        for (String[] item : list) {
            csvWriter.writeNext(item);
        }

        csvWriter.close();
        return true;
    }

    // Convert data from ArrayList<DataModel> to CSV data
    static List<String[]> generateCsvData(ArrayList<DataModel> data, Context context) {
        List<String[]> list = new List<String[]>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String[]> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(String[] strings) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String[]> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends String[]> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public String[] get(int i) {
                return new String[0];
            }

            @Override
            public String[] set(int i, String[] strings) {
                return new String[0];
            }

            @Override
            public void add(int i, String[] strings) {

            }

            @Override
            public String[] remove(int i) {
                return new String[0];
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<String[]> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<String[]> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<String[]> subList(int i, int i1) {
                return null;
            }
        };
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

            list.add(new String[] {itemName, str_itemPrice, str_itemQuantity, str_priceTax, str_totalCost});
        }
        return list;
    }
}
