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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

@SuppressLint("NonConstantResourceId")

public class AddItem extends AppCompatActivity {
    EditText itemNameEditText;
    TextView totalCostLabel;
    EditText itemCostEditText;
    CheckBox taxDeductible;
    EditText itemQuantityEditText;
    Boolean isCancelling = true;
    FloatingActionButton floatingActionButton;
    Boolean somethingChanged = false; //Checked before activity exits. If true, the "Confirm exit & discard" warning dialog will be shown if the user tries to exit the activity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.add_item);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Edit item details.");
        actionBar.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //Init Components
        floatingActionButton = findViewById(R.id.confirmFloatingActionButton);

        /**
         * The following method is not available for the current class, and it's derivative class (ExtendedFloatingActionButton) will not
         * work without reworking, which will result in broken functionality.
         */
        //floatingActionButton.setAnimateShowBeforeLayout(true);

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
                if (s.length() < 1) {
                    itemQuantityEditText.setText(R.string.quantity_minValue);
                }
                UpdateTotalLabel();
            }
        });
        taxDeductible = findViewById(R.id.isTaxDeductible_CheckBox);
        taxDeductible.setOnCheckedChangeListener((buttonView, isChecked) -> UpdateTotalLabel());

        floatingActionButton.setOnClickListener(v -> ConfirmAndExit());
    }

    void UpdateTotalLabel() {
        try {
            double defaultTaxRate = PriceHandling.getDefaultTaxRatePercentage(this);
            String itemCostText = itemCostEditText.getText().toString();
            if (taxDeductible.isChecked()) {
                totalCostLabel.setText(PriceHandling.calculatePrice(Double.parseDouble(itemCostText), defaultTaxRate, Integer.parseInt(itemQuantityEditText.getText().toString())));
            } else {
                totalCostLabel.setText(PriceHandling.calculatePrice(Double.parseDouble(itemCostText), 0, Integer.parseInt(itemQuantityEditText.getText().toString())));
            }
        } catch (Exception ex) {
            totalCostLabel.setText(R.string.totalCost_zeroString);
        }
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
                        .setMessage("Are you sure you want to discard this entry and go back?")
                        .setTitle("Discard Entry?")
                        .setIcon(R.drawable.ic_baseline_cancel_24)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true);
                materialAlertDialogBuilder.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_additem, menu);
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