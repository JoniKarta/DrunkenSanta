package com.example.hw1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hw1.utilities.MySharedPreferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.hw1.GamePlayFinals.ACCELEROMETER_SERVICE;


public class MainActivity extends AppCompatActivity {

    /* Variable for lunching the game*/
    private Button startGame;

    /* Variable includes all players available in the game*/
    private LinearLayout playersLayout;

    /* Variable to insert the user name */
    private EditText userNameEditText;

    /* Hold the current song in the game */
    private MediaPlayer introSong;

    /**
     * These Section of Variables refer to game setting.
     * The game settings is a custom dialog box which includes
     * all the operation for the user's gamePlay settings
     */

    /* Custom dialog that show the setting in the game*/
    private Dialog settingDialog;

    /* Set the game easy by default */
    private int level = GamePlayFinals.MEDIUM;

    /* Set the user's chosen level */
    private int chosenLevel = GamePlayFinals.EASY;

    /* Set the user's sound choice */
    private boolean soundOn = true;

    /* Set the user's accelerometer choice */
    private boolean accelerometerIsOn = false;

    /* Hold the user's current location */
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configGameWindow();
        setContentView(R.layout.activity_main);

        // Setting permissions from the user to get the location
        requestPermission();
        accessClientLocation();

        // Getting the relevant widgets and views from the main xml
        startGame = findViewById(R.id.playButton);
        playersLayout = findViewById(R.id.gamePlayers);
        Button settingBtn = findViewById(R.id.settingButton);
        userNameEditText = findViewById(R.id.userName);
        Button highScore = findViewById(R.id.highScoreButton);

        // Setting the game music
        introSong = MediaPlayer.create(this, R.raw.introsound);
        introSong.setLooping(true);


        // Create new dialog which hold the setting in the whole game
        settingDialog = new Dialog(this);
        settingDialog.setContentView(R.layout.setting);

        // Getting the relevant widgets and views from the setting xml
        ImageView musicImage = settingDialog.findViewById(R.id.dialogMusicImage);
        Button howToPlayButton = settingDialog.findViewById(R.id.howToPlayButton);
        Button gameLevelButton = settingDialog.findViewById(R.id.levelButton);
        Button producer = settingDialog.findViewById(R.id.producerButton);

        /* Set the user's accelerometer on or off */
        Button accelerometer = settingDialog.findViewById(R.id.accelerometer);

         /* Start event, this event will trigger the game and gives the
            user list of player which will he could choose.
         */
        startGame.setOnClickListener((e) -> {
            if (userNameEditText.getText().toString().trim().length() > 0 && userLocation != null) {
                startGame.setEnabled(false);
                userNameEditText.setEnabled(false);
                AlphaAnimation playerClick = new AlphaAnimation(1F, 0.6F);
                int[] images = {R.drawable.dragonsanta, R.drawable.elfsanta, R.drawable.santa};
                ImageView[] players = new ImageView[images.length];
                for (int i = 0; i < players.length; i++) {
                    final int j = i;
                    players[j] = createPlayers(images[j]);
                    playersLayout.addView(players[j]);
                    players[j].setOnClickListener((v) -> {
                        v.startAnimation(playerClick);
                        Intent intent = new Intent(getApplicationContext(), GamePlayActivity.class);
                        GameUser gameUser = new GameUser(userNameEditText.getText().toString(), userLocation.getAltitude(), userLocation.getLatitude(), GamePlayFinals.INITIALIZED_SCORE, images[j]);
                        // Passing the argument to the gamePlay
                        intent.putExtra(GamePlayFinals.USER_OBJECT, gameUser).putExtra(GamePlayFinals.MUSIC_SERVICE, soundOn).putExtra(ACCELEROMETER_SERVICE, accelerometerIsOn);
                        // setting the level in the game
                        intent.putExtra(GamePlayFinals.GAME_LEVEL, chosenLevel);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                    });
                }
            } else {
                Toast.makeText(this, "ENTER YOUR NAME ", Toast.LENGTH_SHORT).show();
            }
        });

        // Transfer the user to the high score Class
        highScore.setOnClickListener((score) -> {
            Intent intent = new Intent(getApplicationContext(), HighScoreActivity.class);
            startActivity(intent);
        });

        /* Setting event, this event will show the player a popup.
           The user could choose his preferred  option in the game.
        */
        settingBtn.setOnClickListener((e) -> {
            settingDialog.show();
            musicImage.setOnClickListener((r) -> {
                soundOn = !soundOn;
                if (!soundOn) {
                    musicImage.setBackgroundResource(R.drawable.soundoff);
                    introSong.pause();
                } else {
                    musicImage.setBackgroundResource(R.drawable.soundon);
                    introSong.seekTo(0);
                    introSong.start();
                }
            });


            /* This event will show the player a description of how to play the game.*/
            howToPlayButton.setOnClickListener((play) ->
                    new AlertDialog.Builder(this)
                            .setTitle("How to play : ")
                            .setMessage("There are two options available: " +
                                    "\n1. Move the player right or left while you are pressing on him.\n\n" +
                                    "2. Move the player using your phone, while tilt right will move the player right and " +
                                    "tile left will move the player to the left")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show());


            /* This event will show the player the game options .*/
            gameLevelButton.setOnClickListener((x) -> {
                switch (level % GamePlayFinals.GAME_LEVELS_OPTIONS) {
                    case GamePlayFinals.HARD:
                        gameLevelButton.setText(R.string.level_hard);
                        chosenLevel = GamePlayFinals.HARD;
                        break;
                    case GamePlayFinals.MEDIUM:
                        gameLevelButton.setText(R.string.level_medium);
                        chosenLevel = GamePlayFinals.MEDIUM;
                        break;
                    case GamePlayFinals.EASY:
                        gameLevelButton.setText(R.string.level_easy);
                        chosenLevel = GamePlayFinals.EASY;
                        break;
                }
                level--;
                if(level < 0){
                    level = GamePlayFinals.EASY;
                }
            });


            /* This event will show the player a description of the producers which create the game.*/
            producer.setOnClickListener((v) -> new AlertDialog.Builder(this)
                    .setTitle("Producers : ")
                    .setMessage("Santa and his friends are in rehab, they aren\'t allowed to drink any kind of alcohol." +
                            "\nMove Santa or one of his friend to left or right while taping on your touch screen device.\n\n" +
                            "©Jonathan Karta & Gil Glick.")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show());
        });

        accelerometer.setOnClickListener((v) -> {
            accelerometerIsOn = !accelerometerIsOn;
            if (accelerometerIsOn) {
                Toast.makeText(getApplicationContext(), "Accelerometer ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Accelerometer OFF ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method configure the window in the game.
     * The game will be displayed on full screen.
     */
    private void configGameWindow() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * This creates players to the game.
     *
     * @return ImageView of new player.
     */
    private ImageView createPlayers(int resource) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(GamePlayFinals.PLAYER_WIDTH, GamePlayFinals.PLAYER_HEIGHT);
        params.setMargins(50, 50, 50, 50);
        ImageView player = new ImageView(this);
        player.setLayoutParams(params);
        player.setBackgroundResource(resource);
        return player;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void accessClientLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "PERMISSION ACCEPTED", Toast.LENGTH_SHORT).show();
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, (location) -> {
            if (location != null) {
                userLocation = location;
            }
        });

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finish();
                    if (introSong != null) {
                        introSong.stop();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel()).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (introSong != null) {
            introSong.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (introSong != null) {
            introSong.pause();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (imm != null) {
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);

    }


}