package com.josephm101.pricecalc;

import static com.josephm101.pricecalc.ExportToCsv.ExportToCSV;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance;
import com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("ALL")
@SuppressLint("NonConstantResourceId")

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")

    private static CustomAdapter listAdapter;

    ArrayList<DataModel> listItems = new ArrayList<>();
    ArrayList<DataModel> listItems_BKP = new ArrayList<>();

    // For undo functionality
    ArrayList<ArrayList<DataModel>> listItems_undo = new ArrayList<>();
    ArrayList<ArrayList<DataModel>> listItems_redo = new ArrayList<>();
    int maxUndoCount = 10;
    int maxRedoCount = 10;

    ListView listView;
    ExtendedFloatingActionButton addItem_FloatingActionButton;
    ProgressBar loadingProgressBar;
    TextView totalCostLabel;
    LinearLayout noItems_CardView;
    Boolean inDeleteMode = false;
    Menu referencedMenu;
    CardView cardView;
    androidx.appcompat.app.ActionBar actionBar;

    Context current = this;
    int listView_position;
    private boolean Card_Hidden;

    // Preferences
    boolean hideDock_preference;
    boolean floatingDockPreference_value;

    ListInstance listInstance;
    SharedPreferences Memory; // For remembering things :)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHandling.ApplyTheme(this.getApplicationContext()); // Apply theme
        actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        assert actionBar != null;
        super.onCreate(savedInstanceState);

        // Initialize classes
        ContextTattletale.setLocalContext(getApplicationContext()); // Store the application context; MUST be done before ListFileHandler is initialized.
        ListFileHandler.init(); // Tell ListFileHandler to initialize

        // Show the welcome screen (New, Beta)
        SharedPreferences WelcomeScreenSettings = getApplicationContext().getSharedPreferences(Preferences.WelcomeScreen.PreferenceGroup, 0);
        int wasShown = WelcomeScreenSettings.getInt(Preferences.WelcomeScreen.ENTRY_SHOWN, 0);
        if (wasShown == 0) { // The Welcome screen was never shown; possibly a first start of the app.
            Intent welcomeScreen_Intent = new Intent(this, WelcomeScreen.class);
            startActivity(welcomeScreen_Intent);
        }

        SharedPreferences FirstStartPref = getApplicationContext().getSharedPreferences("firststart", 0);
        boolean isFirstStart = FirstStartPref.getBoolean("isFirstStart", true);
        if (isFirstStart) {
            FirstStart.DoFirstStartSetup(); // Run first-start functions
            SharedPreferences.Editor e = FirstStartPref.edit();
            e.putBoolean("isFirstStart", false);
            e.apply();
        }

        // Load main view layout based on settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        floatingDockPreference_value = sharedPreferences.getBoolean("floatingDock_Preference", false);
        hideDock_preference = sharedPreferences.getBoolean("hideDock_preference", false);
        if (!hideDock_preference) {
            if (floatingDockPreference_value) {
                setContentView(R.layout.activity_main_floating_toolbar);
            } else {
                setContentView(R.layout.activity_main);
            }
        } else {
            setContentView(R.layout.activity_main);
        }
        cardView = findViewById(R.id.cardView);
        if (hideDock_preference) {
            cardView.setVisibility(View.GONE);
        }

        // AppBeta.ShowBetaMessage(this);

        totalCostLabel = findViewById(R.id.totalCost_Label);
        addItem_FloatingActionButton = findViewById(R.id.addItem_floatingActionButton);
        addItem_FloatingActionButton.setAnimateShowBeforeLayout(true);
        addItem_FloatingActionButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            Animation ani = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_action_button_scale_down_ani); //Shrink the FAB
            ani.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    addItem_FloatingActionButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent addNew = new Intent(v.getContext(), AddItem.class);
                    NewItemActivityLauncher.launch(addNew);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ani.setFillAfter(true);
            addItem_FloatingActionButton.startAnimation(ani);
        });
        listView = findViewById(R.id.items_listBox);
        // RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


        // listView.setOnItemLongClickListener(this::onItemLongClick);

        // if (floatingDockPreference_value) {
        //     cardView.setOnTouchListener(new View.OnTouchListener() {
        //         @Override
        //         public boolean onTouch(View v, MotionEvent event) {
        //             if (event.getAction() == MotionEvent.AXIS_VSCROLL) {
        //                 MainCardView_ChangeShowStatus(false);
        //             }
        //             return true;
        //         }
        //     });
        // }

        loadingProgressBar = findViewById(R.id.progressBar2);
        loadingProgressBar.setVisibility(View.GONE);
        noItems_CardView = findViewById(R.id.noItems_View);
        noItems_CardView.setVisibility(View.GONE);

        registerForContextMenu(listView);

        // Figure out if we should (and can) restore the list that was open before the app was last closed
        Memory = getApplicationContext().getSharedPreferences(Preferences.General.PreferenceGroup, 0);
        if (sharedPreferences.getBoolean("loadRecentListOnLaunch_Preference", false)) {
            String lastUuid = Memory.getString(Preferences.General.LastUuid_Pref, "");
            if (!lastUuid.isEmpty()) {
                // Check if this UUID exists
                try {
                    if (ListFileHandler.GetListUUIDs().contains(UUID.fromString(lastUuid))) {
                        Load(lastUuid);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ShowListPickerDialog();
                }
            } else {
                ShowListPickerDialog();
            }
        } else {
            ShowListPickerDialog();
        }

        // RefreshView();
        // Cleanup();
    }

    void ShowListPickerDialog() {
        // Show dialog
        Intent openListActivity = new Intent(this, OpenList_Activity.class);
        OpenListActivityLauncher.launch(openListActivity);
    }

    void Load(String uuid) {
        // Try and load list from UUID
        try {
            // Intent i = getIntent();
            // UUID uuid = UUID.fromString(i.getStringExtra("uuid"));
            // listInstance = ListFileHandler.getListInstanceFromUUID(uuid);

            listInstance = ListFileHandler.getListInstanceFromUUID(UUID.fromString(uuid));
            listItems = listInstance.ListItems;
            actionBar.setSubtitle(listInstance.getListFriendlyName());

            // Attach list adapter to new instance
            listAdapter = new CustomAdapter(listItems, MainActivity.this);
            listAdapter.setNotifyOnChange(true);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                listView_position = position;
                DataModel dataModel = listItems.get(listView_position);
                Intent itemInfo = new Intent(this, ItemInfo.class);
                itemInfo.putExtra("dataModel", dataModel);
                ItemInfoActivityLauncher.launch(itemInfo);
            });

            // Save list UUID to "last UUID" preference
            SharedPreferences.Editor editor = Memory.edit();
            editor.putString(Preferences.General.LastUuid_Pref, uuid);
            editor.apply();

            RefreshEverything(false);
            RefreshView();

        } catch (Exception e) {
            Log.e("M_LOAD-LIST", "onCreate: Failed to load list: " + e.getMessage());
            e.printStackTrace();
            MaterialAlertDialogBuilder errorAlertDialog = new MaterialAlertDialogBuilder(current, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                    .setTitle("Error loading list")
                    .setMessage("Something happened, and we couldn't open the list.")
                    .setIcon(R.drawable.ic_baseline_error_24)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false);
            errorAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ShowListPickerDialog();
                }
            });
            errorAlertDialog.show();
        }
    }

    void UnloadList() {
        actionBar = getSupportActionBar();
        actionBar.setSubtitle("");

        // Disconnect instance
        listInstance = null;

        // Clear all lists & reset all views
        listAdapter.clear();
        listAdapter.notifyDataSetChanged();
        listItems.clear();
        listItems_BKP.clear();

        SharedPreferences.Editor editor = Memory.edit();
        editor.clear(); // Reset
        editor.apply(); // Save changes

        ShowListPickerDialog();
    }

    final ActivityResultLauncher<Intent> OpenListActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = null;
                        if (data != null) {
                            extras = data.getExtras();
                        }
                        if (extras != null) {
                            Load(extras.getString("uuid"));
                        } else {
                            TerminatePremature("\"Extras\" is null");
                        }
                    } else {
                        finishAffinity();
                    }
                    // TerminatePremature("Result code was something other than \"RESULT_OK\"");
                }
            });

    void TerminatePremature(String e) {
        Log.e("PrematureTerminate", e);
        finish();
    }

    final ActivityResultLauncher<Intent> NewItemActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    loadingProgressBar.setVisibility(View.GONE);
                    Animation ani = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_action_button_scale_up_ani);
                    ani.setFillAfter(true);
                    addItem_FloatingActionButton.startAnimation(ani);
                    addItem_FloatingActionButton.setEnabled(true);

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = null;
                        if (data != null) {
                            extras = data.getExtras();
                        }
                        if (extras != null) {
                            AddEntry(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"), extras.getString("itemUrl"), extras.getString("itemNotes"), true);
                        }
                    }
                }
            });

    final ActivityResultLauncher<Intent> EditItemActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = null;
                        if (data != null) {
                            extras = data.getExtras();
                        }
                        if (extras != null) {
                            // Build the entry
                            DataModel item = new DataModel(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"), extras.getString("itemUrl"), extras.getString("itemNotes"));
                            listAdapter.remove(listAdapter.getItem(listView_position)); // Delete the old entry
                            listAdapter.insert(item, listView_position); // Insert the new entry
                            RefreshEverything(true);
                        }
                    }
                }
            });

    final ActivityResultLauncher<Intent> ItemInfoActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = null;
                        if (data != null) {
                            extras = data.getExtras();
                        }
                        if (extras != null) {
                            // Build the entry
                            DataModel item = (DataModel) extras.getSerializable("newEntry");
                            listAdapter.remove(listAdapter.getItem(listView_position)); // Delete the old entry
                            listAdapter.insert(item, listView_position); // Insert the new entry
                            RefreshEverything(true);
                        }
                    }
                }
            });

    final ActivityResultLauncher<Intent> ExportCSV_SaveFileActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = result.getData().getData();
                        try {
                            File outputDir = getApplicationContext().getCacheDir();
                            File outputFile = File.createTempFile("export-", ".csv", outputDir);
                            String path = outputFile.getPath();
                            String fName = outputFile.getName();
                            ExportToCSV(current, listItems, path);

                            InputStream is = new FileInputStream(outputFile);
                            OutputStream os = getContentResolver().openOutputStream(uri);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                            os.close();
                            is.close();
                        } catch (IOException e) {
                            MaterialAlertDialogBuilder errorAlertDialog = new MaterialAlertDialogBuilder(current, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                                    .setTitle("Error exporting")
                                    .setMessage("Something happened, and we couldn't export the list.")
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setCancelable(false);
                            errorAlertDialog.show();
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Deprecated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        referencedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshView_menuItem:
                RefreshEverything(false); // Don't save; we're just refreshing
                Toaster.pop(this, "Refreshed", 200);
                break;
            case R.id.settings_menuItem:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.clearList_menuItem:
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Clear the entire list?")
                        .setMessage("Are you sure you want to clear the entire list? This cannot be undone!")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("Yes, clear it", (dialog, which) -> ClearAll())
                        .setNegativeButton("No, don't!", (dialog, which) -> dialog.dismiss())
                        .setCancelable(false);
                materialAlertDialogBuilder.show();
                break;
            case R.id.openTipCalculator_menuItem:
                Intent tipCalc_Intent = new Intent(this, TipCalculator.class);
                startActivity(tipCalc_Intent);
                break;
            case R.id.aboutApplication_menuItem:
                Intent aboutBox_Intent = new Intent(this, About.class);
                startActivity(aboutBox_Intent);
                break;
            case R.id.newEntry_menuItem:
                Intent addNew = new Intent(this, AddItem.class);
                NewItemActivityLauncher.launch(addNew);
                break;
            case R.id.confirmDeletion_menuItem:
                LeaveDeleteMode(this);
                break;
            case R.id.cancelDeletion_menuItem:
                LeaveDeleteMode(this);
                break;
            case R.id.exportToCsv_menuItem:
                try {
                    Intent saveDocument = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    saveDocument.addCategory(Intent.CATEGORY_OPENABLE);
                    saveDocument.setType("text/csv");
                    saveDocument.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
                    String tempFileName = sdf.format(new Date()) + ".csv";
                    saveDocument.putExtra(Intent.EXTRA_TITLE, tempFileName);
                    ExportCSV_SaveFileActivityResultLauncher.launch(saveDocument);
                } catch (Exception e) {
                    MaterialAlertDialogBuilder errorAlertDialog = new MaterialAlertDialogBuilder(current, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Error exporting")
                            .setMessage("Something happened, and we couldn't export the list.")
                            .setIcon(R.drawable.ic_baseline_error_24)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(false);
                    errorAlertDialog.show();
                    e.printStackTrace();
                }
                break;
            case R.id.shareList_menuItem:
                try {
                    File outputDir = getApplicationContext().getCacheDir();
                    File outputFile = File.createTempFile("export-", ".csv", outputDir);
                    String path = outputFile.getPath();
                    String fName = outputFile.getName();
                    ExportToCSV(current, listItems, path);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/csv");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, fName);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, fName);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, outputFile.getPath());
                    startActivity(Intent.createChooser(shareIntent, "Share"));
                } catch (IOException e) {
                    MaterialAlertDialogBuilder errorAlertDialog = new MaterialAlertDialogBuilder(current, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Error sharing")
                            .setMessage("Something happened, and we couldn't export the list.")
                            .setIcon(R.drawable.ic_baseline_error_24)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(false);
                    errorAlertDialog.show();
                    e.printStackTrace();
                }
                break;
            case R.id.undo_menuItem:
                ListItems_Undo();
                break;
            case R.id.openList_menuItem:
                ShowListPickerDialog();
                break;
            case R.id.renameList_menuItem:
                RenameList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
//            Uri uri = data.getData();
//            try {
//                OutputStream output = getContext().getContentResolver().openOutputStream(uri);
//
//                output.write(SOME_CONTENT.getBytes());
//                output.flush();
//                output.close();
//            }
//            catch(IOException e) {
//                Toast.makeText(context, "Couldn't open the file.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    void RenameList() {
        Intent RenameList_TextPrompt = new Intent(this, TextPromptDialog.class);
        RenameList_TextPrompt.putExtra("title", "Rename list");
        RenameList_TextPrompt.putExtra("hint", "Enter list name");
        RenameList_TextPrompt.putExtra("text", listInstance.getListFriendlyName());
        RenameListActivityLauncher.launch(RenameList_TextPrompt);
    }

    final ActivityResultLauncher<Intent> RenameListActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = null;
                    if (data != null) {
                        extras = data.getExtras();
                    }
                    if (extras != null) {
                        try {
                            listInstance.setListFriendlyName(extras.getString("text"));
                            RefreshEverything(true);
                            RefreshView();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

    void GetTotalCostFromList() {
        double TotalCost;
        double costsWithDeductible = 0;
        double costsWithoutDeductible = 0;
        for (DataModel item : listItems) {
            double totalForItem = Double.parseDouble(item.getItemPrice()) * Integer.parseInt(item.itemQuantity);
            if (item.getIsTaxable()) {
                costsWithDeductible += totalForItem;
            } else {
                costsWithoutDeductible += totalForItem;
            }
            //Log.d("MATH_LOG", "Is taxable:" + BooleanHandling.BoolToString(item.getIsTaxable(), "Yes", "No"));
        }
        TotalCost = costsWithoutDeductible + PriceHandling.calculatePriceDouble(costsWithDeductible, PriceHandling.getDefaultTaxRatePercentage(this));
        SetDashText(PriceHandling.PriceToString(TotalCost));
    }

    void AddEntry(String itemName, String itemPrice, Boolean isTaxDeductible, String quantity, String itemUrl, String itemNotes, @SuppressWarnings("SameParameterValue") Boolean CommitChanges) {
        //listItems.add(new DataModel(itemName, itemPrice, isTaxDeductible, quantity));
        DataModel dataModel = new DataModel(itemName, itemPrice, isTaxDeductible, quantity, itemUrl, itemNotes);
        listAdapter.add(dataModel);
        RefreshEverything(CommitChanges);
    }

    void AddEntry(DataModel dataModel, @SuppressWarnings("SameParameterValue") Boolean CommitChanges) {
        listAdapter.add(dataModel);
        RefreshEverything(CommitChanges);
    }

    void RefreshEverything(Boolean saveList) {
        listAdapter.notifyDataSetChanged();
        try {
            GetTotalCostFromList();
        } catch (Exception ex) {
            SetDashText("Error");
        }
        if (saveList) {
            try {
                // Save list instance
                listInstance.ListItems = listItems;
                ListFileHandler.SaveList(listInstance);
            } catch (IOException e) {
                SetDashText("Save Error");
            }
        }
        RefreshView();
        Log.i("VIEW_REFRESH", "View refreshed, " + BooleanHandling.BoolToString(saveList, "changes saved.", "file untouched."));
    }

    void SetDashText(String text) {
        if (hideDock_preference) {
            actionBar.setSubtitle(text);
        } else {
            totalCostLabel.setText(text);
        }
    }

    /*
    // Loads list items into list view
    void LoadList() {
        try {
            // ArrayList<DataModel> dataModels = ListFileHandler.GetEntries(savedList_FileName);
            for (int i = 0; i < dataModels.size(); i++) {
                AddEntry(dataModels.get(i), false);
            }
        } catch (IOException ignored) {

        }
        RefreshEverything(false);
    }
     */

    String getSavedItemID(int index) {
        return "entry" + index;
    }

    // DataModel ProcessEntryInfo(Set<String> stringSet) {
    //     /*
    //      * Entry Order:
    //      * 0. Item Name
    //      * 1. Item Price
    //      * 2. Is item taxable? (Bool as String)
    //      * 3. Item Quantity
    //      * 4. Item URL
    //      * 5. Item notes
    //      */
    //     String[] stringArray = (String[]) stringSet.toArray();
    //     return new DataModel(stringArray[0], stringArray[1], BooleanHandling.StringToBool(stringArray[2], BooleanHandling.PositiveValue), stringArray[3], stringArray[4], stringArray[5]);
    // }

    // Empty the list
    public void ClearAll() {
        listAdapter.clear();
        listAdapter.notifyDataSetChanged();
        RefreshEverything(true);
    }

    public void RefreshView() {
        if (listAdapter.getCount() < 1) {
            noItems_CardView.setVisibility(View.VISIBLE);
        } else {
            noItems_CardView.setVisibility(View.GONE);
        }

        if (listInstance != null) {
            actionBar = getSupportActionBar();
            actionBar.setSubtitle(listInstance.getListFriendlyName());
        } else {
            actionBar = getSupportActionBar();
            actionBar.setSubtitle("");
        }
    }

    private boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        listView_position = position;
        openOptionsMenu();
        return true;
    }

    enum CardShowStatus {
        HIDE,
        SHOW
    }

    void MainCardView_ChangeShowStatus(Boolean status) {
        if (floatingDockPreference_value) {
            if (!status) {
                if (Card_Hidden == false) { //Make sure it's not already hidden.
                    Card_Hidden = true;
                    AnimateCardOut();
                }
            } else {
                if (Card_Hidden == true) {
                    Card_Hidden = false;
                    AnimateCardIn();
                }
            }
        }
    }

    void RunItemDeletionProcedure() {
        final DataModel dataModel = listItems.get(listView_position);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                .setMessage("Are you sure you want to permanently delete this entry? \r\n" + dataModel.getItemName())
                .setTitle("Delete Entry?")
                .setIcon(R.drawable.ic_baseline_delete_forever_24)
                .setPositiveButton("Yes, delete it.", (dialog, which) -> {
                    // Backup... just in case :)
                    listItems_BKP.clear();
                    for (DataModel item : listItems) {
                        listItems_BKP.add(item);
                    }
                    listItems.remove(listView_position);
                    // ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
                    CardView cardView = findViewById(R.id.cardView);
                    View layout = findViewById(R.id.mainConstraintLayout);
                    // Show snackbar
                    {
                        ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Entry deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", v1 -> {
                                    listItems.clear();
                                    for (DataModel item : listItems_BKP) {
                                        listItems.add(item);
                                    }
                                    RefreshEverything(true);
                                    // Snackbar snackbarUndone = Snackbar.make(coordinatorLayout, "Action undone", Snackbar.LENGTH_SHORT)
                                    //         .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                    // snackbarUndone.show();
                                })
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                        snackbar.show();
                    }
                    RefreshEverything(true);
                })
                .setNegativeButton("No, don't!", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);
        materialAlertDialogBuilder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.items_listBox) {
            ContextMenu.ContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            listView_position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            DataModel dataModel = listItems.get(listView_position);
            MenuInflater inflater = getMenuInflater();
            menu.setHeaderTitle("Item properties");

            String itemUrl = dataModel.getItemUrl();
            if(!itemUrl.isEmpty())
            {
                try {
                    URL url = new URL(itemUrl);
                    inflater.inflate(R.menu.menu_itemsmenu_url, menu); // URL is valid
                } catch (MalformedURLException e) {
                    inflater.inflate(R.menu.menu_itemsmenu, menu); // URL is not valid; inflate default context list
                }
            }
            else
            {
                inflater.inflate(R.menu.menu_itemsmenu, menu); // No URL; inflate default context list
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DataModel dataModel = listItems.get(listView_position);
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent editItem = new Intent(this, AddItem.class);
                editItem.putExtra("dataModel", dataModel);
                EditItemActivityLauncher.launch(editItem);
                return true;
            case R.id.delete_item:
                RunItemDeletionProcedure();
                return true;
            case R.id.open_item_url:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataModel.getItemUrl()));
                startActivity(browserIntent);
                return true;
            case R.id.copy_item_url:
                String url = dataModel.getItemUrl(); // Get URL
                ClipboardManager systemClipboard; // Create clipboard manager
                systemClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE); // Establish connection with system clipboard
                // ClipData item_url = ClipData.newRawUri(url, Uri.parse(url)); // Set URI as clip data
                ClipData item_url = ClipData.newPlainText(url, url);
                systemClipboard.setPrimaryClip(item_url); // Set new clip data as primary clip
            default:
                return super.onContextItemSelected(item);
        }
    }

    void ShowDismissableSnackbar(String text) {
        ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
        Snackbar dismissable_snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        dismissable_snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissable_snackbar.dismiss();
            }
        });
        dismissable_snackbar.show();
    }

    void Cleanup() {
        try {
            ContextWrapper c = new ContextWrapper(getApplicationContext());
            String pathRoot = c.getCacheDir().toString();
            c.deleteFile(pathRoot + "PriceCalc_update.apk");
        } catch (Exception ignored) {

        }
    }

    // Animations
    public void scaleView(@NotNull View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }

    void AnimateFloatingActionButton_ScaleUp(Context context, @NotNull ExtendedFloatingActionButton fab) {
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.floating_action_button_scale_up_ani);
        ani.setFillAfter(true);
        fab.startAnimation(ani);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    void AnimateFloatingActionButton_ScaleDown(Context context, ExtendedFloatingActionButton fab) {
        Animation ani = AnimationUtils.loadAnimation(context, R.anim.floating_action_button_scale_down_ani);
        ani.setFillAfter(true);
        fab.startAnimation(ani);
    }

    /**
     * Deprecated component scaling method (inefficient & buggy; replaced by simpler AnimateCard methods)
     */
