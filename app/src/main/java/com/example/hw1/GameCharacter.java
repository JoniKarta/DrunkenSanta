package com.example.hw1;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

public class GameCharacter extends AppCompatImageView {

    public GameCharacter(Context context){
        super((context));
    }

    public GameCharacter(Context context,float x, float y,int width, int height) {
        super(context);
        setX(x);
        setY(y);
        setLayoutParams(new android.view.ViewGroup.LayoutParams(width, height));
    }
}
