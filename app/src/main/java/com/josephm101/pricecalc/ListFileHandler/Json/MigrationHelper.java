package com.josephm101.pricecalc.ListFileHandler.Json;

import android.content.Context;
import android.util.Log;

import com.josephm101.pricecalc.ContextTattletale;
import com.josephm101.pricecalc.DataModel;
import com.josephm101.pricecalc.Defaults;
import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance;
import com.josephm101.pricecalc.ListFileHandler.ListFileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MigrationHelper {
    private static String MigratedName = "Default (migrated)";
    private static String LogName = "MigrationHelper";

    public static void MigrateFromText() {
        l("Checking if anything can be migrated...");
        String AppData_RootPath = ContextTattletale.getLocalContext().getFilesDir().getParent();
        File old = java.nio.file.Paths.get(AppData_RootPath, Defaults.primarySavedListFileName).toFile();
        if (old.exists()) {
            l("Found text-based list");
            l("Migrating to JSON...");
            try {
                ArrayList<DataModel> data = ListFileHandler.GetEntries(old.getPath());
                UUID newList = com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler.CreateNewList(MigratedName);
                ListInstance instance = com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler.getListInstanceFromUUID(newList);
                instance.ListItems = data;
                com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler.SaveList(instance);
                old.delete();
                l("Migration complete!");
            } catch (IOException e) {
                e.printStackTrace();
                e("Failed to migrate.");
            }
        }
    }

    private static void l(String message) {
        Log.i(LogName, message);
    }
    private static void e(String message) {
        Log.e(LogName, message);
    }
}