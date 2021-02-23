package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import org.w3c.dom.Text;

public class CheckForUpdates extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_updates);
        setTitle("Updates");
        CardView loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        CardView cardView_updateInfo = findViewById(R.id.cardView_updateInfo);
        CardView cardView_noUpdates = findViewById(R.id.cardView_noUpdates);

        loading_CardView.setVisibility(View.VISIBLE);
        cardView_updateInfo.setVisibility(View.GONE);
        cardView_noUpdates.setVisibility(View.GONE);

        /**
         * Initialize the updater
         */
        AppUpdaterUtils appUpdater = new AppUpdaterUtils(this);
        //AppUpdater appUpdater = new AppUpdater(getApplicationContext());
        appUpdater.setGitHubUserAndRepo("JosephM101", "PriceCalc");
        appUpdater.setUpdateFrom(UpdateFrom.GITHUB);
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
                    cardView_updateInfo.setVisibility(View.VISIBLE);
                } else {
                    setTitle("No new updates.");
                    cardView_noUpdates.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(AppUpdaterError error) {

            }
        });
        appUpdater.start();
    }
}