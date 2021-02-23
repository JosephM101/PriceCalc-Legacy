package com.josephm101.pricecalc;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/*
Shows information about the app (app version, author, etc.)

<-- MAY NOT BE IMPLEMENTED -->
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
        setTheme(android.R.style.Theme_Material_Dialog);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            //Get info
            version = BuildConfig.VERSION_NAME;
            buildType = BuildConfig.BUILD_TYPE;
            fullPackageName = BuildConfig.APPLICATION_ID;
            packageName = pInfo.applicationInfo.name + " " + BuildConfig.VERSION_CODE;
            revision = String.valueOf(pInfo.baseRevisionCode);
            //Print info
            TextView packageName_textView = findViewById(R.id.packageName_textView);
            TextView fullPackageName_textView = findViewById(R.id.fullPackageName_textView);
            TextView appVersion_textView = findViewById(R.id.appVersion_textView);

            packageName_textView.setText(packageName);
            fullPackageName_textView.setText(fullPackageName);
/*
              String appVersionString = version +
                    " (" +
                    buildType +
                    " build)";
*/
            String appVersionString = version +
                    "-" +
                    buildType;

            appVersion_textView.setText(appVersionString);
            Button dismissButton = findViewById(R.id.aboutActivity_dismissButton);
            dismissButton.setOnClickListener(v -> finish());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            finish();
        }
    }
}