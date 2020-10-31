package com.josephm101.pricecalc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddItem extends AppCompatActivity {
    EditText itemNameEditText;
    TextView totalCostLabel;
    EditText itemCostEditText;
    CheckBox taxDeductible;
    EditText itemQuantityEditText;
    Boolean isCancelling = true;

    TextView itemCostBoxEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.add_item);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //Init Components
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemCostEditText = findViewById(R.id.itemCostEditText);
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
        taxDeductible.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UpdateTotalLabel();
            }
        });
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

        //Warning labels and objects
        itemCostBoxEmpty = findViewById(R.id.itemCostBoxEmpty);
        itemCostBoxEmpty.setVisibility(View.GONE);
    }

    void UpdateTotalLabel() {
        try {
            double defaultTaxRate = PriceHandling.getDefaultTaxDeductionPercentage(this);
            String itemCostText = itemCostEditText.getText().toString();
            if (taxDeductible.isChecked() == true) {
                totalCostLabel.setText(PriceHandling.calculatePrice(Double.parseDouble(itemCostText), defaultTaxRate, Integer.parseInt(itemQuantityEditText.getText().toString())));
            } else {
                totalCostLabel.setText(PriceHandling.calculatePrice(Double.parseDouble(itemCostText), 0, Integer.parseInt(itemQuantityEditText.getText().toString())));
            }
        } catch (Exception ex) {
            totalCostLabel.setText(R.string.totalCost_zeroString);
        }
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
            itemCostBoxEmpty.setVisibility(View.VISIBLE);
        }
    }

    void ShowDiscardWarning() {
        if (isCancelling == true) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to discard this entry and go back?")
                    .setTitle("Discard Entry?")
                    .setIcon(R.drawable.ic_baseline_cancel_24)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true);
            alertDialog.show();
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
    public void onBackPressed() {
        ShowDiscardWarning();
    }
}