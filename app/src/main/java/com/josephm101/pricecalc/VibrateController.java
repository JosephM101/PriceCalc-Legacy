package com.josephm101.pricecalc;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;

public class VibrateController {
    final Vibrator vibrator;
    final int tapDuration = 100;
    int shortVibrate = 300;
    int pauseVibrate = 100;
    int longVibrate = 500;

    public VibrateController(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void Tap() {
        //vibrator.vibrate(VibrationEffect.createOneShot(tapDuration, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public void DoubleTap() {
        vibrator.vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK);
    }

    public void LongPress() {
        vibrator.vibrate(VibrationEffect.EFFECT_HEAVY_CLICK);
    }

    private void HandleVibrations(int duration) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(duration);
        }
    }
}