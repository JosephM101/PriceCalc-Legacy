package com.josephm101.pricecalc.UpdateHandler.API;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class GitHub {

/*    public static class GitHubUpdateListener implements RetrievalListener {
        @Override
        public void onRetrievalComplete(ReleaseInfo releaseInfo) {

        }

        @Override
        public void onRetrievalError(String request) {

        }
    }*/

    String userName; //Required for link generation.
    String repoName; //Required for link generation.
    String apiCall_prefix = "https://api.github.com/repos/";
    String apiCall_suffix = "releases/latest";
    String apiCall_url_separator = "/";
    private JsonElement response;

    public interface RetrievalListener {
        void onRetrievalComplete(ReleaseInfo releaseInfo);
        void onRetrievalError(String request);
    }

    private RetrievalListener listener;

    public GitHub(String user_name, String repo_name, RetrievalListener listener) {
        userName = user_name; //Required for link generation
        repoName = repo_name; //Required for link generation
        this.listener = listener;
    }

    public GitHub(String user_name, String repo_name) {
        userName = user_name; //Required for link generation
        repoName = repo_name; //Required for link generation
        this.listener = null;
    }

    public void GetData(){
        Thread thread = new Thread(() -> {
            try {
                String url = generateApiCallURL();
                Log.d("RESPONSE", url);
                response = GetJsonResponse(generateApiCallURL());
                JsonObject root = response.getAsJsonObject();
                Thread.sleep(2000); //Sleep for a quick second before continuing to make sure all the data comes in.
                if (!(root.isJsonNull())) { //If the JsonObject 'root' is not null, then we can keep going.
                    ReleaseInfo releaseInfo = new ReleaseInfo(); //Create new instance of class ReleaseInfo to return when we're done.
                    releaseInfo.setDownloadUrl(root.getAsJsonArray("assets").get(0).getAsJsonObject().get("browser_download_url").getAsString()); //Get the download URL for the first available asset (most likely the executable package)
                    releaseInfo.setReleaseNotes(root.get("body").getAsString()); //
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
                    Log.d("GIT1", releaseInfo.getReleaseName());
                    Log.d("GIT2", releaseInfo.getDownloadUrl());
                    Log.d("GIT_V", releaseInfo.getReleaseVersion());
                    if (listener != null)
                        listener.onRetrievalComplete(releaseInfo); //Everything worked. Return the final class to the listener.
                } else {
                    if (listener != null)
                        listener.onRetrievalError(generateApiCallURL()); //Something happened; issue an error
                }
            }
            catch (Exception ex) //We ran into an exception; issue an error, and throw the information out.
            {
                if (listener != null)
                    listener.onRetrievalError(generateApiCallURL());
                ex.printStackTrace();
            }
        });
        thread.start();
    }

    private JsonElement GetJsonResponse(String url_to_request_from) throws IOException { //Gets JSON response from the URL we generated.
        URL url = new URL(url_to_request_from);
        URLConnection request = url.openConnection();
        request.connect();
        return JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
    }

    private String generateApiCallURL() { //Generates request URL from the user's GitHub username and the repository name. Example: https://api.github.com/repos/JosephM101/PriceCalc/releases/latest
        return apiCall_prefix +
                userName +
                apiCall_url_separator +
                repoName +
                apiCall_url_separator +
                apiCall_suffix;
    }

    public GitHub setGitHubUpdateListener(RetrievalListener listener) {
        this.listener = listener;
        return this;
    }
}