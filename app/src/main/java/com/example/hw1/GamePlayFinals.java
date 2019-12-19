package com.example.hw1;

import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GamePlayFinals {

    /** Specify fixed size for the player image height */
    public static final int PLAYER_HEIGHT = 175;

    /** Specify fixed size for the player image width */
    public static final int PLAYER_WIDTH = 150;

    /** Specify fixed size for the enemy image height */
    public static final int ENEMY_HEIGHT = 200;

    /** Specify fixed size for the enemy image width */
    public static final int ENEMY_WIDTH = 140;

    /** Specify fixed size for the lives image height */
    public static final int ELEMENT_HEIGHT = 100;

    /** Specify fixed size for the lives image width */
    public static final int ELEMENT_WIDTH = 100;


    /** Define the number of lives the user start with */
    public static final int NUM_OF_LIVES = 3;

    /** Define the bottom bound which calculated when positioning the views enemies */
    public static final int BOTTOM_BOUND = 1000;

    /** Define the top bound which calculated when positioning the views enemies */
    public static final int TOP_BOUND = 1400;

    /** Helper variable for calculation of top bound and bottom bound*/
    public static final int CALC_FRAME = 180;


    /** Size of text Score in use in the view package */
    public static final int SCORE_TEXT_SIZE = 15;

    /** Fixed Range for the enemy's movements*/
    public static final int Y_ENEMY_DISTANCE = 10;

    /** Initializing the socre of the player to 0*/
    public static final int INITIALIZED_SCORE = 0;

    /** Game levels variables */
    public static final int EASY = 2;
    public static final int MEDIUM = 1;
    public static final int HARD = 0;

    /** Variable that indicate when the level's up*/
    public static final int LEVEL_SPEED_UP = 20;

    /**The maximum speed of the last level the game can hold*/
    public static final int MAX_GAME_SPEED = 5;

    /** Initialize the starting speed of the game */
    public static final int GAME_STARTING_SPEED = 30;

    /** THREE OPTIONS TO THE GAME IS HARDNESS */
    public static final int GAME_LEVELS_OPTIONS = 3;



    public static final String MUSIC_SERVICE = "Music";
    public static final String ACCELEROMETER_SERVICE = "Accelerometer_service";
    public static final String GAME_LEVEL = "GameLevel";
    public static final String USER_OBJECT = "User";

    public static final int LINEAR_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    public static final int LINEAR_WRAPPED = LinearLayout.LayoutParams.WRAP_CONTENT;
    public static final int FRAME_PARENT = FrameLayout.LayoutParams.MATCH_PARENT;
    public static final int FRAME_WRAPPED = FrameLayout.LayoutParams.WRAP_CONTENT;



//    // GOOGLE MAPS CONSTANTS
//    public static final int ERROR_DIALOG_REQUEST = 9001;
//    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
//    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

}




