// For text-based list files

package com.josephm101.pricecalc.ListFileHandler;

import android.util.Log;

import com.josephm101.pricecalc.BooleanHandling;
import com.josephm101.pricecalc.DataModel;
import com.josephm101.pricecalc.StringHandling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ListFileHandler {
    private static final String NewLineSeparator = "`";
    private static final String splitChar = "§";

    // Retrieve entries from save file

    /*
    NOTE:
    First line of save file contains info about the list such as its name.
    The second line contains our actual save data.
     */
    public static ArrayList<DataModel> GetEntries(String savedFileName) throws IOException {
        String LoggerName = "ListFileParser";
        ArrayList<DataModel> dataModels = new ArrayList<>();
        File logFile = new File(savedFileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
        StringBuilder everything = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            everything.append(line);
            Log.d("READ_FILE", line);
        }
        Log.d(LoggerName, StringHandling.combineStrings(everything.toString(), "Full String: "));
        String[] lines = everything.toString().split(NewLineSeparator);
        Log.d(LoggerName, StringHandling.combineStrings(String.valueOf(lines.length), "Lines: "));
        for (String nextLine : lines) {
            try {
                Log.d(LoggerName, nextLine);
                String[] splitString = nextLine.split(splitChar);
                Log.d(LoggerName, "---LINE CONTENTS---");
                Log.d(LoggerName, StringHandling.combineStrings("", splitString[0]));
                Log.d(LoggerName, StringHandling.combineStrings("", splitString[1]));
                Log.d(LoggerName, StringHandling.combineStrings("", splitString[2]));
                Log.d(LoggerName, StringHandling.combineStrings("", splitString[3]));
                dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue), splitString[3]));
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("LOAD_FILE", "Error parsing string/line in file. It may be empty.");
            }
        }
        Log.d(LoggerName, StringHandling.combineStrings(String.valueOf(dataModels.size()), "DataModel Final Size: "));
        return dataModels;
    }

    //Overwrites any information still saved, and writes in current list items.
    public static void SaveList(String fileName, ArrayList<DataModel> dataModel) throws IOException {
        // ArrayList<DataModel> dataModel = adapter.getDataSet();
        File logFile = new File(fileName);
        try {
            final boolean delete = logFile.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final boolean newFile = logFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false));
        Log.d("DataModelSize", String.valueOf(dataModel.size()));
        for (int i = 0; i < dataModel.size(); i++) {
            try {
                writer.write(GenerateEntry(dataModel.get(i)));
                // writer.newLine();
                if (i < dataModel.size()) {
                    writer.append(NewLineSeparator);
                }
                Log.d("SaveFileWriter", "Wrote Line " + i + ": " + GenerateEntry(dataModel.get(i)));
            } catch (Exception ex) {
                // throw ex;
                ex.printStackTrace();
            }
        }
        writer.close();
    }

    private static String GenerateEntry(@org.jetbrains.annotations.NotNull DataModel dataModel) {
        return dataModel.getItemName() +
                splitChar +
                dataModel.getItemPrice() +
                splitChar +
                BooleanHandling.BoolToString(dataModel.getIsTaxable(), "Yes", "No") +
                splitChar +
                dataModel.getItemQuantity();
    }
}
