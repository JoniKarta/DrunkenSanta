package com.example.hw1.view;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw1.GameRecyclerAdapter;
import com.example.hw1.R;
import com.example.hw1.GameUser;
import com.example.hw1.utilities.MySharedPreferences;

import java.util.ArrayList;

public class HighScoreFragment extends Fragment {
    View v;
    private ArrayList<GameUser> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_highscore, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        GameRecyclerAdapter exampleAdapter = new GameRecyclerAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(exampleAdapter);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getContext() != null){
            MySharedPreferences shared = new MySharedPreferences(getContext());
            list = shared.read();
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
         v = null; // now cleaning up!
    }
}
