package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@SuppressLint("NonConstantResourceId")

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter adapter;
    final ArrayList<DataModel> listItems = new ArrayList<>();
    //private final String NewLine = "\r\n";
    private final String NewLineSeparator = "`";
    private final String splitChar = "§";
    //private final int AddNew_RequestCode = 1;
    ListView listView;
    ExtendedFloatingActionButton addItem_FloatingActionButton;
    ProgressBar loadingProgressBar;
    TextView totalCostLabel;
    LinearLayout noItems_CardView;
    private String savedList_FileName;
    final ActivityResultLauncher<Intent> NewItemActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    loadingProgressBar.setVisibility(View.GONE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHandling.ApplyTheme(this); //Apply theme
        super.onCreate(savedInstanceState);
        String savedListFileName = "/saved_list.txt";
        savedList_FileName = getFilesDir().getParent() + savedListFileName;

        //Load layout based on settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean value = sharedPreferences.getBoolean("floatingDock_Preference", false);
        if (value) {
            setContentView(R.layout.activity_main_floating_toolbar);
        } else {
            setContentView(R.layout.activity_main);
        }
        AppBeta.ShowBetaMessage(this);

        totalCostLabel = findViewById(R.id.totalCost_Label);
        addItem_FloatingActionButton = findViewById(R.id.addItem_floatingActionButton);
        addItem_FloatingActionButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            Intent addNew = new Intent(v.getContext(), AddItem.class);
            NewItemActivityLauncher.launch(addNew);
            //startActivityForResult(addNew, AddNew_RequestCode);
        });
        listView = findViewById(R.id.items_listBox);
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(listItems, MainActivity.this);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DataModel dataModel = listItems.get(position);
            Intent i = new Intent(view.getContext(), ItemInfo.class);
            i.putExtra("dataModel", dataModel);
            startActivity(i);
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            final DataModel dataModel = listItems.get(position);
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(parent.getContext(), R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                    .setMessage("Are you sure you want to permanently delete this entry? \r\n" + dataModel.getItemName())
                    .setTitle("Delete Entry?")
                    .setIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setPositiveButton("Yes, delete it.", (dialog, which) -> {
                        listItems.remove(position);
                        RefreshEverything(true);
                    })
                    .setNegativeButton("No, don't!", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true);
            materialAlertDialogBuilder.show();
            return true;
        });
        loadingProgressBar = findViewById(R.id.progressBar2);
        loadingProgressBar.setVisibility(View.GONE);
        noItems_CardView = findViewById(R.id.noItems_View);
        noItems_CardView.setVisibility(View.GONE);
        LoadList();
        RefreshView();
    }

    @Deprecated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshView_menuItem:
                RefreshEverything(false); //Don't save; we're just refreshing
                //Toaster.pop(this, "Refreshed");
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
        }
        return super.onOptionsItemSelected(item);
    }

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

    void AddEntry(String itemName, String itemPrice, Boolean isTaxDeductible, String quantity, Boolean CommitChanges) {
        //listItems.add(new DataModel(itemName, itemPrice, isTaxDeductible, quantity));
        DataModel dataModel = new DataModel(itemName, itemPrice, isTaxDeductible, quantity);
        adapter.add(dataModel);
        RefreshEverything(CommitChanges);
    }

    void AddEntry(DataModel dataModel, Boolean CommitChanges) {
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
        if(saveList) {
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
        totalCostLabel.setText(text);
    }

    //Loads list items into list view
    void LoadList() {
/*        SharedPreferences savedList = getApplicationContext().getSharedPreferences(Preferences.MainSettings.PreferenceGroup, 0);
        for (int i = 0; i < savedList.getInt(Preferences.MainSettings.ItemCount, 0) + 1; i++) {
            Set<String> stringSet = savedList.getStringSet(getSavedItemID(i), null);
            if (stringSet != null) {
                AddEntry(ProcessEntryInfo(stringSet));
                adapter.notifyDataSetChanged(); //Do it every time...
            }
        }
 */
        try {
            ArrayList<DataModel> dataModels = GetEntries();
            for (int i = 0; i < dataModels.size(); i++) {
                AddEntry(dataModels.get(i), false);
            }
        } catch (IOException ignored) {

        }
        RefreshEverything(false);
    }

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
        /*
        Scanner scanner = new Scanner(everything.toString());
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            Log.d("STRING_HANDLING", nextLine);
            String[] splitString = nextLine.split(splitChar);
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P0", splitString[0]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P1", splitString[1]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P2", splitString[2]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P3", splitString[3]));
            dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue, BooleanHandling.NegativeValue), splitString[3]));
        }
         */
        Log.d("STRING_HANDLING", StringHandling.combineStrings(everything.toString(), "Full String: "));
        String[] lines = everything.toString().split(NewLineSeparator);
        Log.d("STRING_HANDLING", StringHandling.combineStrings(String.valueOf(lines.length), "Lines: "));
        for (String nextLine : lines) {
            Log.d("STRING_HANDLING", nextLine);
            String[] splitString = nextLine.split(splitChar);
            Log.d("STRING_HANDLING", "---LINE CONTENTS---");
            Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[0]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[1]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[2]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("", splitString[3]));
            dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue), splitString[3]));
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
                if(i<dataModels.size()) {
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
}