package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NonConstantResourceId")

public class AddItem extends AppCompatActivity {
    EditText itemNameEditText;
    TextView totalCostLabel;
    EditText itemCostEditText;
    CheckBox taxDeductible;
    EditText itemQuantityEditText;
    Boolean isCancelling = true;
    FloatingActionButton floatingActionButton;
    Boolean somethingChanged = false; //This is checked when the activity is closing. If true, the "Confirm exit & discard" warning dialog will be shown if the user tries to exit the activity.
    MenuItem addItem_menuBar_totalCostLabel;
    CoordinatorLayout coordinatorLayout;
    Boolean editMode = false;
    DataModel importedDataModel;
    Timer refreshTimer;
    String result; //Stores the final string to write to the labels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.add_item);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Edit item details.");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_item);
        //Init Components
        floatingActionButton = findViewById(R.id.confirmFloatingActionButton);
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                somethingChanged = true; //Changed the title
            }
        });
        itemCostEditText = findViewById(R.id.itemCostEditText);
        itemCostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                UpdateTotalLabel();
            }
        });
        totalCostLabel = findViewById(R.id.totalCostLabel);
        itemQuantityEditText = findViewById(R.id.itemQuantityEditText);
        itemQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                UpdateTotalLabel();
            }
        });
        itemQuantityEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (itemQuantityEditText.getText().length() < 1) {
                    itemQuantityEditText.setText(R.string.quantity_minValue);
                }
            }
            //UpdateTotalLabel();
        });
        taxDeductible = findViewById(R.id.isTaxDeductible_CheckBox);
        taxDeductible.setOnCheckedChangeListener((buttonView, isChecked) -> UpdateTotalLabel());
        floatingActionButton.setOnClickListener(v -> ConfirmAndExit());
        EditMode();
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(addItem_menuBar_totalCostLabel != null) {
                        addItem_menuBar_totalCostLabel.setTitle(result);
                    }
                });
            }
        }, 100, 50);
    }

    void EditMode() {
        Intent i = getIntent();
        if (i.hasExtra("dataModel")) {
            setTitle("Edit Item");
            importedDataModel = (DataModel) i.getSerializableExtra("dataModel");
            editMode = true;
            setTitle("Edit Item");
            itemNameEditText.setText(importedDataModel.getItemName());
            itemCostEditText.setText(importedDataModel.getItemPrice());
            itemQuantityEditText.setText(importedDataModel.getItemQuantity());
            taxDeductible.setChecked(importedDataModel.getIsTaxable());
            UpdateTotalLabel();
            setResult(RESULT_FIRST_USER, null);
        }
    }

    void UpdateTotalLabel() {
        try {
            double defaultTaxRate = PriceHandling.getDefaultTaxRatePercentage(this);
            String itemCostText = itemCostEditText.getText().toString();
            if (taxDeductible.isChecked()) {
                result = PriceHandling.calculatePrice(Double.parseDouble(itemCostText), defaultTaxRate, Integer.parseInt(itemQuantityEditText.getText().toString()));
            } else {
                result = PriceHandling.calculatePrice(Double.parseDouble(itemCostText), 0, Integer.parseInt(itemQuantityEditText.getText().toString()));
            }
        } catch (Exception ex) {
            result = getString(R.string.totalCost_zeroString);
        }
        totalCostLabel.setText(result);
        somethingChanged = true; //We're refreshing because the user changed something. Now, we set this to true so that the "Confirm discard" dialog shows if the user tries to leave the activity.
    }

    void ConfirmAndExit() {
        isCancelling = false;
        if (!(itemCostEditText.length() < 1)) {
            Intent data = new Intent();
            data.putExtra("itemName", itemNameEditText.getText().toString());
            data.putExtra("itemCost", itemCostEditText.getText().toString());
            //String isTaxDeductibleString = BooleanHandling.BoolToString(taxDeductible.isChecked(), getString(R.string.taxDeductible_Yes), getString(R.string.taxDeductible_No));
            data.putExtra("isTaxDeductible", taxDeductible.isChecked());
            data.putExtra("itemQuantity", itemQuantityEditText.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        } else {
            itemCostEditText.setError(getString(R.string.error_RequiredField));
            isCancelling = true;
        }
    }

    void ShowDiscardWarning() {
        if (somethingChanged == true) {
            if (isCancelling) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                        .setMessage("Are you sure you want to discard unsaved changes?")
                        .setTitle("Discard Changes?")
                        .setIcon(R.drawable.ic_baseline_cancel_24)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true);
                materialAlertDialogBuilder.show();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_additem, menu);
        addItem_menuBar_totalCostLabel = menu.findItem(R.id.addItem_menuBar_totalCostLabel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_actionButton:
                ConfirmAndExit();

            case R.id.discard_actionButton:
                ShowDiscardWarning();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        ShowDiscardWarning();
    }
}