/*    void AnimateCardIn(int duration) {
        CardView cardView = findViewById(R.id.cardView);
        Rect rectf = new Rect();
        cardView.getGlobalVisibleRect(rectf);
        float target = rectf.bottom - rectf.height();
        ObjectAnimator animation = ObjectAnimator.ofFloat(cardView, View.Y, target);
        animation.setDuration(duration);
        animation.start();
    }

    void AnimateCardOut(int duration) {
        CardView cardView = findViewById(R.id.cardView);
        Rect rectf = new Rect();
        cardView.getGlobalVisibleRect(rectf);
        float target = rectf.bottom + rectf.height();
        cardView.setPadding(0, 64, 0, 0);
        ObjectAnimator animation = ObjectAnimator.ofFloat(cardView, View.Y, target);
        animation.setDuration(duration);
        animation.start();
    }*/

    void AnimateCardIn() {
        Animation cardAni = AnimationUtils.loadAnimation(this, R.anim.card_show_ani);
        cardAni.setFillAfter(true);
        //CardView cardView = findViewById(R.id.cardView);
        cardView.startAnimation(cardAni);
        Animation fabAni = AnimationUtils.loadAnimation(this, R.anim.floating_action_button_scale_up_full_ani);
        fabAni.setFillAfter(true);
        addItem_FloatingActionButton.startAnimation(fabAni);
    }

    void AnimateCardOut() {
        Animation cardAni = AnimationUtils.loadAnimation(this, R.anim.card_hide_ani);
        cardAni.setFillAfter(true);
        //CardView cardView = findViewById(R.id.cardView);
        cardView.startAnimation(cardAni);
        Animation fabAni = AnimationUtils.loadAnimation(this, R.anim.floating_action_button_scale_down_full_ani);
        fabAni.setFillAfter(true);
        addItem_FloatingActionButton.startAnimation(fabAni);
    }

    void EnterDeleteMode(Context context) {
        CardView cardView = findViewById(R.id.cardView);
        Animation cardAni = AnimationUtils.loadAnimation(context, R.anim.card_hide_ani);
        cardAni.setFillAfter(true);
        cardView.startAnimation(cardAni);
        //AnimateCardOut(1000);
        setTitle("Delete items");
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); //Allow multiple items to be selected & deleted.
        referencedMenu.clear();
        getMenuInflater().inflate(R.menu.menu_main_deleting_items, referencedMenu);
        inDeleteMode = true;
    }

    void LeaveDeleteMode(Context context) {
        Animation cardAni = AnimationUtils.loadAnimation(context, R.anim.card_show_ani);
        cardAni.setFillAfter(true);
        CardView cardView = findViewById(R.id.cardView);
        cardView.startAnimation(cardAni);

        //AnimateCardIn(1000);
        setTitle(getString(R.string.app_name)); //Reset the menu bar title.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //Go back to only allowing a single item.
        referencedMenu.clear();
        getMenuInflater().inflate(R.menu.menu_main, referencedMenu);
        inDeleteMode = false;
    }

    Boolean ListItems_CanUndo() {
        // Check if we can undo
        if (listItems_undo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    Boolean ListItems_CanRedo() {
        // Check if we can redo
        if (listItems_redo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    Boolean ListItems_Undo() {
        if (ListItems_CanUndo()) {
            int index = listItems_undo.size() - 1;
            // Add the item to undo to the redo list
            listItems_redo.add(listItems_undo.get(index));
            // Assign last index of undo to listItems array
            listItems = listItems_undo.get(index);
            // Remove last index of undo from undo array
            listItems_undo.remove(index);
            return true;
        } else {
            return false;
        }
    }

    Boolean ListItems_Redo() {
        if (ListItems_CanRedo()) {
            int index = listItems_redo.size() - 1;
            // Add the item to redo to the undo list
            listItems_undo.add(listItems_redo.get(index));
            // Assign last index of redo to listItems array
            listItems = listItems_redo.get(index);
            // Remove last index of redo from redo array
            listItems_redo.remove(index);
            ClipRedoCollection();
            ClipUndoCollection();
            return true;
        } else {
            return false;
        }
    }

    void SaveChangesToUndoCollection() {
        // Save the current listItems to the undo array
        listItems_undo.add(listItems);
        ClipUndoCollection();
        // Clear the redo array
        listItems_redo.clear();
    }

    void ClipUndoCollection() {
        // Check if the undo collection is greater than the undo limit
        while (listItems_undo.size() > maxUndoCount) {
            // Remove the first item in the collection
            listItems_undo.remove(0);
        }
    }

    void ClipRedoCollection() {
        // Check if the redo collection is greater than the redo limit
        while (listItems_redo.size() > maxRedoCount) {
            // Remove the first item in the collection
            listItems_redo.remove(0);
        }
    }
}