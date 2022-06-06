package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class TipCalculator extends AppCompatActivity {
    float currentTipPercentage;
    boolean isCustomPercentage = false;
    EditText billCost_EditText;
    SeekBar tipPercentageSeekBar;
    TextView totalTip_label;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Tip Calculator");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Beta");
        actionBar.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        billCost_EditText = findViewById(R.id.billCost_EditText);
        billCost_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                RefreshTotalLabel();
            }
        });
        tipPercentageSeekBar = findViewById(R.id.tipPercentageSeekBar);
        totalTip_label = findViewById(R.id.totalTip_label);
        TextView tipPercentageSeekBar_Label = findViewById(R.id.tipPercentageSeekBar_Label);
        Chip chip_minusTen = findViewById(R.id.chip_removeTen);
        Chip chip_minusFive = findViewById(R.id.chip_removeFive);
        Chip chip_addFive = findViewById(R.id.chip_addFive);
        Chip chip_addTen = findViewById(R.id.chip_addTen);
        CardView tipPercentage_custom_card = findViewById(R.id.tipPercentage_custom_card);
        tipPercentage_custom_card.setVisibility(View.INVISIBLE);

        List<String> list = Arrays.asList("10%", "15%", "20%", "Custom");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_list_item, list);
        TextInputLayout textInputLayout = findViewById(R.id.tipPercentage_spinner);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.tipPercentage_autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: //10%
                    currentTipPercentage = 10;
                    tipPercentage_custom_card.setVisibility(View.INVISIBLE);
                    isCustomPercentage = false;
                    break;
                case 1: //15%
                    currentTipPercentage = 15;
                    tipPercentage_custom_card.setVisibility(View.INVISIBLE);
                    isCustomPercentage = false;
                    break;
                case 2: //20%
                    currentTipPercentage = 20;
                    tipPercentage_custom_card.setVisibility(View.INVISIBLE);
                    isCustomPercentage = false;
                    break;
                case 3: //Custom
                    currentTipPercentage = tipPercentageSeekBar.getProgress();
                    tipPercentage_custom_card.setVisibility(View.VISIBLE);
                    isCustomPercentage = true;
                    break;
            }
            RefreshTotalLabel();
        });

        chip_minusFive.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = -5;
            if (!((progress + changeVal) < 0)) {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            } else {
                tipPercentageSeekBar.setProgress(0);
            }
        });
        chip_minusTen.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = -10;
            if (!((progress + changeVal) < 0)) {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            } else {
                tipPercentageSeekBar.setProgress(0);
            }
        });
        chip_addTen.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = 10;
            if (!((progress + changeVal) > 100)) {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            } else {
                tipPercentageSeekBar.setProgress(100);
            }
        });
        chip_addFive.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = 5;
            if (!((progress + changeVal) > 100)) {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            } else {
                tipPercentageSeekBar.setProgress(100);
            }
        });

        tipPercentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tipPercentageSeekBar_Label.setText(String.format("%d%%", progress));
                currentTipPercentage = progress;
                RefreshTotalLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    void RefreshTotalLabel() {
        String tip = "$0.00";
        try {
            double billCost = Double.parseDouble(billCost_EditText.getText().toString());
            float numToMultiply = currentTipPercentage / 100;
            double finalCost = (billCost * numToMultiply);
            DecimalFormat df = new DecimalFormat("0.00");
            String formatted = df.format(finalCost);
            tip = String.format("$%s", formatted);
        } catch (Exception ex) {

        }
        totalTip_label.setText("Tip: " + tip);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}