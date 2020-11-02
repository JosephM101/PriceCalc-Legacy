package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

@SuppressLint("NonConstantResourceId")

public class MainActivity extends AppCompatActivity {
    ArrayList<DataModel> listItems = new ArrayList<>();

    private final String NewLine = "\r\n";
    private final String NewLineSeparator = "¶";
    private final String splitChar = "§";

    private String savedList_FileName;

    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter adapter;
    ListView listView;
    FloatingActionButton addItem_FloatingActionButton;
    private int AddNew_RequestCode = 1;
    ProgressBar loadingProgressBar;
    TextView totalCostLabel;

    private final String savedListFileName = "/saved_list.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHandling.ApplyTheme(this); //Apply theme
        super.onCreate(savedInstanceState);
        savedList_FileName = getFilesDir().getParent() + savedListFileName;
        //Load layout based on settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean value = sharedPreferences.getBoolean("floatingDock_Preference", false);
        if (value) {
            setContentView(R.layout.activity_main_floating_toolbar);
        } else {
            setContentView(R.layout.activity_main);
        }
        AppBeta.ShowBetaMessage(this);

        totalCostLabel = findViewById(R.id.totalCost_Label);
        addItem_FloatingActionButton = findViewById(R.id.addItem_floatingActionButton);
        addItem_FloatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                Intent addNew = new Intent(v.getContext(), AddItem.class);
                startActivityForResult(addNew, AddNew_RequestCode);
            }
        });
        listView = findViewById(R.id.items_listBox);
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CustomAdapter(listItems, MainActivity.this);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel = listItems.get(position);
                Intent i = new Intent(view.getContext(), ItemInfo.class);
                i.putExtra("dataModel", dataModel);
                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final DataModel dataModel = listItems.get(position);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(parent.getContext())
                        .setMessage("Are you sure you want to permanently delete this entry? \r\n" + dataModel.getItemName())
                        .setTitle("Delete Entry?")
                        .setIcon(R.drawable.ic_baseline_delete_forever_24)
                        .setPositiveButton("Yes, delete it.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listItems.remove(position);
                                RefreshEverything();
                            }
                        })
                        .setNegativeButton("No, don't!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true);
                alertDialog.show();
                return true;
            }
        });

        try {
            LoadList();
        } catch (Exception ex) {
            /**
             * The file doesn't exist. Just ignore it for now; it will get created later.
             */
            //MessageHandling.ShowMessage(this, "Error", ex.getMessage(), "OK");
            //throw ex;
        }

        loadingProgressBar = findViewById(R.id.progressBar2);
        loadingProgressBar.setVisibility(View.GONE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadingProgressBar.setVisibility(View.GONE);
        if (requestCode == AddNew_RequestCode) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    AddEntry(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"));
                    try {
                        SaveList();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
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
                RefreshEverything();
                Toaster.pop(this, "Refreshed");
                break;
            case R.id.settings_menuItem:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void GetTotalCostFromList() {
        double TotalCost = 0;
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

    void AddEntry(String itemName, String itemPrice, Boolean isTaxDeductible, String quantity) {
        //listItems.add(new DataModel(itemName, itemPrice, isTaxDeductible, quantity));
        DataModel dataModel = new DataModel(itemName, itemPrice, isTaxDeductible, quantity);
        adapter.add(dataModel);
        RefreshEverything();
    }

    void AddEntry(DataModel dataModel) {
        adapter.add(dataModel);
        RefreshEverything();
    }

    void RefreshEverything() {
        adapter.notifyDataSetChanged();
        try {
            GetTotalCostFromList();
        } catch (Exception ex) {
            SetDashText("Error");
        }
        try {
            SaveList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void SetDashText(String text) {
        totalCostLabel.setText(text);
    }

    //Loads list items into list view
    void LoadList() throws IOException {
/*        SharedPreferences savedList = getApplicationContext().getSharedPreferences(Preferences.MainSettings.PreferenceGroup, 0);
        for (int i = 0; i < savedList.getInt(Preferences.MainSettings.ItemCount, 0) + 1; i++) {
            Set<String> stringSet = savedList.getStringSet(getSavedItemID(i), null);
            if (stringSet != null) {
                AddEntry(ProcessEntryInfo(stringSet));
                adapter.notifyDataSetChanged(); //Do it every time...
            }
        }
        //DB

 */
        try {
            ArrayList<DataModel> dataModels = GetEntries();
            for (int i = 0; i < dataModels.size(); i++) {
                AddEntry(dataModels.get(i));
            }
        } catch (IOException e) {
            throw e;
        }
        RefreshEverything();
    }

    public ArrayList<DataModel> GetEntries() throws IOException {
        ArrayList<DataModel> dataModels = new ArrayList<>();
        File logFile = new File(savedList_FileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
        StringBuilder everything = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            everything.append(line);
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
        String[] lines = everything.toString().split(NewLineSeparator);
        for (int i = 0; i < lines.length; i++) {
            String nextLine = lines[i];
            Log.d("STRING_HANDLING", nextLine);
            String[] splitString = nextLine.split(splitChar);
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P0", splitString[0]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P1", splitString[1]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P2", splitString[2]));
            Log.d("STRING_HANDLING", StringHandling.combineStrings("P3", splitString[3]));
            dataModels.add(new DataModel(splitString[0], splitString[1], BooleanHandling.StringToBool(splitString[2], BooleanHandling.PositiveValue, BooleanHandling.NegativeValue), splitString[3]));
        }
        return dataModels;
    }

    //Overwrites any information still saved, and writes in current list items.
    void SaveList() throws IOException {
        ArrayList<DataModel> dataModels = adapter.getDataSet();
        File logFile = new File(savedList_FileName);
        try {
            logFile.delete();
        } catch (Exception ex) {

        }
        logFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false));
        for (int i = 0; i < dataModels.size(); i++) {
            try {
                writer.write(GenerateEntry(dataModels.get(i)));
                //writer.newLine();
                writer.append(NewLineSeparator);
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
        /**
         * Entry Order:
         * 1. Item Name
         * 2. Item Price
         * 3. Is item taxable? (Bool as String)
         * 4. Item Quantity
         */

        String[] stringArray = (String[]) stringSet.toArray();
        return new DataModel(stringArray[0], stringArray[1], BooleanHandling.StringToBool(stringArray[2], BooleanHandling.PositiveValue, BooleanHandling.NegativeValue), stringArray[3]);
    }

    public String GenerateEntry(DataModel dataModel) {
        StringBuilder sb = new StringBuilder();
        sb.append(dataModel.getItemName());
        sb.append(splitChar);
        sb.append(dataModel.getItemPrice());
        sb.append(splitChar);
        sb.append(BooleanHandling.BoolToString(dataModel.getIsTaxable(), "Yes", "No"));
        sb.append(splitChar);
        sb.append(dataModel.getItemQuantity());
        return sb.toString();
    }

    public void ClearAll() {
        File logFile = new File(savedList_FileName);
        try {
            logFile.delete();
        } catch (Exception ex) {

        }
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}