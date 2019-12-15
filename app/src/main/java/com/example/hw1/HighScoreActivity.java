package com.example.hw1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.hw1.view.HighScoreFragment;
import com.example.hw1.view.UserMapFragment;


public class HighScoreActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_holder);
        addHighScoreFragment();
        addMapFragment();
    }

    // This function add the game score fragment to the HighScoreActivity
    public void addHighScoreFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        HighScoreFragment highScore = new HighScoreFragment();
        fragmentTransaction.add(R.id.highScoreLayout, highScore);
        fragmentTransaction.commit();
    }

    // This function add the map fragment to the HighScoreActivity
    public void addMapFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        UserMapFragment mapFragment1 = new UserMapFragment();
        fragmentTransaction.add(R.id.mapLayout, mapFragment1);
        fragmentTransaction.commit();
    }

}
