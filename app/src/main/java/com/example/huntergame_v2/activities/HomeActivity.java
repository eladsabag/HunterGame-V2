package com.example.huntergame_v2.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.example.huntergame_v2.R;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences.Editor editor;

    private MaterialButton home_BTN_play, home_BTN_playsensor, home_BTN_top10;
    private MediaPlayer ring;
    private ImageButton home_BTN_sound,home_BTN_vibration;
    private boolean isSound, isVibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        isSound = prefs.getBoolean("Sound",true);
        isVibration = prefs.getBoolean("Vibration",true);

        getSupportActionBar().hide();

        findViews();

        initViews();

        ring = MediaPlayer.create(HomeActivity.this,R.raw.jazzyfrenchy);

        setSoundAndVibrationResources();
    }

    private void setSoundAndVibrationResources() {
        // init sound and vibration pictures and start ring if needed
        if(isSound) {
            ring.start();
            home_BTN_sound.setBackgroundResource(R.drawable.ic_soundon);
        } else {
            home_BTN_sound.setBackgroundResource(R.drawable.ic_soundoff);
        }

        if(isVibration) {
            home_BTN_vibration.setBackgroundResource(R.drawable.ic_vibrationon);
        } else {
            home_BTN_vibration.setBackgroundResource(R.drawable.ic_vibrationoff);
        }
    }

    /**
     * This function init all the actions of the views.
     */
    private void initViews() {
        home_BTN_play.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    home_BTN_play.setPressed(true);
                    return true;
                }
                home_BTN_play.setPressed(false);
                return false;
            }
        });
        home_BTN_play.setOnClickListener(e -> {
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra("Sensor", false);
            intent.putExtra("Sound", isSound);
            intent.putExtra("Vibration", isVibration);
            startActivity(intent);
        });

        home_BTN_playsensor.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    home_BTN_playsensor.setPressed(true);
                    return true;
                }
                home_BTN_playsensor.setPressed(false);
                return false;
            }
        });
        home_BTN_playsensor.setOnClickListener(e -> {
            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
            intent.putExtra("Sensor", true);
            intent.putExtra("Sound", isSound);
            intent.putExtra("Vibration", isVibration);
            startActivity(intent);
        });

        home_BTN_top10.setOnClickListener(e -> {
            Intent intent = new Intent(HomeActivity.this, ScoresActivity.class);
            intent.putExtra("PlayAgain", false);
            intent.putExtra("Sound",isSound);
            intent.putExtra("Score",-1);
            startActivity(intent);
        });

        home_BTN_sound.setOnClickListener(e -> {
            if(isSound) {
                home_BTN_sound.setBackgroundResource(R.drawable.ic_soundoff);
                ring.stop();
            }
            else {
                home_BTN_sound.setBackgroundResource(R.drawable.ic_soundon);
                ring = MediaPlayer.create(HomeActivity.this,R.raw.jazzyfrenchy);
                ring.start();
            }
            isSound = !isSound;
        });

        home_BTN_vibration.setOnClickListener(e -> {
            if(isVibration)
                home_BTN_vibration.setBackgroundResource(R.drawable.ic_vibrationoff);
            else {
                home_BTN_vibration.setBackgroundResource(R.drawable.ic_vibrationon);
                vibrateOnce();
            }
            isVibration = !isVibration;
        });

        home_BTN_top10.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    home_BTN_top10.setPressed(true);
                    return true;
                }
                home_BTN_top10.setPressed(false);
                return false;
            }
        });
    }

    /**
     * This function find all views ids and sets them to their variables.
     */
    private void findViews() {
        home_BTN_play = findViewById(R.id.home_BTN_play);
        home_BTN_playsensor = findViewById(R.id.home_BTN_playsensor);
        home_BTN_top10 = findViewById(R.id.home_BTN_top10);
        home_BTN_sound = findViewById(R.id.home_BTN_sound);
        home_BTN_vibration = findViewById(R.id.home_BTN_vibration);
    }

    /**
     * This function make the phone vibrate once.
     */
    private void vibrateOnce() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    // ---------- ---------- Cycle ---------- ----------

    @Override
    protected void onPause() {
        super.onPause();
        if(isSound)
            ring.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSound)
            ring.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putBoolean("Sound",isSound);
        editor.putBoolean("Vibration",isVibration);
        editor.apply();
    }
}