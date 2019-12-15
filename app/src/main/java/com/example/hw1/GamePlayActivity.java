package com.example.hw1;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hw1.view.GameView;


public class GamePlayActivity extends AppCompatActivity implements SensorEventListener2 {

    /* Manage all the view in the game */
    private GameView gameView;

    /* Manage all the game logic */
    private GameManager gameManager;

    /* Playing the main song in the game */
    private MediaPlayer gamePlaySong;

    /* Check if the user want to hear song in the game */
    private boolean soundOn;
    private SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configGameWindow();
        setContentView(gameView = new GameView(this));
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gamePlaySong = MediaPlayer.create(this, R.raw.gamesong);
        gameView.post(this::runGame);
    }

    /**
     * This method configure the window in the game.
     * The game will be displayed on full screen.
     */
    public void configGameWindow() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    /**
     * Running the game with all configuration
     */
    private void runGame() {
        soundOn = getIntent().getBooleanExtra(GamePlayFinals.MUSIC_SERVICE, true);
        if (soundOn) {
            gamePlaySong.start();
        }
        GameUser gameUser = getIntent().getParcelableExtra(GamePlayFinals.USER_OBJECT);
        if (gameUser != null) {
            gameManager = new GameManager(this, gameView, gameUser);
            gameManager.generateGame(getIntent().getIntExtra(GamePlayFinals.GAME_LEVEL, GamePlayFinals.EASY));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gameManager.moveCharacter(event);
    }

    @Override
    public void onBackPressed() {
        if (gamePlaySong != null) {
            gamePlaySong.stop();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);

        if (gameManager != null && gamePlaySong != null) {
            gameManager.setGameStatus(false);
            gamePlaySong.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameManager != null && gamePlaySong != null) {
            gameManager.setGameStatus(true);
            if (soundOn) {
                gamePlaySong.start();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accelerationX;
        //float accelerationY;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerationX = event.values[0];
            //accelerationY = -event.values[1];
            if (gameManager != null) {
                gameManager.updatePlayer(accelerationX);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}