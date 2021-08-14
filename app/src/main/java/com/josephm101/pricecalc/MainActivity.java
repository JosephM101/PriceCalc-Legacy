package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
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

//For Auto-update checker

//import com.downloader.BuildConfig;
//import com.josephm101.pricecalc.UpdateHandler.API.GitHub;
//import com.josephm101.pricecalc.UpdateHandler.API.ReleaseInfo;
//import com.josephm101.pricecalc.UpdateHandler.Version.VersionCompare;
//import com.josephm101.pricecalc.UpdateHandler.Version.VersionInfo;
//import com.josephm101.pricecalc.UpdateHandler.Version.VersionParser;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("ALL")
@SuppressLint("NonConstantResourceId")

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter adapter;
    ArrayList<DataModel> listItems = new ArrayList<>();
    ArrayList<DataModel> listItems_BKP = new ArrayList<>();
    private final String NewLineSeparator = "`";
    private final String splitChar = "§";
    ListView listView;
    ExtendedFloatingActionButton addItem_FloatingActionButton;
    ProgressBar loadingProgressBar;
    TextView totalCostLabel;
    LinearLayout noItems_CardView;
    Boolean inDeleteMode = false;
    Menu referencedMenu;
    CardView cardView;
    androidx.appcompat.app.ActionBar actionBar;
    Boolean isFromFile = false;
    int listView_position;

    //Preferences
    boolean hideDock_preference;
    boolean floatingDockPreference_value;

    private String savedList_FileName;
    private boolean Card_Hidden;
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
                            AddEntry(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"), true);
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
                            //Build the entry
                            DataModel item = new DataModel(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"));
                            adapter.remove(adapter.getItem(listView_position)); //Delete the old entry
                            adapter.insert(item, listView_position); //Insert the new entry
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
                            //Build the entry
                            DataModel item = (DataModel) extras.getSerializable("newEntry");
                            adapter.remove(adapter.getItem(listView_position)); //Delete the old entry
                            adapter.insert(item, listView_position); //Insert the new entry
                            RefreshEverything(true);
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHandling.ApplyTheme(this); //Apply theme
        actionBar = getSupportActionBar();
        assert actionBar != null;
        super.onCreate(savedInstanceState);

        //Show the welcome screen (New, Beta)
        SharedPreferences WelcomeScreenSettings = getApplicationContext().getSharedPreferences(Preferences.WelcomeScreen.PreferenceGroup, 0);
        int wasShown = WelcomeScreenSettings.getInt(Preferences.WelcomeScreen.ENTRY_SHOWN, 0);
        if (wasShown == 0) { //The Welcome screen was never shown; possibly a first start of the app.
            Intent welcomeScreen_Intent = new Intent(this, WelcomeScreen.class);
            startActivity(welcomeScreen_Intent);
        }

        //Load the saved list
        String savedListFileName = "/saved_list.txt";
        savedList_FileName = getFilesDir().getParent() + savedListFileName;
        //Load layout based on settings
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

        //AppBeta.ShowBetaMessage(this);

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
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(listItems, MainActivity.this);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            listView_position = position;
            DataModel dataModel = listItems.get(listView_position);
            Intent itemInfo = new Intent(this, ItemInfo.class);
            itemInfo.putExtra("dataModel", dataModel);
            ItemInfoActivityLauncher.launch(itemInfo);
        });

        //listView.setOnItemLongClickListener(this::onItemLongClick);

        //if (floatingDockPreference_value) {
        //    cardView.setOnTouchListener(new View.OnTouchListener() {
        //        @Override
        //        public boolean onTouch(View v, MotionEvent event) {
        //            if (event.getAction() == MotionEvent.AXIS_VSCROLL) {
        //                MainCardView_ChangeShowStatus(false);
        //            }
        //            return true;
        //        }
        //    });
        //}

        loadingProgressBar = findViewById(R.id.progressBar2);
        loadingProgressBar.setVisibility(View.GONE);
        noItems_CardView = findViewById(R.id.noItems_View);
        noItems_CardView.setVisibility(View.GONE);

        registerForContextMenu(listView);

        LoadList();
        RefreshView();
        Timer timer = new Timer();
        Context thisContext = this;
        //timer.schedule(new TimerTask() {
        //    @Override
        //    public void run() {
        //        MainActivity.this.runOnUiThread(new Runnable() {
        //            public void run() {
        //                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //                if (sharedPreferences.getBoolean("autoCheckForUpdates", false) == true) {
        //                    GitHub release_repo = new GitHub(GitHubParams.username, GitHubParams.repo_name, new GitHub.RetrievalListener() {
        //                        @Override
        //                        public void onRetrievalComplete(ReleaseInfo releaseInfo) {
        //                            VersionInfo releaseVersionInfo = VersionParser.parse(releaseInfo.getReleaseVersion());
        //                            VersionInfo currentVersionInfo = VersionParser.parse(BuildConfig.VERSION_NAME);
        //                            if (releaseInfo != null) {
        //                                SharedPreferences UpdateSettings = getApplicationContext().getSharedPreferences("AutomaticUpdater", 0);
        //                                VersionCompare.VersionComparison comparison = VersionCompare.CompareVersions(currentVersionInfo, releaseVersionInfo);
        //                                String savedVersion = UpdateSettings.getString("skip_version", "0.0.0");
        //                                String rVers = releaseInfo.getReleaseVersion();
        //                                if (comparison == VersionCompare.VersionComparison.VERSION_NEWER) {
        //                                    if (rVers.contentEquals(savedVersion)) {
