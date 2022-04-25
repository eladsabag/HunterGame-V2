package com.example.huntergame_v2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huntergame_v2.R;
import com.google.android.material.textview.MaterialTextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    // Game
    private ImageView[] game_IMG_hearts;
    private ImageView[][] game_IMG_imgs;
    private MaterialTextView game_LBL_score;
    private ImageButton[] game_BTN_arrows;
    private GameManager gameManager;
    private Player player,hunter;
    private int direction = -1;
    private boolean move = false;
    private int coinCounter = 0, coinX, coinY,maxScore = 0;
    private static final int BOARD_X = 7, BOARD_Y = 5;

    // Sound & Vibration
    private MediaPlayer ring;
    private boolean isSound;
    private boolean isVibration;

    // Sensor
    private boolean isSensor;
    private SensorManager sensorManager;
    private Sensor accSensor;
    private SensorEventListener accSensorEventListener;

    // Location
    private double latitude,longitude;
    private int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    private int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update
    private LocationManager mLocationManager;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Location Settings
        setLocationSettings();

        getSupportActionBar().hide();

        gameManager = new GameManager();
        player = new Player(6,2,"Player");
        hunter = new Player(0,2,"Hunter");

        findViews();

        isSensor = getIntent().getExtras().getBoolean("Sensor");
        isSound = getIntent().getExtras().getBoolean("Sound");
        isVibration = getIntent().getExtras().getBoolean("Vibration");

        if(isSensor) { // sensor mode
            setSensorMode();
        } else { // regular mode
            setRegularMode();
        }

        if(isSound) {
            ring = MediaPlayer.create(GameActivity.this, R.raw.moose);
            ring.start();
        }

        startScoreTimer();
    }

    /**
     * This function sets the game mode to regular(only buttons).
     */
    private void setRegularMode() {
        // up
        game_BTN_arrows[0].setOnClickListener(e -> {
            direction=0;
            setChosenArrowButton(0);
        });

        // down
        game_BTN_arrows[1].setOnClickListener(e -> {
            direction=1;
            setChosenArrowButton(1);
        });

        // right
        game_BTN_arrows[2].setOnClickListener(e -> {
            direction=2;
            setChosenArrowButton(2);
        });

        // left
        game_BTN_arrows[3].setOnClickListener(e -> {
            direction=3;
            setChosenArrowButton(3);
        });
    }

    /**
     * This function sets the game mode to sensors.
     */
    private void setSensorMode() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accSensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                NumberFormat formatter = new DecimalFormat("#0.00");

                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];

                    // move up through sensor
                    if(y < 3) {
                        direction=0;
                        setChosenArrowButton(0);
                    }
                    // move down through sensor
                    if(y > 8) {
                        direction=1;
                        setChosenArrowButton(1);
                    }
                    // move right through sensor
                    if(x < -3) {
                        direction=2;
                        setChosenArrowButton(2);
                    }
                    // move left through sensor
                    if(x > 3) {
                        direction=3;
                        setChosenArrowButton(3);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                Log.d("pttt", "onAccuracyChanged");
            }
        };
    }

    /**
     * This function sets the location settings of the user at the current game.
     */
    private void setLocationSettings() {
        Criteria locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setBearingRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        @SuppressLint("MissingPermission") ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                // Then update all the time and at every meters change.
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                        0, mLocationListener);
                                String providerName = mLocationManager.getBestProvider(locationCritera, true);
                                Location location  = mLocationManager.getLastKnownLocation(providerName);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                // Then update every limited time and at every limited meters change.
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                                        LOCATION_REFRESH_DISTANCE, mLocationListener);
                                String providerName = mLocationManager.getBestProvider(locationCritera, true);
                                Location location  = mLocationManager.getLastKnownLocation(providerName);
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } else {
                                // No location access granted.
                                // Then we can't use the location track and the 1st score location functionality is disabled.
                                // That's why we have set the default location to Ness Ziona(could be anywhere else just need default).
                            }
                        }
                );
        // check whether the app already has the permissions,
        // and whether the app needs to show a permission rationale dialog.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * This function set the chosen arrow opacity to 255 and the other arrows opacity to 128.
     * @param chosenArrowButton - The arrow that needs to be highlighted.
     */
    private void setChosenArrowButton(int chosenArrowButton) {
        for (int i = 0; i < 4; i++)
            if(i == chosenArrowButton)
                game_BTN_arrows[i].getBackground().setAlpha(255);
            else
                game_BTN_arrows[i].getBackground().setAlpha(128);
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

    /**
     * This function execute the choose direction function for the player.
     */
    private void selectedPlayerDirection() {
        chooseDirection(player,direction);
    }

    /**
     * This function randomize direction and execute the choose direction function for the hunter.
     */
    private void randomHunterDirection() {
        chooseDirection(hunter,(int) (Math.random() * 4));
    }

    /**
     * This function randomize location for the coin appearance on game board and set the coin image on the selected location.
     */
    private void randomCoinAppearance() {
        coinX = (int) (Math.random() * BOARD_X);
        coinY = (int) (Math.random() * BOARD_Y);
        // as long as the coin location equal to player or hunter keep randomizing new location
        while((coinX == player.getX() && coinY == player.getY()) || (coinX == hunter.getX() && coinY == hunter.getY())) {
            coinX = (int) (Math.random() * BOARD_X);
            coinY = (int) (Math.random() * BOARD_Y);
        }
        game_IMG_imgs[coinX][coinY].setImageResource(R.drawable.ic_coin);
    }

    /**
     * This function execute the move function in the direction that selected for the player/hunter.
     * @param p The selected character - player or hunter.
     * @param d The selected direction.
     */
    private void chooseDirection(Player p,int d) {
        switch (d) {
            case 0:
                moveUp(p);
                break;
            case 1:
                moveDown(p);
                break;
            case 2:
                moveRight(p);
                break;
            case 3:
                moveLeft(p);
                break;
            default:
                break;
        }
    }

    /**
     * This function move the character left.
     * @param p - The selected character - player or hunter.
     */
    private void moveLeft(Player p) {
        if (p.getY() > 0) {
            game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_web);
            p.setY(p.getY() - 1);
            if(p.getName().equalsIgnoreCase("Player"))
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_spiderman);
            else
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_goblin);
            updateUI();
        }
    }

    /**
     * This function move the character right.
     * @param p - The selected character - player or hunter.
     */
    private void moveRight(Player p) {
        if (p.getY() < BOARD_Y - 1) {
            game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_web);
            p.setY(p.getY() + 1);
            if(p.getName().equalsIgnoreCase("Player"))
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_spiderman);
            else
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_goblin);
            updateUI();
        }
    }

    /**
     * This function move the character down.
     * @param p - The selected character - player or hunter.
     */
    private void moveDown(Player p) {
        if (p.getX() < BOARD_X - 1) {
            game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_web);
            p.setX(p.getX() + 1);
            if(p.getName().equalsIgnoreCase("Player"))
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_spiderman);
            else
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_goblin);
            updateUI();
        }
    }

    /**
     * This function move the character up.
     * @param p - The selected character - player or hunter.
     */
    private void moveUp(Player p) {
        if (p.getX() > 0) {
            game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_web);
            p.setX(p.getX() - 1);
            if(p.getName().equalsIgnoreCase("Player"))
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_spiderman);
            else
                game_IMG_imgs[p.getX()][p.getY()].setImageResource(R.drawable.ic_goblin);
            updateUI();
        }
    }

    /**
     * This function update the scores and lives images according to their condition.
     */
    private void updateUI() {
        if(player.getX() == hunter.getX() && player.getY() == hunter.getY()) {
            if(gameManager.getScore() > maxScore)
                maxScore=gameManager.getScore();
            direction = -1;
            move = false;
            gameManager.reduceLives();
            gameManager.setScore();
            if(isVibration)
                vibrateOnce();
            updateBoardUI();
        }
        if(player.getX() == coinX && player.getY() == coinY) {
            coinX = -1;
            coinY = -1;
            gameManager.addCoinToScore();
        }
        else if(hunter.getX() == coinX && hunter.getY() == coinY) {
            coinX = -1;
            coinY = -1;
        }
        game_LBL_score.setText("" + gameManager.getScore());

        for (int i = 0; i < game_IMG_hearts.length; i++)
            game_IMG_hearts[i].setVisibility(gameManager.getLives() > i ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * This function update the game board images i.e player & hunter location and empty locations.
     */
    private void updateBoardUI() {
        if(gameManager.isDead()) {
            game_IMG_imgs[player.getX()][player.getY()].setImageResource(R.drawable.ic_grave);
            if(isVibration)
                vibrateOnce();
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
            finishGame();
            return;
        }
        else
            game_IMG_imgs[player.getX()][player.getY()].setImageResource(R.drawable.ic_web);
        initNewRound();
    }

    /**
     * This function sets the game board as it was at the start of the game every new round.
     */
    private void initNewRound() {
        player.setX(6);
        player.setY(2);
        hunter.setX(0);
        hunter.setY(2);
        game_IMG_imgs[player.getX()][player.getY()].setImageResource(R.drawable.ic_spiderman);
        game_IMG_imgs[hunter.getX()][hunter.getY()].setImageResource(R.drawable.ic_goblin);
        for (int i = 0; i < 4; i++)
            game_BTN_arrows[i].getBackground().setAlpha(255);
    }

    /**
     * This function find all views ids and sets them to their variables.
     */
    private void findViews() {
        game_IMG_imgs = new ImageView[][]{
                {findViewById(R.id.game_IMG_img1), findViewById(R.id.game_IMG_img2), findViewById(R.id.game_IMG_img3), findViewById(R.id.game_IMG_img4), findViewById(R.id.game_IMG_img5)},
                {findViewById(R.id.game_IMG_img6), findViewById(R.id.game_IMG_img7), findViewById(R.id.game_IMG_img8), findViewById(R.id.game_IMG_img9), findViewById(R.id.game_IMG_img10)},
                {findViewById(R.id.game_IMG_img11), findViewById(R.id.game_IMG_img12), findViewById(R.id.game_IMG_img13), findViewById(R.id.game_IMG_img14), findViewById(R.id.game_IMG_img15)},
                {findViewById(R.id.game_IMG_img16), findViewById(R.id.game_IMG_img17), findViewById(R.id.game_IMG_img18), findViewById(R.id.game_IMG_img19), findViewById(R.id.game_IMG_img20)},
                {findViewById(R.id.game_IMG_img21), findViewById(R.id.game_IMG_img22), findViewById(R.id.game_IMG_img23), findViewById(R.id.game_IMG_img24), findViewById(R.id.game_IMG_img25)},
                {findViewById(R.id.game_IMG_img26), findViewById(R.id.game_IMG_img27), findViewById(R.id.game_IMG_img28), findViewById(R.id.game_IMG_img29), findViewById(R.id.game_IMG_img30)},
                {findViewById(R.id.game_IMG_img31), findViewById(R.id.game_IMG_img32), findViewById(R.id.game_IMG_img33), findViewById(R.id.game_IMG_img34), findViewById(R.id.game_IMG_img35)}
    };
        game_IMG_hearts = new ImageView[] {
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };
        game_BTN_arrows = new ImageButton[] {
                findViewById(R.id.game_BTN_up),
                findViewById(R.id.game_BTN_down),
                findViewById(R.id.game_BTN_right),
                findViewById(R.id.game_BTN_left)
        };
        game_LBL_score = findViewById(R.id.game_LBL_score);
    }

    /**
     * This function finish the game and transfer all of the game data to the next activity.
     */
    private void finishGame() {
        Intent intent = new Intent(GameActivity.this, ScoresActivity.class);
        intent.putExtra("Sound",isSound);
        intent.putExtra("Vibration",isVibration);
        intent.putExtra("PlayAgain",true);
        intent.putExtra("Score",maxScore);
        intent.putExtra("Latitude",latitude);
        intent.putExtra("Longitude",longitude);
        startActivity(intent);
        finish();
    }

    private static class Player {
        private int x,y;
        private final String name;
        public Player(int x,int y,String name) { this.x=x; this.y=y; this.name=name; }
        public void setX(int x) { this.x=x; }
        public int getX() { return x; }
        public void setY(int y) { this.y=y; }
        public int getY() { return y; }
        public String getName() { return name; }
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
        if(isSensor)
            sensorManager.registerListener(accSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(isSound)
            ring.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensor)
            sensorManager.unregisterListener(accSensorEventListener);
        if(isSound)
            ring.pause();
    }

    // ---------- ---------- TIMER ---------- ----------

    private final int DELAY = 1000;
    private enum TIMER_STATUS {
        OFF,
        RUNNING,
        PAUSE
    }
    private TIMER_STATUS timerStatus = TIMER_STATUS.OFF;
    private Timer timer;

    private void startScoreTimer() {
        if (timerStatus == TIMER_STATUS.RUNNING) {
            stopTimer();
            timerStatus = TIMER_STATUS.OFF;
        } else {
            startTimer();
        }
    }

    private void tick() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectedPlayerDirection();
                if(!move) // disable movement at the first second( just for better view, not important )
                    move=true;
                else {
                    if(coinCounter++ == 10) {
                        coinCounter = 0;
                        randomCoinAppearance();
                    }
                    randomHunterDirection();
                    gameManager.addToScore();
                }
                game_LBL_score.setText("" + gameManager.getScore());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerStatus == TIMER_STATUS.RUNNING) {
            stopTimer();
            timerStatus = TIMER_STATUS.PAUSE;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (timerStatus == TIMER_STATUS.PAUSE)
            startTimer();
    }

    private void startTimer() {
        timerStatus = TIMER_STATUS.RUNNING;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        }, 0, DELAY);
    }

    private void stopTimer() { timer.cancel(); }
}