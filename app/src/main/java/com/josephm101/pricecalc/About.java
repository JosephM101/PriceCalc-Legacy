package com.josephm101.pricecalc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
Shows information about the app (app version, author, etc.)
 */

public class About extends AppCompatActivity {
    String version;
    String packageName;
    String fullPackageName;
    String revision;
    String buildType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_window);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTheme(android.R.style.Theme_DeviceDefault_DayNight);
        }
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            //Get info
            version = BuildConfig.VERSION_NAME;
            buildType = BuildConfig.BUILD_TYPE;
            fullPackageName = BuildConfig.APPLICATION_ID;
            packageName = getString(R.string.app_name); //+ String.valueOf(BuildConfig.VERSION_CODE);
            //Print info
            TextView packageName_textView = findViewById(R.id.packageName_textView);
            TextView fullPackageName_textView = findViewById(R.id.fullPackageName_textView);
            TextView appVersion_textView = findViewById(R.id.appVersion_textView);

            packageName_textView.setText(packageName);
            fullPackageName_textView.setText(fullPackageName);

            //String appVersionString = version +
            //        "-" +
            //        buildType;

            String appVersionString = version;

            appVersion_textView.setText(appVersionString);
            Button dismissButton = findViewById(R.id.aboutActivity_dismissButton);
            dismissButton.setOnClickListener(v -> finish());
            Button aboutActivity_checkForUpdatesButton = findViewById(R.id.aboutActivity_checkForUpdatesButton);
            aboutActivity_checkForUpdatesButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CheckForUpdates.class);
                startActivity(intent);
                finish();
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            finish();
        }
    }
}