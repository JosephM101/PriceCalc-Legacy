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

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class CheckForUpdates extends AppCompatActivity {
    public static final int progress_bar_type = 0;
    private ProgressDialog pDialog;
    String urlToPing = "https://api.github.com/repos/JosephM101/PriceCalc/releases/latest"; //Returns JSON summary of latest package

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_updates);
        setTitle("Updates");
        CardView loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        CardView cardView_updateInfo = findViewById(R.id.cardView_updateInfo);
        CardView cardView_noUpdates = findViewById(R.id.cardView_noUpdates);
        CardView cardView_updateError = findViewById(R.id.cardView_updateError);

        loading_CardView.setVisibility(View.VISIBLE);
        cardView_updateInfo.setVisibility(View.GONE);
        cardView_noUpdates.setVisibility(View.GONE);
        cardView_updateError.setVisibility(View.GONE);

        /**
         * Initialize the updater
         */
        AppUpdaterUtils appUpdater = new AppUpdaterUtils(getApplicationContext());
        //AppUpdater appUpdater = new AppUpdater(getApplicationContext());
        //appUpdater.setButtonUpdate("Update");
        //appUpdater.setButtonDismiss("Not now");
        //appUpdater.setButtonDoNotShowAgain("Not interested at all.");
        //appUpdater.setTitleOnUpdateAvailable("Update available");
        //appUpdater.setCancelable(false);
        //appUpdater.setTitleOnUpdateNotAvailable("No new updates.");
        //appUpdater.setContentOnUpdateNotAvailable("Nothing to do here! You're running the latest and greatest version.");
        //appUpdater.setDisplay(Display.DIALOG);
        appUpdater.withListener(new AppUpdaterUtils.UpdateListener() {
            @Override
            public void onSuccess(Update update, Boolean isUpdateAvailable) {
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
            }

            @Override
            public void onFailed(AppUpdaterError error) {
                loading_CardView.setVisibility(View.GONE);
                setTitle("Couldn't update.");
                cardView_updateError.setVisibility(View.VISIBLE);
                Button button_dismiss = findViewById(R.id.button_dismissFailedDialog);
                button_dismiss.setOnClickListener(v -> finish());
            }
        });
        appUpdater.start();
        appUpdater.setGitHubUserAndRepo("JosephM101", "PriceCalc");
        appUpdater.setUpdateFrom(UpdateFrom.GITHUB);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a typical 0-100%
                // progress bar
                int lengthOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream("pricecalc_update.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();
            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.parse("pricecalc_update.apk"),
                            "application/vnd.android.package-archive");
            startActivity(promptInstall);
        }
    }
}