//
        //                                    } else {
        //                                        ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
        //                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Update available.", Snackbar.LENGTH_LONG)
        //                                                .setAction("VIEW", v1 -> {
        //                                                    MaterialAlertDialogBuilder updateNotifierDialog = new MaterialAlertDialogBuilder(thisContext)
        //                                                            .setTitle("Update available")
        //                                                            .setMessage("An update for PriceCalc is available.")
        //                                                            .setPositiveButton("View update info", (dialog, which) -> {
        //                                                                Intent intent = new Intent(getApplicationContext(), CheckForUpdates.class);
        //                                                                startActivity(intent);
        //                                                            })
        //                                                            .setNegativeButton("Skip this version", (dialog, which) -> {
        //                                                                UpdateSettings.edit().putString("skip_version", releaseInfo.getReleaseVersion()).apply();
        //                                                                ShowDismissableSnackbar("You will no longer be notified of updates until the next release.");
        //                                                            })
        //                                                            .setNeutralButton("Disable automatic updates", (dialog, which) -> {
        //                                                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("autoCheckForUpdates", false).apply();
        //                                                                ShowDismissableSnackbar("Automatic updates disabled. You can re-enable it in Settings.");
        //                                                            })
        //                                                            .setCancelable(true);
        //                                                    updateNotifierDialog.show();
        //                                                    //Intent intent = new Intent(getApplicationContext(), CheckForUpdates.class);
        //                                                    //startActivity(intent);
        //                                                })
        //                                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        //                                        snackbar.show();
        //                                    }
        //                                }
        //                            }
        //                        }
//
        //                        @Override
        //                        public void onRetrievalError(String request) {
        //                        }
        //                    });
