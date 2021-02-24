package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.MemoryFile;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.josephm101.pricecalc.API.GitHub;
import com.josephm101.pricecalc.API.ReleaseInfo;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class CheckForUpdates extends AppCompatActivity {
    public static final int progress_bar_type = 0;
    private ProgressDialog pDialog;
    String urlToPing = "https://api.github.com/repos/JosephM101/PriceCalc/releases/latest"; //Returns JSON summary of latest package
    JsonElement response;
    CardView cardView_updateError;
    CardView cardView_noUpdates;
    CardView cardView_updateInfo;
    CardView loading_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_updates);
        setTitle("Updates");
        loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        cardView_updateInfo = findViewById(R.id.cardView_updateInfo);
        cardView_noUpdates = findViewById(R.id.cardView_noUpdates);
        cardView_updateError = findViewById(R.id.cardView_updateError);

        loading_CardView.setVisibility(View.VISIBLE);
        cardView_updateInfo.setVisibility(View.GONE);
        cardView_noUpdates.setVisibility(View.GONE);
        cardView_updateError.setVisibility(View.GONE);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GitHub release_repo = new GitHub("JosephM101", "PriceCalc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    /*    void LoadingComplete(String version) {
            loading_CardView.setVisibility(View.GONE);
            if (isUpdateAvailable) {
                setTitle("Update available.");
                TextView currentVersionTextView = findViewById(R.id.textView_currentVersion);
                TextView newVersionTextView = findViewById(R.id.textView_newVersion);
                TextView changelogTextView = findViewById(R.id.textView_changelog);
                currentVersionTextView.setText(BuildConfig.VERSION_NAME);
                newVersionTextView.setText(update.getLatestVersion());
                changelogTextView.setText(update.getReleaseNotes());
                Button button_updateNow = findViewById(R.id.button_confirmUpdate);
                button_updateNow.setOnClickListener(v -> {
                    //DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    //Uri update_link = Uri.parse(update.getUrlToDownload().toString());
                    //DownloadManager.Request request = new DownloadManager.Request(update_link);
                    //request.setTitle("PriceCalc");
                    //request.setDescription("Downloading update for PriceCalc...");
                    //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //request.setVisibleInDownloadsUi(false);
                    //request.setDestinationUri(Uri.parse("file://" + getApplicationContext().getFilesDir() + "/pricecalc_update.apk"));
                    pDialog = new ProgressDialog(getApplicationContext());
                    pDialog.setMessage("Downloading update...");
                    pDialog.setIndeterminate(false);
                    pDialog.setMax(100);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    new DownloadFileFromURL().execute(update.getUrlToDownload().toString());
                });
                Button button_dismiss = findViewById(R.id.button_updateLater);
                button_dismiss.setOnClickListener(v -> finish());
                cardView_updateInfo.setVisibility(View.VISIBLE);
            } else {
                setTitle("No new updates.");
                cardView_noUpdates.setVisibility(View.VISIBLE);
                Button button_dismiss = findViewById(R.id.button_dismissUpdateDialog);
                button_dismiss.setOnClickListener(v -> finish());
            }

            public void onFailed(AppUpdaterError error) {
                    loading_CardView.setVisibility(View.GONE);
                    setTitle("Couldn't update.");
                    cardView_updateError.setVisibility(View.VISIBLE);
                    Button button_dismiss = findViewById(R.id.button_dismissFailedDialog);
                    button_dismiss.setOnClickListener(v -> finish());
                }
     */
    void Error() {
        loading_CardView.setVisibility(View.GONE);
        setTitle("Couldn't update.");
        cardView_updateError.setVisibility(View.VISIBLE);
        Button button_dismiss = findViewById(R.id.button_dismissFailedDialog);
        button_dismiss.setOnClickListener(v -> finish());
    }
}