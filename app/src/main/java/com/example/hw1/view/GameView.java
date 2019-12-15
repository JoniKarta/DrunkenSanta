package com.example.hw1.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hw1.R;
import com.example.hw1.GameCharacter;
import com.example.hw1.GamePlayFinals;
import com.example.hw1.GameUser;

import java.util.Random;


public class GameView extends LinearLayout {

    private FrameLayout gamePlayFrame;

    private FrameLayout topGamePlayFrame;

    private LinearLayout container;

    private ImageView[] lifeList;

    private TextView score;

    private Random rand = new Random();

    public GameView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.game_background);
        setOrientation(VERTICAL);
        configGamePlayTopFrame();
        configGamePlayFrame();
        setTopFrameAttr();

    }

    private void configGamePlayTopFrame() {
        topGamePlayFrame = new FrameLayout(getContext());
        FrameLayout.LayoutParams topFrameParams = new FrameLayout.LayoutParams(GamePlayFinals.FRAME_PARENT, GamePlayFinals.FRAME_WRAPPED);
        topGamePlayFrame.setLayoutParams(topFrameParams);
        addView(topGamePlayFrame);
    }

    private void configGamePlayFrame() {
        gamePlayFrame = new FrameLayout(getContext());
        FrameLayout.LayoutParams gamePlayParams = new FrameLayout.LayoutParams(GamePlayFinals.FRAME_PARENT, GamePlayFinals.FRAME_PARENT);
        gamePlayFrame.setLayoutParams(gamePlayParams);
        addView(gamePlayFrame);
    }

    private void setTopFrameAttr() {
        createContainer();
        container.addView(createScore());
        container.addView(createTextLives());
        lifeList = new ImageView[GamePlayFinals.NUM_OF_LIVES];
        for (int i = 0; i < GamePlayFinals.NUM_OF_LIVES; i++) {
            container.addView(lifeList[i] = createLife());
        }
        topGamePlayFrame.addView(container);
    }

    private void createContainer() {
        container = new LinearLayout(getContext());
        LinearLayout.LayoutParams livesLayoutParams = new LinearLayout.LayoutParams(GamePlayFinals.LINEAR_PARENT, GamePlayFinals.LINEAR_WRAPPED);
        container.setGravity(Gravity.END);
        container.setOrientation(HORIZONTAL);
        container.setLayoutParams(livesLayoutParams);
    }

    private TextView createScore() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GamePlayFinals.LINEAR_WRAPPED, GamePlayFinals.LINEAR_WRAPPED);
        params.setMargins(30, 30, 30, 30);
        score = new TextView(getContext());
        score.setLayoutParams(params);
        score.setTextColor(Color.WHITE);
        score.setTextSize(GamePlayFinals.SCORE_TEXT_SIZE);
        score.setText(R.string.score);
        return score;
    }


    private TextView createTextLives() {
        TextView textLives = new TextView(getContext());
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(GamePlayFinals.LINEAR_WRAPPED, GamePlayFinals.LINEAR_WRAPPED);
        textViewParams.setMargins(30, 30, 30, 30);
        textLives.setText(R.string.lives);
        textLives.setTextColor(Color.WHITE);
        textLives.setLayoutParams(textViewParams);
        return textLives;
    }

    private ImageView createLife() {
        ImageView life = new ImageView(getContext());
        life.setLayoutParams(new android.view.ViewGroup.LayoutParams(GamePlayFinals.LIVES_WIDTH, GamePlayFinals.LIVES_HEIGHT));
        return life;
    }

    public GameCharacter createPlayer(GameUser gameUser) {
        GameCharacter player = new GameCharacter(getContext(), gamePlayFrame.getWidth() / 2,
                gamePlayFrame.getHeight() - (int) (GamePlayFinals.PLAYER_HEIGHT * 1.75), GamePlayFinals.PLAYER_WIDTH, GamePlayFinals.PLAYER_HEIGHT);
        player.setBackgroundResource(gameUser.getPlayerCharacter());
        gamePlayFrame.addView(player);
        return player;
    }

    /**
     * This method handle the creation of all the enemies
     */
    public GameCharacter[] createEnemies() {
        int[] images = {R.drawable.whisky, R.drawable.vodka, R.drawable.beers, R.drawable.bloodymary};
        GameCharacter[] enemies = new GameCharacter[gamePlayFrame.getWidth() / GamePlayFinals.CALC_FRAME];
        for (int i = 0, current = 0; i < enemies.length; i++) {
            enemies[i] = new GameCharacter(getContext(), current,
                    rand.nextInt(GamePlayFinals.BOTTOM_BOUND) - GamePlayFinals.TOP_BOUND, GamePlayFinals.ENEMY_WIDTH, GamePlayFinals.ENEMY_HEIGHT);
            enemies[i].setBackgroundResource(images[rand.nextInt(images.length)]);
            current = current + GamePlayFinals.CALC_FRAME;
            gamePlayFrame.addView(enemies[i]);
        }
        return enemies;
    }

    public ImageView createExtraLife() {
        ImageView extraLife = new ImageView(getContext());
        extraLife.setImageResource(R.drawable.heart);
        extraLife.setLayoutParams(new android.view.ViewGroup.LayoutParams(GamePlayFinals.LIVES_WIDTH, GamePlayFinals.LIVES_HEIGHT));
        extraLife.setVisibility(View.INVISIBLE);
        gamePlayFrame.addView(extraLife);
        return extraLife;
    }


    public FrameLayout getGamePlayFrame() {
        return gamePlayFrame;
    }

    public ImageView[] getLifeList() {
        return lifeList;
    }

    public TextView getScore() {
        return score;
    }


}
