package com.example.hw1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;


public class SettingDialog extends Dialog {

    private int level = GamePlayFinals.MEDIUM;
    private int chosenLevel = GamePlayFinals.EASY;
    private boolean soundOn = true;
    private boolean accelerometerIsOn = false;
    private MediaPlayer introSong;


    public SettingDialog(@NonNull Context context, MediaPlayer introSong) {
        super(context);
        this.introSong = introSong;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        ImageView musicImage = findViewById(R.id.dialogMusicImage);
        Button howToPlayButton = findViewById(R.id.howToPlayButton);
        Button gameLevelButton = findViewById(R.id.levelButton);
        Button producer = findViewById(R.id.producerButton);
        Button accelerometer = findViewById(R.id.accelerometer);

        musicImage.setOnClickListener(r -> {
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

        howToPlayButton.setOnClickListener(play ->
                new AlertDialog.Builder(getContext())
                        .setTitle("How to play : ")
                        .setMessage("There are two options available: " +
                                "\n1. Move the player right or left while you are pressing on him.\n\n" +
                                "2. Move the player using your phone, while tilt right will move the player right and " +
                                "tile left will move the player to the left")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show());

        gameLevelButton.setOnClickListener(x -> {
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
                default:
                    break;
            }
            if (--level < 0) {
                level = GamePlayFinals.EASY;
            }
        });


        producer.setOnClickListener(prod -> new AlertDialog.Builder(getContext())
                .setTitle("Producers : ")
                .setMessage("Santa and his friends are in rehab, they aren\'t allowed to drink any kind of alcohol." +
                        "\nMove Santa or one of his friend to left or right while taping on your touch screen device.\n\n" +
                        "©Jonathan Karta & Gil Glick.")
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        accelerometer.setOnClickListener(acc -> {
            accelerometerIsOn = !accelerometerIsOn;
            if (accelerometerIsOn) {
                Toast.makeText(getContext(), "Accelerometer ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Accelerometer OFF ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getChosenLevel() {
        return chosenLevel;
    }

    public boolean getSound() {
        return soundOn;
    }

    public boolean getAccelerometer() {
        return accelerometerIsOn;
    }
}