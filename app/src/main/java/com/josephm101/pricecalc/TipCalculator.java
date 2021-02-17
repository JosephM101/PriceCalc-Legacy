package com.josephm101.pricecalc;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

public class TipCalculator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        SeekBar tipPercentageSeekBar = findViewById(R.id.tipPercentageSeekBar);
        TextView tipPercentageSeekBar_Label = findViewById(R.id.tipPercentageSeekBar_Label);
        Chip chip_minusTen = findViewById(R.id.chip_removeTen);
        Chip chip_minusFive = findViewById(R.id.chip_removeFive);
        Chip chip_addFive = findViewById(R.id.chip_addFive);
        Chip chip_addTen = findViewById(R.id.chip_addTen);

        chip_minusFive.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = -5;
            if(!((progress + changeVal) < 0))
            {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            }
            else
            {
                tipPercentageSeekBar.setProgress(0);
            }
        });
        chip_minusTen.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = -10;
            if(!((progress + changeVal) < 0))
            {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            }
            else
            {
                tipPercentageSeekBar.setProgress(0);
            }
        });
        chip_addTen.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = 10;
            if(!((progress + changeVal) > 100))
            {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            }
            else
            {
                tipPercentageSeekBar.setProgress(100);
            }
        });
        chip_addFive.setOnClickListener(v -> {
            int progress = tipPercentageSeekBar.getProgress();
            int changeVal = 5;
            if(!((progress + changeVal) > 100))
            {
                tipPercentageSeekBar.setProgress(progress + changeVal);
            }
            else
            {
                tipPercentageSeekBar.setProgress(100);
            }
        });


        tipPercentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tipPercentageSeekBar_Label.setText(String.format("%d%%", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}