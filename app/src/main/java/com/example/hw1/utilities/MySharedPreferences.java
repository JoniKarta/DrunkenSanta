package com.example.hw1.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.hw1.GameUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPreferences {

    private SharedPreferences shared;
    private ArrayList<GameUser> list;

    public MySharedPreferences(Context context) {
        shared = context.getSharedPreferences("App", MODE_PRIVATE);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void add(GameUser gameUser) {
        //clearSharedPreferences();
        if ((list = read()) != null) {
            list.add(gameUser);
        } else {
            list = new ArrayList<>();
            list.add(gameUser);
        }
        list.sort((o1, o2) -> {
            if (o1.getScore() == o2.getScore()) {
                return 0;
            }
            return o1.getScore() < o2.getScore() ? 1 : -1;
        });

        writeDataToStorage(list);
    }


    private void writeDataToStorage(ArrayList<GameUser> list) {
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("task_list", json);
        editor.apply();
    }

    public ArrayList<GameUser> read() {
        Gson gson = new Gson();
        String json = shared.getString("task_list", null);
        Type type = new TypeToken<ArrayList<GameUser>>() {
        }.getType();
        list = gson.fromJson(json, type);
        return list;
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }


}
