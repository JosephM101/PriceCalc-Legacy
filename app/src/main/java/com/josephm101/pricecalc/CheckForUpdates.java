package com.josephm101.pricecalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

public class CheckForUpdates extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_updates);
        setTitle("Updates");
        CardView loading_CardView = findViewById(R.id.cardView_checkingForUpdates);
        CardView updateSummary_cardView = findViewById(R.id.cardView_updateInfo);
        loading_CardView.setVisibility(View.VISIBLE);
        loading_CardView.setVisibility(View.GONE);
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
                loading_CardView.setVisibility(View.VISIBLE);
                if (isUpdateAvailable) {
                    setTitle("Update available.");
                }
            }

            @Override
            public void onFailed(AppUpdaterError error) {

            }
        });
        appUpdater.start();
    }
}