//
        //                    release_repo.GetData();
        //                }
        //            }
        //        });
        //    }
        //}, 1000);

        //Cleanup();
    }

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
                RefreshEverything(false); //Don't save; we're just refreshing
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
            case R.id.deleteEntry_menuItem:
                EnterDeleteMode(this);
                break;
            case R.id.confirmDeletion_menuItem:
                LeaveDeleteMode(this);
                break;
            case R.id.cancelDeletion_menuItem:
                LeaveDeleteMode(this);
                break;
            case R.id.openList_menuItem:
                Intent openDocument = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openDocument.addCategory(Intent.CATEGORY_OPENABLE);
                openDocument.setType("*/*");
                //intent.putExtra(Intent.EXTRA_TITLE, "");
                startActivityForResult(openDocument, 2);
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

    void AddEntry(String itemName, String itemPrice, Boolean isTaxDeductible, String quantity, @SuppressWarnings("SameParameterValue") Boolean CommitChanges) {
        //listItems.add(new DataModel(itemName, itemPrice, isTaxDeductible, quantity));
        DataModel dataModel = new DataModel(itemName, itemPrice, isTaxDeductible, quantity);
        adapter.add(dataModel);
        RefreshEverything(CommitChanges);
    }

    void AddEntry(DataModel dataModel, @SuppressWarnings("SameParameterValue") Boolean CommitChanges) {
        adapter.add(dataModel);
        RefreshEverything(CommitChanges);
    }

    void RefreshEverything(Boolean saveList) {
        adapter.notifyDataSetChanged();
        try {
            GetTotalCostFromList();
        } catch (Exception ex) {
            SetDashText("Error");
        }
        if (saveList) {
            try {
                SaveList();
            } catch (IOException e) {
                SetDashText("Save Error");
            }
        }
        RefreshView();
        Log.d("VIEW_REFRESH", "View refreshed, " + BooleanHandling.BoolToString(saveList, "commits saved.", "file untouched."));
    }

    void SetDashText(String text) {
        if (hideDock_preference) {
            actionBar.setSubtitle(text);
        } else {
            totalCostLabel.setText(text);
        }
    }

    //Loads list items into list view
    void LoadList() {
        try {
            ArrayList<DataModel> dataModels = GetEntries();
            for (int i = 0; i < dataModels.size(); i++) {
                AddEntry(dataModels.get(i), false);
            }
        } catch (IOException ignored) {

        }
        RefreshEverything(false);
    }

    //Retrieve entries from internal save
    public ArrayList<DataModel> GetEntries() throws IOException {
        ArrayList<DataModel> dataModels = new ArrayList<>();
        File logFile = new File(savedList_FileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
        StringBuilder everything = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            everything.append(line);
            Log.d("READ_FILE", line);
        }
        Log.d("STRING_HANDLING", StringHandling.combineStrings(everything.toString(), "Full String: "));
        String[] lines = everything.toString().split(NewLineSeparator);
        Log.d("STRING_HANDLING", StringHandling.combineStrings(String.valueOf(lines.length), "Lines: "));
        for (String nextLine : lines) {
            try {
                Log.d("STRING_HANDLING", nextLine);
                String[] splitString = nextLine.split(splitChar);
                Log.d("STRING_HANDLING", "---LINE CONTENTS---");
                Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[0]));
                Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[1]));
                Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[2]));
                Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[3]));
                dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue), splitString[3]));
            } catch (Exception ex) {
                Log.e("LOAD_FILE", "Error parsing string/line in file. It may be empty.");
            }
        }
        Log.d("STRING_HANDLING", StringHandling.combineStrings(String.valueOf(dataModels.size()), "DataModel Final Size: "));
        return dataModels;
    }

    //Overwrites any information still saved, and writes in current list items.
    void SaveList() throws IOException {
        ArrayList<DataModel> dataModels = adapter.getDataSet();
        File logFile = new File(savedList_FileName);
        try {
            final boolean delete = logFile.delete();
        } catch (Exception ignored) {

        }
        final boolean newFile = logFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false));
        Log.d("DataModelSize", String.valueOf(dataModels.size()));
        for (int i = 0; i < dataModels.size(); i++) {
            try {
                writer.write(GenerateEntry(dataModels.get(i)));
                //writer.newLine();
                if (i < dataModels.size()) {
                    writer.append(NewLineSeparator);
                }
                Log.d("SaveFileWriter", "Wrote Line " + i + ": " + GenerateEntry(dataModels.get(i)));
            } catch (Exception ex) {
                //throw ex;
            }
        }
        writer.close();
    }

    String getSavedItemID(int index) {
        return "entry" + index;
    }

    DataModel ProcessEntryInfo(Set<String> stringSet) {
        /*
         * Entry Order:
         * 1. Item Name
         * 2. Item Price
         * 3. Is item taxable? (Bool as String)
         * 4. Item Quantity
         */
        String[] stringArray = (String[]) stringSet.toArray();
        return new DataModel(stringArray[0], stringArray[1], BooleanHandling.StringToBool(stringArray[2], BooleanHandling.PositiveValue), stringArray[3]);
    }

    public String GenerateEntry(@org.jetbrains.annotations.NotNull DataModel dataModel) {
        return dataModel.getItemName() +
                splitChar +
                dataModel.getItemPrice() +
                splitChar +
                BooleanHandling.BoolToString(dataModel.getIsTaxable(), "Yes", "No") +
                splitChar +
                dataModel.getItemQuantity();
    }

    //When called, empty the list and delete the save file.
    public void ClearAll() {
        File logFile = new File(savedList_FileName);
        try {
            final boolean delete = logFile.delete();
        } catch (Exception ignored) {

        }
        adapter.clear();
        adapter.notifyDataSetChanged();
        RefreshEverything(true);
    }

    public void RefreshView() {
        if (adapter.getCount() < 1) {
            noItems_CardView.setVisibility(View.VISIBLE);
        } else {
            noItems_CardView.setVisibility(View.GONE);
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
                    //Backup... just in case :)
                    listItems_BKP.clear();
                    for (DataModel item : listItems) {
                        listItems_BKP.add(item);
                    }
                    listItems.remove(listView_position);
                    //ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
                    CardView cardView = findViewById(R.id.cardView);
                    View layout = findViewById(R.id.mainConstraintLayout);
                    //Show snackbar
                    {
                        ConstraintLayout coordinatorLayout = findViewById(R.id.mainConstraintLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Entry deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", v1 -> {
                                    listItems.clear();
                                    for (DataModel item : listItems_BKP) {
                                        listItems.add(item);
                                    }
                                    RefreshEverything(true);
                                    //Snackbar snackbarUndone = Snackbar.make(coordinatorLayout, "Action undone", Snackbar.LENGTH_SHORT)
                                    //        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                    //snackbarUndone.show();
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
            MenuInflater inflater = getMenuInflater();
            //menu.setHeaderTitle("Item options");
            inflater.inflate(R.menu.menu_itemsmenu, menu);
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
            //String pathRoot = c.getFilesDir().toString();
            String pathRoot = c.getCacheDir().toString();
            c.deleteFile(pathRoot + "PriceCalc_update.apk");
        } catch (Exception ignored) {

        }
    }

    //Animations
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
}