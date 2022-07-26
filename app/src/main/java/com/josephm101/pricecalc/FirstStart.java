package com.josephm101.pricecalc;

import com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler;

import java.io.IOException;

public class FirstStart {
    public static void DoFirstStartSetup() {
        try {
            ListFileHandler.CreateNewList(Defaults.NameOfFirstList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
