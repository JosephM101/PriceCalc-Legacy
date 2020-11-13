package com.josephm101.pricecalc;

import android.content.Context;
import android.os.Vibrator;

public class VibrateController {
    final Vibrator vibrator;
    final int tapDuration = 200;
    int shortVibrate = 300;
    int pauseVibrate = 100;
    int longVibrate = 500;

    public VibrateController(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void TapEffect() {
        vibrator.vibrate(tapDuration);
    }

    public void doVibrate(int duration) {
        vibrator.vibrate(duration);
    }

    private void HandleVibrations(int duration) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(duration);
        }
    }

    /*
    public void doVibrate(VibrateEffect vibrateEffect) {
        switch (vibrateEffect)
        {
            case TAP:
                doVibrate(tapDuration);
                break;
            case DOUBLE_TAP:
                doVibrate(shortVibrate);
                wait(pauseVibrate);
        }
        vibrator.vibrate(vibrateEffect);
    }
    */

    public enum VibrateEffect {
        TAP,
        DOUBLE_TAP,
        NOTIFICATION_PATTERN
    }
}