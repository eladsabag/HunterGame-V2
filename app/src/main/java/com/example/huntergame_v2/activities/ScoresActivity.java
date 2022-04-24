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
import java.util.Set;
import java.util.TreeSet;

public class ScoresActivity extends AppCompatActivity {
    // Scores
    private MediaPlayer ring;
    private boolean isSound,isPlayAgain;
    private Set<String> scores;
    private int lastScore = 0;

    // Fragments
    private Fragment_Rank fragment_rank;
    private Fragment_Map fragment_map;
    private Fragment_Button fragment_button;

    // Shared Preferences
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        scores = new TreeSet<>((s1, s2) -> Integer.parseInt(s2) - Integer.parseInt(s1));

        // init top 10 scores from shared preferences
        // if the str isn't exist then init with 0
        for (int i = 0; i < 10; i++) {
            scores.add(prefs.getString("str"+i,"0"));
        }

        // read max score from last game played, and read latitude and longitude if needed
        // lastScore == -1 when intent starts from Home Activity, then this actions are unnecessary.
        lastScore = getIntent().getExtras().getInt("Score");
        if(lastScore != -1) {
            updateScoreSet();
            // if the last score equal to first score in set then location must be updated
            // and can be pushed into the Shared Preferences.
            if(Integer.parseInt((String) scores.toArray()[0]) == lastScore)
                editor.putFloat("Latitude",(float)getIntent().getExtras().getDouble("Latitude"));
                editor.putFloat("Longitude",(float)getIntent().getExtras().getDouble("Longitude"));
                editor.apply();
        }


        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(null);

        fragment_rank = new Fragment_Rank();
        fragment_rank.setCallBack_ScoreClicked(callBack_ScoreClicked);
        fragment_rank.setScores((TreeSet<String>) scores);
        getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_rank, fragment_rank).commit();

        fragment_map = new Fragment_Map();
        fragment_map.setCallBack_ScoreClicked(callBack_ScoreClicked);
        // read the last 1st score coordinates from shared preferences
        // if exist, else init with Ness Ziona Location.
        fragment_map.setChosenMap(
                (double)prefs.getFloat("Latitude", (float) 31.934849725807606),
                (double)prefs.getFloat("Longitude", (float) 34.804768052711324));
        getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_map, fragment_map).commit();

        isSound = getIntent().getExtras().getBoolean("Sound");
        isPlayAgain = getIntent().getExtras().getBoolean("PlayAgain");
        if(isSound) {
            ring = MediaPlayer.create(ScoresActivity.this, R.raw.energy);
            ring.start();
        }

        FrameLayout layout = (FrameLayout)findViewById(R.id.scores_LAY_button);
        if(isPlayAgain) {
            if(layout.getVisibility() == View.GONE)
                layout.setVisibility(View.VISIBLE);
            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
            fragment_button = new Fragment_Button();
            getSupportFragmentManager().beginTransaction().add(R.id.scores_LAY_button, fragment_button).commit();
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            layout.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
    }

    /**
     * This function update the scores set and cut it back to size 10 if needed.
     */
    private void updateScoreSet() {
        scores.add("" + lastScore);
        // if there is 11 scores then delete the last one
        if(scores.size() > 10)
            scores.remove( ((TreeSet) scores).last() );
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

    @Override
    protected void onStop() {
        super.onStop();
        int i = 0;
        for(String s : scores) {
            editor.putString("str" + i , s);
            i++;
        }
        editor.apply();
    }

    // ---------- ---------- CallBacks ---------- ----------

    private CallBack_ScoreClicked callBack_ScoreClicked = new CallBack_ScoreClicked() {
        @Override
        public void scoreClicked() {
            fragment_map.focusOnScoreLocationAndZoomIn();
        }
    };

}
