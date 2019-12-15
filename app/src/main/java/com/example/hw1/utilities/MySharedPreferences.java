package com.example.hw1.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.hw1.GameUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

public class MySharedPreferences {

    private SharedPreferences shared;
    private ArrayList<GameUser> list;

    public MySharedPreferences(Context context) {
        shared = context.getSharedPreferences("App", MODE_PRIVATE);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void add(GameUser gameUser, Comparator<GameUser> comparator) {
            list = read();
            if (!list.contains(gameUser)) {
                Log.i(null, "NOT INSIDE HERE");
                list.add(gameUser);
                list.sort(comparator);
                writeDataToStorage(list);
            } else {
                int idx = list.indexOf(gameUser);
                GameUser currentUser = list.get(idx);
                if (currentUser.getScore() < gameUser.getScore()) {
                    list.remove(currentUser);
                    list.add(gameUser);
                    list.sort(comparator);
                    writeDataToStorage(list);
                }
            }
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
        if((list = gson.fromJson(json, type)) == null)
            list = new ArrayList<>();
        return list;
    }

    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }


}
