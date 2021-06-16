package com.josephm101.pricecalc;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.request.DownloadRequest;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.josephm101.pricecalc.UpdateHandler.API.GitHub;
import com.josephm101.pricecalc.UpdateHandler.API.ReleaseInfo;
import com.josephm101.pricecalc.UpdateHandler.Version.VersionCompare;
import com.josephm101.pricecalc.UpdateHandler.Version.VersionInfo;
import com.josephm101.pricecalc.UpdateHandler.Version.VersionParser;

import java.io.File;
import java.nio.file.Paths;

public class CheckForUpdates extends AppCompatActivity {
    CardView cardView_updateError;
    CardView cardView_noUpdates;
    CardView cardView_updateInfo;
    CardView loading_CardView;
    CardView updating_CardView;
    GitHub release_repo;
    Context context;
    private Error error_info;
    //long downloadID;
    //DownloadManager downloadManager;
    //private Context mContext;
    //private DownloadManager.Request downloadRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(false)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        setContentView(R.layout.activity_check_for_updates);
        setTheme(android.R.style.Theme_DeviceDefault_DayNight);
        setTitle("Updates");
        loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        updating_CardView = findViewById(R.id.cardView_updatingNow);
        cardView_updateInfo = findViewById(R.id.cardView_updateInfo);
        cardView_noUpdates = findViewById(R.id.cardView_noUpdates);
        cardView_updateError = findViewById(R.id.cardView_updateError);

        loading_CardView.setVisibility(View.VISIBLE);
        updating_CardView.setVisibility(View.GONE);
        cardView_updateInfo.setVisibility(View.GONE);
        cardView_noUpdates.setVisibility(View.GONE);
        cardView_updateError.setVisibility(View.GONE);
        //downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        release_repo = new GitHub("JosephM101", "PriceCalc", new GitHub.RetrievalListener() {
            @Override
            public void onRetrievalComplete(ReleaseInfo releaseInfo) {
                runOnUiThread(() -> {
                    loading_CardView.setVisibility(View.GONE);
                    VersionInfo releaseVersionInfo = VersionParser.parse(releaseInfo.getReleaseVersion());
                    VersionInfo currentVersionInfo = VersionParser.parse(BuildConfig.VERSION_NAME);
                    Log.d("CurrentVersion", currentVersionInfo.getOriginalVersionString());
                    Log.d("RetrievedVersion", releaseVersionInfo.getOriginalVersionString());
                    if (releaseInfo != null) {
                        //if (releaseInfo.getReleaseVersion() != BuildConfig.VERSION_NAME) {
                        VersionCompare.VersionComparison comparison = VersionCompare.CompareVersions(currentVersionInfo, releaseVersionInfo);
                        if (comparison == VersionCompare.VersionComparison.VERSION_NEWER) {
                            setTitle("Update available.");
                            TextView currentVersionTextView = findViewById(R.id.textView_currentVersion);
                            TextView newVersionTextView = findViewById(R.id.textView_newVersion);
                            TextView changelogTextView = findViewById(R.id.textView_changelog);
                            currentVersionTextView.setText(BuildConfig.VERSION_NAME);
                            newVersionTextView.setText(releaseInfo.getReleaseVersion());
                            changelogTextView.setText(releaseInfo.getReleaseNotes());
                            Button button_updateNow = findViewById(R.id.button_confirmUpdate);
                            button_updateNow.setOnClickListener(v -> {
                                setTitle("Updating...");
                                loading_CardView.setVisibility(View.GONE);
                                updating_CardView.setVisibility(View.VISIBLE);
                                cardView_updateInfo.setVisibility(View.GONE);
                                cardView_noUpdates.setVisibility(View.GONE);
                                cardView_updateError.setVisibility(View.GONE);
                                CircularProgressIndicator circularProgressIndicator = findViewById(R.id.progressBarUpdating);
                                TextView updateStatusTextView = findViewById(R.id.textView_currentUpdateStatus);
                                String linkToRelease = releaseInfo.getDownloadUrl();
                                Log.d("PATH", Paths.get(getFilesDir().getPath(), "PriceCalc_update.apk").toString());
                                ContextWrapper c = new ContextWrapper(getApplicationContext());
                                String pathRoot = c.getFilesDir().toString();
                                DownloadRequest request = PRDownloader.download(linkToRelease, pathRoot, "PriceCalc_update.apk")
                                        .build()
                                        .setOnStartOrResumeListener(() -> {
                                            updateStatusTextView.setText("Downloading update...");
                                            circularProgressIndicator.setIndeterminate(false);
                                        })
                                        .setOnPauseListener(() -> {

                                        })
                                        .setOnCancelListener(() -> {

                                        })
                                        .setOnProgressListener(progress -> {
                                            int percentage = (int) QuickMath.Companion.map(progress.currentBytes, 0, progress.totalBytes, 0, 100);
                                            circularProgressIndicator.setProgress(percentage, false);
                                        });
                                long download_id = request.start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        Thread thread = new Thread(() -> {
                                            try {
                                                Thread.sleep(1000); //Give things a chance to catch up
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            runOnUiThread(() -> {
                                                updateStatusTextView.setText("Installing update...");
                                                circularProgressIndicator.hide();
                                                circularProgressIndicator.setIndeterminate(true);
                                                circularProgressIndicator.show();
                                            });
                                            try {
                                                Thread.sleep(1000); //Give things a chance to catch up
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            runOnUiThread(() -> {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                String path = Paths.get(pathRoot, request.getFileName()).toString();
                                                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(path));
                                                Log.d("PackageManager", fileUri.getPath());
                                                //intent.setDataAndType(Uri.fromFile(new File(getFilesDir().getParent() + "/PriceCalc_update.apk")), "application/vnd.android.package-archive");
                                                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            });
                                        });
                                        thread.start();
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        error_info = error;
                                        Error();
                                    }
                                });
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

    void Error() {
        loading_CardView.setVisibility(View.GONE);
        setTitle("Couldn't update.");
        TextView updateErrorCode = findViewById(R.id.updater_errorCodeTextView);
        try {
            if (error_info.isConnectionError()) {
                updateErrorCode.setText("Connection error.");
            } else {
                String errorString = "HTTP: " +
                        error_info.getResponseCode() +
                        "\n" +
                        "\n" +
                        "Server message: " +
                        error_info.getServerErrorMessage();
                updateErrorCode.setText(errorString);
            }
        } catch (Exception ex) {
            updateErrorCode.setText("Error unknown");
        }
        cardView_updateError.setVisibility(View.VISIBLE);
        Button button_dismiss = findViewById(R.id.button_dismissFailedDialog);
        button_dismiss.setOnClickListener(v -> finish());
    }
}