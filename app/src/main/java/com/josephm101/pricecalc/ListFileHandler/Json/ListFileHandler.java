package com.josephm101.pricecalc.ListFileHandler.Json;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.josephm101.pricecalc.ContextTattletale;
import com.josephm101.pricecalc.DataModel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import com.google.gson.Gson;
import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance;


// Class to handle storing/loading lists in JSON format
public final class ListFileHandler {
    private static String filename;
    private static Gson gson;
    private static JsonParser parser;

    private static String rootPath;
    private static final String jsonFolder = "/lists";
    private static Path fullJsonFolderPath;
    private static File fullJsonFolderPathAsFile;

    private static String ext = ".json";
    private static final FilenameFilter jsonFilenameFilter = (file, s) -> {
        if (s.toLowerCase().endsWith(ext)) return true;
        else return false;
    };

    private static final Context localContext = ContextTattletale.getLocalContext();
    private static boolean Initialized = false;

    public static void init() {
        if(!Initialized) {
            Log.i("ListFileHandler", "Init");
            gson = new Gson(); // Initialize Gson

            // Resolve JSON file directory
            // Log.i("LFH_init", "Building path");
            rootPath = localContext.getFilesDir().getParent();
            fullJsonFolderPath = java.nio.file.Paths.get(rootPath, jsonFolder);
            fullJsonFolderPathAsFile = fullJsonFolderPath.toFile();
            // Log.i("LFH_init", "Done building path");
            // Log.i("LFH_init", fullJsonFolderPath.toString());
            // Log.i("LFH_init", fullJsonFolderPathAsFile.getAbsolutePath());
            fullJsonFolderPathAsFile.mkdir();
            Log.i("ListFileHandler", "Init done");

            // Run migration functions, just in case
            MigrationHelper.MigrateFromText();

            Initialized = true;
        } else {
            Log.i("ListFileHandler", "Already initialized");
        }
    }

    // Get JSON files in JSON directory
    public static ArrayList<File> GetListFiles() {
        ArrayList<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(fullJsonFolderPathAsFile.listFiles(jsonFilenameFilter))) {
            files.add(file);
        }
        return files;
    }

    // Get the UUIDs of existing lists
    public static ArrayList<UUID> GetListUUIDs() {
        ArrayList<File> files = GetListFiles();
        ArrayList<UUID> uuids = new ArrayList<>();
        for (File f : files) {
            String u = f.getName().replace(".json", "");
            uuids.add(UUID.fromString(u));
            Log.i("UUID", u);
        }
        return uuids;
    }

    public static File ResolveJsonFilename(UUID uuid) {
        return java.nio.file.Paths.get(fullJsonFolderPath.toString(), uuid.toString() + ".json").toFile(); // <root>/<uuid>.json
    }

    // Update list data in master file from ListInstance.
    public static void SaveList(ListInstance list) throws IOException {
        SaveList(list.getUuid(), list.getListFriendlyName(), list.ListItems);
    }

    public static void SaveList(UUID uuid, String friendlyName, ArrayList<DataModel> data) throws IOException {
        File f = ResolveJsonFilename(uuid);
        if (f.exists()) {
            f.delete();
            f.createNewFile();
        }
        JsonWriter jsonWriter = new JsonWriter(new FileWriter(f));
        gson.toJson(new ListInstance(uuid, friendlyName, data), ListInstance.class, jsonWriter);
        jsonWriter.close();
    }

    public static ListInstance getListInstanceFromUUID(UUID uuid) throws IOException {
        File f = ResolveJsonFilename(uuid);
        if (f.exists()) {
            JsonReader jsonReader = new JsonReader(new FileReader(f));
            ListInstance i = gson.fromJson(jsonReader, ListInstance.class);
            jsonReader.close();
            return i;
        }
        return null;
    }

    private static UUID generateUuid() {
        while (true) {
            UUID newUuid = java.util.UUID.randomUUID();
            if (!ResolveJsonFilename(newUuid).exists()) {
                return newUuid; // We found a valid UUID
            }
        }
    }

    public static UUID CreateNewList(String friendlyName) throws IOException {
        UUID newUuid = generateUuid();
        ArrayList<DataModel> newDataModel = new ArrayList<>();
        ListInstance listInstance = new ListInstance(newUuid, friendlyName, newDataModel);
        /// TODO: Add code to create list file with new instance
        SaveList(listInstance);
        return newUuid;
    }
}