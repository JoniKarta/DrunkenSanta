package com.example.hw1;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GamePlayActivity extends AppCompatActivity implements Finals {

    /** Variables that managing the flow in the game*/
    private volatile boolean playing = true;
    private int numOfLives = NUM_OF_LIVES;
    private int gameLevel = GAME_SPEED;
    private int gameScore = INITIALIZED_SCORE;

    /** Variables for the views in the game*/
    private FrameLayout gamePlayFrame;
    private LinearLayout gameOverLayout;
    private LinearLayout topGameFrame;

    /** All the views in the game */
    private GameCharacter player;
    private GameCharacter[] enemies;
    private ImageView[] lifeList;

    /** Variables for the end of the game */
    private Button playAgain;
    private Button exitGame;
    private TextView score;
    private MediaPlayer boom;

    /** Handle all the UI in the game */
    private Handler handler = new Handler();

    /** This timer handle the score which has it's own time in the game */
    private Timer scoreTimer = new Timer();

    /** This timer handle the routine of the game */
    private Timer gamePlayTimer = new Timer();

    /** Use to randomize different locations in the game */
    private Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_screen);
        gamePlayFrame = findViewById(R.id.gamePlay);
        topGameFrame = findViewById(R.id.gameLives);
        gamePlayFrame.post(this::createGame);
        gamePlayFrame.post(this::runGame);
        boom = MediaPlayer.create(this, R.raw.hit);
    }


    private void createGame() {
        createPlayer();
        createEnemies();
        createScore();
        createLives();
        createGameOverView();
    }


    public void createPlayer() {
        player = new GameCharacter(this, gamePlayFrame.getWidth() / 2, gamePlayFrame.getHeight() - (int) (PLAYER_HEIGHT * 1.75), PLAYER_WIDTH, PLAYER_HEIGHT);
        player.setBackgroundResource(R.drawable.player);
        gamePlayFrame.addView(player);
    }

    public void createEnemies() {
        int[] images = {R.drawable.whisky,R.drawable.vodka,R.drawable.beers,R.drawable.bloodymary};
        enemies = new GameCharacter[gamePlayFrame.getWidth() / CALC_FRAME];
        for (int i = 0, current = 0; i < enemies.length; i++) {
            enemies[i] = new GameCharacter(this, current, rand.nextInt(BOTTOM_BOUND) - TOP_BOUND, ENEMY_WIDTH, ENEMY_HEIGHT);
            enemies[i].setBackgroundResource(images[rand.nextInt(images.length)]);
            current = current + CALC_FRAME;
            gamePlayFrame.addView(enemies[i]);
        }
    }

    public void createScore() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAPPED, WRAPPED);
        params.setMargins(30, 30, 30, 30);
        score = new TextView(this);
        score.setLayoutParams(params);
        score.setTextColor(Color.WHITE);
        score.setTextSize(15);
        score.setText(R.string.score + gameScore);
        topGameFrame.addView(score);

    }

    private void createLives() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAPPED, WRAPPED);
        params.setMargins(30, 30, 30, 30);
        lifeList = new ImageView[numOfLives];
        TextView textLives = new TextView(this);
        textLives.setText(R.string.lives);
        textLives.setTextColor(Color.WHITE);
        textLives.setLayoutParams(params);
        topGameFrame.addView(textLives);

        for (int i = 0; i < numOfLives; i++) {
            lifeList[i] = new ImageView(this);
            lifeList[i].setBackgroundResource(R.drawable.gamelives);
            lifeList[i].setLayoutParams(new android.view.ViewGroup.LayoutParams(LIVES_WIDTH, LIVES_HEIGHT));
            topGameFrame.addView(lifeList[i]);
        }
    }

    public void runGame() {
        reScheduleTimer(gameLevel);

        scoreTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!playing)
                    return;
                handler.post(() -> {
                    gameScore += 1;
                    score.setText("Score: " + gameScore);
                    if (gameScore % LEVEL_SPEED_UP  == 0) {
                        gamePlayTimer.cancel();
                        gameLevel = gameLevel - 5;
                        reScheduleTimer(gameLevel);
                    }
                });
            }
        }, 0, 1000);
    }


    public void reScheduleTimer(int duration) {
        if(duration < MAX_DURATION)
            duration = MAX_DURATION;

        gamePlayTimer = new Timer("alertTimer", true);
        TimerTask gamePlay = new TimerTask() {

            @Override
            public void run() {
                if (!playing)
                    return;
                handler.post(() -> {
                    moveEnemy();
                    if (detectCollision()) {
                        boom.start();
                        playerGetsHit();
                    }
                });
            }
        };
        gamePlayTimer.schedule(gamePlay, 0, duration);
    }



    private void moveEnemy() {
        for (GameCharacter enemy : enemies)
            enemy.setY(enemy.getY() + 10);

        for (GameCharacter enemy : enemies) {
            if (enemy.getY() > gamePlayFrame.getHeight())
                enemy.setY(rand.nextInt(BOTTOM_BOUND) - TOP_BOUND);
        }
    }

    public boolean detectCollision() {
        for (GameCharacter enemy : enemies) {
            if (enemy.getY() + enemy.getHeight() - 50 > player.getY() && enemy.getY() <= player.getY()
                    && (enemy.getX() >= player.getX() && enemy.getX() <= player.getX() + player.getWidth()
                    || enemy.getX() <= player.getX() && enemy.getX() + enemy.getWidth() >= player.getX())) {
                enemy.setY(rand.nextInt(BOTTOM_BOUND) - TOP_BOUND);
                return true;
            }
        }
        return false;
    }

    public void playerGetsHit() {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(player, "rotation", 0f, 20f, 0f, -20f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.
        rotate.setRepeatCount(6);
        rotate.setDuration(30);
        rotate.start();
        if (numOfLives > 0)
            lifeList[--numOfLives].setVisibility(View.INVISIBLE);
        if (numOfLives == 0) {
            gameOver();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float centerX= player.getX() + (float)(PLAYER_WIDTH/ 2);
        boolean validRight = (event.getX() >= centerX) && (event.getX() <= centerX + PLAYER_WIDTH * 2);
        boolean validLeft = (event.getX() <= centerX) && event.getX() >= (centerX - PLAYER_WIDTH*2);
        if (event.getAction() == MotionEvent.ACTION_MOVE && event.getX() <= gamePlayFrame.getWidth() - (float)(PLAYER_WIDTH/2)
                && (validRight || validLeft)) {
            player.setX(event.getX() - (float)PLAYER_WIDTH/2);
            return true;
        }
        return false;
    }

    public void gameOver() {
        // Stop the game
        playing = false;

        // Clear all game view from the screen
        for (GameCharacter enemy : enemies)
            enemy.setVisibility(View.INVISIBLE);
        player.setVisibility(View.INVISIBLE);

        gameOverLayout.setVisibility(View.VISIBLE);

        playAgain.setOnClickListener((e) -> {
            gameOverLayout.setVisibility(View.INVISIBLE);
            restartGame();
            reScheduleTimer(GAME_SPEED);
            playing = true;
            //runGame();
        });

        exitGame.setOnClickListener((e) -> {
            playing = false;
            MainActivity.gamePlaySoundtrack.stop();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            restartGame();

        });

    }

    public void restartGame() {
        numOfLives = NUM_OF_LIVES;
        gameScore = INITIALIZED_SCORE;
        gameLevel = GAME_SPEED;
        for (ImageView life : lifeList)
            life.setVisibility(View.VISIBLE);

        for (GameCharacter enemy : enemies) {
            enemy.setVisibility(View.VISIBLE);
            enemy.setY(rand.nextInt(BOTTOM_BOUND) - TOP_BOUND);
        }
        player.setVisibility(View.VISIBLE);
        gamePlayTimer.cancel();

    }

    public void createGameOverView() {
        // Initializing game over views
        playAgain = new Button(this);
        exitGame = new Button(this);
        TextView gameOver = new TextView(this);

        // Configuration of game over layout
        gameOverLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        gameOverLayout.setGravity(Gravity.CENTER);
        gameOverLayout.setOrientation(LinearLayout.VERTICAL);
        gameOverLayout.addView(gameOver);
        gameOverLayout.addView(playAgain);
        gameOverLayout.addView(exitGame);

        // Configuration of TextView
        params.setMargins(50, 20, 20, 20);
        gameOver.setLayoutParams(params);
        gameOver.setTextColor(Color.WHITE);
        gameOver.setText(R.string.gameOver);
        gameOver.setTextSize(50);

        // Configure margin to buttons
        params.setMargins(20, 20, 20, 20);

        // Configuration of playAgain button
        playAgain.setBackgroundResource(R.drawable.play_again_button);
        playAgain.setTextColor(Color.WHITE);
        playAgain.setText(R.string.playAgain);
        playAgain.setLayoutParams(new LinearLayout.LayoutParams(gamePlayFrame.getWidth() / 2, 100));
        playAgain.setLayoutParams(params);

        // Configuration of exit button
        exitGame.setBackgroundResource(R.drawable.exit_game_button);
        exitGame.setTextColor(Color.WHITE);
        exitGame.setText(R.string.exitGame);
        exitGame.setLayoutParams(new LinearLayout.LayoutParams(gamePlayFrame.getWidth() / 2, 100));
        exitGame.setLayoutParams(params);


        gameOverLayout.setVisibility(View.INVISIBLE);
        gamePlayFrame.addView(gameOverLayout);

        // Create rotation animation to the text view
        ObjectAnimator rotate = ObjectAnimator.ofFloat(gameOver, "rotation", 0f, 360f); // rotate o degree then 20 degree and so on for one loop of rotation.
        rotate.setDuration(1000);
        rotate.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.gamePlaySoundtrack.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.gamePlaySoundtrack.pause();
        playing = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.gamePlaySoundtrack.start();
        playing = true;

    }


}

