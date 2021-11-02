package com.josephm101.pricecalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ItemInfo extends AppCompatActivity {
    DataModel importedDataModel;

    final ActivityResultLauncher<Intent> EditItemActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = null;
                    if (data != null) {
                        extras = data.getExtras();
                    }
                    if (extras != null) {
                        //Build the entry
                        DataModel item = new DataModel(extras.getString("itemName"), extras.getString("itemCost"), extras.getBoolean("isTaxDeductible"), extras.getString("itemQuantity"));
                        Intent sendBack = new Intent();
                        sendBack.putExtra("newEntry", item);
                        setResult(RESULT_OK, sendBack);
                        finish();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.item_info);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        //Init Action Bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Log.i("ItemInfo_INIT", "Importing data model...");
        Intent i = getIntent();
        importedDataModel = (DataModel) i.getSerializableExtra("dataModel");
        Log.i("ItemInfo_INIT", "Defining entries...");
        TextView ItemNameLabel = findViewById(R.id.itemNameLabel);
        TextView ItemPriceLabel = findViewById(R.id.itemPriceLabel);
        TextView ItemQuantityLabel = findViewById(R.id.itemQuantityLabel);
        TextView ItemTaxDeductionLabel = findViewById(R.id.itemTaxableLabel);
        TextView ItemTotalCostLabel = findViewById(R.id.itemTotalCost);
        Log.i("ItemInfo_INIT", "Reading & writing values...");

        ItemNameLabel.setText(importedDataModel.getItemName());
        double itemPrice = Double.parseDouble(importedDataModel.getItemPrice());
        int itemQuantity = Integer.parseInt(importedDataModel.itemQuantity);
        ItemPriceLabel.setText(PriceHandling.PriceToString(itemPrice));
        ItemQuantityLabel.setText(StringHandling.combineStrings(importedDataModel.itemQuantity, "Quantity: "));
        ItemTaxDeductionLabel.setText(StringHandling.combineStrings(BooleanHandling.BoolToString(importedDataModel.getIsTaxable(), "Yes", "No"), "Is taxable: "));
        StringBuilder stringBuilder = new StringBuilder();
        double priceWithQuantity = (itemPrice * itemQuantity);
        stringBuilder.append(PriceHandling.PriceToString(itemPrice * itemQuantity));
        stringBuilder.append("\r\n");
        stringBuilder.append("Tax cost: ");
        double priceTax = ((priceWithQuantity * PriceHandling.getDefaultTaxRatePercentage(this)) / 100);
        stringBuilder.append(PriceHandling.PriceToString(priceTax));
        stringBuilder.append("\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append("Total overall cost: ");
        double totalCostOverall = priceWithQuantity + priceTax;
        stringBuilder.append(PriceHandling.PriceToString(totalCostOverall));
        ItemTotalCostLabel.setText(stringBuilder.toString());
        Log.i("ItemInfo_INIT", "Init done; calculations complete.");
        Log.i("ItemInfo_INIT", "All values printed.");
        setResult(RESULT_FIRST_USER, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewitem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.EditItem:
                Intent editItem = new Intent(this, AddItem.class);
                editItem.putExtra("dataModel", importedDataModel);
                EditItemActivityLauncher.launch(editItem);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}