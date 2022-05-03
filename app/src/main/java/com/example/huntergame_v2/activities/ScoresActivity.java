package com.example.huntergame_v2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.example.huntergame_v2.CallBack_ScoreClicked;
import com.example.huntergame_v2.R;
import com.example.huntergame_v2.fragments.Fragment_Button;
import com.example.huntergame_v2.fragments.Fragment_Rank;
import com.example.huntergame_v2.fragments.Fragment_Map;
import com.example.huntergame_v2.utils.MSP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.TreeMap;

public class ScoresActivity extends AppCompatActivity {
    // Scores & Sounds
    private MediaPlayer ring;
    private boolean isSound,isPlayAgain;
    private int lastScore = 0;

    // Maps
    private TreeMap<Integer, Double> scoresAndLatitudes;
    private TreeMap<Integer, Double> scoresAndLongitudes;

    // Fragments
    private Fragment_Rank fragment_rank;
    private Fragment_Map fragment_map;
    private Fragment_Button fragment_button;

    // Shared Preferences
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);

        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        // get the maps jsons from shared preferences or initial init
        readMapsFromSharedPreferences();

        // read max score from last game played, and read latitude and longitude if needed
        // lastScore == -1 when intent starts from Home Activity, then this actions are unnecessary.
        lastScore = getIntent().getExtras().getInt("Score");
        if(lastScore != -1) {
            updateMaps();

            // when new score arrives from last game played then save the maps as jsons into shared preference.
            saveMapsToSharedPreferences();
        }

        fragment_rank = new Fragment_Rank();
        fragment_rank.setCallBack_ScoreClicked(callBack_ScoreClicked);
        fragment_rank.setAllScores(scoresAndLatitudes.descendingKeySet());
        fragment_rank.setMaps(scoresAndLatitudes,scoresAndLongitudes);
        getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_rank, fragment_rank).commit();

        fragment_map = new Fragment_Map();
        fragment_map.setCallBack_ScoreClicked(callBack_ScoreClicked);
        fragment_map.setMaps(scoresAndLatitudes,scoresAndLongitudes);
        getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_map, fragment_map).commit();

        isSound = getIntent().getExtras().getBoolean("Sound");
        isPlayAgain = getIntent().getExtras().getBoolean("PlayAgain");
        if(isSound) {
            ring = MediaPlayer.create(ScoresActivity.this, R.raw.energy);
            ring.start();
        }

        FrameLayout layout = (FrameLayout)findViewById(R.id.scores_LAY_button);
        if(isPlayAgain) { // came from game
            if(layout.getVisibility() == View.GONE)
                layout.setVisibility(View.VISIBLE);
            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
            fragment_button = new Fragment_Button();
            getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_button, fragment_button).commit();
        } else { // came from home
            actionBar.setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().hide();
            layout.setVisibility(View.GONE);
        }
    }

    /**
     * This function reads the maps jsons from shared preferences and init them.
     */
    private void readMapsFromSharedPreferences() {
        TypeToken token = new TypeToken<TreeMap<Integer,Double>>() {};
        scoresAndLatitudes = MSP.getMe(this).getMap("ScoresAndLatitudes", token);
        scoresAndLongitudes = MSP.getMe(this).getMap("ScoresAndLongitudes",token);
    }

    /**
     * This function saves the scores and latitudes/longitudes maps as jsons into shared preferences.
     */
    private void saveMapsToSharedPreferences() {
        MSP.getMe(this).putMap("ScoresAndLatitudes",scoresAndLatitudes);
        MSP.getMe(this).putMap("ScoresAndLongitudes",scoresAndLongitudes);
    }

    /**
     * This function retrieves latitude and longitude from last score and update the scores and latitudes/longitudes maps.
     */
    private void updateMaps() {
        scoresAndLatitudes.put(lastScore,getIntent().getExtras().getDouble("Latitude"));
        scoresAndLongitudes.put(lastScore,getIntent().getExtras().getDouble("Longitude"));
        if(scoresAndLatitudes.size() > 10) {
            scoresAndLatitudes.firstEntry();
        }
        if(scoresAndLongitudes.size() > 10) {
            scoresAndLongitudes.firstEntry();
        }
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------- ---------- Cycle ---------- ----------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isSound)
            ring.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSound)
            ring.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSound)
            ring.pause();
    }

    // ---------- ---------- CallBacks ---------- ----------

    private CallBack_ScoreClicked callBack_ScoreClicked = new CallBack_ScoreClicked() {
        @Override
        public void scoreClicked(int rank) {
            fragment_map.focusOnScoreLocationAndSetMarker(rank);
        }
    };

}
