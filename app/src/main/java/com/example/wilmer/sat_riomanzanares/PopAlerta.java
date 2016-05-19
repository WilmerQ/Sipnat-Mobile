package com.example.wilmer.sat_riomanzanares;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Wilmer on 29/03/2016.
 */
public class PopAlerta extends Activity {
    Vibrator vibrator;
    MediaPlayer mediaPlayer;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_alerta);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 0};
        vibrator.vibrate(pattern, 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        getWindow().setLayout((int) (width * .70), (int) (height * .60));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        vibrator.cancel();
        mediaPlayer.stop();

        Log.d("SAT", "ontouch: " + event.toString());
        return super.onTouchEvent(event);
    }
}
