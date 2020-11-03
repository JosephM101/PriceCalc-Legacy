package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DatabaseWriter extends AppCompatActivity {
    StringBuilder stringBuilder;
    File logFile;
    BufferedWriter writer;
    private String NewLine = "\r\n";
    private String splitChar = "`";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DatabaseWriter() throws IOException {
        //stringBuilder = new StringBuilder();
        logFile = new File(getFilesDir().getParent(), "saved_list.txt");
        logFile.createNewFile();
        try {
            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void close() {
        try {
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void AddEntry(DataModel dataModel) throws IOException {
        writer.write(dataModel.getItemName());
        writer.append(splitChar);
        writer.append(dataModel.getItemPrice());
        writer.append(splitChar);
        writer.append(BooleanHandling.BoolToString(dataModel.getIsTaxable(), BooleanHandling.PositiveValue, BooleanHandling.NegativeValue));
        writer.append(splitChar);
        writer.append(BooleanHandling.BoolToString(dataModel.getIsTaxable(), BooleanHandling.PositiveValue, BooleanHandling.NegativeValue));
        writer.newLine();
    }

    private void AppendNewLine() {
        stringBuilder.append(NewLine);
    }
}