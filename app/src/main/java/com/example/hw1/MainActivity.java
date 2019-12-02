package com.example.hw1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    public static MediaPlayer gamePlaySoundtrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Button startGame = findViewById(R.id.playButton);
      new Thread(()-> {
            gamePlaySoundtrack = MediaPlayer.create(this, R.raw.gamesong);
            gamePlaySoundtrack.setLooping(true);
            gamePlaySoundtrack.start();
        }).start();

        startGame.setOnClickListener((e)-> {
            AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
            e.startAnimation(buttonClick);
            Intent intent = new Intent(getApplicationContext(), GamePlayActivity.class);
            startActivity(intent);
        });

        Button exit = findViewById(R.id.exitButton);
        exit.setOnClickListener((e)->{
            gamePlaySoundtrack.stop();
            finish();
        });

    }


}
