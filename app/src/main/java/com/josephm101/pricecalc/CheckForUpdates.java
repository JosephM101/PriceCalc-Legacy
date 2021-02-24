package com.josephm101.pricecalc;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.josephm101.pricecalc.Update.API.GitHub;
import com.josephm101.pricecalc.Update.API.ReleaseInfo;

public class CheckForUpdates extends AppCompatActivity {
    CardView cardView_updateError;
    CardView cardView_noUpdates;
    CardView cardView_updateInfo;
    CardView loading_CardView;
    GitHub release_repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_updates);
        setTheme(android.R.style.Theme_DeviceDefault_DayNight);
        setTitle("Updates");
        loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        cardView_updateInfo = findViewById(R.id.cardView_updateInfo);
        cardView_noUpdates = findViewById(R.id.cardView_noUpdates);
        cardView_updateError = findViewById(R.id.cardView_updateError);

        loading_CardView.setVisibility(View.VISIBLE);
        cardView_updateInfo.setVisibility(View.GONE);
        cardView_noUpdates.setVisibility(View.GONE);
        cardView_updateError.setVisibility(View.GONE);
        release_repo = new GitHub("JosephM101", "PriceCalc", new GitHub.RetrievalListener() {
            @Override
            public void onRetrievalComplete(ReleaseInfo releaseInfo) {
                runOnUiThread(() -> {
                    loading_CardView.setVisibility(View.GONE);
                    if (releaseInfo != null) {
                        if (releaseInfo.getReleaseVersion() != BuildConfig.VERSION_NAME) {
                            cardView_updateInfo.setVisibility(View.GONE);
                            setTitle("Update available.");
                            TextView currentVersionTextView = findViewById(R.id.textView_currentVersion);
                            TextView newVersionTextView = findViewById(R.id.textView_newVersion);
                            TextView changelogTextView = findViewById(R.id.textView_changelog);
                            currentVersionTextView.setText(BuildConfig.VERSION_NAME);
                            newVersionTextView.setText(releaseInfo.getReleaseVersion());
                            changelogTextView.setText(releaseInfo.getReleaseNotes());
                            Button button_updateNow = findViewById(R.id.button_confirmUpdate);
                            button_updateNow.setOnClickListener(v -> {
                                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri update_link = Uri.parse(releaseInfo.getDownloadUrl());
                                DownloadManager.Request request = new DownloadManager.Request(update_link);
                                request.setTitle("PriceCalc");
                                request.setDescription("Downloading update for PriceCalc...");
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationUri(Uri.parse("file://" + getApplicationContext().getFilesDir() + "/pricecalc_update.apk"));
                                downloadManager.enqueue(request);
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
                    } else {
                        //runOnUiThread(() -> Error());
                        Error();
                    }
                });
            }

            @Override
            public void onRetrievalError(String request) {
                runOnUiThread(() -> Error());
            }
        });
        release_repo.GetData();
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