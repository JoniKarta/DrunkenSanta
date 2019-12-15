package com.example.hw1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hw1.utilities.MySharedPreferences;
import com.example.hw1.view.EndGameView;

public class EndGameActivity extends AppCompatActivity {

    private MediaPlayer endGameSong;

    private GameUser gameUser;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configGameWindow();
        endGameSong = MediaPlayer.create(this, R.raw.endsound);
        EndGameView endGameView;
        setContentView(endGameView = new EndGameView(this));
        gameUser = getIntent().getParcelableExtra(GamePlayFinals.USER_OBJECT);
        Toast.makeText(this,"USER RESULT IS: " + gameUser,Toast.LENGTH_LONG).show();
        MySharedPreferences shared = new MySharedPreferences(getApplicationContext());
        shared.add(gameUser);

        /* On click event, which transfer the client play another game*/
        endGameView.getPlayAgain().setOnClickListener((e) -> {
            Intent intent = new Intent(this, GamePlayActivity.class);
            intent.putExtra(GamePlayFinals.USER_OBJECT,gameUser);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        /* On click event, which transfer the client to main activity*/
        endGameView.getExitGame().setOnClickListener((e) -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        /* On click event, which transfer the client to HighScoreActivity*/
        endGameView.getHighScore().setOnClickListener((e)->{
            Intent intent = new Intent(this, HighScoreActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }

    /**
     * This method configure the window in the game.
     * The game will be displayed on full screen.
     */
    public void configGameWindow() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onBackPressed() {
        if (endGameSong != null) {
            endGameSong.stop();
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
        if (endGameSong != null)
            endGameSong.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (endGameSong != null)
            endGameSong.start();
    }
}