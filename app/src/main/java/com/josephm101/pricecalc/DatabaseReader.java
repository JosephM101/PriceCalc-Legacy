package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseReader extends AppCompatActivity {
    FileInputStream fis;
    private String NewLine = "\r\n";

    public DatabaseReader() {
        try {
            fis = openFileInput("saved_list");
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
    }

    public ArrayList<DataModel> GetEntries() throws IOException {
        ArrayList<DataModel> dataModels = new ArrayList<DataModel>();
        int c;
        String contents = "";
        while ((c = fis.read()) != -1) {
            contents = contents + Character.toString((char) c);
        }
        fis.close();
        Scanner scanner = new Scanner(contents);
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String[] splitString = line.split("`");
            dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue, BooleanHandling.NegativeValue), splitString[3]));
        }
        return dataModels;
    }
}
