package com.example.hw1;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.hw1.view.GameView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameManager {

    /**
     * Variables that managing the flow of the game
     */
    public volatile boolean playing = true;
    private int numOfLives = GamePlayFinals.NUM_OF_LIVES;
    private int gameScore = GamePlayFinals.INITIALIZED_SCORE;

    /**
     * All the views in the game
     */
    private GameCharacter player;
    private GameCharacter[] enemies;

    /**
     * Handle the game runtime events
     */
    private Handler handler = new Handler();
    private Timer scoreTask = new Timer();
    private Timer gameTask = new Timer();

    /**
     * Use to randomize different locations in the game
     */
    private Random rand = new Random();

    /**
     * Hold the gamePlayActivity context
     */
    private Context context;

    /**
     * Hold the GameView instance to handle all event in the game
     */
    private GameView gamePlayView;

    /**
     * Sound effect in the game
     */
    private MediaPlayer boomEffect;
    private MediaPlayer addLifeSoundEffect;

    // Object hold all the data of the user whose playing the game
    private GameUser gameUser;

    /**
     * Starting speed of the game
     */
    private int gameLevel = GamePlayFinals.GAME_STARTING_SPEED;

    /**
     * View Element in the game
     */
    private ImageView heart;
    private ImageView coins;
    private volatile boolean lifeExist = false;
    private volatile boolean coinExist= false;



    public GameManager(Context context, GameView gameView, GameUser gameUser) {
        this.context = context;
        this.gamePlayView = gameView;
        this.gameUser = gameUser;
        boomEffect = MediaPlayer.create(context, R.raw.hit);
        addLifeSoundEffect = MediaPlayer.create(context, R.raw.get_life);

    }

    public void generateGame(int gameLevel) {
        player = gamePlayView.createPlayer(gameUser);
        enemies = gamePlayView.createEnemies(gameLevel);
        heart = gamePlayView.createExtraLife();
        setLives(gamePlayView.getLifeList(), gameUser.getPlayerCharacter());
        coins = gamePlayView.createMoneyBag();
        runGame(gameLevel);
    }


    private void runGame(int level) {
        reScheduleTimer(gameLevel);

        scoreTask.schedule(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (!playing) {
                    handler.removeCallbacksAndMessages(null);
                    return;
                }
                handler.post(() -> {
                    gameScore += 1;
                    gamePlayView.getScore().setText("Score " + gameScore);
                    if (gameScore % GamePlayFinals.LEVEL_SPEED_UP - level * 3 == 0) {
                        gameTask.cancel();
                        gameLevel = gameLevel - 3 - level;
                        reScheduleTimer(gameLevel);
                    }
                });
            }
        }, 0, 1000);
    }

    private void reScheduleTimer(int gameSpeed) {
        if (gameSpeed < GamePlayFinals.MAX_GAME_SPEED)
            gameSpeed = GamePlayFinals.MAX_GAME_SPEED;

        gameTask = new Timer();
        TimerTask gamePlay = new TimerTask() {

            @Override
            public void run() {
                if (!playing) {
                    handler.removeCallbacksAndMessages(null);
                    return;
                }
                handler.post(() -> {
                    moveEnemy();
                    if (detectCollision()) {
                        boomEffect.start();
                        playerGetsHit();
                    }
                    randomizeLife();
                    randomizeCoins();
                });
            }
        };
        gameTask.schedule(gamePlay, 0, gameSpeed);
    }

    /**
     * Move each  enemy by 10 pixels on each frame
     */
    private void moveEnemy() {
        for (GameCharacter enemy : enemies) {
            enemy.setY(enemy.getY() + GamePlayFinals.Y_ENEMY_DISTANCE);
        }
        for (GameCharacter enemy : enemies) {
            if (enemy.getY() > gamePlayView.getGamePlayFrame().getHeight()) {
                setRandomLoc(enemy);
            }
        }
    }

    /**
     * Create random location which the enemy is going to be positioned
     */
    private void setRandomLoc(GameCharacter character) {
        character.setY(rand.nextInt(GamePlayFinals.BOTTOM_BOUND) - GamePlayFinals.TOP_BOUND);
        character.setX(rand.nextInt(gamePlayView.getWidth() / GamePlayFinals.CALC_FRAME) * GamePlayFinals.CALC_FRAME);
    }

    /**
     * This method check the position of player and the enemy and check for collide
     *
     * @return boolean if collision detected
     */
    private boolean detectCollision() {
        for (GameCharacter enemy : enemies) {
            if (collisionDetection(enemy, player)) {
                setRandomLoc(enemy);
                return true;
            }
        }
        return false;
    }

    /**
     * This method create animation if the player gets hit and check for his lives
     */
    private void playerGetsHit() {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(player, "rotation", 0f, 20f, 0f, -20f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.
        rotate.setRepeatCount(6);
        rotate.setDuration(30);
        rotate.start();
        if (numOfLives > 0) {
            gamePlayView.getLifeList()[--numOfLives].setVisibility(View.INVISIBLE);
        }
        if (numOfLives == 0) {
            gameOver();
        }
    }

    /**
     * This method make the main character move right or left on the screen
     */
    public boolean moveCharacter(MotionEvent event) {
        float centerX = player.getX() + (float) (GamePlayFinals.PLAYER_WIDTH / 2);
        boolean validRight = (event.getX() >= centerX) && (event.getX() <= centerX + GamePlayFinals.PLAYER_WIDTH * 2);
        boolean validLeft = (event.getX() <= centerX) && event.getX() >= (centerX - GamePlayFinals.PLAYER_WIDTH * 2);
        if ((event.getAction() == MotionEvent.ACTION_MOVE) && (event.getX() <= gamePlayView.getGamePlayFrame().getWidth() - (float) (GamePlayFinals.PLAYER_WIDTH / 2))
                && (event.getX() >= (float) (GamePlayFinals.PLAYER_WIDTH / 2)) && (validRight || validLeft)) {
            player.setX(event.getX() - (float) GamePlayFinals.PLAYER_WIDTH / 2);
            return true;
        }
        return false;
    }

    private void setLives(ImageView[] lifeList, int player) {
        for (ImageView life : lifeList) {
            life.setBackgroundResource(player);
        }
    }

    /**
     * Create random life to the player
     */
    private boolean raffleLife() {
        if (rand.nextInt(800) == 10) {
            heart.setX(rand.nextInt(gamePlayView.getWidth() / GamePlayFinals.PLAYER_WIDTH) * GamePlayFinals.PLAYER_WIDTH);
            heart.setY(rand.nextInt(GamePlayFinals.BOTTOM_BOUND) - GamePlayFinals.TOP_BOUND);
            heart.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    /**
     * Move the life down on the screen
     */
    private void moveDown() {
        if (heart.getY() - player.getWidth() > player.getY()) {
            heart.setVisibility(View.INVISIBLE);
            lifeExist = false;
        }
        heart.setY(heart.getY() + 10);

    }

    /**
     * Detect collision of the player with the heart.
     */
    private boolean heartCollision() {
        if (heart.getVisibility() == View.VISIBLE && collisionDetection(heart, player)) {
            heart.setY(0);
            heart.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    /**
     * If collision detected the user gets another life, else the user gets nothing.
     * If the user have already three life he also gets nothing.
     */
    private void addPlayerLife() {
        if (numOfLives < GamePlayFinals.NUM_OF_LIVES) {
            gamePlayView.getLifeList()[numOfLives].setVisibility(View.VISIBLE);
            addLifeSoundEffect.start();
            numOfLives++;
        }
    }
    private void randomizeLife(){
        if (!lifeExist)
            lifeExist = raffleLife();
        if (lifeExist && heartCollision()) {
            addPlayerLife();
        } else {
            moveDown();

        }
    }

    /**
     * Create random life to the player
     */
    private boolean raffleCoin() {
        if (rand.nextInt(500) == 10) {
            coins.setX(rand.nextInt(gamePlayView.getWidth() / GamePlayFinals.PLAYER_WIDTH) * GamePlayFinals.PLAYER_WIDTH);
            coins.setY(rand.nextInt(GamePlayFinals.BOTTOM_BOUND) - GamePlayFinals.TOP_BOUND);
            coins.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    /**
     * Move the life down on the screen
     */
    private void coinsMoveDown() {
        if (coins.getY() - player.getWidth() > player.getY()) {
            coins.setVisibility(View.INVISIBLE);
            coinExist = false;
        }
        coins.setY(coins.getY() + 7);

    }

    /**
     * Detect collision of the player with the heart.
     */
    private boolean coinCollision() {
        if (coins.getVisibility() == View.VISIBLE && collisionDetection(coins, player)) {
            coins.setY(0);
            coins.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    /**
     * If collision detected the user gets another life, else the user gets nothing.
     * If the user have already three life he also gets nothing.
     */
    private void addPlayerCoins() {
        gameScore += 10;
    }

    private void randomizeCoins(){
        if (!coinExist)
            coinExist = raffleCoin();
        if (coinExist && coinCollision()) {
            addPlayerCoins();
        } else {
            coinsMoveDown();
        }
    }
    public void setGameStatus(boolean playing) {
        this.playing = playing;
    }


    private boolean collisionDetection(ImageView collideElement, ImageView player) {
        return collideElement.getY() + collideElement.getHeight() - 50 > player.getY()
                && collideElement.getY() <= player.getY()
                && (collideElement.getX() >= player.getX() &&
                collideElement.getX() <= player.getX() + player.getWidth()
                || collideElement.getX() <= player.getX()
                && collideElement.getX() + collideElement.getWidth() >= player.getX());

    }

    /**
     * Accelerometer working partially
     */

    public void playerAccelerometer(float x) {
        if (player.getX() <= gamePlayView.getWidth() - player.getWidth() && player.getX() >= 0) {
            player.setX(player.getX() - x);
            if (player.getX() >= gamePlayView.getWidth() - player.getWidth()) {
                player.setX(gamePlayView.getWidth() - player.getWidth());
            } else if (player.getX() < 0) {
                player.setX(0);
            }
        }
    }

    /**
     * If game over detected move to game over view
     */
    private void gameOver() {
        gameUser.setScore(gameScore);
        refreshGame();
        Intent intent = new Intent(context, EndGameActivity.class);
        intent.putExtra(GamePlayFinals.USER_OBJECT, gameUser);
        context.startActivity(intent);
        ((GamePlayActivity) context).finish();

    }


    private void refreshGame() {
        playing = false;
        numOfLives = GamePlayFinals.NUM_OF_LIVES;
    }


}