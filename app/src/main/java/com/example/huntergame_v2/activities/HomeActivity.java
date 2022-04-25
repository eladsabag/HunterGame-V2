package com.example.huntergame_v2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.huntergame_v2.R;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences.Editor editor;

    private MaterialButton home_BTN_play, home_BTN_playsensor, home_BTN_top10;
    private MediaPlayer ring;
    private ImageButton home_BTN_sound,home_BTN_vibration,home_BTN_user;
    private boolean isSound, isVibration;

    private boolean isChoseUsername;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        isSound = prefs.getBoolean("Sound",true);
        isVibration = prefs.getBoolean("Vibration",true);
        isChoseUsername = prefs.getBoolean("ChoseUsername",true);
        username = prefs.getString("Username","");

        if(isChoseUsername) {
            chooseUsername();
        } else
            Toast.makeText(HomeActivity.this,"Hi " + username,Toast.LENGTH_LONG).show();

        getSupportActionBar().hide();

        findViews();

        initViews();

        ring = MediaPlayer.create(HomeActivity.this,R.raw.jazzyfrenchy);

        setSoundAndVibrationResources();
    }

    private void chooseUsername() {
        // Set `EditText` to `dialog`. You can add `EditText` from `xml` too.
        final EditText input = new EditText(HomeActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Enter username");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        username = input.getText().toString();
                        isChoseUsername = false;
                    }
                });
        if(!isChoseUsername) {
            builder.setMessage("Hi " + username + ", you can change your user name here. click cancel if you don't want to change your user name.");
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // DO TASK
                        }
                    });
        }
        builder.setCancelable(false);
        builder.setView(input);

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Initially disable the button
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Set the textchange listener for edittext
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    // Disable ok button
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                } else {
                    // Something into edit text. Enable the button.
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });
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

        home_BTN_user.setOnClickListener(e -> chooseUsername());
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
        home_BTN_user = findViewById(R.id.home_BTN_user);
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
        editor.putBoolean("ChoseUsername",isChoseUsername);
        editor.putString("Username",username);
        editor.apply();
    }
}