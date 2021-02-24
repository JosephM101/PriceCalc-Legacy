package com.josephm101.pricecalc.API;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class GitHub {
    String userName;
    String repoName;
    String apiCall_prefix = "https://api.github.com/repos/";
    String apiCall_suffix = "releases/latest";
    String apiCall_url_separator = "/";
    private JsonElement response;

    public GitHub(String user_name, String repo_name) {
        userName = user_name;
        repoName = repo_name;
        //Make the API call.
        try {
            ReleaseInfo rInfo = GetData();
            Log.d("GIT1", rInfo.getReleaseName());
            Log.d("GIT2", rInfo.getDownloadUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReleaseInfo GetData() throws IOException {
        response = GetJsonResponse(generateApiCallURL());
        JsonObject root = response.getAsJsonObject();
        if (!(root.isJsonNull())) {
            ReleaseInfo releaseInfo = new ReleaseInfo();
            releaseInfo.setDownloadUrl(root.getAsJsonArray("assets").get(0).getAsJsonObject().get("browser_download_url").getAsString());
            releaseInfo.setReleaseNotes(root.get("body").getAsString());
            releaseInfo.setReleaseName(root.get("name").getAsString());
            releaseInfo.setReleaseVersion(root.get("tag_name").getAsString());
            String utcString = root.get("created_at").getAsString();
            releaseInfo.setUploadDate_raw(utcString);
            Log.d("UTC", utcString);
            Instant timestamp = Instant.parse(utcString);
            ZonedDateTime currentTime = timestamp.atZone(TimeZone.getDefault().toZoneId());
            Date finalDate = Date.from(currentTime.toInstant());
            Log.d("Local", currentTime.toString());
            releaseInfo.setUploadDate(finalDate);
            releaseInfo.setUploadDateString(currentTime.toString());
            return releaseInfo;
        } else {
            return null;
        }
    }

    private JsonElement GetJsonResponse(String url_to_request_from) throws IOException {
        URL url = new URL(url_to_request_from);
        URLConnection request = url.openConnection();
        request.connect();
        return JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
    }

    private String generateApiCallURL() {
        return apiCall_prefix +
                userName +
                apiCall_url_separator +
                repoName +
                apiCall_url_separator +
                apiCall_suffix;
    }
}